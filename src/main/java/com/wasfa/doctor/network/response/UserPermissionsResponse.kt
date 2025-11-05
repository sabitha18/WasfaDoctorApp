package com.wasfa.doctor.network.response

data class UserPermissionsResponse (
    val permissions: List<String>,
    val newPosRx: String
)
