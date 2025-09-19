package com.wasfa.doctor.network.response

data class AddressListResponse(
    val id: String,
    val addressTitle: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val governorateId: String,
    val governorateName: String,
    val areaId: String,
    val areaName: String,
    val block: String,
    val phone: String,
    val setDefault: String,
    val street: String,
    val building: String,
    val floor: String,
    val appartment: String,
    val alternatePhone: String,
    val mapLink: String
)
