package com.wasfa.doctor.network.response


data class CartResponse(
    val cartItems: List<CartItem>,
    val patientInfo: List<PatientInfo>,
    val logo: String,
    val qrCode: String,
    val doctorInfo: List<DoctorInfo>,

)

data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val productThumbnailImage: String,
    val variation: String?,
    val price: String,
    val unitPrice: String,
    val actualPrice: String,
    val currencySymbol: String,
    val doseday: String?,
    val dose: String?,
    val unit: String,
    val dose_time: String?,
    val course_day: String?,
    val course_duration: String?,
    val description: String?,
    val quantity: Int
)

data class PatientInfo(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val alt_phone: String?,
    val civil_id: String,
    val civilId: String,
    val dob: String,
    val address: List<AddressItem>,
    val profilePic: String,
    val gender: String,
    val nationality: String
)
data class AddressItem(
    val id: String,
    val addressTitle: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val governorateId: String?,
    val governorateName: String,
    val areaId: String,
    val areaName: String,
    val block: String,
    val phone: String,
    val setDefault: String,
    val street: String,
    val building: String,
    val floor: String

)