package com.wasfa.doctor.network.response

data class AccountResponse (
  val data: List<AccountData>
)
data class AccountData(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val profilePic: String
)