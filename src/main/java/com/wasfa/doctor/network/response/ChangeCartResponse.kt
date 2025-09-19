package com.wasfa.doctor.network.response

data class ChangeCartResponse(
    val subTotal: String,
    val tax: String,
    val shippingCost: String,
    val discount: String,
    val grandTotal: String,
    val itemCount: String
)