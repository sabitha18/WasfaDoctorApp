package com.wasfa.doctor.network.response

data class CategoryDetailsResponse (
    val id: String,
    val name: String,
    val banner: String,
    val icon: String,
    val arabicName: String,
    val parentName: String,
    val orderLevel: String,
    val digital: String,
    val metaTitle: String,
    val metaDescription: String,
    val arabicMetaTitle: String,
    val arabicMetaDescription: String,
    val parentId: String,
    val commisionRate: String,
    val bannerId: String,
    val iconId: String,
    val logo: String,
    val logoid: String
)