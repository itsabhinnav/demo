package com.test.design.presentation.demos.playground

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class PlaygroundComponentPropsTest {

    @Test
    fun mergeWithDefaults_fillsMissingKeys() {
        val merged = PlaygroundComponentProps.mergeWithDefaults(
            componentId = "button-primary",
            props = mapOf("label" to "Drive"),
        )

        assertEquals("Drive", merged["label"])
        assertEquals("true", merged["enabled"])
        assertEquals("Variant", merged["bgColor"])
        assertEquals("12", merged["cornerRadiusDp"])
        assertEquals("1", merged["borderWidthDp"])
        assertEquals("LabelLarge", merged["typography"])
    }

    @Test
    fun boolean_parsesStoredValues() {
        val props = mapOf("enabled" to "false")

        assertFalse(PlaygroundComponentProps.boolean(props, "enabled", default = true))
    }
}
