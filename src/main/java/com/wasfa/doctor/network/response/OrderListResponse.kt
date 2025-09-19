package com.wasfa.doctor.network.response

data class OrderListResponse(

    val totalorderCount: String,
    val totalPages: String,
    val orders: List<Orders>
)

data class Orders(
    val id: String,
    val orderCode: String,
    val customername: String,
    val orderAmount: String,
    val productCount: String,
    val customerAddress: String,
    val mobileNumber: String,
    val deliveryStatus: String,
    val paymentStatus: String,
    val pickupPoints: String,
    val zone: String,
    val refund: String,
    val influencer: String,
    val createdAt: String,
    val collectedByApix: String,
    val collectedByApixEnableStatus: String,
    val paymentMethod: String,
    val additionalNote: String,
    val area: String,
    val governorate: String,
    val customerNote: String,
    val prescriptionId: String,
    val prescriptionNo: String,
    val patientName: String,
    val areaLat: String,
    val areaLong: String,
    val paymentLink: String,
    val addressArray: AddressOrder,
    val orderStatusTime: String,
    val mapLink: String,
    val seller: String

)