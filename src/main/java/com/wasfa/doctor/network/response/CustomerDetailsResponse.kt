package com.wasfa.doctor.network.response

data class CustomerDetailsResponse(
    val id: String,
    val name: String,
    val banned: String,
    val type: String,
    val email: String,
    val phone: String,
    val civilId: String,
    val dob: String,
    val alternateNumber: String,
    val profilePic: String,
    val gender: String,
    val nationality: String,
    val createdBy: String,
    val createdOn: String,
    val spendAmount: String,
    val cartAmount: String,
    val segment: String,
    val addressList: List<AddressList>
)
data class AddressList(
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
