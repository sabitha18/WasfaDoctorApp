package com.wasfa.doctor.helper

object PermissionKeys {

    /*product*/

    const val ADD_NEW_PRODUCT = "add_new_product"
    const val SHOW_ALL_PRODUCT = "show_all_products"
    const val PRODUCT_EDIT = "product_edit"
    const val PRODUCT_DUPLICATE = "product_duplicate"
    const val PRODUCT_DELETE = "product_delete"

    /*category*/
    const val VIEW_PRODUCT_CATEGORIES = "view_product_categories"
    const val ADD_PRODUCT_CATEGORY = "add_product_category"
    const val EDIT_PRODUCT_CATEGORY = "edit_product_category"
    const val DELETE_PRODUCT_CATEGORY = "delete_product_category"

    /*Brands*/
    const val VIEW_ALL_BRANDS = "view_all_brands"
    const val ADD_BRAND = "add_brand"
    const val EDIT_BRAND = "edit_brand"
    const val DELETE_BRAND = "delete_brand"

    /*sku*/
    const val ADD_NEW_APIX_SKU = "add_new_apix_sku"

    /*unit*/
    const val ADD_UNIT = "add_unit"

    /*order*/
    const val VIEW_ALL_ORDERS = "view_all_orders"
    const val VIEW_ALL_PENDING_ORDERS = "view_all_pending_orders"
    const val VIEW_ALL_CANCELLED_ORDERS = "view_all_cancelled_orders"
    const val VIEW_ORDER_DETAILS = "view_order_details"
    const val UPDATE_ORDER_PAYMENT_STATUS = "update_order_payment_status"
    const val UPDATE_ORDER_DELIVERY_STATUS = "update_order_delivery_status"
    const val DELETE_ORDER = "delete_order"
    const val ASSIGN_DELIVERY_BOY_FOR_ORDERS = "assign_delivery_boy_for_orders"

    /*customer*/

    const val VIEW_ALL_CUSTOMERS = "view_all_customers"
    const val LOGIN_AS_CUSTOMER = "login_as_customer"
    const val BAN_CUSTOMER = "ban_customer"
    const val DELETE_CUSTOMER = "delete_customer"


    const val VIEW_ALL_SELLER = "view_all_seller"
    const val VIEW_SELLER_PROFILE = "view_seller_profile"
    const val LOGIN_AS_SELLER = "login_as_seller"
    const val VIEW_ALL_STAFFS = "view_all_staffs"
    const val ADD_STAFF = "add_staff"
    const val EDIT_STAFF = "edit_staff"


    const val ADD_NEW_INFLUENCER = "add_new_influencer"
    const val SHOW_ALL_INFLUENCERS = "show_all_influencers"
    const val VIEW_ALL_GOVERNORATE = "view_all_governorate"
    const val ADD_GOVERNORATE = "add_governorate"
    const val EDIT_CUSTOMERS = "edit_customers"
    const val VENDOR_LIST = "vendor_list"
    const val LOGIN_AS_INFLUENCER = "login_as_influencer"

    const val VIEW_ALL_SEGMANT = "view_all_segmant"
    const val ADD_SEGMANT = "add_segmant"
    const val EDIT_SEGMANT = "edit_segmant"
    const val DELETE_SEGMANT = "delete_segmant"
    const val VIEW_ALL_TYPE = "view_all_type"
    const val ADD_TYPE = "add_type"
    const val EDIT_TYPE = "edit_type"
    const val DELETE_TYPE = "delete_type"
    const val LOGIN_AS_STAFF = "login_as_staff"
    const val VIEW_ALL_ORIGINS = "view_all_origins"
    const val ADD_ORIGIN = "add_origin"
    const val EDIT_ORIGIN = "edit_origin"
    const val DELETE_ORIGIN = "delete_origin"


    const val CONVERSION_RATE = "conversion_rate"
    const val NEWLY_ADDED_VENDORS = "newly_added_vendors"
    const val NEWLY_ADDED_DOCTORS = "newly_added_doctors"
    const val LAST_SEARCH_TERM = "last_search_term"
    const val NEW_CUSTOMERS = "new_customers"
    const val RECENT_ORDERS = "recent_orders"
    const val TOP_SEARCH_TERM = "top_search_term"

    const val DOCTOR_PERFORMANCE = "doctor_performance"
    const val SALES_HOURLY_BASIS = "sales_hourly_basis"
    const val SALES_ANALYTICS_NO_OF_SALE = "sales_analytics_no_of_sale"
    const val SALES_ANALYTICS_NO_OF_SALE_GROSS_APIX = "sales_analytics_no_of_sale_gross_apix"


