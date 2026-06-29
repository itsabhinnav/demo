package com.test.design.domain.model

enum class DemoCategory(val label: String) {
    All("All"),
    Compose("Compose"),
    DesignSystem("Design System"),
    Components("Components"),
    Layouts("Layouts"),
}

data class FeatureDemo(
    val id: String,
    val title: String,
    val description: String,
    val category: DemoCategory,
    val tagline: String,
)
