package com.test.design.domain.model

enum class DemoCategory(val label: String) {
    All("All"),
    DesignSystem("Design System"),
    Components("Components"),
    Layouts("Layouts"),
    Vehicle("Vehicle"),
}

data class FeatureDemo(
    val id: String,
    val title: String,
    val description: String,
    val category: DemoCategory,
    val tagline: String,
)
