package com.wasfa.doctor.network.response

data class TypeResponse (
    val types: List<Types>,
    val totalCount: String,
    val totalPages: String

)
data class Types(
    val id: String,
    val name: String,
    val arabicName: String,
    val status: String

)