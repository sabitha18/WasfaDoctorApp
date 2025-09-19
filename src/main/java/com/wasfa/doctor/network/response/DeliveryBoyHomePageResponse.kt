package com.wasfa.doctor.network.response

data class DeliveryBoyHomePageResponse (
    val totalOrdersCount: String,
    val pendingOrdersCount: String,
    val deliveredOrdersCount: String
)