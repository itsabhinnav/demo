package com.test.design.presentation.navigation

object Routes {
    const val HOME = "home"
    const val DEMO = "demo/{demoId}"

    fun demo(demoId: String): String = "demo/$demoId"
}
