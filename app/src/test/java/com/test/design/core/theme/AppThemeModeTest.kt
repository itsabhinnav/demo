package com.test.design.core.theme

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppThemeModeTest {

    @Test
    fun systemFollowsDevice() {
        assertTrue(AppThemeMode.System.resolveDarkTheme(systemInDarkTheme = true))
        assertFalse(AppThemeMode.System.resolveDarkTheme(systemInDarkTheme = false))
    }

    @Test
    fun lightAndDarkOverrideSystem() {
        assertFalse(AppThemeMode.Light.resolveDarkTheme(systemInDarkTheme = true))
        assertTrue(AppThemeMode.Dark.resolveDarkTheme(systemInDarkTheme = false))
    }

    @Test
    fun darkStaysDarkRegardlessOfSystem() {
        assertTrue(AppThemeMode.Dark.resolveDarkTheme(systemInDarkTheme = false))
        assertTrue(AppThemeMode.Dark.resolveDarkTheme(systemInDarkTheme = true))
    }
}
