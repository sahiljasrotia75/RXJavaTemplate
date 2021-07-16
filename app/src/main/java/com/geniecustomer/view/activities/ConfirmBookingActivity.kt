package com.geniecustomer.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.geniecustomer.R
import com.geniecustomer.adapters.BookingDetailAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.interfaces.ServiceRemoveListener
import com.geniecustomer.model.Address
import com.geniecustomer.model.ReviewDetailModel
import com.geniecustomer.model.TaskObject
import com.geniecustomer.model.booking.BookingRequest
import com.geniecustomer.model.booking.BookingResponse
import com.geniecustomer.model.booking.Location
import com.geniecustomer.model.initializePaymentGateway.InitializePaymentGatewayResponseModel
import com.geniecustomer.model.signin.Data
import com.geniecustomer.view.activities.urlViewer.UrlViewer
import com.geniecustomer.view.register.LoginActivity
import com.geniecustomer.viewmodels.BankListViewModel
import com.geniecustomer.viewmodels.BookingHistoryDetailViewModel
import com.geniecustomer.viewmodels.BookingViewModel
import com.geniecustomer.viewmodels.InitializeGatewayViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import genie.service.provider.activities.addBankDetail.getBanksListResponseModel.BankListResponseModel
import kotlinx.android.synthetic.main.activity_confirm_booking.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ConfirmBookingActivity : BaseActivity(), ServiceRemoveListener {

    var adapter: BookingDetailAdapter? = null
    var list: MutableList<TaskObject> = ArrayList()
    var service_list: MutableList<String> = ArrayList()
    var review_detail_model: ReviewDetailModel? = null
    val model by viewModel<BookingViewModel>()
    val reScheduleModel by viewModel<BookingHistoryDetailViewModel>()
    var total_amount = 0.0
    var typeofBooking = 0
    var location: Location? = null
    var scheduleTime: MutableList<String>? = null
    var df = DecimalFormat("0.00")
    private  val initializeGatewayViewModel by viewModel<InitializeGatewayViewModel>()
    private val bankListViewModel by viewModel<BankListViewModel>()
    private lateinit var responseData: com.geniecustomer.model.booking.Data

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_booking)

        if (sharedPref.contains("guest") && sharedPref.getBoolean(
                "guest",
                false
            )
        ) {
            editor.remove("guest").apply()
        }

        if (intent != null && intent.hasExtra("review_model")) {

            //Log.e("BOOKING_TYPE","typeee  "+typeofBooking)
            typeofBooking = intent.getIntExtra("typeOfBooking", 0)
            Log.e("BOOKING_TYPE", "typeee  " + typeofBooking)
            review_detail_model = intent.getSerializableExtra("review_model") as ReviewDetailModel
            Log.e("BOOKING_TYPE_get_DATA", "typeee  " + Gson().toJson(review_detail_model))
//            review_detail_model = intent.getStringExtra("review_model") as ReviewDetailModel
            if (review_detail_model?.bookingType.equals("Schedule", true)) {
                scheduleTime = mutableListOf()
                scheduleTime = review_detail_model?.scheduleTime as MutableList<String>?
            }

            Log.e("BOOKING_TYPE_get_DATA", "typeee  " + Gson().toJson(review_detail_model))
        }
        if (sharedPref.contains("address")) {
            val address = MyApp.gson.fromJson(
                sharedPref.getString("address", ""),
                Address::class.java
            )
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(address.latitude ?: 0.0, address.longitude ?: 0.0, 1)
                val cityName = addresses[0].locality
                location = Location(address.address, cityName, address.latitude, address.longitude)
            } catch (e: java.lang.Exception) {
                Log.e("error", "city" + e.message)
            }

