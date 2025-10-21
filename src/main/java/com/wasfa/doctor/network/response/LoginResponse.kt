package com.wasfa.doctor.network.response

data class LoginResponse(
    val userDetails: User,
    val token: String
)

data class User(
    val id: String,
    val phone: String,
    val name: String,
    val email: String,
    val user_type: String,
    val permissions: List<String>,
    val doctorType: String
)