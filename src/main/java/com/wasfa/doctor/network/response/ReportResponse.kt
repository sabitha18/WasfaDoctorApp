package com.wasfa.doctor.network.response

data class ReportResponse(
    val totalPrescriptions: String,
    val totalPages: String,
    val prescriptions: List<Report>
)

data class Report(
    val orderCode: String,
    val date: String,
    val itenname: String,
    val sellingPriceAfterAllDiscount: String,
    val doctorAppreciationAmount: String,
    val isPharmacutecal: String,
    val thumbnailImage: String
)
