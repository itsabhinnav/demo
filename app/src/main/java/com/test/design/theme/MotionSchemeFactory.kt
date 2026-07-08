package com.test.design.theme

import androidx.compose.material3.MotionScheme
import com.test.design.core.motion.AppMotionScheme

fun AppMotionScheme.toMotionScheme(): MotionScheme = when (this) {
    AppMotionScheme.Standard -> MotionScheme.standard()
    AppMotionScheme.Expressive -> MotionScheme.expressive()
    AppMotionScheme.Custom -> CustomMotionScheme
}