    /*Admin dashboard */
    const val MOST_VIEWED_PRODUCTS = "most_viewed_products"
    const val BEST_SELLING_PRODUCTS = "best_selling_products"
    const val REVENUE_AN_SALES_PIECHART = "revenue_an_sales_piechart"
    const val REVENUE_AN_SALES_BARCHART = "revenue_an_sales_barchart"
    const val MONTHLY_SALES_COG_VS_SALES = "monthly_sales_cog_vs_sales"
    const val MONTHLY_SALES_COG_VS_PRODUCT_SALES = "monthly_sales_sales_vs_product_sales"
    const val MONTHLY_SALES_SALES_VS_APIXMARGIN_VS_NETMARGIN =
        "monthly_sales_sales_vs_apixmargin_vs_netmargin"
    const val MONTHLY_SALES_NOOFSALES_VS_AMOUNTSALES_VS_PRODUCTSALES =
        "monthly_sales_noofsales_vs_amountsales_vs_productsales"


    const val DAILY_SALES_WIDGET = "daily_sales_widget"
    const val DAILY_ORDERS_WIDGET = "daily_orders_widget"
    const val PENDING_DELIVERY_WIDGET = "pending_delivery_widget"
    const val GROSS_MARGIN_WIDGET = "gross_margin_widget"
    const val NET_MARGIN_WIDGET = "net_margin_widget"
    const val REFUND_REQUEST_WIDGET = "refund_request_widget"
    const val APPROVE_ORDERS_WIDGET = "approve_orders_widget"
    const val CANCELLED_ORDERS_WIDGET = "cancelled_orders_widget"
    const val CLOSED_ORDERS_WIDGET = "closed_orders_widget"

    const val DATA_MANAGEMENT_SELLER_MISSING_WIDGET = "data_management_seller_missing_widget"
    const val DATA_MANAGEMENT_SELLER_COMMISSION_WIDGET = "data_management_seller_commission_widget"
    const val DATA_MANAGEMENT_DOCTOR_COMMISSION_WIDGET = "data_management_doctor_commission_widget"
    const val DATA_MANAGEMENT_DOCTOR_COMMISSION_NILL_WIDGET =
        "data_management_doctor_commission_nill_widget"
    const val DATA_MANAGEMENT_DOCTOR_COMMISSION_PHARMA_WITH_OUT_ZERO =
        "data_management_doctor_commission_pharmaseutical_with_out_zero"
    const val INFLUENCER_NOT_PHARMA_PRODUCT_REPORT = "influencer_not_pharma_product_report"

    const val GENERAL_TOTAL_SALE_WIDGET = "general_total_sale_widget"
    const val GENERAL_TOTAL_CLIENT_WIDGET = "general_total_client_widget"
    const val GENERAL_TOTAL_SALE_AMOUNT_WIDGET = "general_total_sale_amount_widget"
    const val GENERAL_TOTAL_ORDERS_WIDGET = "general_total_orders_widget"
    const val GENERAL_ORDER_AMOUNT_WIDGET = "general_order_amount_widget"
    const val GENERAL_TOTAL_DOCTORS_WIDGET = "general_total_doctors_widget"
    const val GENERAL_TOTAL_VENDORS_WIDGET = "general_total_vendors_widget"
    const val GENERAL_STOCK_COUNT_WIDGET = "general_stock_count_widget"
    const val GENERAL_DOCTOR_OUTSTANDING_WIDGET = "general_doctor_outstanding_widget"
    const val GENERAL_PAID_TO_DOCTOR_WIDGET = "general_paid_to_doctor_widget"
    const val GENERAL_SELLER_OUTSTANDING_WIDGET = "general_seller_outstanding_widget"
    const val GENERAL_PAID_TO_SELLER_WIDGET = "general_paid_to_seller_widget"
    const val GENERAL_GROSS_MARGIN_WIDGET = "general_gross_margin_widget"
    const val GENERAL_NET_MARGIN_WIDGET = "general_net_margin_widget"
    const val GENERAL_PENDING_DELIVERY_WIDGET = "general_pending_delivery_widget"
    const val GENERAL_SALES_RETURN_WIDGET = "general_sales_return_widget"
    const val GENERAL_APPROVED_REQUEST_WIDGET = "general_approved_request_widget"
    const val GENERAL_DELIVERY_COST_WIDGET = "general_delivery_cost_widget"
    const val GENERAL_DELIVERY_COUNT_WIDGET = "general_delivery_count_widget"
    const val GENERAL_TOTAL_CANCELLED_WIDGET = "general_total_cancelled_widget"
    const val GENERAL_TOTAL_CLOSED_WIDGET = "general_total_closed_widget"
}
