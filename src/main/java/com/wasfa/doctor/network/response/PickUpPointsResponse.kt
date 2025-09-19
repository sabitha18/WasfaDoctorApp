package com.wasfa.doctor.network.response

data class PickUpPointsResponse(
    val pickuppoints: List<PickUps>
)

data class PickUps(
    val id: String,
    val name: String
)