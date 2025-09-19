package com.wasfa.doctor.network.response

data class CartListResponse(
    val cartItems: List<CartList>,
    val subTotal: String,
    val tax: String,
    val shippingCost: String,
    val discount: String,
    val grandTotal: String,
    val itemCount: String
)

data class CartList(
    val id: String,
    val productId: String,
    val productName: String,
    val productThumbnailImage: String,
    val variation: String,
    val price: String,
    val unitPrice: String,
    val shippingCost: String,
    val apixDiscount: String,
    val sellerDiscount: String,
    val quantity: String,
    val itemDiscount: String

)