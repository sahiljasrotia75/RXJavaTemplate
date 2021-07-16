package  com.geniecustomer.api

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
import retrofit2.http.*

interface ApiCallInterface {

    @POST(Urls.REGISTER_USER)
    fun registerUser(
        @Body request: JsonObject
    ): Observable<JsonElement>

    @POST(Urls.OTP_REQUEST)
    fun requestOtp(
        @Body request: OtpRequest
    ): Observable<JsonElement>

    @POST(Urls.generateInvoice)
    fun generateInvoice(
        @Header("tId") token: String?,
        @Body `object`: RequestBody?
    ): Observable<JsonElement?>?

    @POST(Urls.initializingGateway)
    fun initializeGateway(
        @Body `object`: RequestBody?
    ): Observable<JsonElement?>?

    @GET(Urls.bankList)
    fun bankList(
        @Header("tId") token: String?
    ): Observable<JsonElement?>?


    @POST(Urls.RESEND_OTP_REQUEST)
    fun resendtOtp(
        @Body request: JsonObject
    ): Observable<JsonElement>


    @Multipart
    @POST(Urls.PROFILE)
    fun updateProfile(
        @Header("tId") token: String,
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>
    ): Observable<JsonElement>


    @Multipart
    @POST(Urls.PROFILE)
    fun updateProfileWithImage(
        @Header("tId") token: String,
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part
    ): Observable<JsonElement>

    @POST(Urls.CHANGE_PASSWORD)
    fun changePassword(
        @Header("tId") token: String,
        @Body jsonObject: JsonObject
    ): Observable<JsonElement>

    @POST(Urls.RESET_PASSWORD)
    fun resetPassword(
        @Header("tId") token: String,
        @Body jsonObject: JsonObject
    ): Observable<JsonElement>

    @GET(Urls.DASHBOARD_DATA)
    fun getDashBoardData(
        @Header("tId") token: String
    ): Observable<JsonElement>

    @POST(Urls.GET_PROVIDERS_LIST)
    fun getProvidersList(
        @Header("tId") token: String,
        @Body providersList : ServiceProvidersListRequest
    ): Observable<JsonElement>

    @POST(Urls.DO_BOOKING)
    fun doBooking(
        @Header("tId") token: String,
        @Body bookingRequest: BookingRequest
    ): Observable<JsonElement>

    @POST(Urls.HISTORY_BOOKING)
    fun historyBooking(
        @Header("tId") token: String,
        @Body jsonObject: JsonObject
    ): Observable<JsonElement>

    @POST(Urls.POST_RATING)
    fun postRating(
        @Header("tId") token: String,
        @Body jsonObject: JsonObject
    ): Observable<JsonElement>

    @POST(Urls.SUGGESTION)
    fun suggestion(
        @Header("tId") token: String,
        @Body jsonObject: JsonObject
    ): Observable<JsonElement>

    @POST(Urls.Re_SCHEDULE)
    fun reScheduleBooking(
        @Header("tId") token: String,
        @Body jsonObject: RequestBody
    ): Observable<JsonElement>

    @GET(Urls.GET_PROVIDER_DATA)
    fun getProviderData(
        @Header("tId") token: String,
        @Path("id")id : String,
        @Query("service")service: String
    ): Observable<JsonElement>

    @GET(Urls.GET_TRENDING)
    fun getTrending(
        @Query("limit")limit : Int,
        @Query("skip")skip: Int
    ): Observable<JsonElement>

    @GET(Urls.CANCEL_REQUEST_BOOKING)
    fun doCancelRequest(
        @Header("tId") token: String,
        @Path("id")id : String
    ): Observable<JsonElement>

    @POST(Urls.CANCEL_BOOKING)
    fun cancelBooking(
        @Header("tId") token: String?,
        @Body `object`: RequestBody?
    ): Observable<JsonElement>

    @GET(Urls.HISTORY_BOOKING_DETAIL)
    fun historyBookingDetail(
        @Header("tId") token: String,
        @Path("id") id : String
    ): Observable<JsonElement>

    @POST(Urls.FORGOT_PASSWORD)
    fun forgotPassword(
        @Body jsonObject: JsonObject
    ): Observable<JsonElement>


    @POST(Urls.LOGIN)
    fun login(
        @Body loginRequest: SigninRequest
    ): Observable<JsonElement>

    @POST(Urls.SOCIAL_LOGIN)
    fun social_login(
        @Body loginRequest: SocialRequest
    ): Observable<JsonElement>

    @POST(Urls.SEARCH)
    fun search(
        @Body jsonObject: JsonObject
    ): Observable<JsonElement>

    @GET(Urls.GET_CHAT_LIST)
    fun getChatHistory(
        @Header("tId") token: String,
        @Query("limit")limit: Int,
        @Query("skip")skip: Int
    ): Observable<JsonElement>

    @GET(Urls.GET_PROFILE)
    fun getProfile(
        @Header("tId") token: String
    ): Observable<JsonElement>

    @GET(Urls.CHAT_HISTORY)
    fun getChat(
        @Header("tId") token: String,
        @Path("id")bookingId: String
    ): Observable<JsonElement>

    @GET(Urls.TIME_SLOT)
    fun getTimeSlot(
        @Header("tId") token: String,
        @Path("id") bookingId: String
    ): Observable<JsonElement>

    @GET(Urls.ISUSERCOMPLETE)
    fun isUserComplete(
        @Header("tId") token: String,
        @Path("id") bookingId: String
    ): Observable<JsonElement>


    @POST(Urls.LOGOUT)
    fun logout(
        @Header("tId") token: String
    ): Observable<JsonElement>

}