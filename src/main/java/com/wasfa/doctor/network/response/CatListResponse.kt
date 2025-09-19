package com.wasfa.doctor.network.response

data class CatListResponse (
    val totalCategoryCount: String,
    val totalPages: String,
    val categories: List<CatList>
)
data class CatList(
    val id: String? = null,
    val name: String? = null,
    val icon: String? = null,
    val parentCategory: String? = null,
    val children: List<CatList>? = null,
    val level: Int = 0
)