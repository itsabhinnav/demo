package com.test.design.di

import com.test.design.domain.repository.FeatureDemoRepository
import com.test.design.domain.repository.FeatureDemoRepositoryImpl

object AppContainer {
    val featureDemoRepository: FeatureDemoRepository by lazy {
        FeatureDemoRepositoryImpl()
    }
}
