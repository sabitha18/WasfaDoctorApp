package com.wasfa.doctor.network.response

data class ChangeApixDiscountResponse(
    val subTotal: String,
    val discount: String,
    val grandTotal: String
)