//            tv_location_value.setText(address.address)
//            latlng = LatLng(address.latitude?:0.0, address.longitude?:0.0)
        }

        tv_address_value.text = review_detail_model?.address
        tv_details_value.text = review_detail_model?.task_details
        tv_service_man_name.text = review_detail_model?.provider_name
        tv_service_man_address.text = review_detail_model?.provider_address
        tv_review.text = "" + review_detail_model?.provider_review_num + " Reviews"
        rating_bar.rating = review_detail_model?.provider_rating ?: 0f


        //Set a current date
        setCurrentDate()


//        tv_review_type.setText("" + review_detail_model?.provider_review_num + " Reviews")
        if (rating_bar.rating > 4f) {
            tv_review_type.text = getString(R.string.great)
        } else if (rating_bar.rating > 3f) {
            tv_review_type.text = getString(R.string.good)
        } else if (rating_bar.rating > 2f) {
            tv_review_type.text = getString(R.string.okay)
        } else if (rating_bar.rating > 1f) {
            tv_review_type.text = getString(R.string.bad)
        } else {
        }


        for (i in 0 until review_detail_model?.task_list?.size!!) {
            list.add(review_detail_model?.task_list?.get(i)!!)
            Log.e("BADSHAH", "total_amount:${list.get(i).rate}")
            total_amount += list.get(i).rate
            service_list.add(list.get(i).id)
        }

