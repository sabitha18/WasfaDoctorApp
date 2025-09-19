package com.wasfa.doctor.network.response

data class PresDetailsResponse(
    val medications: List<Med>,
    val description: String,
    val patientInfo: PatientInfoDetails
)

data class Med(
    val id: String,
    val productId: String,
    val productName: String,
    val productThumbnailImage: String,
    val variation: String?,
    val price: String,
    val unit: String,
    val unitPrice: String,
    val actualPrice: String,
    val currencySymbol: String,
    val doseday: String?,
    val dose: String?,
    val dose_time: String?,
    val course_day: String?,
    val course_duration: String?,
    val description: String?,
    val quantity: Int
)
data class PatientInfoDetails(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String,
    val alt_phone: String?,
    val civil_id: String,
    val dob: String,
    val address: List<AddressItem>,
    val profilePic: String,
    val gender: String,
    val nationality: String
)
