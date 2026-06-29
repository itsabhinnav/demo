package com.test.design.domain.repository

import com.test.design.domain.model.DemoCategory
import com.test.design.domain.model.FeatureDemo

interface FeatureDemoRepository {
    fun getAll(): List<FeatureDemo>
    fun findById(id: String): FeatureDemo?
    fun getCategories(): List<DemoCategory>
}
