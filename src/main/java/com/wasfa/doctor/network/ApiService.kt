package com.wasfa.doctor.network


import com.wasfa.doctor.network.response.AccountResponse
import com.wasfa.doctor.network.response.AddressListResponse
import com.wasfa.doctor.network.response.AllPermissionsResponse
import com.wasfa.doctor.network.response.ApixSkuAddResponse
import com.wasfa.doctor.network.response.ApixSkuResponse
import com.wasfa.doctor.network.response.AreaResponse
import com.wasfa.doctor.network.response.BrandListResponse
import com.wasfa.doctor.network.response.CalculatorResponse
import com.wasfa.doctor.network.response.CartCountResponse
import com.wasfa.doctor.network.response.CartListResponse
import com.wasfa.doctor.network.response.CartResponse
import com.wasfa.doctor.network.response.CatListResponse
import com.wasfa.doctor.network.response.CategoryDetailsResponse
import com.wasfa.doctor.network.response.ChangeApixDiscountResponse
import com.wasfa.doctor.network.response.ChangeCartResponse
import com.wasfa.doctor.network.response.CountryListResponse
import com.wasfa.doctor.network.response.CreatePatientResponse
import com.wasfa.doctor.network.response.CustomerDetailsResponse
import com.wasfa.doctor.network.response.CustomerListResponse
import com.wasfa.doctor.network.response.DeliveryBoyHomePageResponse
import com.wasfa.doctor.network.response.DeliveryBoyResponse
import com.wasfa.doctor.network.response.GovernorateResponse
import com.wasfa.doctor.network.response.GraphResponse
import com.wasfa.doctor.network.response.HomeDataResponse
import com.wasfa.doctor.network.response.InfluencerListResponse
import com.wasfa.doctor.network.response.LoginResponse
import com.wasfa.doctor.network.response.MediaResponse
import com.wasfa.doctor.network.response.MedicalRepListResponse
import com.wasfa.doctor.network.response.OrderDetailsResponse
import com.wasfa.doctor.network.response.OrderListResponse
import com.wasfa.doctor.network.response.PickUpPointsResponse
import com.wasfa.doctor.network.response.PresDetailsResponse
import com.wasfa.doctor.network.response.PresListResponse
import com.wasfa.doctor.network.response.ProductDetailsResponse
import com.wasfa.doctor.network.response.ProductListResponse
import com.wasfa.doctor.network.response.ProfileData
import com.wasfa.doctor.network.response.PurchaseFormResponse
import com.wasfa.doctor.network.response.ReportResponse
import com.wasfa.doctor.network.response.SCPLResponse
import com.wasfa.doctor.network.response.SearchInfluencerResponse
import com.wasfa.doctor.network.response.SegmentResponse
import com.wasfa.doctor.network.response.SellerListResponse
import com.wasfa.doctor.network.response.SubmitResponse
import com.wasfa.doctor.network.response.TotalCashClearedResponse
import com.wasfa.doctor.network.response.TypeResponse
import com.wasfa.doctor.network.response.UnitResponse
import com.wasfa.doctor.network.response.UserPermissionsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {

    @GET("getTotalCashCleared")
    suspend fun getTotalCashCleared(@Header("Authorization") token: String): Response<ApiResponse<TotalCashClearedResponse>>

    @GET("userPermissions")
    suspend fun userPermissions(@Header("Authorization") token: String): Response<ApiResponse<UserPermissionsResponse>>

    @GET("allPermissions")
    suspend fun allPermissions(@Header("Authorization") token: String): Response<ApiResponse<AllPermissionsResponse>>

    @POST("deleteShippingAddress")
    suspend fun deleteShippingAddress(
        @Header("Authorization") token: String,
        @Body requestBody: CartRemoveRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("segmantList")
    suspend fun getSegmentListNew(
        @Header("Authorization") token: String,
        @Body requestBody: BrandRequest
    ): Response<ApiResponse<SegmentResponse>>

    @POST("typeList")
    suspend fun getTypeListNew(
        @Header("Authorization") token: String,
        @Body requestBody: BrandRequest
    ): Response<ApiResponse<TypeResponse>>

    @POST("updateTypeStatus")
    suspend fun updateTypeStatus(
        @Header("Authorization") token: String,
        @Body requestBody: FavRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("deleteType")
    suspend fun deleteType(
        @Header("Authorization") token: String,
        @Body requestBody: CartRemoveRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("updateTypeDetails")
    suspend fun updateTypeDetails(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateSegmentDetailsRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("addType")
    suspend fun addType(
        @Header("Authorization") token: String,
        @Body requestBody: AddSegmentRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("addSegmant")
    suspend fun addSegmant(
        @Header("Authorization") token: String,
        @Body requestBody: AddSegmentRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("updateSegmantDetails")
    suspend fun updateSegmentDetails(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateSegmentDetailsRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("deleteSegmant")
    suspend fun deleteSegmant(
        @Header("Authorization") token: String,
        @Body requestBody: CartRemoveRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("updateSegmantStatus")
    suspend fun updateSegmantStatus(
        @Header("Authorization") token: String,
        @Body requestBody: FavRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("customerUpdate")
    suspend fun customerUpdate(
        @Header("Authorization") token: String,
        @Body requestBody: CustomerUpdateRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("customerDetails")
    suspend fun customerDetails(
        @Header("Authorization") token: String,
        @Body requestBody: CartRemoveRequest
    ): Response<ApiResponse<List<CustomerDetailsResponse>>>

    @POST("customerUpdateBanStatus")
    suspend fun customerUpdateBanStatus(
        @Header("Authorization") token: String,
        @Body requestBody: FavRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("customerDelete")
    suspend fun customerDelete(
        @Header("Authorization") token: String,
        @Body requestBody: CartRemoveRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("updateType")
    suspend fun updateType(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateTypeRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("updateSegmant")
    suspend fun updateSegmant(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateSegmantRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("updateCashReceivedStatus")
    suspend fun updateCashReceivedStatus(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateDeliveryStatusRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("updatePickedUpStatus")
    suspend fun updatePickedUpStatus(
        @Header("Authorization") token: String,
        @Body requestBody: ChangePickupStatusRequest
    ): Response<ApiResponseNoData<Unit>>

    @GET("typesList")
    suspend fun getTypesList(@Header("Authorization") token: String): Response<ApiResponse<TypeResponse>>

    @GET("segmentsList")
    suspend fun getSegmentsList(@Header("Authorization") token: String): Response<ApiResponse<SegmentResponse>>

    @POST("homePage")
    suspend fun getHomePageDeliveryBoy(@Header("Authorization") token: String,@Body requestBody: HomeRequest): Response<ApiResponse<DeliveryBoyHomePageResponse>>

    @GET("countryList")
    suspend fun getCountryList(@Header("Authorization") token: String): Response<ApiResponse<List<CountryListResponse>>>

    @POST("influencerSearch")
    suspend fun influencerSearch(
        @Header("Authorization") token: String,
        @Body requestBody: ChangePickupPointRequest
    ): Response<ApiResponse<SearchInfluencerResponse>>

    @Multipart
    @POST("updateProfile")
    suspend fun updateAdminProfile(
        @Header("Authorization") token: String,
        @Part("phone") phone: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("confirm_password") confirm_password: RequestBody,
        @Part profilePic: MultipartBody.Part?
    ): Response<ApiResponseNoData<Unit>>

    @GET("profile")
    suspend fun getAdminProfile(@Header("Authorization") token: String): Response<ApiResponse<AccountResponse>>

    @POST("updateLead")
    suspend fun updateLead(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateLeadRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("assignDeliveryboy")
    suspend fun assignDeliveryboy(
        @Header("Authorization") token: String,
        @Body requestBody: AssignDeliveryBoyRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("assignInfluencer")
    suspend fun assignInfluencer(
        @Header("Authorization") token: String,
        @Body requestBody: AssignInfluencerStatusRequest
    ): Response<ApiResponseNoData<Unit>>

    @GET("deliveryboysList")
    suspend fun getDeliveryBoysList(@Header("Authorization") token: String): Response<ApiResponse<List<DeliveryBoyResponse>>>

    @POST("orderDetails")
    suspend fun orderDetails(
        @Header("Authorization") token: String,
        @Body requestBody: CatDetailsRequest
    ): Response<ApiResponse<List<OrderDetailsResponse>>>

    @POST("updatePaymentStatus")
    suspend fun updatePaymentStatus(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateDeliveryStatusRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("updateDeliveryStatus")
    suspend fun updateDeliveryStatus(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateDeliveryStatusRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("orderList")
    suspend fun orderList(
        @Header("Authorization") token: String,
        @Body requestBody: OrderListRequest
    ): Response<ApiResponse<OrderListResponse>>

    @POST("addUnit")
    suspend fun addUnit(
        @Header("Authorization") token: String,
        @Body requestBody: AddUnitRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("deleteCategory")
    suspend fun deleteCategory(
        @Header("Authorization") token: String,
        @Body requestBody: CatDetailsRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("deleteBrand")
    suspend fun deleteBrand(
        @Header("Authorization") token: String,
        @Body requestBody: CatDetailsRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("updateBrand")
    suspend fun editBrand(
        @Header("Authorization") token: String,
        @Body requestBody: EditBrandRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("brandsDetails")
    suspend fun getBrandDetails(
        @Header("Authorization") token: String,
        @Body requestBody: CatDetailsRequest
    ): Response<ApiResponse<List<CategoryDetailsResponse>>>

    @POST("updateProduct")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateProductRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("addProduct")
    suspend fun addProduct(
        @Header("Authorization") token: String,
        @Body requestBody: AddProductRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("updateCategory")
    suspend fun editCategory(
        @Header("Authorization") token: String,
        @Body requestBody: EditCatRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("categoryDetails")
    suspend fun getCategoryDetails(
        @Header("Authorization") token: String,
        @Body requestBody: CatDetailsRequest
    ): Response<ApiResponse<List<CategoryDetailsResponse>>>

    @POST("addCategory")
    suspend fun addCategory(
        @Header("Authorization") token: String,
        @Body requestBody: AddCatRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("mediaList")
    suspend fun getMediaList(
        @Header("Authorization") token: String,
        @Body requestBody: MediaRequest
    ): Response<ApiResponse<MediaResponse>>

    @GET("purchaseFromList")
    suspend fun getPurchaseFromList(@Header("Authorization") token: String): Response<ApiResponse<PurchaseFormResponse>>

    @GET("unitList")
    suspend fun getUnitList(@Header("Authorization") token: String): Response<ApiResponse<UnitResponse>>

    @POST("PickupPointsList")
    suspend fun getPickupPointsList(
        @Header("Authorization") token: String,
        @Body requestBody: PickUpPointsRequest
    ): Response<ApiResponse<PickUpPointsResponse>>

    @POST("categoryList")
    suspend fun getCategory(
        @Header("Authorization") token: String,
        @Body requestBody: CatRequest
    ): Response<ApiResponse<CatListResponse>>

    @POST("SingleProductInfluencerCommissionUpdate")
    suspend fun changeInfluencerCommission(
        @Header("Authorization") token: String,
        @Body requestBody: CommissionRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("InfluencerProductList")
    suspend fun InfluencerProductList(
        @Header("Authorization") token: String,
        @Body requestBody: InfluencerRequest
    ): Response<ApiResponse<SCPLResponse>>

    @POST("generateApixSku")
    suspend fun generateApixSku(
        @Header("Authorization") token: String,
        @Body requestBody: GenerateSKURequest
    ): Response<ApiResponse<ApixSkuAddResponse>>

    @POST("apixSkuList")
    suspend fun apixSkuList(
        @Header("Authorization") token: String,
        @Body requestBody: SKURequest
    ): Response<ApiResponse<ApixSkuResponse>>

    @POST("sellerCommissionProductList")
    suspend fun sellerCommissionProductList(
        @Header("Authorization") token: String,
        @Body requestBody: SCPLRequest
    ): Response<ApiResponse<SCPLResponse>>

    @POST("UpdateProductStatus")
    suspend fun updateProductStatus(
        @Header("Authorization") token: String,
        @Body requestBody: UpdatePSRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("changeSellerCommission")
    suspend fun changeSellerCommission(
        @Header("Authorization") token: String,
        @Body requestBody: CommissionRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("conditionsSubmit")
    suspend fun conditionSubmit(
        @Header("Authorization") token: String,
        @Body requestBody: ConditionRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("calculateMargin")
    suspend fun calculateApixMargin(
        @Header("Authorization") token: String,
        @Body requestBody: CalcApixRequest
    ): Response<ApiResponse<CalculatorResponse>>

    @POST("updateFavourite")
    suspend fun updateFav(
        @Header("Authorization") token: String,
        @Body requestBody: FavRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("prescriptionReport")
    suspend fun getReportList(
        @Header("Authorization") token: String,
        @Body requestBody: ReportRequest
    ): Response<ApiResponse<ReportResponse>>

    @POST("prescriptionGraphData")
    suspend fun getGraphData(
        @Header("Authorization") token: String,
        @Body requestBody: GraphRequest
    ): Response<ApiResponse<GraphResponse>>

    @Multipart
    @POST("updateProfile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("phone") phone: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("civilId") civilId: RequestBody,
        @Part("alternateNumber") alternateNumber: RequestBody,
        @Part profilePic: MultipartBody.Part?
    ): Response<ApiResponseNoData<Unit>>

    @POST("homePage")
    suspend fun getDocHome(@Header("Authorization") token: String, @Body requestBody: HomeRequest): Response<ApiResponse<HomeDataResponse>>

    @POST("prescriptionDetails")
    suspend fun getPresDetails(
        @Header("Authorization") token: String,
        @Body requestBody: PresDetailsRequest
    ): Response<ApiResponse<List<PresDetailsResponse>>>

    @GET("profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ApiResponse<ProfileData>>

    @POST("prescriptions")
    suspend fun getPresList(
        @Header("Authorization") token: String,
        @Body requestBody: PresRequest
    ): Response<ApiResponse<PresListResponse>>

    @POST("submitRx")
    suspend fun submitRx(
        @Header("Authorization") token: String,
        @Body requestBody: SubmitRXRequest
    ): Response<ApiResponse<SubmitResponse>>

    @POST("updateRxQuantity")
    suspend fun updateRXCart(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateRXCartRequest
    ): Response<ApiResponseNoData<Unit>>

    @GET("rxList")
    suspend fun getCartRX(@Header("Authorization") token: String): Response<ApiResponse<CartResponse>>

    @GET("emptyCart")
    suspend fun emptyCart(@Header("Authorization") token: String): Response<ApiResponseNoData<Unit>>

    @POST("createPatient")
    suspend fun createPatient(
        @Header("Authorization") token: String,
        @Body requestBody: CreatePatientRequest
    ): Response<ApiResponse<CreatePatientResponse>>

    @POST("customerList")
    suspend fun checkPatientExist(
        @Header("Authorization") token: String,
        @Body requestBody: CheckPatientRequest
    ): Response<ApiResponse<CustomerListResponse>>

    @GET("medicalRepList")
    suspend fun getMedicalRepList(@Header("Authorization") token: String): Response<ApiResponse<List<MedicalRepListResponse>>>

    @GET("sellersList")
    suspend fun getSellerList(@Header("Authorization") token: String): Response<ApiResponse<List<SellerListResponse>>>

    @POST("brandsList")
    suspend fun getBrandList(
        @Header("Authorization") token: String,
        @Body requestBody: BrandRequest
    ): Response<ApiResponse<BrandListResponse>>

    @GET("influencerList")
    suspend fun getInfluencerList(@Header("Authorization") token: String): Response<ApiResponse<List<InfluencerListResponse>>>

    @GET("cartCount")
    suspend fun getCartCount(@Header("Authorization") token: String): Response<ApiResponse<CartCountResponse>>

    @POST("placeOrder")
    suspend fun placeOrderPOS(
        @Header("Authorization") token: String,
        @Body requestBody: OrderRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("changeApixDisount")
    suspend fun changeApixCart(
        @Header("Authorization") token: String,
        @Body requestBody: ChangeApixRequest
    ): Response<ApiResponse<ChangeApixDiscountResponse>>

    @POST("updateShippingAddress")
    suspend fun updateAddress(
        @Header("Authorization") token: String,
        @Body requestBody: UpdateAddressRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("createShippingAddress")
    suspend fun addAddress(
        @Header("Authorization") token: String,
        @Body requestBody: AddAddressRequest
    ): Response<ApiResponse<List<AddressListResponse>>>

    @POST("areaList")
    suspend fun getAreaList(
        @Header("Authorization") token: String,
        @Body requestBody: AreaRequest
    ): Response<ApiResponse<List<AreaResponse>>>

    @POST("governorateList")
    suspend fun getGovernorateList(@Header("Authorization") token: String): Response<ApiResponse<List<GovernorateResponse>>>

    @POST("customerAddressList")
    suspend fun getAddressList(
        @Header("Authorization") token: String,
        @Body requestBody: AddressListRequest
    ): Response<ApiResponse<List<AddressListResponse>>>

    @POST("customerList")
    suspend fun getCustomerList(
        @Header("Authorization") token: String,
        @Body requestBody: CustomerListRequest
    ): Response<ApiResponse<CustomerListResponse>>

    @POST("removeCartItem")
    suspend fun removeCart(
        @Header("Authorization") token: String,
        @Body requestBody: CartRemoveRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("changeQuantity")
    suspend fun changeQuantity(
        @Header("Authorization") token: String,
        @Body requestBody: ChangeCartRequest
    ): Response<ApiResponse<ChangeCartResponse>>

    @GET("cartList")
    suspend fun getCartList(@Header("Authorization") token: String): Response<ApiResponse<CartListResponse>>

    @POST("addToCart")
    suspend fun addCartShop(
        @Header("Authorization") token: String,
        @Body requestBody: AddCartRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("addToRx")
    suspend fun addCartRX(
        @Header("Authorization") token: String,
        @Body requestBody: AddCartRXRequest
    ): Response<ApiResponseNoData<Unit>>

    @POST("productDetails")
    suspend fun getProductDetails(
        @Header("Authorization") token: String,
        @Body requestBody: ProductDetailsRequest
    ): Response<ApiResponse<List<ProductDetailsResponse>>>

    @POST("auth/login")
    suspend fun userLogin(@Body requestBody: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("productsList")
    suspend fun getProductList(
        @Header("Authorization") token: String,
        @Body requestBody: ProductRequest
    ): Response<ApiResponse<ProductListResponse>>

    data class ApiResponse<T>(
        val error: Boolean,
        val status: String,
        val message: String,
        val data: T?
    )

    data class ApiResponseNoData<T>(val error: Boolean, val status: String, val message: String)

    data class CreatePatientRequest(
        val phone: String,
        val civilId: String,
        val name: String,
        val email: String,
        val altMobileNo: String,
        val dob: String,
        val id: String,
        val gender: String,
        val nationality: String
    )

    data class UpdateProfileRequest(
        val phone: String,
        val name: String,
        val email: String,
        val dob: String,
        val password: String,
        val civilId: String,
        val alternateNumber: String,
    )

    data class PresDetailsRequest(
        val id: String
    )

    data class GraphRequest(
        val date: String
    )

    data class ReportRequest(
        val per_page: String,
        val page_no: String,
        val date: String,
        val itemNameSearch: String,
        val isPharmaceutical: String,
        val clearance: String
    )

    data class CatDetailsRequest(
        val id: String
    )

    data class AssignInfluencerStatusRequest(
        val id: String,
        val influencerId: String
    )

    data class AssignDeliveryBoyRequest(
        val id: String,
        val deliveryBoy: String
    )

    data class UpdateLeadRequest(
        val id: String,
        val lead: String
    )

    data class ChangePickupPointRequest(
        val bookletNo: String,
        val prescriptionNo: String
    )

    data class HomeRequest(
        val date : String
    )
    data class ChangePickupStatusRequest(
        val id: String,
        val status: String
    )

    data class UpdateAdminProfileRequest(
        val name: String,
        val email: String,
        val phone: String,
        val password: String,
        val confirm_password: String,

        )

    data class AddUnitRequest(
        val name: String,
        val arabicName: String
    )

    data class UpdateDeliveryStatusRequest(
        val id: String,
        val status: String
    )

    data class UpdateSegmantRequest(
        val user_id: String,
        val segment_id: String
    )

    data class UpdateTypeRequest(
        val user_id: String,
        val type_id: String
    )

    data class OrderListRequest(
        val userId: String,
        val IsPrescription: String,
        val order_type: String,
        val search: String,
        val area: String,
        val zones: String,
        val payment_status: String,
        val payment_change: String,
        val payment_method: String,
        val delivery_status: String,
        val delivery_boy: String,
        val date: String,
        val collected_by_seller: String,
        val collected_by: String,
        val per_page: String,
        val page_no: String,
        val driverCleared: String
    )

    data class EditBrandRequest(
        val name: String,
        val arabicName: String,
        val logo: String,
        val metaTitle: String,
        val metaDescription: String,
        val arabicMetaTitle: String,
        val arabicMetaDescription: String,
        val id: String
    )

    data class UpdateProductRequest(
        val id: String,
        val apix_sku: String,
        val name: String,
        val arabic_name: String,
        val scientific_name: String,
        val short_description: String,
        val arabic_short_description: String,
        val usage_form: String,
        val category_id: String,
        val brand_id: String,
        val origin_id: String,
        val seller_id: String,
        val pick_up_point_id: List<String>,
        val medical_rep_id: String,
        val unit: String,
        val weight: String,
        val purchase_from: List<String>,
        val purchase_price: String,
        val min_qty: String,
        val max_qty: String,
        val tags: String,
        val arabic_tags: String,
        val related_products: List<String>,
        val up_sell_products: List<String>,
        val cross_sell_products: List<String>,
        val barcode: String,
        val refundable: String,
        val cancelable: String,
        val is_pharmaceutical: String,
        val on_request: String,
        val expiry_date: String,
        val product_level: String,
        val unit_price: String,
        val date_range: String,
        val discount: String,
        val discount_type: String,
        val seller_discount: String,
        val apix_discount: String,
        val max_discount: String,
        val sku: String,
        val current_stock: String,
        val external_link: String,
        val external_link_btn: String,
        val photos: String,
        val thumbnail_img: String,
        val description: String,
        val arabic_description: String,
        val video_provider: String,
        val video_link: String,
        val pdf: String,
        val meta_title: String,
        val arabic_meta_title: String,
        val meta_description: String,
        val arabic_meta_description: String,
        val meta_img: String,
        val shipping_type: String, // "free" or "flat_rate"
        val flat_shipping_cost: String,
        val low_stock_quantity: String,
        val stock_visibility_state: String, // "quantity", "text", "hide"
        val cash_on_delivery: String, // "1" or "0"
        val featured: String,
        val todays_deal: String,
        val flash_deal_id: String,
        val flash_discount: String,
        val flash_discount_type: String,
        val button: String
    )

    data class AddProductRequest(
        val apix_sku: String,
        val name: String,
        val arabic_name: String,
        val scientific_name: String,
        val short_description: String,
        val arabic_short_description: String,
        val usage_form: String,
        val category_id: String,
        val brand_id: String,
        val origin_id: String,
        val seller_id: String,
        val pick_up_point_id: List<String>,
        val medical_rep_id: String,
        val unit: String,
        val weight: String,
        val purchase_from: List<String>,
        val purchase_price: String,
        val min_qty: String,
        val max_qty: String,
        val tags: String,
        val arabic_tags: String,
        val related_products: List<String>,
        val up_sell_products: List<String>,
        val cross_sell_products: List<String>,
        val barcode: String,
        val refundable: String,
        val cancelable: String,
        val is_pharmaceutical: String,
        val on_request: String,
        val expiry_date: String,
        val product_level: String,
        val unit_price: String,
        val date_range: String,
        val discount: String,
        val discount_type: String,
        val seller_discount: String,
        val apix_discount: String,
        val max_discount: String,
        val sku: String,
        val current_stock: String,
        val external_link: String,
        val external_link_btn: String,
        val photos: String,
        val thumbnail_img: String,
        val description: String,
        val arabic_description: String,
        val video_provider: String,
        val video_link: String,
        val pdf: String,
        val meta_title: String,
        val arabic_meta_title: String,
        val meta_description: String,
        val arabic_meta_description: String,
        val meta_img: String,
        val shipping_type: String, // "free" or "flat_rate"
        val flat_shipping_cost: String,
        val low_stock_quantity: String,
        val stock_visibility_state: String, // "quantity", "text", "hide"
        val cash_on_delivery: String, // "1" or "0"
        val featured: String,
        val todays_deal: String,
        val flash_deal_id: String,
        val flash_discount: String,
        val flash_discount_type: String,
        val button: String // "publish" or "unpublish"
    )

    data class EditCatRequest(
        val name: String,
        val arabicName: String,
        val orderLevel: String,
        val digital: String,
        val banner: String,
        val icon: String,
        val metaTitle: String,
        val metaDescription: String,
        val arabicMetaTitle: String,
        val arabicMetaDescription: String,
        val parentId: String,
        val commisionRate: String,
        val id: String
    )

    data class AddCatRequest(
        val name: String,
        val arabicName: String,
        val orderLevel: String,
        val digital: String,
        val banner: String,
        val icon: String,
        val metaTitle: String,
        val metaDescription: String,
        val arabicMetaTitle: String,
        val arabicMetaDescription: String,
        val parentId: String,
        val commisionRate: String
    )

    data class MediaRequest(
        val per_page: String,
        val page_no: String,
        val type: String,
        val search: String,
        val sortBy: String
    )

    data class PickUpPointsRequest(
        val sellerId: String
    )

    data class CatRequest(
        val parent_id: String,
        val per_page: String,
        val page_no: String
    )

    data class CommissionRequest(
        val id: String,
        val percentage: String
    )

    data class GenerateSKURequest(
        val count: String
    )

    data class InfluencerRequest(
        val search: String,
        val pageNo: String,
        val perPage: String,
        val influencerId: String
    )

    data class SKURequest(
        val search: String,
        val pageNo: String,
        val perPage: String
    )

    data class SCPLRequest(
        val search: String,
        val pageNo: String,
        val perPage: String,
        val sellerId: String
    )

    data class UpdatePSRequest(
        val productId: String,
        val statusType: String,
        val status: String
    )

    data class ConditionRequest(
        val sellerId: String,
        val conditions: String,
        val nilCondition: String,
        val percentage: String
    )

    data class CalcApixRequest(
        val purchasePrice: String,
        val sellingPrice: String
    )

    data class FavRequest(
        val id: String,
        val status: String
    )

    data class PresRequest(
        val per_page: String,
        val page_no: String,
        val keyword: String,
        val userId: String
    )

    data class SubmitRXRequest(
        val influencerId: String,
        val customerId: String
    )

    data class UpdateRXCartRequest(
        val id: String,
        val quantity: String,
        val dose_day: String,
        val dose: String,
        val dose_time: String,
        val description: String,
        val course_day: String,
        val course_duration: String
    )

    data class CheckPatientRequest(
        val keyword: String
    )

    data class ProductRequest(
        val per_page: String,
        val page_no: String,
        val category: String,
        val brand: String,
        val sku: String,
        val seller: String,
        val medical_rep_id: String,
        val keyword: String,
        val influencer_id: String,
        val isFavourite: String,
        val listFrom: String
    )

    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class ProductDetailsRequest(
        val productId: String
    )

    data class AddCartRequest(
        val productId: String,
        val quantity: String
    )

    data class AddCartRXRequest(
        val productId: String,
        val quantity: String,
        val patientId: String,
        val dose_day: String,
        val dose: String,
        val dose_time: String,
        val description: String,
        val course_day: String,
        val course_duration: String
    )

    data class ChangeCartRequest(
        val id: String,
        val quantity: String
    )

    data class CartRemoveRequest(
        val id: String
    )

    data class AddSegmentRequest(
        val name: String,
        val arabicName: String
    )

    data class UpdateSegmentDetailsRequest(
        val name: String,
        val arabicName: String,
        val id: String
    )

    data class CustomerUpdateRequest(
        val id: String,
        val name: String,
        val email: String,
        val phone: String
    )

    data class CustomerListRequest(
        val keyword: String,
        val per_page: String,
        val page_no: String
    )

    data class AddressListRequest(
        val customerId: String
    )

    data class AreaRequest(
        val governorateId: String
    )

    data class AddAddressRequest(
        val governorateId: String,
        val areaId: String,
        val phone: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val setDefault: String,
        val addressTitle: String,
        val street: String,
        val appartment: String,
        val building: String,
        val floor: String,
        val alternatePhone: String,
        val block: String,
        val customerId: String,
        val mapLink: String
    )

    data class UpdateAddressRequest(
        val governorateId: String,
        val areaId: String,
        val phone: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val setDefault: String,
        val addressTitle: String,
        val street: String,
        val appartment: String,
        val building: String,
        val floor: String,
        val alternatePhone: String,
        val block: String,
        val customerId: String,
        val id: String,
        val mapLink: String
    )

    data class ChangeApixRequest(
        val id: String,
        val apix_discount: String
    )

    data class BrandRequest(
        val per_page: String,
        val page_no: String
    )

    data class CountRequest(
        val rxCount: String
    )

    data class OrderRequest(
        val addressId: String,
        val payment_type: String,
        val customerId: String,
        val influencerId: String,
        val medicalRepId: String,
        val deliveryByApix: String,
        val collectedByApix: String,
        val pickupByApix: String,
        val leadName: String,
        val device: String,
        val additionalSellerNote: String,
        val internalNotes: String,
        val deliverBoyId: String,
        val bookletNo: String,
        val PrescriptionId: String
    )
}