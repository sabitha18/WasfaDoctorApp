package com.wasfa.doctor.network.response

data class SubmitResponse (
    val id: String,
    val logo: String,
    val qrCode: String,
    val doctorInfo: List<DoctorInfo>,
    val patientInfo: List<PatientInfo>,
    val prescriptionDetails: List<PresDetails>,
    val clinicName: String,
    val designation: String
)
data class PresDetails(
    val id: String,
    val productId: String,
    val productName: String,
    val productThumbnailImage: String,
    val variation: String,
    val price: String,
    val unitPrice: String,
    val actualPrice: String,
    val currencySymbol: String,
    val doseday: String,
    val dose: String,
    val dose_time: String,
    val course_day: String,
    val course_duration: String,
    val description: String,
    val quantity: String
)
data class PatientDetailInfo(
    val id: String,
    val name: String,
    val type: String,
    val email: String,
    val phone: String,
    val civilId: String,
    val dob: String,
    val alternateNumber: String,
    val profilePic: String
)
data class DoctorInfo(
    val id: String,
    val name: String,
    val type: String,
    val email: String,
    val phone: String,
    val civilId: String,
    val dob: String,
    val alternateNumber: String,
    val profilePic: String
)