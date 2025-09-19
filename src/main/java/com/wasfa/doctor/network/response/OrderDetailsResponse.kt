package com.wasfa.doctor.network.response

data class OrderDetailsResponse (
    val id: String,
    val code: String,
    val paymentType: String,
    val paymentStatus: String,
    val deliveryStatus: String,
    val address: AddressOrder,
    val deliveryBoys: List<DeliveryBoys>,
    val grandTotal: String,
    val date: String,
    val influencerId: String,
    val influencerName: String,
    val deleiveryBoyId: String,
    val deleiveryBoyName: String,
    val lead: String,
    val itemsList: List<ItemsList>,

)
data class AddressOrder(
    val name: String,
    val last_name: String,
    val governorate: String,
    val governorate_id: String,
    val areaName: String,
    val area_id: String,
    val block: String,
    val street: String,
    val building: String,
    val appartment: String,
    val floor: String,
    val phone: String,
    val address_title: String,
    val alternate_phone: String,
    val email: String,
    val lane: String,
    val flat: String
)
data class DeliveryBoys(
    val id: String,
    val name: String,
    val created_by: String,
    val spend_amount: String,
    val cart_count: String,
    val age: String
)
data class ItemsList(
    val id: String,
    val productId: String,
    val productName: String,
    val price: String,
    val thumbnailImage: String,
    val quantity: String,
    val deliveryStatus: String,
    val lastReturnDate: String,
    val influencerName: String,
    val seller: String,
    val PickedUpStatus: Int,
    val purchaseFrom : List<PurchaseForm>
)