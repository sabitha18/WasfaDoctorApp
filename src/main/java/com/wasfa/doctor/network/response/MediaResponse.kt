package com.wasfa.doctor.network.response

data class MediaResponse (
    val totalFilesCount: String,
    val totalPages: String,
    val uploads: List<Uploads>
)
data class Uploads(
    val id: String,
    val file_original_name: String,
    val file_name: String,
    val url: String,
    val user_id: String,
    val file_size: String,
    val extension: String,
    val type: String
)