package com.wasfa.doctor.network.response

data class POSEditRXNewResponse (
    val id : String,
    val prescriptionDetails: List<PresDetails>,
    val patientInfo: List<PatientInfo>,
    val doctorInfo: List<DoctorInfo>,
    val qrCode: String,
    val logo: String,
    val clinicName: String,
    val designation: String,
    val items_new: String,
    val doctor_new_note: String
)