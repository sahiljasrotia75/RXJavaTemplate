package com.geniecustomer.api

object Urls {

//    const val BASE_URL = "https://appgrowthcompany.com:1337/"
//    const val SOCKET_URL = "https://appgrowthcompany.com:1337"

    const val BASE_URL = "https://prod.webdevelopmentsolution.net:3023/"     //Local
//    const val BASE_URL = "https://appgrowthcompany.com:9034/"     //Local
//
//    const val BASE_URL = "https://genie.mt:9034/"                   //Live
//

//    const val SOCKET_URL = "https://appgrowthcompany.com:9034"    //Local
    const val SOCKET_URL = "https://prod.webdevelopmentsolution.net:3023"    //Local
//    const val SOCKET_URL = "https://genie.mt:9034"                  //Live

    //
    const val OTP_REQUEST = "api/v1/auth/verifyOtp"
    const val RESEND_OTP_REQUEST = "api/v1//auth/resend_otp"
    const val REGISTER_USER = "api/v1/auth/userSignUp"
    const val LOGIN = "api/v1/auth/userSingIn"
    const val SOCIAL_LOGIN = "api/v1/auth/social_login"
    const val CHANGE_PASSWORD = "api/v1/customer/changePassword"
    const val RESET_PASSWORD = "api/v1/customer/resetPassword"
    const val FORGOT_PASSWORD = "api/v1/auth/forgotPassword"
    const val PROFILE = "api/v1/customer/userEditProfile"
    const val DASHBOARD_DATA = "api/v1/customer/getDashboardData"
    const val GET_PROVIDERS_LIST = "api/v1/customer/service/getServiceList"
    const val GET_PROVIDER_DATA = "api/v1/customer/service/getProviderDetail/{id}"
    const val GET_TRENDING = "api/v1/customer/trending"
    const val DO_BOOKING = "api/v1/customer/request/sendRequest"
    const val CANCEL_REQUEST_BOOKING = "api/v1/customer/request/cancelRequest/{id}"
    const val CANCEL_BOOKING = "api/v1/customer/booking/cancel_booking"
    const val HISTORY_BOOKING_DETAIL = "api/v1/customer/booking/getBookingDetail/{id}"
    const val HISTORY_BOOKING = "api/v1/customer/booking/getBookingHistory"
    const val POST_RATING = "api/v1/customer/rating"
    const val SEARCH = "api/v1/customer/search"
    const val GET_CHAT_LIST = "api/v1/public/chatHistory"
    const val SUGGESTION = "api/v1/public/suggestion"
    const val GET_PROFILE = "api/v1/customer/profile"
    const val Re_SCHEDULE = "api/v1/customer/booking/reschedule"
    const val CHAT_HISTORY = "api/v1/public/chat/{id}"
    const val TIME_SLOT = "api/v1/customer/booking/getSlot/{id}"
    const val ISUSERCOMPLETE = "api/v1/customer/userComplete/{id}"
    const val generateInvoice = "api/v1/public/invoice"
    const val bankList = "api/v1/public/setting"
    const val LOGOUT = "api/v1/auth/signOut"

    //For PAyment Gateway:
    const val initializingGateway = "https://www.apsp.biz/MerchantTools/MerchantTools.svc/BuildXMLToken"

}