plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.test.design.systemui.scalableui"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.test.design.systemui.scalableui"
        minSdk = 34
        targetSdk = 36
        versionCode = 2
        versionName = "2.0-adaptive-space"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        buildConfig = false
        resValues = false
    }

    packaging {
        resources {
            excludes += setOf("**/*.kotlin_builtins", "META-INF/**", "kotlin/**")
        }
    }
}
