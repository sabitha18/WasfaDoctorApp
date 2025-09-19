package com.wasfa.doctor.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wasfa.doctor.network.response.Permissions

class AppPreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "MyPrefs"

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferences(context).also { INSTANCE = it }
            }
        }
    }

    fun clearAddress() {
        preferences.edit().remove("address").apply()
    }
    fun saveAddress(data: Address) {
        val jsonAddress = gson.toJson(data)
        preferences.edit().putString("address", jsonAddress).apply()
    }

    fun getAddress(): Address? {
        val jsonAddress = preferences.getString("address", null)
        return if (jsonAddress != null) {
            gson.fromJson(jsonAddress, Address::class.java)
        } else {
            null
        }
    }
    fun getAddProduct(): Product? {
        val jsonAddress = preferences.getString("add_product", null)
        return if (jsonAddress != null) {
            gson.fromJson(jsonAddress, Product::class.java)
        } else {
            null
        }
    }
    fun saveAddProduct(data: Product) {
        val jsonAddress = gson.toJson(data)
        preferences.edit().putString("add_product", jsonAddress).apply()
    }
    fun clearAddProduct() {
        preferences.edit().remove("add_product").apply()
    }

    fun saveStaffPermissions(list: List<String>) {
        val gson = Gson()
        val json = gson.toJson(list)
        preferences.edit().putString("staff_permissions", json).apply()
    }

    fun getStaffPermissions(): List<String> {
        val json = preferences.getString("staff_permissions", null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun saveAllPermissions(list: List<Permissions>) {
        val gson = Gson()
        val json = gson.toJson(list)
        preferences.edit().putString("all_permissions", json).apply()
    }

    fun getAllPermissions(): List<Permissions> {
        val json = preferences.getString("all_permissions", null) ?: return emptyList()
        val type = object : TypeToken<List<Permissions>>() {}.type
        return Gson().fromJson(json, type)
    }



    fun saveCategoryId(service: String?) {
        preferences.edit().putString("category_id", service).apply()
    }

    fun getCategoryId(): String? {
        return preferences.getString("category_id", null)
    }
    fun saveImgSelectionStatus(service: String?) {
        preferences.edit().putString("img_selection_status", service).apply()
    }

    fun getImgSelectionStatus(): String? {
        return preferences.getString("img_selection_status", null)
    }
    fun saveImgStatus(service: String?) {
        preferences.edit().putString("img_status", service).apply()
    }

    fun getImgStatus(): String? {
        return preferences.getString("img_status", null)
    }

    fun saveCatImgId(service: String?) {
        preferences.edit().putString("cat_img_id", service).apply()
    }

    fun getCatImgId(): String? {
        return preferences.getString("cat_img_id", null)
    }
    fun saveCatImgName(service: String?) {
        preferences.edit().putString("cat_img_name", service).apply()
    }

    fun getCatImgName(): String? {
        return preferences.getString("cat_img_name", null)
    }

    // cat icon
    fun saveCatIconImgId(service: String?) {
        preferences.edit().putString("cat_icon_img_id", service).apply()
    }

    fun getCatIconImgId(): String? {
        return preferences.getString("cat_icon_img_id", null)
    }
    fun saveCatIconImgName(service: String?) {
        preferences.edit().putString("cat_icon_img_name", service).apply()
    }

    fun getCatIconImgName(): String? {
        return preferences.getString("cat_icon_img_name", null)
    }
    //cat icon
    fun saveCustId(service: String?) {
        preferences.edit().putString("cust_id", service).apply()
    }

    fun getCustId(): String? {
        return preferences.getString("cust_id", null)
    }
    fun saveCustName(service: String?) {
        preferences.edit().putString("cust_name", service).apply()
    }

    fun getCustName(): String? {
        return preferences.getString("cust_name", null)
    }
    fun saveAddressId(service: String?) {
        preferences.edit().putString("address_id", service).apply()
    }

    fun getAddressId(): String? {
        return preferences.getString("address_id", null)
    }
    fun saveCartCount(service: String?) {
        preferences.edit().putString("cart_count", service).apply()
    }

    fun getCartCount(): String? {
        return preferences.getString("cart_count", null)
    }
    fun saveSellerId(service: String?) {
        preferences.edit().putString("seller_id", service).apply()
    }

    fun getSellerId(): String? {
        return preferences.getString("seller_id", null)
    }
    fun saveProductId(service: String?) {
        preferences.edit().putString("product_id", service).apply()
    }

    fun getProductId(): String? {
        return preferences.getString("product_id", null)
    }
    fun savePDType(service: String?) {
        preferences.edit().putString("pd_type", service).apply()
    }

    fun getPDType(): String? {
        return preferences.getString("pd_type", null)
    }
    fun saveAddOrEditStatus(service: String?) {
        preferences.edit().putString("add_or_edit_status", service).apply()
    }

    fun getAddOrEditStatus(): String? {
        return preferences.getString("add_or_edit_status", null)
    }
    fun saveRememberStatus(service: String?) {
        preferences.edit().putString("remember_status", service).apply()
    }

    fun getRememberStatus(): String? {
        return preferences.getString("remember_status", null)
    }
    fun saveLoginType(service: String?) {
        preferences.edit().putString("login_type", service).apply()
    }

    fun getLoginType(): String? {
        return preferences.getString("login_type", null)
    }
    fun saveToken(service: String?) {
        preferences.edit().putString("token", service).apply()
    }

    fun getToken(): String? {
        return preferences.getString("token", null)
    }
    fun saveEmail(service: String?) {
        preferences.edit().putString("email", service).apply()
    }

    fun getEmail(): String? {
        return preferences.getString("email", null)
    }
    fun savePass(service: String?) {
        preferences.edit().putString("pass", service).apply()
    }

    fun getPass(): String? {
        return preferences.getString("pass", null)
    }
    fun saveKey(service: String?) {
        preferences.edit().putString("key", service).apply()
    }

    fun getKey(): String? {
        return preferences.getString("key", null)
    }
    fun saveFavStatus(service: String?) {
        preferences.edit().putString("fav_status", service).apply()
    }

    fun getFavStatus(): String? {
        return preferences.getString("fav_status", null)
    }
    fun saveFrom(service: String?) {
        preferences.edit().putString("from", service).apply()
    }

    fun getFrom(): String? {
        return preferences.getString("from", null)
    }
    fun saveOrderID(service: String?) {
        preferences.edit().putString("order_id", service).apply()
    }

    fun getOrderID(): String? {
        return preferences.getString("order_id", null)
    }
    fun saveOrderType(service: String?) {
        preferences.edit().putString("order_type", service).apply()
    }

    fun getOrderType(): String? {
        return preferences.getString("order_type", null)
    }
    fun savePresID(service: String?) {
        preferences.edit().putString("pres_id", service).apply()
    }

    fun getPresID(): String? {
        return preferences.getString("pres_id", null)
    }

    fun savePatientId(service: String?) {
        preferences.edit().putString("patient_id", service).apply()
    }

    fun getPatientId(): String? {
        return preferences.getString("patient_id", null)
    }
    fun saveIsPres(service: String?) {
        preferences.edit().putString("is_pres", service).apply()
    }

    fun getIsPres(): String? {
        return preferences.getString("is_pres", null)
    }

    fun clearAllPreferences() {
        preferences.edit().clear().apply()
    }


}
