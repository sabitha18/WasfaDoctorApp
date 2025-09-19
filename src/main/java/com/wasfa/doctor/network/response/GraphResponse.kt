package com.wasfa.doctor.network.response

data class GraphResponse(
    val num_of_sale_value: List<String>,
    val weekdays: List<String>,
    val totalPrescriptions: String,
    val mostlyPrescibed: String,
    val amount: String,
    val countOfSale: String,
    val pendingCommission: String,
    val completedPayment: String,
    val refundOrders: String,
    val refundAmount: String,
    val currentMonthAmount: String,
    val num_of_sale_amt: List<String>,
    val influencers: List<Influencer>,
    val num_of_gross_margin: List<String>,
    val num_of_apix_margin: List<String>,
    val months: Map<String, MonthData>,
    val hourSales: List<HourSales>
)
data class HourSales(
    val hour: String,
    val sales: String,
    val orders: String
)
data class MonthData(
    val num_sales: String,
    val amount_sales: String,
    val product_sales: String,
    val cog: String,
    val apix_margin: String,
    val net_margin: String
)
data class Influencer(
    val label: String,
    val data: List<String>,
    val borderColor: String,
    val backgroundColor: String,
    val fill: String
)


