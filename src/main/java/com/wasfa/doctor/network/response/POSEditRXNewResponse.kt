package com.wasfa.doctor.network.response

data class POSEditRXNewResponse (
    val prescriptionDetails: List<PresDetails>,
    val patientInfo: List<PatientInfo>,
    val doctorInfo: List<DoctorInfo>
)