//        if(review_detail_model?.scheduleTime?.size?:0 > 0 && review_detail_model?.bookingType.equals("Schedule", true)){
//            total_amount = (0.5) * total_amount
//            Log.e("total"," AMount "+total_amount)
//        }


        adapter = BookingDetailAdapter(
            this,
            list as ArrayList<TaskObject>,
            0,
            this,
            review_detail_model?.bookingType
        )
        recycler_view_task.adapter = adapter
        recycler_view_task.layoutManager = LinearLayoutManager(this)

        model.bookingResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!, 1)
        })

        reScheduleModel.bookingResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!, 2)
        })

        initializeGatewayViewModel.liveDataCommonResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(
                it!!,
                6
            )
        })

        bankListViewModel.liveDataCommonResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(
                it!!,
                7
            )
        })

    }


    private fun setCurrentDate() {
        try {
            val currentDate = SimpleDateFormat("d MMM, yy hh:mm a").format(Date())
            tv_date_time_value.text = currentDate
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun consumeResponse(apiResponse: ApiResponse, type: Int) {

        when (apiResponse.status) {

            Status.LOADING -> showProgress()

            Status.SUCCESS -> {
                hideProgress()
                try {
                    renderSuccessResponse(apiResponse.data!!, type)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Status.ERROR -> {
                hideProgress()
                Log.e("FAILURE", apiResponse.error?.message ?: "")
                showSnackBar(apiResponse.error?.message ?: "")
            }

            else -> {
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun renderSuccessResponse(response: JsonElement, type: Int) {
        if (!response.isJsonNull) {

            Log.e("response", "$response")

            if (type == 1) {
                val data: String = MyApp.gson.toJson(response)
                val signupResponse = MyApp.gson.fromJson(data, BookingResponse::class.java)
                if (signupResponse.success != null && signupResponse.success.toString()
                        .equals("TRUE", true)
                ) {
                    if (signupResponse.data != null) {
                        responseData = signupResponse.data
                        if (review_detail_model?.price_type.equals("fixed", true)) {
                            Log.e("BADSHAH", "booking ID:${signupResponse.data.id}")
                            //API Hitting:
                        /*    val jsonData = JSONObject()
                            jsonData.put("MerchID", "4171")
                            jsonData.put("MerchPass", "nH04ecy5WFmOuT")
                            jsonData.put(
                                "XMLParam",
                                "<Transaction hash=\"password\"><ProfileID>EF459CE6111C4C7387C29A902476A737</ProfileID><Value>$total_amount</Value><Curr>978</Curr><Lang>en</Lang><ORef>${signupResponse.data.id}</ORef><ClientAcc>Test_Acc</ClientAcc><MobileNo>123456</MobileNo><Email>joedoe@mail.com</Email><RedirectionURL>${Urls.BASE_URL}api/v1/public/paymentCallback</RedirectionURL><UDF1/><UDF2/><UDF3/><FastPay><CardRestrict/><ListAllCards>Last/All</ListAllCards><NewCard1Try/><NewCardOnFail/><PromptCVV/><PromptExpiry/></FastPay><ExtendedErr/><ActionType>1</ActionType><WYCB/><TEST/><status_url urlEncode=\"true\">${Urls.BASE_URL}api/v1/public/statusUrl</status_url><RegName>APCO Test</RegName></Transaction>"
                            )
                            *//*jsonData.put(
                                "XMLParam",
                                "<Transaction hash=\"password\"><ProfileID>EF459CE6111C4C7387C29A902476A737</ProfileID><Value>$total_amount</Value><Curr>978</Curr><Lang>en</Lang><ORef>${signupResponse.data.id}</ORef><ClientAcc>${"" + user_obj?.firstName + "" + user_obj?.lastName}</ClientAcc><MobileNo>${"" + user_obj?.countryCode + "" + user_obj?.phone}</MobileNo><Email>${user_obj?.email ?: ""}</Email><RedirectionURL>${Urls.BASE_URL}api/v1/public/paymentCallback</RedirectionURL><UDF1/><UDF2/><UDF3/><FastPay><CardRestrict/><ListAllCards>Last/All</ListAllCards><NewCard1Try/><NewCardOnFail/><PromptCVV/><PromptExpiry/></FastPay><ExtendedErr/><ActionType>1</ActionType><WYCB/><status_url urlEncode=\"true\">${Urls.BASE_URL}api/v1/public/statusUrl</status_url></Transaction>"
                            )*//*
                            Log.e("PaymentUrl", "paymenturl $jsonData")
                            val requestBody = RequestBody.create(
                                MediaType.parse("application/json"),
                                jsonData.toString()
                            )

                            initializeGatewayViewModel.initialGateway(requestBody)
*/

                            startActivity(
                                Intent(this, BookingSuccessActivity::class.java).putExtra(
                                    "details",
                                    signupResponse.data
                                )
                            )
                            finish()
                        }
                        else {
                            startActivity(
                                Intent(this, BookingSuccessActivity::class.java).putExtra(
                                    "details",
                                    signupResponse.data
                                )
                            )
                            finish()
                        }
                    }

                } else {

                    if (user_obj != null)
                        signupResponse.message?.let { showSnackBar(it) }
                    else
                    {
                        AlertDialog.Builder(this)
                            .setMessage(getString(R.string.login_proceed))
                            .setPositiveButton(
                                getString(R.string.yes),
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        editor.putBoolean("guest", true).apply()
                                        navigate(LoginActivity::class.java)
                                    }
                                }).setNegativeButton(R.string.no, null).show()
                    }
                }
            }
            else if (type == 6) {
                val data: String = MyApp.gson.toJson(response)
                val initializePaymentGateway = MyApp.gson.fromJson(
                    data,
                    InitializePaymentGatewayResponseModel::class.java
                )
                Log.e("BADSHAH", "InitializePAymentGateway Response:$initializePaymentGateway")
                val url = "${initializePaymentGateway.BaseURL}${initializePaymentGateway.Token}"
                val i = Intent(context, UrlViewer::class.java)
                i.putExtra("nameTitle", "Payment")
                i.putExtra("url", url)
                i.putExtra("details", responseData)
                startActivity(i)
//                finish()
            }
            else  if (type == 7)
            {
                try {
                    val data: String = MyApp.gson.toJson(response)
                    val dataModel = Gson().fromJson(data, BankListResponseModel::class.java)
                    if (dataModel?.success == true)
                    {
                        Log.e("DataGet", "$dataModel")
                       updateVatAndTax()

                    }
                } catch (e: Exception) {
                    Log.e("BADSHAH", "Exception Occurred:$e")
                }
            }
            else {
                showToast("Booking done successfully")
                startActivity(
                    Intent(this, BookingDetailActivity::class.java)
                        .putExtra("_id", review_detail_model?.bookingId!!)
                )
                finishAffinity()
            }
        }
    }

    private fun updateVatAndTax() {

        tvSubTotValue.text = "$${df.format(total_amount.toFloat() / 1.18)}"
        tvTaxValue.text = "$${df.format((total_amount.toFloat() / 1.18) * 0.18)}"
        tv_amount.text = "$"+df.format(total_amount)

        if (review_detail_model?.price_type.equals("fixed", true))
        {
            tvNormalAmount.text = "Amount"
            tvNormalAmountValue.text = "$"+df.format(total_amount)
        }
        else
        {
            tvNormalAmount.text = "Negotiable Price"
            tvNormalAmountValue.text = "Starting from $"+df.format(total_amount)
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.tv_bottom_btn -> {

                if (user_obj != null) {
                    if (!user_obj?.phone.isNullOrEmpty() && !user_obj?.email.isNullOrEmpty()) {
                        if (typeofBooking == 1) {
                            if (review_detail_model?.price_type.equals("fixed", true)) {
                                hitBookingApiRequest()
                            } else {
                                AlertDialog.Builder(this)
                                    .setMessage(getString(R.string.youhavetochatwithserviceprovidertonegotiateprice))
                                    .setPositiveButton(
                                        getString(R.string.ohk),
                                        object : DialogInterface.OnClickListener {
                                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                                hitBookingApiRequest()
                                            }
                                        }).setNegativeButton(R.string.cancel, null).show()
                            }

                        } else if (typeofBooking == 2) {

                            val jsonObject = JSONObject()
                            val json = JSONArray()
                            for (i in scheduleTime?.indices!!) {
                                json.put(scheduleTime?.get(i))
                            }

                            jsonObject.put("bookingId", review_detail_model?.bookingId ?: "")
                            jsonObject.put("amount", total_amount)
                            jsonObject.put("scheduleTime", json)

                            val locationObj = JSONObject()
                            locationObj.put("address", location?.address!!)
                            locationObj.put("city", location?.city)
                            locationObj.put("latitude", location?.latitude)
                            locationObj.put("longitude", location?.longitude)
                            jsonObject.put("location", locationObj)
                            jsonObject.put("desc", review_detail_model?.task_details!!)
                            Log.e("Data", " for send " + jsonObject)
                            val body = RequestBody.create(
                                MediaType.parse("application/json"),
                                jsonObject.toString()
                            )


                            AlertDialog.Builder(this)
                                .setMessage(getString(R.string.youhavetochatwithserviceprovidertonegotiateprice))
                                .setPositiveButton(
                                    getString(R.string.ohk),
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(p0: DialogInterface?, p1: Int) {
                                            reScheduleModel.reScheduleBooking(
                                                user_obj?.token ?: "",
                                                body
                                            )
                                        }
                                    }).setNegativeButton(R.string.cancel, null).show()

                            //reScheduleModel.reScheduleBooking(user_obj?.token ?: "", body)
                        }
                    } else {
                        val message = when {
                            user_obj?.phone.isNullOrEmpty() -> {
                                getString(R.string.nophone)
                            }
                            user_obj?.email.isNullOrEmpty() -> {
                                getString(R.string.noEmail)
                            }
                            else -> {
                                getString(R.string.nophone)
                            }
                        }

                        AlertDialog.Builder(this)
                            .setMessage(message)
                            .setPositiveButton(
                                getString(R.string.gotoeditprofile)
                            ) { p0, p1 ->
//                                navigateFinishAffinity(HomeActivity::class.java)
                                (context as Activity).startActivity(
                                    Intent(
                                        context,
                                        ProfileActivity::class.java
                                    )
                                )
                            }.show()
                    }

                } else {
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.login_proceed))
                        .setPositiveButton(
                            getString(R.string.yes)
                        ) { p0, p1 ->
                            editor.putBoolean("guest", true).apply()
                            navigate(LoginActivity::class.java)
                        }.setNegativeButton(R.string.no, null).show()
                }

            }
            R.id.iv_back -> {
                finish()
            }
        }
    }

    private fun hitBookingApiRequest() {

        //For current address fetch
        if (RequireServiceActivity.newAddress != null) {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses =
                geocoder.getFromLocation(
                    RequireServiceActivity.newAddress?.latLng?.latitude ?: 0.0,
                    RequireServiceActivity.newAddress?.latLng?.longitude ?: 0.0,
                    1
                )
            val cityName = addresses[0].locality
            location = Location(
                RequireServiceActivity.newAddress?.address,
                cityName,
                RequireServiceActivity.newAddress?.latLng?.latitude,
                RequireServiceActivity.newAddress?.latLng?.latitude
            )

        }

        val bookingRequest = BookingRequest(
            scheduleTime,
            total_amount,
            review_detail_model?.provider_id,
            service_list,
            review_detail_model?.bookingType,
            review_detail_model?.price_type,
            location,
            review_detail_model?.task_details
        )

        Log.d("SendingRequest", "requestbody : $bookingRequest")
        model.hitDoBooking(user_obj?.token ?: "", bookingRequest)
    }

    override fun onResume() {
        super.onResume()

        //for guest user yhaa prr hai fields
        if(sharedPref.contains("user_obj")) {
            user_obj = MyApp.gson.fromJson(sharedPref.getString("user_obj", ""), Data::class.java)
            review_detail_model?.fullName =
                user_obj?.firstName + " " + user_obj?.lastName
            if ((!user_obj?.countryCode.isNullOrEmpty()) && (!user_obj?.phone.isNullOrEmpty())) {
                review_detail_model?.phone = user_obj?.countryCode + " " + user_obj?.phone
            } else {
                review_detail_model?.phone = "N/A"
            }

            tv_full_name_value.text = review_detail_model?.fullName
            tv_ph_no_value.text = review_detail_model?.phone
        } else {
            if ((!user_obj?.countryCode.isNullOrEmpty()) && (!user_obj?.phone.isNullOrEmpty())) {
                review_detail_model?.phone = user_obj?.countryCode + " " + user_obj?.phone
            } else {
                review_detail_model?.phone = "N/A"
            }
            tv_ph_no_value.text = review_detail_model?.phone
        }

        if (user_obj != null && user_obj?.token != null) {
            bankListViewModel.bankList(user_obj?.token ?: "")
        }

        updateVatAndTax()
    }

    override fun onServiceRemoved(position: Int) {

        if (list.size > 1)
        {
            list.removeAt(position)
            adapter!!.notifyDataSetChanged()
            scheduleTime?.removeAt(position)

            total_amount = 0.0
            service_list.clear()

            for (i in list.indices)
            {
                total_amount += list[i].rate
                service_list.add(list[i].id)
            }

            val taxAmount = (18.toFloat() / 100) * total_amount

            tvSubTotValue.text = "$${df.format(total_amount.toFloat() / 1.18)}"
            tvTaxValue.text = "$${df.format((total_amount.toFloat() / 1.18) * 0.18)}"
            tv_amount.text = "$"+df.format(total_amount)


            tv_amount.text = "$"+df.format(total_amount)

            if (review_detail_model?.price_type.equals("fixed", true))
                tvNormalAmountValue.text = "$"+df.format(total_amount)
            else
                tvNormalAmountValue.text = "Starting from $"+df.format(total_amount)
        }
        else
            showSnackBar(getString(R.string.minimumoneserviceisrequiredtocontinue))

    }
}
