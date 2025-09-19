package com.wasfa.doctor.network.response

data class SegmentResponse(
    val segments: List<Segments>,
    val totalCount: String,
    val totalPages: String
)
data class Segments(
    val id: String,
    val name: String,
    val arabicName: String,
    val status: String
)