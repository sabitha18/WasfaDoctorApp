package com.wasfa.doctor.network.response

data class PrescribedRXResponse(
    val list: List<ListRX>,
    val totalCount: String,
    val totalPages: String
)

data class ListRX(
    val prescription_id: String,
    val customer: String,
    val product_count: String,
    val doctor: String,
    val customerId: String,
    val status_order: String,
    val date: String,
    val edited: String

)

