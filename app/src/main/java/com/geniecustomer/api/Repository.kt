package   com.geniecustomer.api

import com.geniecustomer.model.booking.BookingRequest
import com.geniecustomer.model.otp.OtpRequest
import com.geniecustomer.model.service_providers.ServiceProvidersListRequest
import com.geniecustomer.model.signin.SigninRequest
import com.geniecustomer.model.social.SocialRequest
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(val apiCallInterface: ApiCallInterface) {

    fun executeRegisterUser(request: JsonObject): Observable<JsonElement> {
        return apiCallInterface.registerUser(request)
    }
    fun executeLogin(loginRequest: SigninRequest): Observable<JsonElement> {
        return apiCallInterface.login(loginRequest)
    }

    fun generateInvoice(token:String,`object`: RequestBody?): Observable<JsonElement?>? {
        return apiCallInterface.generateInvoice(token,`object`)
    }

    fun initializeGateway(`object`: RequestBody?): Observable<JsonElement?>? {
        return apiCallInterface.initializeGateway(`object`)
    }

    fun bankList(token:String): Observable<JsonElement?>? {
        return apiCallInterface.bankList(token)
    }

    fun getOtp(otpRequest: OtpRequest): Observable<JsonElement> {
        return apiCallInterface.requestOtp(otpRequest)
    }
    fun getResendOtp(otpRequest: JsonObject): Observable<JsonElement> {
        return apiCallInterface.resendtOtp(otpRequest)
    }
    fun updateProfile(token : String, paramsMap: HashMap<String, RequestBody>): Observable<JsonElement> {
        return apiCallInterface.updateProfile(token,paramsMap)
    }
    fun updateProfileWithImage(token : String, paramsMap: HashMap<String, RequestBody>,
                               image: MultipartBody.Part): Observable<JsonElement> {
        return apiCallInterface.updateProfileWithImage(token,paramsMap,image)
    }

    fun executeSocialLogin(loginRequest: SocialRequest): Observable<JsonElement> {
        return apiCallInterface.social_login(loginRequest)
    }
    fun changePassword(token: String,jsonObject: JsonObject): Observable<JsonElement> {
        return apiCallInterface.changePassword(token,jsonObject)
    }
    fun resetPassword(token: String,jsonObject: JsonObject): Observable<JsonElement> {
        return apiCallInterface.resetPassword(token,jsonObject)
    }
    fun forgotPassword(jsonObject: JsonObject): Observable<JsonElement> {
        return apiCallInterface.forgotPassword(jsonObject)
    }
    fun search(jsonObject: JsonObject): Observable<JsonElement> {
        return apiCallInterface.search(jsonObject)
    }

    fun getDashboardData(token : String): Observable<JsonElement> {
        return apiCallInterface.getDashBoardData(token)
    }

    fun getProvidersList(token : String, providerRequest : ServiceProvidersListRequest): Observable<JsonElement> {
        return apiCallInterface.getProvidersList(token,providerRequest)
    }
    fun getProviderData(token : String,id : String, service_id:String): Observable<JsonElement> {
        return apiCallInterface.getProviderData(token,id,service_id)
    }
    fun getTrending(limit : Int,skip:Int): Observable<JsonElement> {
        return apiCallInterface.getTrending(limit,skip)
    }

//    fun hitCancelBookingRequest(token : String,id : String): Observable<JsonElement> {
//        return apiCallInterface.doCancelRequest(token,id)
//    }

    fun hitCancelBookingRequest(token : String,`object`: RequestBody?): Observable<JsonElement> {
        return apiCallInterface.cancelBooking(token,`object`)
    }

    fun historyBookingDetail(token : String,id : String): Observable<JsonElement> {
        return apiCallInterface.historyBookingDetail(token,id)
    }

    fun isUserComplete(token : String,id : String): Observable<JsonElement> {
        return apiCallInterface.isUserComplete(token,id)
    }

    fun hitDoBooking(token : String,bookingRequest: BookingRequest): Observable<JsonElement> {
        return apiCallInterface.doBooking(token,bookingRequest)
    }

    fun historyBooking(token : String,bookingRequest: JsonObject): Observable<JsonElement> {
        return apiCallInterface.historyBooking(token,bookingRequest)
    }
    fun postRating(token : String,bookingRequest: JsonObject): Observable<JsonElement> {
        return apiCallInterface.postRating(token,bookingRequest)
    }
    fun suggestion(token : String,suggestion: JsonObject): Observable<JsonElement> {
        return apiCallInterface.suggestion(token,suggestion)
    }

    fun reScheduleBooking(token : String,obj: RequestBody): Observable<JsonElement> {
        return apiCallInterface.reScheduleBooking(token,obj)
    }

    fun getChatHistory(token : String,skip : Int, limit:Int): Observable<JsonElement> {
        return apiCallInterface.getChatHistory(token,limit,skip)
    }

    fun getProfile(token: String): Observable<JsonElement> {
        return apiCallInterface.getProfile(token)
    }

    fun getChat(token: String, bookingID: String): Observable<JsonElement> {
        return apiCallInterface.getChat(token, bookingID)
    }

    fun getTimeSlot(token: String, bookingID: String): Observable<JsonElement> {
        return apiCallInterface.getTimeSlot(token, bookingID)
    }

    fun logoutUser(token: String): Observable<JsonElement> {
        return apiCallInterface.logout(token)
    }

//
//    fun executeSocialLogin(lang:String,request: SocialLoginRequest):Observable<JsonElement>{
//        return apiCallInterface.socialLogin(lang,request)
//    }
//
//    fun executeChangePassword(lang:String,request: ChangePasswordRequest):Observable<JsonElement>{
//        return apiCallInterface.changePassword(lang,request)
//    }
//
//   fun executeUpdatePassword(lang:String,authorization:String,request: UpdatePasswordRequest):Observable<JsonElement>{
//        return apiCallInterface.updatePassword(lang,authorization,request)
//    }
//
//    fun getProfile(lang:String,authorization:String):Observable<JsonElement>{
//        return apiCallInterface.profile(lang,authorization)
//    }


}