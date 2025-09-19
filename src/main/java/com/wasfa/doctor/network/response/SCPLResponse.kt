package com.wasfa.doctor.network.response

data class SCPLResponse (
    val products: List<SCProducts>,
    val totalProductsCount: String,
    val totalPages: String
)
data class SCProducts(
    val id: String,
    val name: String,
    val thumbnail_image: String,
    val discount: String,
    val basePrice: String,
    val purchasePrice: String,
    val createdAt: String,
    val published: String,
    val rating: String,
    val numberOfsales: String,
    val sellerDiscount: String,
    val sellerName: String,
    val unitPrice: String,
    val sellerSku: String,
    val seller: String,
    val apixSku: String,
    val currentStock: String,
    val publishedOnWebsite: String,
    val Restricted: String,
    val publishedOnPos: String,
    val isPharma: String,
    val isClean: String,
    val cog: String,
    val apixMargin: String,
    val influencerrMargin: String,
    val isFavourite: Boolean,
    val commissionValue: String
)