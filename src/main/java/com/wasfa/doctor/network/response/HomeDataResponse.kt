package com.wasfa.doctor.network.response

data class HomeDataResponse(
    val topSellingProducts: List<TopSellProd>,
    val mostViewedProducts: List<MostViewed>,
    val prescriptions: List<PresHome>,
    val totalPrescriptions: String,
    val mostlyPrescibed: String,
    val amount: String,
    val countOfSale: String,
    val pendingCommission: String,
    val completedPayment: String,
    val refundOrders: String,
    val refundAmount: String,
    val currentMonthAmount: String,


    val totalSales: String,
    val totalSalesCount: String,
    val totalSalesSum: String,

    val totalOrders: String,
    val totalOrdersNo: String,
    val totalOrdersCount: String,

    val pendingDelivery: String,
    val pendingDeliveryNo: String,

    val grossMargin: String,
    val conversionRate: String,

    val netMargin: String,

    val refundRequest: String,
    val refundRequestNo: String,

    val approveRequest: String,
    val approveRequestNo: String,

    val cancelledOrders: String,
    val cancelledOrdersNo: String,

    val closedOrders: String,
    val closedOrdersNo: String,

    val sellerMissingProductCount: String,
    val sellerCommissionWithNIL: String,
    val doctorCommissionTableCount: String,
    val doctorCommissionWithNil: String,
    val doctorCommissionPharmaceuticalWithZero: String,
    val doctorCommissionNonPharmaceuticalWithZero: String,

    val generalTotalSales: String,
    val totalPatients: String,
    val totalSaleAmount: String,
    val generalTotalOrders: String,
    val totalOrderAmount: String,
    val totalDoctors: String,
    val totalVendors: String,
    val stockCount: String,
    val productsCount: String,
    val doctorOutstanding: String,
    val paidToDoctor: String,
    val sellerOutstanding: String,
    val generalGrossMargin: String,
    val generalNetMargin: String,
    val generalPendingDelivery: String,
    val returnRequests: String,
    val generalApprovedRequests: String,
    val deliveryCost: String,
    val deliveryCount: String,
    val generalCancelledOrders: String,
    val generalClosedOrders: String,
    val paidToSeller: String,


    val recentOrders: List<RecentOrders>,
    val newCustomers: List<NewCustomers>,
    val topSearches: List<TopSearches>,
    val lastSearchTerm: List<LastSearchTerm>,
    val newVendors: List<NewVendors>,
    val newDoctors: List<NewDoctors>

)
data class MostViewed(
    val id: String,
    val name: String,
    val sellerLogo: String,
    val thumbnail_image: String,
    val discount: String,
    val basePrice: String,
    val purchasePrice: String,
    val rating: String,
    val numberOfsales: String,
    val sellerDiscount: String,
    val sellerName: String,
    val sellerSku: String,
    val apixSku: String,
    val currentStock: String,
    val publishedOnWebsite: String,
    val Restricted: String,
    val publishedOnPos: String,
    val isPharma: String,
    val isClean: String,
    val cog: String,
    val apixMargin: String,
    val influencerrMargin: String
)
data class NewDoctors(
    val id: String,
    val name: String,
    val mobile_no: String,
    val ifr_id: String
)
data class NewVendors(
    val id: String,
    val name: String,
    val phone: String
)
data class LastSearchTerm(
    val id: String,
    val query: String
)
data class TopSearches(
    val id: String,
    val query: String
)
data class NewCustomers(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val status: String,
    val enrolled_on: String,
    val recentAddress: List<RecentAddress>
)
data class RecentAddress(
    val id: String,
    val addressTitle: String,
    val governorateName: String,
    val areaName: String,
    val block: String,
    val street: String
)

data class RecentOrders(
    val id: String,
    val code: String,
    val user_id: String,
    val delivery_status: String,
    val storeName: String,
    val date: String,
    val total: String
)

data class PatientInfoHome(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val alt_phone: String?,
    val civil_id: String,
    val dob: String,
    val address: List<AddressItem>,
    val profilePic: String
)

data class PresHome(
    val id: String,
    val name: String,
    val medicationsPrescribed: String,
    val patientInfo: List<PatientInfoHome>,
)

data class TopSellProd(
    val id: String,
    val name: String,
    val sellerLogo: String,
    val thumbnail_image: String,
    val discount: String,
    val basePrice: String,
    val purchasePrice: String,
    val rating: String,
    val numberOfsales: String,
    val sellerDiscount: String,
    val sellerName: String,
    val sellerSku: String,
    val apixSku: String,
    val currentStock: String,
    val publishedOnWebsite: String,
    val Restricted: String,
    val publishedOnPos: String,
    val isPharma: String,
    val isClean: String,
    val cog: String,
    val apixMargin: String,
    val influencerrMargin: String
)