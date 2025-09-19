package com.wasfa.doctor.network.response

data class ProfileData(
    val data: List<ProfileResponse>
)

data class ProfileResponse (
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