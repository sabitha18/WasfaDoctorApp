package com.wasfa.doctor.network.response

data class ApixSkuResponse(
    val apixSku: List<SKU>,
    val totalCount: String,
    val totalPages: String
)

data class SKU(
    val id: String,
    val sku: String
)