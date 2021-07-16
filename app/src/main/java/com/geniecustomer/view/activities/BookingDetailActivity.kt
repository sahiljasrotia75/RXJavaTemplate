package com.geniecustomer.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.geniecustomer.R
import com.geniecustomer.adapters.BookingDetailAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.interfaces.ServiceRemoveListener
import com.geniecustomer.model.MessageItem
import com.geniecustomer.model.ReviewDetailModel
import com.geniecustomer.model.TaskObject
import com.geniecustomer.model.booking.BookingResponse
import com.geniecustomer.model.booking.Data
import com.geniecustomer.model.generateInvoiceResponseModel.GenerateInvoiceResponseModel
import com.geniecustomer.utils.CustomProgressDialog
import com.geniecustomer.view.fragments.BookingFragment
import com.geniecustomer.view.register.LoginActivity
import com.geniecustomer.viewmodels.BookingHistoryDetailViewModel
import com.geniecustomer.viewmodels.BookingViewModel
import com.geniecustomer.viewmodels.GenerateInvoiceViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_booking_detail.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.Serializable
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BookingDetailActivity : BaseActivity(), ServiceRemoveListener {

    var adapter: BookingDetailAdapter? = null
    var list: MutableList<TaskObject> = ArrayList()
    var after_payment = false
    var booking_details: Data? = null
    var _id = ""
    var service_list: MutableList<String> = ArrayList()
    val model by viewModel<BookingViewModel>()
    val historyViewModel by viewModel<BookingHistoryDetailViewModel>()
    var review_detail_model: ReviewDetailModel? = null
    private var isAgree: Boolean = true
    private var lockBtnClicking: Boolean = false
    private var progressDialog: CustomProgressDialog? = null
    private val getChatListViewModel by viewModel<GenerateInvoiceViewModel>()
    var df = DecimalFormat("0.00")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_detail)
        progressDialog = CustomProgressDialog(this)


//        list.add("Carpentry & Construction")
//        list.add("Delivery")
//        list.add("Electrical help")

        if (intent != null && intent.hasExtra("details")) {
            booking_details = intent.getSerializableExtra("details") as Data
            Log.e("amscksdkcksd", "${Gson().toJson(booking_details)}")
            //booking_details!!.id
//            historyViewModel.historyBookingDetail(user_obj?.token?:"",booking_details!!.id!!)

            Log.e("BADSHAH", "TAX TEST:${booking_details?.tax}")
            Log.e("BADSHAH", "TAX TEST:${booking_details?.amount}")
            Log.e("BADSHAH", "TAX TEST:${booking_details?.negotiateAmount}")



            getCurrentDate()

//            val taxAmount = if(booking_details?.priceType?.equals("Fixed")!!){
//                (taxInPercentage?.let { booking_details?.amount?.times(it) })?.div(100)
//            }else{
//                (taxInPercentage?.let { booking_details?.negotiateAmount?.times(it) })?.div(100)
//            }

            val taxInPercentage = booking_details?.tax
            tvNormalAmountValue.text = if (booking_details?.priceType?.equals("Fixed")!!) {
                "$" + df.format(booking_details?.amount)
            } else {
                tvNormalAmountnegi.text = "Negotiable Price"
                "Starting from $" + df.format(booking_details?.negotiateAmount)
            }

            val taxAmount: Double = if (booking_details?.priceType?.equals("Fixed")!!)
                (18.toFloat() / 100) * booking_details?.negotiateAmount!!
            else
                (18.toFloat() / 100) * booking_details?.amount!!

            Log.e("BADSHAH", "Tax Amount: $taxAmount")
//            tvTaxValue.text = "$${df.format(taxAmount)}"
//            tvTaxValue.text = "$${df.format(booking_details?.tax)}"
            if (booking_details?.priceType?.equals("Fixed")!!) {
//                tv_amount.text = "$"+df.format(booking_details?.amount)

                tvSubtotalValue.text = "$" + df.format(booking_details?.amount!! / 1.18)
                tv_amount.text = "$" + df.format(booking_details?.amount!!)
                tvTaxValue.text = "$${df.format((booking_details?.amount!! / 1.18) * 0.18)}"

            } else {
//                tv_amount.text = "$"+df.format(booking_details?.negotiateAmount)

                tvSubtotalValue.text = "$" + df.format(booking_details?.negotiateAmount!! / 1.18)
                tv_amount.text = "$" + df.format(booking_details?.negotiateAmount!!)
                tvTaxValue.text =
                    "$${df.format((booking_details?.negotiateAmount!! / 1.18) * 0.18)}"
            }


        }

