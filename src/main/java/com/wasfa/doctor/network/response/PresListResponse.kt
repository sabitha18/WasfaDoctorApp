package com.wasfa.doctor.network.response

data class PresListResponse(
    val prescriptions: List<Prescriptions>,
    val totalProductsCount: String,
    val totalPages: String
)

data class Prescriptions(
    val id: String,
    val name: String,
    val medicationsPrescribed: String,
    val description: String,
    val profilePic: String,
    val patientInfo: List<PatientInfo>
)