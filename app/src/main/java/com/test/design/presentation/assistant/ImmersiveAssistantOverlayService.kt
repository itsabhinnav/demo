package com.test.design.presentation.assistant

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.test.design.core.DrivingUxState
import com.test.design.core.motion.AppMotionScheme
import com.test.design.core.theme.AppThemeMode
import com.test.design.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Translucent system overlay host for the standalone immersive assistant.
 *
 * AAOS multi-panel parks translucent *activities* under app_panel as invisible;
 * TYPE_APPLICATION_OVERLAY draws over Settings / any app with the same look.
 *
 * While listening ([AssistantPresentation.Compact]), touches outside the corner
 * bubble pass through to underlying apps. Fullscreen hit testing resumes when
 * the session morphs to [AssistantPresentation.Immersive].
 */
class ImmersiveAssistantOverlayService : LifecycleService(),
    SavedStateRegistryOwner,
    ViewModelStoreOwner {

    private val savedStateController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateController.savedStateRegistry

    private val store = ViewModelStore()
    override val viewModelStore: ViewModelStore
        get() = store

    private lateinit var windowManager: WindowManager
    private var overlayView: PassThroughFrameLayout? = null

    private val uiJob = SupervisorJob()
    private val uiScope = CoroutineScope(AndroidUiDispatcher.Main + uiJob)

    private var summonEpoch = 0
    private val bubbleHitRect = Rect()
    @Volatile
    private var presentation: AssistantPresentation = AssistantPresentation.Compact

    override fun onCreate() {
        super.onCreate()
        savedStateController.performAttach()
        savedStateController.performRestore(null)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }
        attachOverlay()
        instance = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopSelf()
            ACTION_SUMMON -> {
                summonEpoch++
                notifyImmersiveAssistantHotword()
            }
            else -> {
                if (overlayView == null && Settings.canDrawOverlays(this)) {
                    attachOverlay()
                }
                if (summonEpoch == 0) {
                    summonEpoch = 1
                    notifyImmersiveAssistantHotword()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        instance = null
        detachOverlay()
        store.clear()
        uiScope.cancel()
        super.onDestroy()
    }

    private fun attachOverlay() {
        if (overlayView != null) return

        val recomposer = Recomposer(AndroidUiDispatcher.Main)
        uiScope.launch(AndroidUiDispatcher.Main) {
            recomposer.runRecomposeAndApplyChanges()
        }

        val composeView = ComposeView(this).also { v ->
            v.setViewTreeLifecycleOwner(this)
            v.setViewTreeSavedStateRegistryOwner(this)
            v.setViewTreeViewModelStoreOwner(this)
            v.compositionContext = recomposer
            // Lean AppTheme host — DesignAppShell requires Activity SavedStateHandle factories.
            v.setContent {
                AppTheme(
                    themeMode = AppThemeMode.Dark,
                    drivingUxState = DrivingUxState.Parked,
                    appMotionScheme = AppMotionScheme.Expressive,
                ) {
                    LaunchedEffect(Unit) {
                        hotwordDetections(this@ImmersiveAssistantOverlayService).collectLatest {
                            notifyImmersiveAssistantHotword()
                        }
                    }
                    VirtualAssistantOverlay(
                        onDismiss = { stopSelf() },
                        modifier = Modifier.fillMaxSize(),
                        awaitHotword = false,
                        onPresentationChanged = { next ->
                            presentation = next
                            if (next == AssistantPresentation.Immersive) {
                                bubbleHitRect.setEmpty()
                            }
                            updateBlurBehind(next == AssistantPresentation.Immersive)
                        },
                        onBubbleBoundsInRoot = { l, t, r, b ->
                            if (presentation == AssistantPresentation.Compact) {
                                bubbleHitRect.set(l, t, r, b)
                            }
                        },
                    )
                }
            }
        }

        val view = PassThroughFrameLayout(this).also { frame ->
            frame.hitRectProvider = {
                when (presentation) {
                    AssistantPresentation.Compact -> {
                        if (bubbleHitRect.isEmpty) {
                            // Until first layout, claim a small bottom-end region.
                            val w = frame.width.coerceAtLeast(1)
                            val h = frame.height.coerceAtLeast(1)
                            val density = resources.displayMetrics.density
                            val bw = (300 * density).roundToInt()
                            val bh = (100 * density).roundToInt()
                            val inset = (20 * density).roundToInt()
                            Rect(w - bw - inset, h - bh - inset, w - inset, h - inset)
                        } else {
                            Rect(bubbleHitRect)
                        }
                    }
                    AssistantPresentation.Immersive -> {
                        Rect(0, 0, frame.width.coerceAtLeast(1), frame.height.coerceAtLeast(1))
                    }
                }
            }
            frame.addView(
                composeView,
                android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                ),
            )
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            // Blur only once immersive — compact listening stays clear.
        }

        overlayView = view
        windowManager.addView(view, params)
    }

    private fun updateBlurBehind(enabled: Boolean) {
        val view = overlayView ?: return
        val params = view.layoutParams as? WindowManager.LayoutParams ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (enabled) {
                params.flags = params.flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                params.blurBehindRadius = 48
            } else {
                params.flags = params.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
                params.blurBehindRadius = 0
            }
            runCatching { windowManager.updateViewLayout(view, params) }
        }
    }

    private fun detachOverlay() {
        overlayView?.let { runCatching { windowManager.removeViewImmediate(it) } }
        overlayView = null
    }

    companion object {
        const val ACTION_STOP = "com.test.design.assistant.IMMERSIVE_STOP"
        const val ACTION_SUMMON = "com.test.design.assistant.IMMERSIVE_SUMMON"

        @Volatile
        private var instance: ImmersiveAssistantOverlayService? = null

        fun show(context: Context) {
            val app = context.applicationContext
            if (instance != null) {
                app.startService(
                    Intent(app, ImmersiveAssistantOverlayService::class.java)
                        .setAction(ACTION_SUMMON),
                )
            } else {
                app.startService(Intent(app, ImmersiveAssistantOverlayService::class.java))
            }
        }

        fun stop(context: Context) {
            context.applicationContext.startService(
                Intent(context.applicationContext, ImmersiveAssistantOverlayService::class.java)
                    .setAction(ACTION_STOP),
            )
        }
    }
}

/**
 * Claims touches only inside [hitRectProvider]; returns false elsewhere so the
 * window manager forwards events to underlying maps / apps.
 */
private class PassThroughFrameLayout(context: Context) : android.widget.FrameLayout(context) {
    var hitRectProvider: (() -> Rect)? = null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val rect = hitRectProvider?.invoke() ?: return super.dispatchTouchEvent(ev)
        val x = ev.x.roundToInt()
        val y = ev.y.roundToInt()
        return if (rect.contains(x, y)) {
            super.dispatchTouchEvent(ev)
        } else {
            false
        }
    }
}