//        Log.e("BADSHAH","TAX TEST:${booking_details?.tax}")
//        Log.e("BADSHAH","TAX TEST:${booking_details?.amount}")
//        Log.e("BADSHAH","TAX TEST:${booking_details?.negotiateAmount}")
//
//        val taxInPercentage = booking_details?.tax
//        tvNormalAmountValue.text = if(booking_details?.priceType?.equals("Fixed")!!){
//             "$"+df.format(booking_details?.amount)
//        }else{
//            tvNormalAmountnegi.text = "Negotiable Amount"
//           "$"+df.format(booking_details?.negotiateAmount)
//        }
//
//        val taxAmount = if(booking_details?.priceType?.equals("Fixed")!!){
//            (taxInPercentage?.let { booking_details?.amount?.times(it) })?.div(100)
//        }else{
//            (taxInPercentage?.let { booking_details?.negotiateAmount?.times(it) })?.div(100)
//        }
//        Log.e("BADSHAH","Tax Amount: $taxAmount")
//        tvTaxValue.text = "$${df.format(taxAmount)}"
//        val finalTotalAmount = if(booking_details?.priceType?.equals("Fixed")!!){
//            taxAmount?.let { booking_details?.amount?.plus(it) }
//        }else{
//            taxAmount?.let { booking_details?.negotiateAmount?.plus(it) }
//        }
//        Log.e("BADSHAH","finalAmount: $finalTotalAmount")
//        tv_amount.text = "$"+df.format(finalTotalAmount)

        model.bookingResponse()
            .observe(this, Observer<ApiResponse> { this.consumeResponse(it!!, 1) })
        historyViewModel.bookingResponse()
            .observe(this, Observer<ApiResponse> { this.consumeResponse(it!!, 2) })
        getChatListViewModel.liveDataCommonResponse()
            .observe(this, Observer<ApiResponse> { this.consumeResponse(it!!, 6) })

        if (intent != null && intent.hasExtra("_id")) {
            after_payment = false
            _id = intent.getStringExtra("_id")!!

            if (!user_obj?.token.isNullOrEmpty()) {
                isAgree = false
                historyViewModel.historyBookingDetail(user_obj?.token ?: "", _id)
            } else {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.sessionExpiredpleaselogin))
                    .setCancelable(false)
                    .setPositiveButton(
                        getString(R.string.ohk),
                        object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                navigateFinishAffinity(LoginActivity::class.java)
                            }
                        }).show()
            }
        }

        Log.e("BookingType1", "${review_detail_model?.bookingType}")
        adapter = BookingDetailAdapter(
            this,
            list as ArrayList<TaskObject>,
            1,
            this,
            booking_details?.bookingType
        )
        recycler_view_task.adapter = adapter
        recycler_view_task.layoutManager = LinearLayoutManager(this)

        /*if(intent!=null && intent.hasExtra("success")){
            setBookingData(1 , booking_details!!)
        }*/

    }


    /** Get Current Date */
    @SuppressLint("SetTextI18n")
    private fun getCurrentDate() {
        try {
            Log.e("clsdmlcsd", "cmksdmc")
            if (booking_details != null) {
                Log.e("clsdmlcsd", "cmksdmcsfcsdf")
                /*if (booking_details?.scheduleTime?.size != 0) {
                    Log.e(
                        "clsdmlcsd",
                        "cmksdmcdsggfgefe   ${booking_details?.scheduleTime?.get(0)?.date}"
                    )
                    val dateTime =
                        (context as BaseActivity).getDate(booking_details?.scheduleTime?.get(0)?.date)
                    tv_date_time_value.text =
                        dateTime + " ${booking_details?.scheduleTime?.get(0)?.startTime} - ${
                            booking_details?.scheduleTime?.get(0)?.endTime
                        }"
                } else {
                    Log.e("clsdmlcsd", "cmksdmcfsfsdfsd")
                    val dateTime =
                        (context as BaseActivity).getDateTime((booking_details?.createdAt!!))
                }*/
                tv_date_time_value.text =
                    (context as BaseActivity).getDateBooking((booking_details?.createdAt!!))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setBookingData(i: Int, booking_details: Data) {

        if (i == 1) {
            after_payment = true
            tv_bottom_btn.visibility = View.GONE
            tv_cancel_booking.visibility = View.VISIBLE
            if (booking_details.priceType?.equals("Fixed")!!) {
                tv_chat_btn.visibility = View.VISIBLE
            } else {
                tv_chat_btn.visibility = View.VISIBLE
            }
        } else {
            after_payment = false
            if (booking_details.priceType?.equals("Fixed")!!) {
                tv_chat_btn.visibility = View.VISIBLE
                if (booking_details.status.equals("Canceled")) {
                    //text_back_corner_cancel
                    tv_bottom_btn.visibility = View.GONE
                    BookingFragment.backfromPast = true
                } else if (booking_details.status.equals("Completed")) {
                    BookingFragment.backfromPast = true
                    clGenerateInvoice?.visibility = View.VISIBLE
                    clGenerateInvoice?.setOnClickListener {
                        if (!lockBtnClicking) {
                            lockBtnClicking = true
                            //Api Hitting:
                            progressDialog?.show()
                            lockBtnClicking = false
                            //Conversion  of Date:
                            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
                            originalFormat.timeZone = TimeZone.getDefault()
                            val targetTimeFormat = SimpleDateFormat("hh:mm a")
                            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
                            val time = originalFormat.parse(booking_details.acceptedDate!!)
                            val formattedTime = targetTimeFormat.format(time!!)
                            Log.e("BADSHAH", "formattedTime: $formattedTime")
                            //Fetching Current Date and Time:
                            val currentDate =
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                            val currentTime =
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            Log.e("BADSHAH", "Current time: $currentTime")
                            Log.e("BADSHAH", "currentDate: $currentDate")
                            val currentDataAndTime = "$currentDate $currentTime"

                            //API Hitting:
                            val jsonData = JSONObject()
                            jsonData.put("bookingId", booking_details.id!!)
                            jsonData.put("acceptedDate", formattedTime)
                            jsonData.put("currentDate", currentDataAndTime)
                            val requestBody = RequestBody.create(
                                MediaType.parse("application/json"),
                                jsonData.toString()
                            )

                            Log.e("Generate Invoice Data","$jsonData")
//                        getChatListViewModel.generateInvoice(userToken,bookingID,formattedTime)
                            getChatListViewModel.generateInvoice(user_obj?.token!!, requestBody)
                            //Api Hitting.
//                            getChatListViewModel.generateInvoice(user_obj?.token!!, booking_details.id!!,formattedTime)
                        }
                    }

                    if (booking_details.isUserRated!!)
                        tv_bottom_btn.visibility = View.GONE
                    else
                        tv_bottom_btn.visibility = View.VISIBLE
                } else if (booking_details.status.equals("Pending")) {
                    tv_cancel_booking.visibility = View.VISIBLE
                    tv_bottom_btn.visibility = View.GONE
                } else {
                    tv_cancel_booking.visibility = View.GONE
                    tv_bottom_btn.visibility = View.GONE
                }
            } else {
                if (booking_details.status.equals("Canceled")) {
                    BookingFragment.backfromPast = true
                    tv_bottom_btn.visibility = View.GONE
                } else if (booking_details.status.equals("Completed")) {
                    BookingFragment.backfromPast = true
                    clGenerateInvoice?.visibility = View.VISIBLE
                    clGenerateInvoice?.setOnClickListener {
                        if (!lockBtnClicking) {
                            lockBtnClicking = true
                            //Api Hitting:
                            progressDialog?.show()
                            lockBtnClicking = false
                            //Conversion  of Date:
                            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
                            originalFormat.timeZone = TimeZone.getDefault()
                            val targetTimeFormat = SimpleDateFormat("hh:mm a")
                            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
                            val time = originalFormat.parse(booking_details.acceptedDate)
                            val formattedTime = targetTimeFormat.format(time!!)
                            Log.e("BADSHAH", "formattedTime: $formattedTime")
                            //Fetching Current Date and Time:
                            val currentDate =
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                            val currentTime =
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            Log.e("BADSHAH", "Current time: $currentTime")
                            Log.e("BADSHAH", "currentDate: $currentDate")
                            val currentDataAndTime = "$currentDate $currentTime"

                            //API Hitting:
                            val jsonData = JSONObject()
                            jsonData.put("bookingId", booking_details.id!!)
                            jsonData.put("acceptedDate", formattedTime)
                            jsonData.put("currentDate", currentDataAndTime)

                            Log.e(
                                "BADSHAH",
                                "invoice request: ${user_obj?.token!!} ----- $jsonData  userid: - ${user_obj!!.id}"
                            )

                            val requestBody = RequestBody.create(
                                MediaType.parse("application/json"),
                                jsonData.toString()
                            )

                            Log.e("Generate Invoice Data","$jsonData")
//                        getChatListViewModel.generateInvoice(userToken,bookingID,formattedTime)
                            getChatListViewModel.generateInvoice(user_obj?.token!!, requestBody)
                            //Api Hitting.
//                            getChatListViewModel.generateInvoice(user_obj?.token!!, booking_details.id!!,formattedTime)
                        }
                    }

                    if (booking_details.isUserRated!!)
                        tv_bottom_btn.visibility = View.GONE
                    else
                        tv_bottom_btn.visibility = View.VISIBLE
                } else if (booking_details.status.equals("Pending")) {

                    tv_chat_btn.visibility = View.VISIBLE
                    tv_cancel_booking.visibility = View.VISIBLE
                    tv_bottom_btn.visibility = View.GONE
                } else if (booking_details.status.equals("Accepted")) {

                    tv_chat_btn.visibility = View.VISIBLE
                    tv_cancel_booking.visibility = View.GONE
                    tv_bottom_btn.visibility = View.GONE
                } else if (booking_details.status.equals("Ongoing")) {
                    tv_chat_btn.visibility = View.VISIBLE
                    tv_cancel_booking.visibility = View.GONE
                    tv_bottom_btn.visibility = View.GONE
                } else {
                    tv_chat_btn.visibility = View.GONE
                    tv_cancel_booking.visibility = View.GONE
                    tv_bottom_btn.visibility = View.GONE
                }


                Log.e("dsnkvdsnlv", "${booking_details.status}")
            }
        }

        tv_status.text = booking_details.status

        if (booking_details.status.equals("Accepted") || booking_details.status.equals("Pending") || booking_details.status.equals(
                "Ongoing"
            )
        ) {
            tv_status.background = resources.getDrawable(R.drawable.text_back_corner)
        } else if (booking_details.status.equals("Completed")) {
            BookingFragment.backfromPast = true
            tv_status.background = resources.getDrawable(R.drawable.text_back_corner_completed)

            if (booking_details.isUserCompleted != null) {

                if (!booking_details.isUserCompleted!!)
                    showAgreePopup(user_obj?.token ?: "", booking_details.id ?: "")
            }

        } else if (booking_details.status.equals("Canceled")) {
            BookingFragment.backfromPast = true
            tv_status.background = resources.getDrawable(R.drawable.text_back_corner_cancel)
        }

        if (booking_details.bookingType.equals("Schedule")) {
            if (booking_details.status.equals("Pending"))
                tv_reschedule_booking.visibility = View.VISIBLE
        }

        /**@Mukesh @28 nov*/
        if (booking_details.sentTo != null) {
            tv_order_id.text = booking_details.sentTo.name.toString()
        }
        iv_back.setImageDrawable(resources.getDrawable(R.drawable.ic_circle_cross))

        tv_service_type.text = booking_details.services.get(0)?.service?.category?.name ?: ""
        val fullName = user_obj?.firstName ?: "" + " " + user_obj?.lastName ?: ""
        tv_full_name_value.text = fullName
        tv_ph_no_value.text = user_obj?.phone ?: ""
        tv_address_value.text = booking_details.location?.address
        tv_details_value.text = booking_details.desc
        tv_service_man_name.text = booking_details.sentTo?.name
        tv_service_man_address.text = booking_details.sentTo?.address
        tv_review.text = "" + booking_details.ratingCount + " Reviews"
        rating_bar.rating = booking_details.avgRating ?: 0f

        tvNormalAmountValue.text = if (booking_details.priceType.equals("Fixed")) {
            "$" + df.format(booking_details.amount)
        } else {
            tvNormalAmountnegi.text = "Negotiable Price"
            "Starting from $" + df.format(booking_details.negotiateAmount)
        }


        if (booking_details.priceType.equals("Fixed")) {
            tvSubtotalValue.text = "$" + df.format(booking_details.amount!! / 1.18)
            tv_amount.text = "$" + df.format(booking_details.amount)
            tvTaxValue.text = "$${df.format((booking_details.amount / 1.18) * 0.18)}"
        } else {
            tvSubtotalValue.text = "$" + df.format(booking_details.negotiateAmount!! / 1.18)
            tv_amount.text = "$" + df.format(booking_details.negotiateAmount)
            tvTaxValue.text = "$${df.format((booking_details.negotiateAmount / 1.18) * 0.18)}"
        }

        if (rating_bar.rating > 4f) {
            tv_review_type.text = getString(R.string.great)
        } else if (rating_bar.rating > 3f) {
            tv_review_type.text = getString(R.string.good)
        } else if (rating_bar.rating > 2f) {
            tv_review_type.text = getString(R.string.okay)
        } else if (rating_bar.rating > 1f) {
            tv_review_type.text = getString(R.string.bad)
        } else if (rating_bar.rating > 0.1f) {
            tv_review_type.text = getString(R.string.terrible)
        } else {
            tv_review_type.text = ""
        }

        Log.e("dsmvsdkmvkcsd", "${booking_details.services}")

        //booking_details?.services?.get(0)
        if (booking_details.scheduleTime.size == 0) {
            for (i in 0 until booking_details.services.size) {
                val taskObject = TaskObject(
                    booking_details.services.get(i)?.service?.id ?: "",
                    booking_details.services.get(i)?.service?.name ?: "",
                    booking_details.services.get(i)?.service?.desc ?: "",
                    booking_details.services.get(i)?.service?.price ?: 0.0,
                    booking_details.services.get(i)?.service?.priceType ?: "",
                    booking_details.services.get(i)?.service?.payType ?: ""
                )
                list.add(taskObject)
                //total_amount += list.get(i).rate
                service_list.add(list.get(i).id)
            }
        } else {
            for (i in booking_details.services.indices) {
                val taskObject = TaskObject(
                    booking_details.services.get(i)?.service?.id ?: "",
                    booking_details.services.get(i)?.service?.name ?: "",
                    booking_details.services.get(i)?.service?.desc ?: "",
                    booking_details.services.get(i)?.service?.price ?: 0.0,
                    booking_details.services.get(i)?.service?.priceType ?: "",
                    booking_details.services.get(i)?.service?.payType ?: "",
                    booking_details.scheduleTime.get(i)?.startTime + " - " + booking_details.scheduleTime.get(
                        i
                    )?.endTime
                )

                list.add(taskObject)
                //total_amount += list.get(i).rate
                service_list.add(list.get(i).id)
            }
        }


        if (booking_details.priceType.equals("Fixed")) {
            tv_paid.text = "Paid"
        } else {
            if (booking_details.isPaymentDone!!) {
                tv_paid.text = "Paid"
            } else {
                tv_paid.text = "Payable Amount"
            }
        }


//        if(booking_details.negotiateAmount == 0.0)
//            tv_amount.setText("$"+booking_details.amount)
//        else
//            tv_amount.setText("$"+booking_details.negotiateAmount)

        Log.e("BookingType", "${review_detail_model?.bookingType}")
        adapter = BookingDetailAdapter(
            this,
            list as ArrayList<TaskObject>,
            1,
            this,
            booking_details.bookingType
        )
        recycler_view_task.adapter = adapter
        recycler_view_task.layoutManager = LinearLayoutManager(this)

    }

    private fun showAgreePopup(userToken: String, bookingid: String) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.serviceproviderhascompletetheorderareyouagree))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.agree),
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        isAgree = true
                        historyViewModel.isUserComplete(userToken, bookingid)
                    }
                })
            /*.setNegativeButton(R.string.no, null)*/
            .show()
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

    private fun renderSuccessResponse(response: JsonElement, type: Int) {
        if (!response.isJsonNull) {

            Log.e("mskcmklas", "$response")

            val data: String = MyApp.gson.toJson(response)

            if (type == 1) {
                val signupResponse = MyApp.gson.fromJson(data, BookingResponse::class.java)
                if (signupResponse.success != null && signupResponse.success.toString()
                        .equals("TRUE", true)
                ) {
                    showToast("Cancelled booking successfully")
                    if (after_payment) {
                        startActivity(
                            Intent(
                                this,
                                HomeActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        finish()
                    } else {
                        finish()
                    }

                } else {
                    signupResponse.message?.let { showSnackBar(it) }
                }


            } else if (type == 6) {
                try {
                    val dataModel = Gson().fromJson(data, GenerateInvoiceResponseModel::class.java)
                    if (dataModel?.success == true) {
                        progressDialog?.dismiss()
                        lockBtnClicking = false
                        val pdfURL = dataModel.data.invoice
                        Log.e(
                            "BADSHAH",
                            "Generated Invoice url in pdf:" + "" + Urls.BASE_URL + "" + pdfURL
                        )
                        if (pdfURL.isNotEmpty() && pdfURL.contains("invoice/")) {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://docs.google.com/viewer?url=" + Urls.BASE_URL + "" + pdfURL)
                            )
                            context.startActivity(browserIntent)
                        }
                    }
                } catch (e: Exception) {
                    lockBtnClicking = false
                    progressDialog?.dismiss()
                    Log.e("BADSHAH", "Exception Occurred in type 6 Generating Invoice:$e")
                }
            } else {
                Log.e("reponse ", " $response")
                try {

                    val signupResponse = MyApp.gson.fromJson(data, BookingResponse::class.java)

                    if (isAgree)
                    {
                        booking_details?.isUserCompleted = true
                    }
                    else
                    {
                        booking_details = Data()
                        booking_details = signupResponse.data


                        if (intent != null && intent.hasExtra("success")) {
                            setBookingData(1, booking_details!!)
                        } else {
                            setBookingData(2, booking_details!!)
                        }

                        review_detail_model = ReviewDetailModel()
                        review_detail_model?.provider_id = signupResponse.data?.sentTo?.id.toString()
                        review_detail_model?.provider_name = signupResponse.data?.sentTo?.name!!
                        review_detail_model?.provider_address =
                            signupResponse.data.sentTo.address ?: ""
                        review_detail_model?.provider_rating =
                            signupResponse.data.sentTo.avgRating ?: 0f
                        review_detail_model?.provider_review_num =
                            signupResponse.data.sentTo.ratingList?.size!!
                        review_detail_model?.task_details = signupResponse.data.desc!!
                    }
                } catch (e: Exception) {
                    Log.e("EXCEPTION ", " " + e)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.iv_back -> {
                if (isTaskRoot) {
                    startActivity(
                        Intent(
                            this,
                            HomeActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .putExtra("type", "fromChat")
                    )
                    finishAffinity()
                } else {
                    if (after_payment) {
                        startActivity(
                            Intent(
                                this,
                                HomeActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        finish()
                    } else
                        finish()
                }
            }
            R.id.tv_bottom_btn -> {
                if (booking_details?.isUserCompleted != null) {
                    if (booking_details?.isUserCompleted!!) {
                        val nvi_intent = Intent(this, RatingActivity::class.java)
                        nvi_intent.putExtra("bookingId", booking_details?.id ?: "")
                        nvi_intent.putExtra("orderId", booking_details?.orderId ?: "")
                        nvi_intent.putExtra("provider", booking_details?.sentTo?.id ?: "")
                        nvi_intent.putExtra("providerImage", booking_details!!.sentTo!!.image ?: "")
                        nvi_intent.putExtra("providerName", booking_details!!.sentTo!!.name ?: "")
                        startActivityForResult(nvi_intent, 10)
                    } else
                        showSnackBar(getString(R.string.torateserviceproviderpleaseagree))
                }

            }
            R.id.tv_cancel_booking -> {

                if (booking_details?.scheduleTime?.size ?: 0 <= 0) {
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.cancel_ur_booking))
                        .setPositiveButton(
                            getString(R.string.yes),
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    val jsonData = JSONObject()
                                    jsonData.put("id", booking_details?.id ?: "")
                                    jsonData.put("status", "Canceled")
                                    jsonData.put("reason", "testing")

                                    val requestBody = RequestBody.create(
                                        MediaType.parse("application/json"),
                                        jsonData.toString()
                                    )
                                    model.hitCancelBookingRequest(
                                        user_obj?.token ?: "",
                                        requestBody
                                    )
                                }
                            }).setNegativeButton(R.string.no, null).show()
                } else {
                    if (DateUtils.isToday(booking_details?.scheduleTime?.get(0)?.dateTime_in_miliseconds!!)) {
                        val currentTime = convertDateUniversalForMilliseconds(
                            "" + System.currentTimeMillis(),
                            "HH:mm"
                        )
                        val currentTimeArray = currentTime.split(":")
                        val serviceTimeArray =
                            booking_details?.scheduleTime?.get(0)?.startTime?.split(":")
                        val currentMinutes = (currentTimeArray[0].toInt() * 60).plus(
                            currentTimeArray[1].toInt()
                        )
                        val serviceTimeMinutes = ((serviceTimeArray?.get(0)?.toInt()
                            ?: 0) * 60).plus((serviceTimeArray?.get(1)?.toInt() ?: 0))

                        Log.e(
                            "response",
                            "timedifference " + serviceTimeMinutes.minus(currentMinutes)
                        )

                        if (serviceTimeMinutes.minus(currentMinutes) > 30) {
                            AlertDialog.Builder(this)
                                .setMessage(getString(R.string.cancel_ur_booking))
                                .setPositiveButton(
                                    getString(R.string.yes),
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(p0: DialogInterface?, p1: Int) {
                                            val jsonData = JSONObject()
                                            jsonData.put("id", booking_details?.id ?: "")
                                            jsonData.put("status", "Canceled")
                                            jsonData.put("reason", "testing")

                                            val requestBody = RequestBody.create(
                                                MediaType.parse("application/json"),
                                                jsonData.toString()
                                            )
                                            model.hitCancelBookingRequest(
                                                user_obj?.token ?: "",
                                                requestBody
                                            )
                                        }
                                    }).setNegativeButton(R.string.no, null).show()
                        } else {
                            showSnackBar("You can cancel the bookings only 30 minutes before.")
                        }

                    } else {
                        AlertDialog.Builder(this)
                            .setMessage(getString(R.string.cancel_ur_booking))
                            .setPositiveButton(
                                getString(R.string.yes),
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        val jsonData = JSONObject()
                                        jsonData.put("id", booking_details?.id ?: "")
                                        jsonData.put("status", "Canceled")
                                        jsonData.put("reason", "testing")

                                        val requestBody = RequestBody.create(
                                            MediaType.parse("application/json"),
                                            jsonData.toString()
                                        )
                                        model.hitCancelBookingRequest(
                                            user_obj?.token ?: "",
                                            requestBody
                                        )
                                    }
                                }).setNegativeButton(R.string.no, null).show()
                    }

                }

            }
            R.id.tv_chat_btn -> {

                /* if (booking_details?.status.equals("Pending")) {
                    showSnackBar("Booking not accepted yet")
                    return
                }*/
                (context as BaseActivity).startActivity(
                    Intent(context, ConversationActivity::class.java)
                        .putExtra(
                            "messageItem",
                            MessageItem(
                                booking_details?.sentTo?.id ?: "",
                                user_obj?.id,
                                "",
                                booking_details?.id ?: "",
                                booking_details?.sentTo?.name ?: "",
                                booking_details?.sentTo?.image ?: "",
                                booking_details?.status ?: ""
                            )
                        )
                )

//                 showToast("In Development...")
            }

            R.id.tv_reschedule_booking -> {
                val task_list: MutableList<TaskObject> = ArrayList()
                for (i in 0 until booking_details!!.services.size) {
                    var taskObject = TaskObject(
                        booking_details?.services?.get(i)?.service?.id ?: "",
                        booking_details?.services?.get(i)?.service?.name ?: "",
                        booking_details?.services?.get(i)?.service?.desc ?: "",
                        booking_details?.services?.get(i)?.service?.price ?: 0.0,
                        booking_details?.services?.get(i)?.service?.priceType ?: "",
                        booking_details?.services?.get(i)?.service?.payType ?: ""
                    )
                    task_list.add(taskObject)
                }
                review_detail_model?.price_type = booking_details?.priceType!!
                review_detail_model?.bookingType = booking_details?.bookingType!!
                review_detail_model?.task_list = task_list
                review_detail_model?.bookingId = booking_details?.id!!

                Log.e("DATA", " dsfd " + review_detail_model)
                try {
                    if (booking_details?.avgRating != null) {
                        review_detail_model?.provider_rating =
                            booking_details?.avgRating!!.toFloat()
                    }


                    if (booking_details?.ratingCount != null) {
                        review_detail_model?.provider_review_num = booking_details?.ratingCount!!
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val intent = Intent(this, RequireServiceActivity::class.java)
                intent.putExtra("review_model", review_detail_model as Serializable)
                intent.putExtra("providerCity", review_detail_model?.provider_city)
                intent.putExtra("booking_id", booking_details?.id!!)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            startActivity(
                Intent(this, HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra("type", "fromChat")
            )
            finishAffinity()
        } else {
            if (after_payment) {
                startActivity(
                    Intent(
                        this,
                        HomeActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                finish()
            } else
                finish()
        }

    }

    private fun convertDateUniversalForMilliseconds(x: String, format: String): String {
        var formatter = SimpleDateFormat()
        formatter = SimpleDateFormat(format)
        val milliSeconds = java.lang.Long.parseLong(x)
        println(milliSeconds)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10) {
                tv_bottom_btn.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver1, IntentFilter("notification"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver1)
    }

    private val receiver1 = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e("recierv ", " manana")
            if (intent.action!!.equals("notification", ignoreCase = true)) {
                if (intent.hasExtra("_id")) {
                    after_payment = true
                    Log.e("data ", " " + intent.getStringExtra("_id"))
                    /* val _id = intent.getStringExtra("_id")
                     historyViewModel.historyBookingDetail(user_obj?.token?:"",_id)*/
                }
            }
        }
    }

    override fun onServiceRemoved(position: Int) {

    }
}
