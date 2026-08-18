package com.company.qurilishmarket.domain.model

data class Category(
    val id: String,
    val name: String,
    val iconUrl: String? = null,
    val parentId: String? = null,   // kelajakda subkategoriya uchun (§10)
    val order: Int = 0
)
