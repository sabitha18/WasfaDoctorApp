package com.wasfa.doctor.network.response

data class AllPermissionsResponse (
    val permissions: List<Permissions>
)
data class Permissions(
    val name: String,
    val section: String
)