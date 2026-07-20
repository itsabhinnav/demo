plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.test.design.framework.scalableui"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.test.design.framework.scalableui"
        minSdk = 35
        targetSdk = 37
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
