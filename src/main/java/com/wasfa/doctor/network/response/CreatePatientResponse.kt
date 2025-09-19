package com.wasfa.doctor.network.response

data class CreatePatientResponse(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val civilId: String,
    val dob: String,
    val alternateNumber: String,
    val gender: String,
    val nationality: String,
    val createdBy: String,
    val createdOn: String,
    val spendAmount: String,
    val cartAmount: String,
    val segment: String,
    val type: String

)
