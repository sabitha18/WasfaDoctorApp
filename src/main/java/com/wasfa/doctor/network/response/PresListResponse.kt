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
    val patientInfo: List<PatientInfo>,
    val customer: String,
    val customerId: String,
    val mobile: String,
    val product_count: String,
    val doctor: String,
    val status_order: String,
    val price_request: String,
    val call_request: String,
    val prescription_id: String
)