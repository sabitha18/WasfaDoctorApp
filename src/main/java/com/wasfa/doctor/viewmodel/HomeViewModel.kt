package com.wasfa.doctor.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.NetworkClient
import com.wasfa.doctor.network.response.AccountResponse
import com.wasfa.doctor.network.response.AddressListResponse
import com.wasfa.doctor.network.response.AllPermissionsResponse
import com.wasfa.doctor.network.response.ApixSkuResponse
import com.wasfa.doctor.network.response.AreaResponse
import com.wasfa.doctor.network.response.BrandListResponse
import com.wasfa.doctor.network.response.CartCountResponse
import com.wasfa.doctor.network.response.CartListResponse
import com.wasfa.doctor.network.response.CartResponse
import com.wasfa.doctor.network.response.CatListResponse
import com.wasfa.doctor.network.response.CategoryDetailsResponse
import com.wasfa.doctor.network.response.ChangeApixDiscountResponse
import com.wasfa.doctor.network.response.ChangeCartResponse
import com.wasfa.doctor.network.response.CountryListResponse
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
import com.wasfa.doctor.network.response.ProfileResponse
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class HomeViewModel(private val context: Context) : ViewModel() {

    private val _showAlertEvent = MutableLiveData<String>()
    val showAlertEvent: LiveData<String> get() = _showAlertEvent

    private val _productEvent = MutableLiveData<String>()
    val productEvent: LiveData<String> get() = _productEvent

    private val _productEventCust = MutableLiveData<String>()
    val productEventCust: LiveData<String> get() = _productEventCust

    private val _presEvent = MutableLiveData<String>()
    val presEvent: LiveData<String> get() = _presEvent

    private val _reportEvent = MutableLiveData<String>()
    val reportEvent: LiveData<String> get() = _reportEvent

    private val _loginDetails = MutableLiveData<LoginResponse?>()
    val loginDetails: LiveData<LoginResponse?> = _loginDetails

    private val _productDetailsData = MutableLiveData<List<ProductDetailsResponse>>()
    val productDetailsData: LiveData<List<ProductDetailsResponse>?> = _productDetailsData

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    var currentPage = 1
    var totalPageCount = 1
    var loading = false
    fun isLastPage() = currentPage > totalPageCount


    var currentPageSellerProduct = 1
    var totalPageCountSellerProduct = 1
    private var loadingSellerProduct = false
    fun isLastPageSellerProduct() = currentPageSellerProduct > totalPageCountSellerProduct

    var currentPageInfluencer= 1
    var totalPageCountInfluencer = 1
    private var loadingInfluencer = false
    fun isLastPageInfluencer() = currentPageInfluencer > totalPageCountInfluencer

    // customer list
    var currentPageCustomer = 1
    var totalPageCountCustomer = 1
    var loadingCustomer = false
    fun isLastPageCustomer() = currentPageCustomer > totalPageCountCustomer

    // customer list
    var currentPageType = 1
    var totalPageCountType = 1
    var loadingType = false
    fun isLastPageType() = currentPageType > totalPageCountType

    // segment list
    var currentPageSegment = 1
    var totalPageCountSegment = 1
    var loadingSegment = false
    fun isLastPageSegment() = currentPageSegment > totalPageCountSegment

    // sku
    var currentPageSKU = 1
    var totalPageCountSKU = 1
    private var loadingSKU = false
    fun isLastPageSKU() = currentPageSKU > totalPageCountSKU

    // media
    var currentPageMedia = 1
    var totalPageCountMedia = 1
    private var loadingMedia = false
    fun isLastPageMedia() = currentPageMedia > totalPageCountMedia

    // order
    var currentPageOrder = 1
    var totalPageCountOrder = 1
    private var loadingOrder = false
    fun isLastPageOrder() = currentPageOrder > totalPageCountOrder


    // category
    var currentPageCat = 1
    var totalPageCountCat = 1
    private var loadingCat = false
    fun isLastPageCat() = currentPageCat > totalPageCountCat

    // brand
    var currentPageBrand = 1
    var totalPageCountBrand = 1
    private var loadingBrand = false
    fun isLastPageBrand() = currentPageBrand > totalPageCountBrand


    //pres
    var currentPagePres = 1
    var totalPageCountPres = 1
    private var loadingPres = false
    fun isLastPagePres() = currentPagePres > totalPageCountPres

    //pres
    var currentPageReport = 1
    var totalPageCountReport = 1
    private var loadingReport = false
    fun isLastPageReport() = currentPageReport > totalPageCountReport

    //medical rep list

    private val _medicalRepList = MutableLiveData<List<MedicalRepListResponse>>()
    val medicalRepList: LiveData<List<MedicalRepListResponse>> get() = _medicalRepList

    fun getMedicalRepList(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getMedicalRepList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _medicalRepList.value = response.body()?.data!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    //seller list

    private val _sellerList = MutableLiveData<List<SellerListResponse>>()
    val sellerList: LiveData<List<SellerListResponse>> get() = _sellerList

    fun getSellerList(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getSellerList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _sellerList.value = response.body()?.data!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    //brand list

    private val _brandList = MutableLiveData<BrandListResponse>()
    val brandList: LiveData<BrandListResponse> get() = _brandList
    init {
        _brandList.value = BrandListResponse(
            totalCategoryCount = "0",
            totalPages = "",
            brands = emptyList()
        )
    }

    fun clearBrandData() {
        _brandList.value =  BrandListResponse(
            totalCategoryCount = "0",
            totalPages = "",
            brands = emptyList()
        )
    }


    fun loadNextPageBrand() {
        if (loadingBrand) return
        loadingBrand= true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.BrandRequest(
                    per_page = "4",
                    page_no = currentPageBrand.toString()
                )

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getBrandList("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _brandList.value = it
                        _productEvent.value = response.body()?.message
                    }
                } else {
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingBrand= false
            }
        }
    }

    fun getBrandList(token: String,request: ApiService.BrandRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getBrandList("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountBrand= data?.totalPages!!.toInt()
                        _brandList.value = data!!
                        _productEvent.value = response.body()?.message
                    } else {
                        // Handle the case where the error field is true
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }


    //influencer list

    private val _influencerList = MutableLiveData<List<InfluencerListResponse>>()
    val influencerList: LiveData<List<InfluencerListResponse>> get() = _influencerList

    fun getInfluencerList(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getInfluencerList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _influencerList.value = response.body()?.data!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }


    //cart count

    private val _cartCount = MutableLiveData<CartCountResponse>()
    val cartCount: LiveData<CartCountResponse> get() = _cartCount

    fun getCartCount(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getCartCount("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _cartCount.value = response.body()?.data!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }


    //place order pos

    private val _orderPlacedStatus = MutableLiveData<String>()
    val orderPlacedStatus: LiveData<String> get() = _orderPlacedStatus

    private val _orderPlacedError = MutableLiveData<String>()
    val orderPlacedError: LiveData<String> get() = _orderPlacedError

    fun placeOrder(token: String, request: ApiService.OrderRequest) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.placeOrderPOS("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.error == false) {
                        _orderPlacedStatus.value = body.message ?: "Order placed successfully"
                    } else {
                        // Handle API-level error
                        _orderPlacedError.value = body?.message ?: "Something went wrong"
                        Log.e("*Home Cat*", "Error in response: ${body?.message}")
                    }
                } else {
                    // Handle HTTP error (e.g., 500, 404)
                    _orderPlacedError.value = "Server error: ${response.message()}"
                    Log.e("*Home Cat*", "HTTP Error: ${response.message()}")
                }

            } catch (e: Exception) {
                // Handle network or conversion errors
                _orderPlacedError.value = "Network error: ${e.localizedMessage}"
                Log.e("*Home Cat*", "Exception: ${e.message}")
            }
        }
    }



    // change apix cart
    private val _apixChangeData = MutableLiveData<ChangeApixDiscountResponse>()
    val apixChangeData: LiveData<ChangeApixDiscountResponse> get() = _apixChangeData

    fun changeApixCart(token: String, request: ApiService.ChangeApixRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.changeApixCart("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _apixChangeData.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response?.body()?.message
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // add address
    private val _addAddressStatus = MutableLiveData<String>()
    val addAddressStatus: LiveData<String> get() = _addAddressStatus
    private val _addAddressData = MutableLiveData<List<AddressListResponse>>()
    val addAddressData: LiveData<List<AddressListResponse>> get() = _addAddressData

    fun addAddress(token: String, request: ApiService.AddAddressRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.addAddress("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _addAddressStatus.value = response.body()?.message!!
                        _addAddressData.value = response.body()?.data!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun updateAddress(token: String, request: ApiService.UpdateAddressRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateAddress("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _addAddressStatus.value = response.body()?.message!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // area list
    private val _areaListData = MutableLiveData<List<AreaResponse>>()
    val areaListData: LiveData<List<AreaResponse>> get() = _areaListData

    fun getAreaList(token: String, request: ApiService.AreaRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getAreaList("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _areaListData.value = response.body()?.data!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // governorate list
    private val _governorateListData = MutableLiveData<List<GovernorateResponse>>()
    val governorateListData: LiveData<List<GovernorateResponse>> get() = _governorateListData

    fun getGovernorateList(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getGovernorateList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _governorateListData.value = response.body()?.data!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // address list
    private val _addressListData = MutableLiveData<List<AddressListResponse>>()
    val addressListData: LiveData<List<AddressListResponse>> get() = _addressListData

    fun getAddressList(token: String, request: ApiService.AddressListRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getAddressList("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _addressListData.value = response.body()?.data!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }
    //  submit rx

    private val _submitStatus = MutableLiveData<SubmitResponse>()
    val submitStatus: LiveData<SubmitResponse> get() = _submitStatus

    private val _submitSaveStatus = MutableLiveData<String>()
    val submitSaveStatus: LiveData<String> get() = _submitSaveStatus

    private val _submitStatusFail = MutableLiveData<String>()
    val submitStatusFail: LiveData<String> get() = _submitStatusFail

    fun submitRX(token: String, request: ApiService.SubmitRXRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.submitRx("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _submitStatus.value = response.body()?.data!!

                    } else {
                        _submitStatusFail.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }
    fun submitSaveRX(token: String, request: ApiService.SubmitRXRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.submitRx("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _submitSaveStatus.value = response.body()?.message

                    } else {
                        _submitStatusFail.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    //  cart rx update

    private val _cartUpdateStatus = MutableLiveData<String>()
    val cartUpdateStatus: LiveData<String> get() = _cartUpdateStatus

    fun updateCartRx(token: String, request: ApiService.UpdateRXCartRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateRXCart("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _cartUpdateStatus.value = response.body()?.message

                    } else {
                        _cartUpdateStatus.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }
    //  cart list rx

    private val _cartList = MutableLiveData<CartResponse>()
    val cartList: LiveData<CartResponse> get() = _cartList

    fun getCart(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getCartRX("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        println("-----------------   ****  ")
                        _cartList.value = response.body()?.data!!

                    } else {
                        _emptyCartStatus.value = response.body()?.message!!
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // empty cart

    private val _emptyCartStatus = MutableLiveData<String>()
    val emptyCartStatus: LiveData<String> get() = _emptyCartStatus

    fun emptyCart(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.emptyCart("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _emptyCartStatus.value = response.body()?.message!!

                    } else {
                        _emptyCartStatus.value = response.body()?.message!!
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // create patient

    private val _createdStatus = MutableLiveData<String>()
    val createdStatus: LiveData<String> get() = _createdStatus

    private val _patientIdStatus = MutableLiveData<String>()
    val patientIdStatus: LiveData<String> get() = _patientIdStatus

    fun createPatient(token: String, request: ApiService.CreatePatientRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.createPatient("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _createdStatus.value = response.body()?.message!!
                        _patientIdStatus.value = response.body()?.data?.id

                    } else {
                        _createdStatus.value = response.body()?.message!!
                        _patientIdStatus.value = ""
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }


    // customer list
    private val _customerListData = MutableLiveData<CustomerListResponse>()
    val customerListData: LiveData<CustomerListResponse> get() = _customerListData



    init {
        _customerListData.value = CustomerListResponse(
            totalUserCount = "0",
            totalPages = "",
            users = emptyList()
        )
    }

    fun clearCustomerData() {
        _customerListData.value = CustomerListResponse(
            totalUserCount = "0",
            totalPages = "",
            users = emptyList()
        )
    }

    fun getCustomerList(token: String,request: ApiService.CustomerListRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getCustomerList("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountCustomer= data?.totalPages!!.toInt()
                        _customerListData.value = data!!
                        _productEventCust.value = response.body()?.message
                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }
    fun loadNextPageCustomer(searchValue: String) {
        if (loadingCustomer) return
        loadingCustomer= true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.CustomerListRequest(
                    keyword = searchValue,
                    per_page = "10",
                    page_no = currentPageCustomer.toString()
                )
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getCustomerList("Bearer $token",request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _customerListData.value = it
                        _productEventCust.value = response.body()?.message
                    }
                } else {
                    currentPageCustomer--
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingCustomer = false
            }
        }
    }

    fun checkCustomerExist(token: String, request: ApiService.CheckPatientRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.checkPatientExist("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _customerListData.value = response.body()?.data!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // cart list

    private val _cartListData = MutableLiveData<CartListResponse?>()
    val cartListData: LiveData<CartListResponse?> = _cartListData

    fun getCartList(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getCartList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        _cartListData.value = data!!

                    } else {
                        _showAlertEvent.value = "no data"
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    _showAlertEvent.value = "no data"
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                _showAlertEvent.value = "no data"
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // delete cart shop
    private val _deleteCartShopStatus = MutableLiveData<String>()
    val deleteCartShopStatus: LiveData<String> get() = _deleteCartShopStatus

    fun deleteCartShop(token: String, request: ApiService.CartRemoveRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.removeCart("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _deleteCartShopStatus.value = response.body()?.message

                    } else {
                        _showAlertEvent.value = response.body()?.message
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // change quantity cart shop
    private val _changeQuantityCartShopStatus = MutableLiveData<ChangeCartResponse>()
    val changeQuantityCartShopStatus: LiveData<ChangeCartResponse> get() = _changeQuantityCartShopStatus

    fun changeQuantityCartShop(token: String, request: ApiService.ChangeCartRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.changeQuantity("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _changeQuantityCartShopStatus.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // add cart shop
    private val _addCartShopStatus = MutableLiveData<String>()
    val addCartShopStatus: LiveData<String> get() = _addCartShopStatus

    fun addCartRX(token: String, request: ApiService.AddCartRXRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.addCartRX("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _addCartShopStatus.value = response.body()!!.message

                    } else {
                        _addCartShopStatus.value = response.body()?.message
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun addCartShop(token: String, request: ApiService.AddCartRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.addCartShop("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _addCartShopStatus.value = response.body()!!.message

                    } else {
                        _addCartShopStatus.value = response.body()?.message
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // product details
    private val _productListData = MutableLiveData<ProductListResponse?>()
    val productListData: LiveData<ProductListResponse?> = _productListData

    fun getProductDetails(token: String, request: ApiService.ProductDetailsRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getProductDetails("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        _productDetailsData.value = data!!

                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun userLogin(request: ApiService.LoginRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.userLogin(request)
                }

                if (response.isSuccessful) {
                    Log.e("*Home Cat*","     success     "+response.body())
                    if (response?.body()?.status == "200") {
                        _loginDetails.value = response?.body()?.data
                        Log.e("*Home Cat*","     200     ")
                    } else {
                        Log.e("*Home Cat*   88      ", response.body()?.status.toString())
                        _showAlertEvent.value = response?.body()?.message
                    }


                } else {
                    val errorMessage = response.errorBody()?.string()
                    val errorResponse = JSONObject(errorMessage)
                    val message = errorResponse.optString("message", "Unknown error occurred")
                    Log.e("*Home Cat*", response.body()?.message.toString())
                    _showAlertEvent.value = message

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {
                Log.e("*Home Cat*","     success     ")
                _loadingState.value = false
            }
        }
    }

    init {
        // Initialize with an empty state
        _productListData.value = ProductListResponse(
            totalProductsCount = "0",
            totalPages = "",
            products = emptyList()
        )
    }

    fun clearProductListData() {
        _productListData.value = ProductListResponse(
            totalProductsCount = "0",
            totalPages = "",
            products = emptyList()
        )
    }

    fun getProductList(token: String, request: ApiService.ProductRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getProductList("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCount = data?.totalPages!!.toInt()
                        _productListData.value = data!!
                        _productEvent.value = response.body()?.message
                    } else {
                        // Handle the case where the error field is true
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    _showAlertEvent.value = response.message()
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun loadNextPage(
        medicalRep: String,
        seller: String,
        searchValue: String,
        influencerId: String,
        sku: String,
        brand: String,
        isFav: String,
        isPOS: String
    ) {
        if (loading) return
        loading = true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.ProductRequest(
                    per_page = "4",
                    page_no = currentPage.toString(),
                    category = "",
                    brand = brand,
                    sku = sku,
                    seller = seller,
                    medical_rep_id = medicalRep,
                    keyword = searchValue,
                    influencer_id = influencerId,
                    isFavourite = isFav,
                    listFrom = isPOS
                )

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getProductList("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _productListData.value = it
                        _productEvent.value = response.body()?.message
                    }
                } else {
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loading = false
            }
        }
    }

    // pres list
    private val _presListData = MutableLiveData<PresListResponse?>()
    val presListData: LiveData<PresListResponse?> = _presListData

    init {
        // Initialize with an empty state
        _presListData.value = PresListResponse(
            totalProductsCount = "0",
            totalPages = "",
            prescriptions = emptyList()
        )
    }

    fun clearPresListData() {
        _presListData.value = PresListResponse(
            totalProductsCount = "0",
            totalPages = "",
            prescriptions = emptyList()
        )
    }

    fun getPresList(token: String, request: ApiService.PresRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getPresList("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountPres = data?.totalPages!!.toInt()
                        _presListData.value = data!!
                        _presEvent.value = response.body()?.data?.prescriptions?.size.toString()
                    } else {
                        // Handle the case where the error field is true
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    _showAlertEvent.value = response.message()
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun loadNextPagePres(
        searchValue: String,
        user_id: String
    ) {
        if (loadingPres) return
        loadingPres = true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.PresRequest(
                    per_page = "5",
                    page_no = currentPagePres.toString(),
                    keyword = searchValue,
                    userId = user_id
                )

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getPresList("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _presListData.value = it
                        _presEvent.value = response.body()?.message
                    }
                } else {
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingPres = false
            }
        }
    }


    //  profile doctor

    private val _profileData = MutableLiveData<List<ProfileResponse>>()
    val profileData: LiveData<List<ProfileResponse>> get() = _profileData

    fun getProfile(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getProfile("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        println("-----------------   ****  ")
                        _profileData.value = response.body()?.data?.data

                    } else {
                        _emptyCartStatus.value = response.body()?.message!!
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }


    //  pres details

    private val _presData = MutableLiveData<List<PresDetailsResponse>>()
    val presData: LiveData<List<PresDetailsResponse>> get() = _presData

    fun getPresDetails(token: String, request: ApiService.PresDetailsRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getPresDetails("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        println("-----------------   ****  ")
                        _presData.value = response.body()?.data!!

                    } else {
                        _emptyCartStatus.value = response.body()?.message!!
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    //  home doctor

    private val _homeData = MutableLiveData<HomeDataResponse>()
    val homeData: LiveData<HomeDataResponse> get() = _homeData

    fun getHomeData(token: String,request: ApiService.HomeRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getDocHome("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        println("*Home ----Cat*------    */*/*/*")
                        _homeData.value = response.body()?.data!!

                    }

                } else {
                    println("*Home ----Cat*------   +-+-+-")
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                _emptyCartStatus.value = "login again"

                println("*Home ----Cat*------                000        " + e::class.java.name)
            } finally {

            }
        }
    }


    // update profile
    private val _profileStatus = MutableLiveData<String>()
    val profileStatus: LiveData<String> get() = _profileStatus
    fun updateProfile(
        token: String,
        request: ApiService.UpdateProfileRequest,
        profileImageFile: File?
    ) {
        viewModelScope.launch {
            try {

                val nameRequestBody = request.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val userPhoneRequestBody =
                    request.phone.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailRequestBody = request.email.toRequestBody("text/plain".toMediaTypeOrNull())
                val dobRequestBody = request.dob.toRequestBody("text/plain".toMediaTypeOrNull())
                val civilRequestBody =
                    request.civilId.toRequestBody("text/plain".toMediaTypeOrNull())
                val altPhoneRequestBody =
                    request.alternateNumber.toRequestBody("text/plain".toMediaTypeOrNull())

                // Prepare the image file part, if available
                val imagePart = prepareFilePart("profilePic", profileImageFile)

                // Make the network request in IO dispatcher
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateProfile(
                        "Bearer $token",
                        userPhoneRequestBody,
                        nameRequestBody,
                        emailRequestBody,
                        dobRequestBody,
                        civilRequestBody,
                        altPhoneRequestBody,
                        imagePart
                    )
                }

                if (response.isSuccessful) {
                    val responseData = response.body()
                    _profileStatus.value = responseData?.message
                } else {
                    Log.e("ProfileUpdate", response.errorBody().toString())
                }

            } catch (e: Exception) {
                Log.e("ProfileUpdate", "Error: ${e.localizedMessage}", e)
            } finally {
                _loadingState.value = false
            }
        }
    }

    private fun prepareFilePart(partName: String, file: File?): MultipartBody.Part? {
        return file?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, it.name, requestFile)
        }
    }

    // graph data

    private val _graphData = MutableLiveData<GraphResponse>()
    val graphData: LiveData<GraphResponse> get() = _graphData

    fun getGraph(token: String, request: ApiService.GraphRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getGraphData("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _graphData.value = response.body()?.data!!

                    } else {
                        println("-----------------   ****  " + response.body())
                        _emptyCartStatus.value = response.body()?.message!!
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // report data

    private val _reportData = MutableLiveData<ReportResponse?>()
    val reportData: MutableLiveData<ReportResponse?> get() = _reportData

    init {
        // Initialize with an empty state
        _reportData.value = ReportResponse(
            totalPrescriptions = "0",
            totalPages = "",
            prescriptions = emptyList()
        )
    }

    fun clearReportListData() {
        _reportData.value = ReportResponse(
            totalPrescriptions = "0",
            totalPages = "",
            prescriptions = emptyList()
        )
    }

    fun getReport(token: String, request: ApiService.ReportRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getReportList("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountReport = data?.totalPages!!.toInt()
                        _reportData.value = data
                        println("0-0-0-0-0-      00     "+data?.totalPages)
                        _reportEvent.value = response.body()?.data?.prescriptions?.size.toString()
                    } else {
                        // Handle the case where the error field is true
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    _showAlertEvent.value = response.message()
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun loadNextPageReport(
        date: String,
        itemNameSearch: String,
        isPharmaceutical: String,
        clearance: String

    ) {
        if (loadingReport) return
        loadingReport = true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.ReportRequest(
                    per_page = "5",
                    page_no = currentPageReport.toString(),
                    date = date,
                    itemNameSearch = itemNameSearch,
                    isPharmaceutical = isPharmaceutical,
                    clearance = clearance

                    )

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getReportList("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _reportData.value = it
                        _reportEvent.value = response.body()?.message
                    }
                } else {
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingReport = false
            }
        }
    }


    // update fav
    private val _favStatus = MutableLiveData<String>()
    val favStatus: LiveData<String> get() = _favStatus

    fun updateFav(token: String, request: ApiService.FavRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateFav("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _favStatus.value = response.body()?.message

                    } else {
                        _favStatus.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // add apix margin
    private val _calculatedValue = MutableLiveData<String>()
    val calculatedValue: LiveData<String> get() = _calculatedValue

    fun calculateApix(token: String, request: ApiService.CalcApixRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.calculateApixMargin("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _calculatedValue.value = response.body()?.data?.margin

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // calculate condition
    private val _commissionStatus = MutableLiveData<String>()
    val commissionStatus: LiveData<String> get() = _commissionStatus

    fun submitCommission(token: String, request: ApiService.ConditionRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.conditionSubmit("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _commissionStatus.value = response.body()?.message

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }
    // update influencer commission
    private val _changeInfluencerCommissionStatus = MutableLiveData<String>()
    val changeInfluencerCommissionStatus: LiveData<String> get() = _changeInfluencerCommissionStatus

    fun changeInfluencerCommission(token: String, request: ApiService.CommissionRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.changeInfluencerCommission("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _changeCommissionStatus.value = response.body()?.message

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }
    // update commission
    private val _changeCommissionStatus = MutableLiveData<String>()
    val changeCommissionStatus: LiveData<String> get() = _changeCommissionStatus

    fun changeCommission(token: String, request: ApiService.CommissionRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.changeSellerCommission("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _changeCommissionStatus.value = response.body()?.message

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // update product status
    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    fun updateProductStatus(token: String, request: ApiService.UpdatePSRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateProductStatus("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _status.value = response.body()?.message

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }


    // seller commission product
    private val _SCPLData = MutableLiveData<SCPLResponse?>()
    val SCPLData: LiveData<SCPLResponse?> get() = _SCPLData

    init {
        // Initialize with an empty state
        _SCPLData.value = SCPLResponse(
            totalProductsCount = "0",
            totalPages = "",
            products = emptyList()
        )
    }

    fun clearSellerCommissionProductListData() {
        _SCPLData.value = SCPLResponse(
            totalProductsCount = "0",
            totalPages = "",
            products = emptyList()
        )
    }

    fun getSellerCommissionProductList(token: String, request: ApiService.SCPLRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.sellerCommissionProductList("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountSellerProduct = data?.totalPages!!.toInt()
                        _SCPLData.value = data!!
                        _productEvent.value = response.body()?.message
                    } else {
                        // Handle the case where the error field is true
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    _showAlertEvent.value = response.message()
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun loadNextPageSellerCommissionProduct(
        searchValue: String,
        sellerId: String
    ) {
        if (loadingSellerProduct) return
        loadingSellerProduct = true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.SCPLRequest(
                    perPage = "4",
                    pageNo = currentPage.toString(),
                    search = searchValue,
                    sellerId = sellerId,
                )

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.sellerCommissionProductList("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _SCPLData.value = it
                        _productEvent.value = response.body()?.message
                    }
                } else {
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingSellerProduct = false
            }
        }
    }


// apix sku


    private val _SKUData = MutableLiveData<ApixSkuResponse?>()
    val SKUData: LiveData<ApixSkuResponse?> get() = _SKUData

    init {
        _SKUData.value = ApixSkuResponse(
            totalCount = "0",
            totalPages = "",
            apixSku = emptyList()
        )
    }

    fun clearSKUData() {
        _SKUData.value = ApixSkuResponse(
            totalCount = "0",
            totalPages = "",
            apixSku = emptyList()
        )
    }

    fun getSKUList(token: String, request: ApiService.SKURequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.apixSkuList("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountSKU= data?.totalPages!!.toInt()
                        _SKUData.value = data!!
                        _productEvent.value = response.body()?.message
                    } else {
                        // Handle the case where the error field is true
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    _showAlertEvent.value = response.message()
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun loadNextPageSKU(
        searchValue: String
    ) {
        if (loadingSKU) return
        loadingSKU= true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.SKURequest(
                    perPage = "4",
                    pageNo = currentPageSKU.toString(),
                    search = searchValue
                )

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.apixSkuList("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _SKUData.value = it
                        _productEvent.value = response.body()?.message
                    }
                } else {
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingSKU = false
            }
        }
    }


    // generate apix sku
    private val _GenerateValue = MutableLiveData<List<String>>()
    val GenerateValue: LiveData<List<String>> get() = _GenerateValue

    private val _GenerateValueStatus = MutableLiveData<String>()
    val GenerateValueStatus : LiveData<String> get() = _GenerateValueStatus

    fun generateApixSku(token: String, request: ApiService.GenerateSKURequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.generateApixSku("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _GenerateValue.value = response.body()?.data?.apixSkus

                    } else {
                        _GenerateValueStatus.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }


    // influencer commission product
    private val _InfluencerData = MutableLiveData<SCPLResponse?>()
    val InfluencerData: LiveData<SCPLResponse?> get() = _InfluencerData

    init {
        // Initialize with an empty state
        _InfluencerData.value = SCPLResponse(
            totalProductsCount = "0",
            totalPages = "",
            products = emptyList()
        )
    }

    fun clearInfluencerProductListData() {
        _SCPLData.value = SCPLResponse(
            totalProductsCount = "0",
            totalPages = "",
            products = emptyList()
        )
    }

    fun getInfluencerProductList(token: String, request: ApiService.InfluencerRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.InfluencerProductList("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountInfluencer = data?.totalPages!!.toInt()
                        _InfluencerData.value = data!!
                        _productEvent.value = response.body()?.message
                    } else {
                        // Handle the case where the error field is true
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    _showAlertEvent.value = response.message()
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun loadNextPageInfluencerProduct(
        searchValue: String,
        influencerId: String
    ) {
        if (loadingInfluencer) return
        loadingInfluencer = true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.InfluencerRequest(
                    perPage = "4",
                    pageNo = currentPage.toString(),
                    search = searchValue,
                    influencerId = influencerId
                )

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.InfluencerProductList("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _InfluencerData.value = it
                        _productEvent.value = response.body()?.message
                    }
                } else {
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingInfluencer= false
            }
        }
    }

    // category list


    private val _categoryList= MutableLiveData<CatListResponse>()
    val categoryList: LiveData<CatListResponse> get() = _categoryList


    init {
        _categoryList.value = CatListResponse(
            totalCategoryCount = "0",
            totalPages = "",
            categories = emptyList()
        )
    }

    fun clearCatData() {
        _categoryList.value = CatListResponse(
            totalCategoryCount = "0",
            totalPages = "",
            categories = emptyList()
        )
    }


    fun loadNextPageCat(parentId: String) {
        if (loadingCat) return
        loadingCat= true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.CatRequest(
                    per_page = "4",
                    page_no = currentPageCat.toString(),
                    parent_id = parentId
                )

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getCategory("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _categoryList.value = it
                        _productEvent.value = response.body()?.message
                    }
                } else {
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingCat= false
            }
        }
    }

    fun getCategoryList(token: String,request: ApiService.CatRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getCategory("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountCat= data?.totalPages!!.toInt()
                        _categoryList.value = data!!
                        _productEvent.value = response.body()?.message
                    } else {
                        // Handle the case where the error field is true
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }


    // pick up points  list
    private val _pickUpList= MutableLiveData<PickUpPointsResponse>()
    val pickUpList: LiveData<PickUpPointsResponse> get() = _pickUpList

    fun getPickUpListList(token: String,request: ApiService.PickUpPointsRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getPickupPointsList("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _pickUpList.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }


    // unit list
    private val _unitList = MutableLiveData<UnitResponse>()
    val unitList: LiveData<UnitResponse> get() = _unitList

    fun getUnitList(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getUnitList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _unitList.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // unit list
    private val _purchaseList = MutableLiveData<PurchaseFormResponse>()
    val purchaseList: LiveData<PurchaseFormResponse> get() = _purchaseList

    fun getPurchaseFormList(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getPurchaseFromList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _purchaseList.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // media list
    private val _mediaList = MutableLiveData<MediaResponse>()
    val mediaList : LiveData<MediaResponse> get() = _mediaList

    init {
        _mediaList.value = MediaResponse(
            totalFilesCount = "0",
            totalPages = "",
            uploads = emptyList()
        )
    }

    fun clearMediaData() {
        _mediaList.value = MediaResponse(
            totalFilesCount = "0",
            totalPages = "",
            uploads = emptyList()
        )
    }

    fun getMediaList(token: String, request: ApiService.MediaRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getMediaList("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountMedia= data?.totalPages!!.toInt()
                        _mediaList.value = data!!
                        _productEvent.value = response.body()?.message
                    } else {
                        // Handle the case where the error field is true
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    _showAlertEvent.value = response.message()
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun loadNextPageMedia(
        searchValue: String,
        type: String,
        sort: String
    ) {
        if (loadingMedia) return
        loadingMedia= true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.MediaRequest(
                    per_page = "4",
                    page_no = currentPageMedia.toString(),
                    search = searchValue,
                    type = type,
                    sortBy = sort
                )

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getMediaList("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _mediaList.value = it
                        _productEvent.value = response.body()?.message
                    }
                } else {
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingMedia = false
            }
        }
    }

    // add category
    private val _addCatStatus = MutableLiveData<String>()
    val addCatStatus : LiveData<String> get() = _addCatStatus

    private val _addCatFailStatus = MutableLiveData<String>()
    val addCatFailStatus : LiveData<String> get() = _addCatFailStatus

    fun addCategory(token: String,request: ApiService.AddCatRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.addCategory("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addCatStatus.value = response.body()?.message!!

                    } else {
                        _addCatFailStatus.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    //  category details
    private val _categoryDetails = MutableLiveData<List<CategoryDetailsResponse>>()
    val categoryDetails : LiveData<List<CategoryDetailsResponse>> get() = _categoryDetails

    fun getCategoryDetails(token: String,request: ApiService.CatDetailsRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getCategoryDetails("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _categoryDetails.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }
    // add category
    private val _editCatStatus = MutableLiveData<String>()
    val editCatStatus : LiveData<String> get() = _editCatStatus

    private val _editCatFailStatus = MutableLiveData<String>()
    val editCatFailStatus : LiveData<String> get() = _editCatFailStatus

    fun editCategory(token: String,request: ApiService.EditCatRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.editCategory("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _editCatStatus.value = response.body()?.message!!

                    } else {
                        _editCatFailStatus.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // add product
    private val _addProductStatus = MutableLiveData<String>()
    val addProductStatus : LiveData<String> get() = _addProductStatus

    private val _addProductFailStatus = MutableLiveData<String>()
    val addProductFailStatus : LiveData<String> get() = _addProductFailStatus

    fun addProduct(token: String,request: ApiService.AddProductRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.addProduct("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addProductStatus.value = response.body()?.message!!

                    } else {
                        _addProductFailStatus.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody ?: "")
                    val errorMessage = jsonObj.optString("message", "Something went wrong")

                    _addProductFailStatus.value = errorMessage
                    Log.e("*AddProduct Error*", errorMessage)
                    Log.e("*Home Cat*", errorMessage)

                }

            } catch (e: Exception) {
                _addProductFailStatus.value = "500 Internal Server Error"
                Log.e("*Home Cat**", e.message.toString())
            } finally {

            }
        }
    }
    fun updateProduct(token: String,request: ApiService.UpdateProductRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateProduct("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addProductStatus.value = response.body()?.message!!

                    } else {
                        _addProductFailStatus.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody ?: "")
                    val errorMessage = jsonObj.optString("message", "Something went wrong")

                    _addProductFailStatus.value = errorMessage
                    Log.e("*AddProduct Error*", errorMessage)
                    Log.e("*Home Cat*", errorMessage)

                }

            } catch (e: Exception) {
                _addProductFailStatus.value = "500 Internal Server Error"
                Log.e("*Home Cat**", e.message.toString())
            } finally {

            }
        }
    }

    //  brand details
    private val _brandDetails = MutableLiveData<List<CategoryDetailsResponse>>()
    val brandDetails : LiveData<List<CategoryDetailsResponse>> get() = _brandDetails

    fun getBrandDetails(token: String,request: ApiService.CatDetailsRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getBrandDetails("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _brandDetails.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // update brand
    private val _editBrandStatus = MutableLiveData<String>()
    val editBrandStatus : LiveData<String> get() = _editBrandStatus

    private val _editBrandFailStatus = MutableLiveData<String>()
    val editBrandFailStatus : LiveData<String> get() = _editBrandFailStatus

    fun editBrand(token: String,request: ApiService.EditBrandRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.editBrand("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _editBrandStatus.value = response.body()?.message!!

                    } else {
                        _editBrandFailStatus.value = response.body()?.message
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // delete brand
    private val _deleteBrandStatus = MutableLiveData<String>()
    val deleteBrandStatus : LiveData<String> get() = _deleteBrandStatus

    private val _deleteBrandFailStatus = MutableLiveData<String>()
    val deleteBrandFailStatus : LiveData<String> get() = _deleteBrandFailStatus

    fun deleteBrand(token: String,request: ApiService.CatDetailsRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.deleteBrand("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _deleteBrandStatus.value = response.body()?.message!!

                    } else {
                        _deleteBrandFailStatus.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _deleteBrandFailStatus.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _deleteBrandFailStatus.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    // delete category
    private val _deleteCatStatus = MutableLiveData<String>()
    val deleteCatStatus : LiveData<String> get() = _deleteCatStatus

    private val _deleteCatFailStatus = MutableLiveData<String>()
    val deleteCatFailStatus : LiveData<String> get() = _deleteCatFailStatus

    fun deleteCategory(token: String,request: ApiService.CatDetailsRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.deleteCategory("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _deleteCatStatus.value = response.body()?.message!!

                    } else {
                        _deleteCatFailStatus.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _deleteCatFailStatus.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _deleteCatFailStatus.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    // add unit
    private val _addUnitStatus = MutableLiveData<String>()
    val addUnitStatus : LiveData<String> get() = _addUnitStatus

    private val _addUnitFailStatus = MutableLiveData<String>()
    val addUnitFailStatus : LiveData<String> get() = _addUnitFailStatus

    fun addUnit(token: String,request: ApiService.AddUnitRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.addUnit("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addUnitStatus.value = response.body()?.message!!

                    } else {
                        _addUnitFailStatus.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _addUnitFailStatus.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _addUnitFailStatus.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    // order list

    private val _orderListData = MutableLiveData<OrderListResponse>()
    val orderListData : LiveData<OrderListResponse> get() = _orderListData


    init {
        _orderListData.value = OrderListResponse(
            totalorderCount = "0",
            totalPages = "",
            orders = emptyList()
        )
    }

    fun clearOrderData() {
        _orderListData.value = OrderListResponse(
            totalorderCount = "0",
            totalPages = "",
            orders = emptyList()
        )
    }

    fun getOrderList(token: String, request: ApiService.OrderListRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.orderList("Bearer $token", request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountOrder= data?.totalPages!!.toInt()
                        _orderListData.value = data!!
                        _productEvent.value = response.body()?.message
                    } else {
                        // Handle the case where the error field is true
                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    _showAlertEvent.value = response.message()
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    fun loadNextPageOrder(
        searchValue: String,
        order_type: String,
        area: String,
        zones: String,
        payment_status: String,
        payment_change: String,
        payment_method: String,
        delivery_status: String,
        delivery_boy: String,
        date: String,
        collected_by_seller: String,
        collected_by: String,
        user_id: String,
        isPrescription: String,
        driverCollected: String

    ) {
        if (loadingOrder) return
        loadingOrder= true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.OrderListRequest(
                    per_page = "4",
                    page_no = currentPageOrder.toString(),
                    search = searchValue,
                    order_type = order_type,
                    area = area,
                    zones = zones,
                    payment_status = payment_status,
                    payment_change = payment_change,
                    payment_method = payment_method,
                    delivery_status = delivery_status,
                    delivery_boy = delivery_boy,
                    date = date,
                    collected_by_seller = collected_by_seller,
                    collected_by = collected_by,
                    userId = user_id,
                    IsPrescription = isPrescription,
                    driverCleared = driverCollected

                )

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.orderList("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _orderListData.value = it
                        _productEvent.value = response.body()?.message
                    }
                } else {
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingOrder = false
            }
        }
    }

    private val _updatePaymentStatus = MutableLiveData<String>()
    val updatePaymentStatus : LiveData<String> get() = _updatePaymentStatus

    fun updatePaymentStatus(token: String,request: ApiService.UpdateDeliveryStatusRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updatePaymentStatus("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _updatePaymentStatus.value = response.body()?.message!!

                    } else {
                        _updatePaymentStatus.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _updatePaymentStatus.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _updatePaymentStatus.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }
    fun updateDeliveryStatus(token: String,request: ApiService.UpdateDeliveryStatusRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateDeliveryStatus("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _updatePaymentStatus.value = response.body()?.message!!

                    } else {
                        _updatePaymentStatus.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _updatePaymentStatus.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _updatePaymentStatus.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }
    fun assignInfluencer(token: String,request: ApiService.AssignInfluencerStatusRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.assignInfluencer("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _updatePaymentStatus.value = response.body()?.message!!

                    } else {
                        _updatePaymentStatus.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _updatePaymentStatus.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _updatePaymentStatus.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }
    fun assignDeliveryBoy(token: String,request: ApiService.AssignDeliveryBoyRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.assignDeliveryboy("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _updatePaymentStatus.value = response.body()?.message!!

                    } else {
                        _updatePaymentStatus.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _updatePaymentStatus.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _updatePaymentStatus.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }
    fun updateLead(token: String,request: ApiService.UpdateLeadRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateLead("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _updatePaymentStatus.value = response.body()?.message!!

                    } else {
                        _updatePaymentStatus.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _updatePaymentStatus.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _updatePaymentStatus.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

// order details
private val _orderDetailsData = MutableLiveData<List<OrderDetailsResponse>>()
    val orderDetailsData : LiveData<List<OrderDetailsResponse>> get() = _orderDetailsData

    fun getOrderDetails(token: String,request: ApiService.CatDetailsRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.orderDetails("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _orderDetailsData.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    // delivery boys
    private val _deliveryBoyData = MutableLiveData<List<DeliveryBoyResponse>>()
    val deliveryBoyData : LiveData<List<DeliveryBoyResponse>> get() = _deliveryBoyData

    fun getDeliveryBoys(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getDeliveryBoysList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _deliveryBoyData.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    // admin profile
    private val _adminProfileData = MutableLiveData<AccountResponse>()
    val adminProfileData : LiveData<AccountResponse> get() = _adminProfileData

    fun getAdminProfile(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getAdminProfile("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _adminProfileData.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    // update profile admin
    private val _adminProfileStatus = MutableLiveData<String>()
    val adminProfileStatus: LiveData<String> get() = _adminProfileStatus
    fun updateAdminProfile(
        token: String,
        request: ApiService.UpdateAdminProfileRequest,
        profileImageFile: File?
    ) {
        viewModelScope.launch {
            try {

                val nameRequestBody = request.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val userPhoneRequestBody =
                    request.phone.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailRequestBody = request.email.toRequestBody("text/plain".toMediaTypeOrNull())
                val passwordRequestBody = request.password.toRequestBody("text/plain".toMediaTypeOrNull())
                val confirmPasswordRequestBody =
                    request.confirm_password.toRequestBody("text/plain".toMediaTypeOrNull())


                // Prepare the image file part, if available
                val imagePart = prepareFilePart("profilePic", profileImageFile)

                // Make the network request in IO dispatcher
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateAdminProfile(
                        "Bearer $token",
                        userPhoneRequestBody,
                        nameRequestBody,
                        emailRequestBody,
                        passwordRequestBody,
                        confirmPasswordRequestBody,
                        imagePart
                    )
                }

                if (response.isSuccessful) {
                    val responseData = response.body()
                    _adminProfileStatus.value = responseData?.message
                } else {
                    Log.e("ProfileUpdate", response.errorBody().toString())
                }

            } catch (e: Exception) {
                Log.e("ProfileUpdate", "Error: ${e.localizedMessage}", e)
            } finally {
                _loadingState.value = false
            }
        }
    }
    // search influencer
    private val _influencerStatus = MutableLiveData<SearchInfluencerResponse>()
    val influencerStatus : LiveData<SearchInfluencerResponse> get() = _influencerStatus

    fun searchInfluencer(token: String,request: ApiService.ChangePickupPointRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.influencerSearch("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _influencerStatus.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    // country list
    private val _countryList = MutableLiveData<List<CountryListResponse>>()
    val countryList : LiveData<List<CountryListResponse>> get() = _countryList

    fun getCountryList(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getCountryList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _countryList.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    // delivery boy home
    private val _homeDataDelivery = MutableLiveData<DeliveryBoyHomePageResponse>()
    val homeDataDelivery : LiveData<DeliveryBoyHomePageResponse> get() = _homeDataDelivery

    fun getDeliveryBoyHome(token: String) {
        viewModelScope.launch {
            try {

                val request = ApiService.HomeRequest(
                    date = ""
                )
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getHomePageDeliveryBoy("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _homeDataDelivery.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    //type

    private val _typeData = MutableLiveData<TypeResponse>()
    val typeData : LiveData<TypeResponse> get() = _typeData

    fun getTypeData(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getTypesList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _typeData.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    //segment

    private val _segmentData = MutableLiveData<SegmentResponse>()
    val segmentData : LiveData<SegmentResponse> get() = _segmentData

    fun getSegmentData(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getSegmentsList("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _segmentData.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    //update status order details

    private val _collectedStatus = MutableLiveData<String>()
    val collectedStatus : LiveData<String> get() = _collectedStatus

    fun collectedByDeliveryBoy(token: String,request: ApiService.ChangePickupStatusRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updatePickedUpStatus("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _collectedStatus.value = response.body()?.message!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    //update cash received status

    private val _cashStatus = MutableLiveData<String>()
    val cashStatus : LiveData<String> get() = _cashStatus

    fun updateCashReceived(token: String,request: ApiService.UpdateDeliveryStatusRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateCashReceivedStatus("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _cashStatus.value = response.body()?.message!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    //update segment

    private val _segmentStatus = MutableLiveData<String>()
    val segmentStatus : LiveData<String> get() = _segmentStatus

    fun updateSegment(token: String,request: ApiService.UpdateSegmantRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateSegmant("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _segmentStatus.value = response.body()?.message!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    //update type

    private val _typeStatus = MutableLiveData<String>()
    val typeStatus : LiveData<String> get() = _typeStatus

    fun updateType(token: String,request: ApiService.UpdateTypeRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateType("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _typeStatus.value = response.body()?.message!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    //delete customer

    private val _deleteCustStatus = MutableLiveData<String>()
    val deleteCustStatus : LiveData<String> get() = _deleteCustStatus

    private val _deleteCustStatusFail = SingleLiveEvent<String>()
    val deleteCustStatusFail: LiveData<String> get() = _deleteCustStatusFail


    fun deleteCustomer(token: String,request: ApiService.CartRemoveRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.customerDelete("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _deleteCustStatus.value = response.body()?.message!!

                    } else {
                        _deleteCustStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    fun deleteCustomerAddress(token: String,request: ApiService.CartRemoveRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.deleteShippingAddress("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _deleteCustStatus.value = response.body()?.message!!

                    } else {
                        _deleteCustStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }



    // customer ban

    private val _custBanStatus = MutableLiveData<String>()
    val custBanStatus : LiveData<String> get() = _custBanStatus

    fun customerUpdateBanStatus(token: String,request: ApiService.FavRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.customerUpdateBanStatus("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _custBanStatus.value = response.body()?.message!!

                    } else {
                        _deleteCustStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    // customer details

    private val _customerDetails = MutableLiveData<List<CustomerDetailsResponse>>()
    val customerDetails : LiveData<List<CustomerDetailsResponse>> get() = _customerDetails

    fun getCustomerDetails(token: String,request: ApiService.CartRemoveRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.customerDetails("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _customerDetails.value = response.body()?.data!!

                    } else {
                        _showAlertEvent.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    // customer update

    private val _customerUpdateStatus = SingleLiveEvent<String>()
    val customerUpdateStatus : LiveData<String> get() = _customerUpdateStatus

    private val _customerUpdateStatusFail = SingleLiveEvent<String>()
    val customerUpdateStatusFail : LiveData<String> get() = _customerUpdateStatusFail

    fun customerUpdate(token: String,request: ApiService.CustomerUpdateRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.customerUpdate("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _customerUpdateStatus.value = response.body()?.message!!

                    } else {
                        _customerUpdateStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    // type add

    private val _addTypeStatus = SingleLiveEvent<String>()
    val addTypeStatus : LiveData<String> get() = _addTypeStatus

    private val _addTypeStatusFail = SingleLiveEvent<String>()
    val addTypeStatusFail : LiveData<String> get() = _addTypeStatusFail

    fun addType(token: String,request: ApiService.AddSegmentRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.addType("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addTypeStatus.value = response.body()?.message!!

                    } else {
                        _addTypeStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    fun updateTypeDetails(token: String,request: ApiService.UpdateSegmentDetailsRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateTypeDetails("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addTypeStatus.value = response.body()?.message!!

                    } else {
                        _addTypeStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    fun deleteType(token: String,request: ApiService.CartRemoveRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.deleteType("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addTypeStatus.value = response.body()?.message!!

                    } else {
                        _addTypeStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    fun updateTypeStatus(token: String,request: ApiService.FavRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateTypeStatus("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addTypeStatus.value = response.body()?.message!!

                    } else {
                        _addTypeStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    fun addSegment(token: String,request: ApiService.AddSegmentRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.addSegmant("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addTypeStatus.value = response.body()?.message!!

                    } else {
                        _addTypeStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    fun updateSegmentDetails(token: String,request: ApiService.UpdateSegmentDetailsRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateSegmentDetails("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addTypeStatus.value = response.body()?.message!!

                    } else {
                        _addTypeStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    fun deleteSegment(token: String,request: ApiService.CartRemoveRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.deleteSegmant("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addTypeStatus.value = response.body()?.message!!

                    } else {
                        _addTypeStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }

    fun updateSegmentStatus(token: String,request: ApiService.FavRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.updateSegmantStatus("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {

                        _addTypeStatus.value = response.body()?.message!!

                    } else {
                        _addTypeStatusFail.value = response.body()?.message
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val errorMessage = errorJson.optString("message", "Something went wrong")
                    _showAlertEvent.value = errorMessage

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
                _showAlertEvent.value = "Something went wrong: ${e.localizedMessage}"
            } finally {

            }
        }
    }


    private val _typeListData = MutableLiveData<TypeResponse>()
    val typeListData: LiveData<TypeResponse> get() = _typeListData

    init {
        _typeListData.value = TypeResponse(
            totalCount = "0",
            totalPages = "",
            types = emptyList()
        )
    }

    fun clearTypeData() {
        _typeListData.value = TypeResponse(
            totalCount = "0",
            totalPages = "",
            types = emptyList()
        )
    }

    fun getTypeListNew(token: String,request: ApiService.BrandRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getTypeListNew("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountType= data?.totalPages!!.toInt()
                        _typeListData.value = data!!
                        _productEventCust.value = response.body()?.message
                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }
    fun loadNextPageType() {
        if (loadingType) return
        loadingType= true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.BrandRequest(
                    per_page = "10",
                    page_no = currentPageType.toString()
                )
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getTypeListNew("Bearer $token",request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _typeListData.value = it
                        _productEventCust.value = response.body()?.message
                    }
                } else {
                    currentPageType--
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingType = false
            }
        }
    }

    // segment list
    private val _segmentListData = MutableLiveData<SegmentResponse>()
    val segmentListData: LiveData<SegmentResponse> get() = _segmentListData

    init {
        _segmentListData.value = SegmentResponse(
            totalCount = "0",
            totalPages = "",
            segments = emptyList()
        )
    }

    fun clearSegmentData() {
        _segmentListData.value = SegmentResponse(
            totalCount = "0",
            totalPages = "",
            segments = emptyList()
        )
    }

    fun getSegmentListNew(token: String,request: ApiService.BrandRequest) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getSegmentListNew("Bearer $token",request)
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        totalPageCountSegment= data?.totalPages!!.toInt()
                        _segmentListData.value = data!!
                        _productEventCust.value = response.body()?.message
                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }
    fun loadNextPageSegment() {
        if (loadingSegment) return
        loadingSegment= true

        viewModelScope.launch {
            try {
                val token = AppPreferences.getInstance(context).getToken().toString()
                val request = ApiService.BrandRequest(
                    per_page = "10",
                    page_no = currentPageSegment.toString()
                )
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getSegmentListNew("Bearer $token",request)
                }

                if (response.isSuccessful && response.body()?.error == false) {
                    val data = response.body()?.data
                    data?.let {
                        _segmentListData.value = it
                        _productEventCust.value = response.body()?.message
                    }
                } else {
                    currentPageType--
                    Log.e("HomeViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
            } finally {
                loadingSegment = false
            }
        }
    }


    // all permissions
    private val _allPermissionList = MutableLiveData<AllPermissionsResponse>()
    val allPermissionList: LiveData<AllPermissionsResponse> get() = _allPermissionList

    fun getAllPermissions(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.allPermissions("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                     _allPermissionList.value = data!!
                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // user permissions
    private val _userPermissionList = MutableLiveData<UserPermissionsResponse>()
    val userPermissionList: LiveData<UserPermissionsResponse> get() = _userPermissionList

    fun getUserPermissions(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.userPermissions("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        _userPermissionList.value = data!!
                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }

    // total cash amount
    private val _cashAmountData = MutableLiveData<TotalCashClearedResponse>()
    val cashAmountData: LiveData<TotalCashClearedResponse> get() = _cashAmountData

    fun getTotalCashCleared(token: String) {
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apiService.getTotalCashCleared("Bearer $token")
                }

                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data
                        _cashAmountData.value = data!!
                    } else {

                        Log.e("*Home Cat*", "Error in response: ${response.body()?.message}")
                    }

                } else {
                    Log.e("*Home Cat*", response.message())

                }

            } catch (e: Exception) {
                Log.e("*Home Cat*", e.message.toString())
            } finally {

            }
        }
    }
}
