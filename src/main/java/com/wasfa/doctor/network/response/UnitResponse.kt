package com.wasfa.doctor.network.response

data class UnitResponse (
    val units: List<Units>

)
data class Units(
    val id: String,
    val name: String,
    val arabic_name: String
)