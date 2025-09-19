package com.wasfa.doctor.network.response

data class BrandListResponse (
    val totalCategoryCount: String,
    val totalPages: String,
    val brands: List<Brands>,

)
data class Brands(
    val id: String,
    val name: String,
    val logo: String
)