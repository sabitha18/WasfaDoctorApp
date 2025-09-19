package com.wasfa.doctor.network.response

data class PurchaseFormResponse(

    val list: List<PurchaseForm>
)

data class PurchaseForm(
    val id: String,
    val purchase_from: String,
    val name: String
)