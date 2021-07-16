package com.geniecustomer.view.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.geniecustomer.R
import com.geniecustomer.adapters.ConversationAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.ConnectivityReceiver
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.MessageItem
import com.geniecustomer.model.chat_history.ChatHistoryResponse
import com.geniecustomer.model.chat_history.DataItem
import com.geniecustomer.model.initializePaymentGateway.InitializePaymentGatewayResponseModel
import com.geniecustomer.utils.GlideApp
import com.geniecustomer.view.activities.urlViewer.UrlViewer
import com.geniecustomer.viewmodels.BankListViewModel
import com.geniecustomer.viewmodels.BookingHistoryDetailViewModel
import com.geniecustomer.viewmodels.BookingViewModel
import com.geniecustomer.viewmodels.InitializeGatewayViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import genie.service.provider.activities.addBankDetail.getBanksListResponseModel.BankListResponseModel
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_conversation.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import java.text.DecimalFormat
import javax.net.ssl.*


class ConversationActivity : BaseActivity(), ConnectivityReceiver.ConnectivityReceiverListener,
    PopupMenu.OnMenuItemClickListener {

    var adapter: ConversationAdapter? = null
    var main_list: MutableList<DataItem> = ArrayList()
    var from_booking: Boolean = false
    var messageItem: MessageItem? = null
    lateinit var socket: Socket
    lateinit var onConnect: Emitter.Listener
    lateinit var onNewMessage: Emitter.Listener
    lateinit var onDisconnect: Emitter.Listener
    lateinit var getOffer: Emitter.Listener
    lateinit var error: Emitter.Listener
    lateinit var respondOffer: Emitter.Listener
    private val bankListViewModel by viewModel<BankListViewModel>()
    var finalTotalAmount = 0.0
    var taxInPercentage = 0
    var df = DecimalFormat("0.00")

    //Payment Details:
    var paymentAmount = 0.0
    var paymentMsgID = ""
    var paymentNegotiableAmount = 0.0
    var modelStatus = ""
    private val initializeGatewayViewModel by viewModel<InitializeGatewayViewModel>()
    // lateinit var onRoomJoined: Emitter.Listener

    var bookingObject: JsonObject? = null
    var bookingId = ""
    var providerId = ""
    var image = ""
    var amount = ""
    var name = ""
    var bookingStatus = ""
    var negotiateAmount = ""
    var roomJoined = false

    var unsendMessageList: MutableList<MessageItem>? = ArrayList()
    val cancelBookingModel by viewModel<BookingViewModel>()
    val model by viewModel<BookingHistoryDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        //this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        /*try{
            socket = IO.socket(Urls.SOCKET_URL)

        }catch (e : Exception){
            Log.e("Socket" ," Exception ")
        }
*/

        setSocketUrl()


        model.bookingResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!, 1)
        })

        cancelBookingModel.bookingResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!, 2)
        })

        initializeGatewayViewModel.liveDataCommonResponse()
            .observe(this, Observer<ApiResponse> { this.consumeResponse(it!!, 6) })
        bankListViewModel.liveDataCommonResponse()
            .observe(this, Observer<ApiResponse> { this.consumeResponse(it!!, 7) })
        bankListViewModel.bankList(user_obj?.token!!)


        // socket.on("getMessage", myMessage)

        if (intent != null && intent.hasExtra("messageItem")) {
            messageItem = intent.getSerializableExtra("messageItem") as MessageItem
            Log.e("boking", " inner ")
            bookingId = messageItem?.bookingId ?: ""
            providerId = messageItem?.receiver ?: ""
            image = messageItem?.image ?: ""
            name = messageItem?.name ?: ""
            bookingStatus = messageItem?.bookingStatus ?: ""
        } else if (intent != null && intent.hasExtra("notification")) {
            bookingId = intent.getStringExtra("bookingId")!!
            providerId = intent.getStringExtra("providerId")!!
            image = intent.getStringExtra("image")!!
            name = intent.getStringExtra("name")!!
            bookingStatus = intent.getStringExtra("bookingStatus")!!
        }

        Log.e("boking", " " + bookingId)
        Log.e("bookingStatus", " " + bookingStatus)

        GlideApp.with(context).load(Urls.BASE_URL + image)
            .placeholder(R.drawable.dummy_placeholder)
            .error(R.drawable.dummy_placeholder)
            .into(iv_profyl)
        tv_name.text = name

        if (user_obj != null)
            model.getChat(user_obj?.token!!, bookingId)


        adapter = ConversationAdapter(this, main_list as ArrayList<DataItem>)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.isNestedScrollingEnabled = false
        listerner()

        if (isNetworkConnected()) {
            if (!socket.connected()) {
                socket.connect()
                Log.e("Socket", " socket ${socket.connected()}")
            }
        }

        initializeSockets()

      //  checkKeyboard()

        Log.e("samlsdm", "asdmasdm  ${bookingStatus}")
        if (bookingStatus.equals("Canceled") || bookingStatus.equals("Completed")) {
            rtCardNumber.visibility = View.GONE
            send_btn.visibility = View.GONE
            bottom_view.visibility = View.GONE
            tv_days.visibility = View.GONE
            tv_wait_for_accept.visibility = View.GONE
        }
        else if(bookingStatus.equals("Pending",true)){
            rtCardNumber.visibility = View.GONE
            send_btn.visibility = View.GONE
            bottom_view.visibility = View.GONE
            tv_days.visibility = View.GONE
            tv_wait_for_accept.visibility = View.VISIBLE
        }
    }

    private fun setSocketUrl() {

        try {
            val socketUrl: String = Urls.SOCKET_URL
            val hostnameVerifier: HostnameVerifier =
                HostnameVerifier { hostname, session -> true }
            val trustAllCerts: Array<TrustManager> =
                arrayOf<TrustManager>(object : X509TrustManager {
                    override
                    fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override
                    fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOfNulls(0)
                    }
                }
                )
            val trustManager: X509TrustManager = trustAllCerts[0] as X509TrustManager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, null)
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .hostnameVerifier(hostnameVerifier)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build()
            val opts = IO.Options()
            opts.callFactory = okHttpClient
            opts.webSocketFactory = okHttpClient
            socket = IO.socket(socketUrl, opts)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }

    }

    private fun checkKeyboard() {
        root_layout.viewTreeObserver.addOnGlobalLayoutListener {
            val idiff = root_layout.rootView.height - root_layout.height
            if (idiff > 100) {
                if (main_list.size > 0)
                    recycler_view.scrollToPosition(main_list.size - 1)
            }
        }
    }

    fun initializeSockets() {

        onConnect = object : Emitter.Listener {
            override fun call(vararg args: Any?) {
                runOnUiThread(object : Runnable {
                    override fun run() {

                        Log.e("onConnect", "onConnect " + bookingObject)
                        var json = JSONObject()
                        json.put("bookingId", bookingId)
                        socket.emit("joinRoom", json)

                        Handler().postDelayed({

                            roomJoined = true
                            if (unsendMessageList?.size ?: 0 > 0) {
                                for (i in 0 until unsendMessageList?.size!!)
                                    socket.emit(
                                        "sendMessage",
                                        JSONObject(MyApp.gson.toJson(unsendMessageList?.get(i)))
                                    )
                            }
                            unsendMessageList?.clear()
                        }, 500)
                    }

                })
            }

        }
        onDisconnect = object : Emitter.Listener {
            override fun call(vararg args: Any?) {
                runOnUiThread(object : Runnable {
                    override fun run() {
                        Log.e("onDisconnect", "onDisconnect")
                        //showMessage(data.toString())
                        //addUser = false
                        roomJoined = false
                        socket.connect()
                        Log.e("Socket", " socket ${socket.connected()}")
                    }

                })
            }
        }

//        onRoomJoined = object : Emitter.Listener {
//            override fun call(vararg args: Any?) {
//                runOnUiThread(object : Runnable {
//                    override fun run() {
//                        Log.e("room_joined","true")
//                        roomJoined = true
//                        if(unsendMessageList?.size?:0>0){
//                            for(i in 0 until unsendMessageList?.size!!)
//                                socket.emit("sendMessage",JSONObject(MyApp.gson.toJson(unsendMessageList?.get(i))))
//                        }
//                        unsendMessageList?.clear()
//                    }
//
//                })
//            }
//
//        }

        onNewMessage = object : Emitter.Listener {
            override fun call(vararg args: Any?) {
                runOnUiThread(object : Runnable {
                    override fun run() {

                        //showToast("cjsdbcjsdbc")

                        val data: JSONObject = args[0] as JSONObject

                        Log.e("onNewMe ", "message " + data)
                        val chat_message =
                            MyApp.gson.fromJson(data.toString(), DataItem::class.java)
                        if (chat_message?.type.equals("text")) {
                            if (chat_message?.sender?._id.equals(user_obj?.id)) {
                                chat_message.typeText = 0
                                main_list.add(chat_message)
                            } else {
                                chat_message.typeText = 1
                                main_list.add(chat_message)
                            }
                        } else {
                            chat_message.typeText = 2
                            main_list.add(chat_message)
                        }
                        adapter?.notifyDataSetChanged()

                        if (main_list.size > 0)
                            recycler_view.scrollToPosition(main_list.size - 1)

                    }
                })
            }
        }



        getOffer = object : Emitter.Listener {
            override fun call(vararg args: Any?) {
                runOnUiThread(object : Runnable {
                    override fun run() {

                        //showToast("cjsdbcjsdbc")
                        Log.e("on", "message")

                        val data: JSONObject = args[0] as JSONObject

                        Log.e("onNewMe ", "message " + data)
                        amount = data.getJSONObject("data").getString("amount")
                        negotiateAmount = data.getJSONObject("data").getString("negotiateAmount")

                        val chat_message = DataItem()

                        chat_message.type = "offer"
                        chat_message.amount = amount.toDouble()
                        chat_message.negotiateAmount = negotiateAmount.toDouble()
                        main_list.add(chat_message)
                        adapter?.notifyDataSetChanged()

                        if (main_list.size > 0)
                            recycler_view.scrollToPosition(main_list.size - 1)

                    }
                })
            }
        }


        error = object : Emitter.Listener {
            override fun call(vararg args: Any?) {
                runOnUiThread(object : Runnable {
                    override fun run() {
                        Log.e("Error", "Error")
                        //showMessage(data.toString())
                        //addUser = false
                    }

                })
            }
        }

        Log.e("sdsds", " " + "getOffer_" + bookingId)
        socket.on("getOffer_" + bookingId, getOffer)
        socket.on("getMessage", onNewMessage)
        socket.on("error", error)
        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
    }

    fun listerner() {

        adapter!!.setOnItemClick(object : ConversationAdapter.ItemClick {
            @SuppressLint("SetTextI18n")
            override fun onItemClick(position: Int, type: Int) {
                val jsonObject = JSONObject()
                for (i in main_list.indices) {
                    val model = main_list.get(i)
                    if (model.id.equals(main_list.get(position).id)) {
                        if (type == 1) {
                            paymentAmount = main_list.get(position).amount
                            paymentMsgID = main_list.get(position).id!!
                            paymentNegotiableAmount = main_list.get(position).negotiateAmount
//                            val taxAmount = (main_list.get(position).negotiateAmount.times(taxInPercentage)).div(100)
                            val taxAmount =
                                (18.toFloat() / 100) * main_list.get(position).negotiateAmount
                            tvTaxAmount.text =
                                "$" + df.format((main_list.get(position).negotiateAmount / 1.18) * 0.18)
                            tvSubtotalAmount.text =
                                "$" + df.format(main_list.get(position).negotiateAmount / 1.18)
                            tvNormalAmountValue.text = "$" + df.format(paymentNegotiableAmount)
                            clBlackOut.visibility = View.VISIBLE
                            Log.e("BADSHAH", "Tax Amount: $taxAmount")
//                            finalTotalAmount = main_list.get(position).negotiateAmount.plus(taxAmount)
                            finalTotalAmount = main_list.get(position).negotiateAmount
                            payableAmount.text = "$" + df.format(finalTotalAmount)
                            Log.e("BADSHAH", "finalAmount: $finalTotalAmount")
                            Log.e("BADSHAH", "booking ID:$bookingId")
                            //API Hitting:
                            val jsonData = JSONObject()
                            jsonData.put("MerchID", "4171")
                            jsonData.put("MerchPass", "nH04ecy5WFmOuT")
                            jsonData.put(
                                "XMLParam",
                                "<Transaction hash=\"password\"><ProfileID>EF459CE6111C4C7387C29A902476A737</ProfileID><Value>$finalTotalAmount</Value><Curr>978</Curr><Lang>en</Lang><ORef>$bookingId</ORef><ClientAcc>Test_Acc</ClientAcc><MobileNo>123456</MobileNo><Email>joedoe@mail.com</Email><RedirectionURL>${Urls.BASE_URL}api/v1/public/paymentCallback</RedirectionURL><UDF1/><UDF2/><UDF3/><FastPay><CardRestrict/><ListAllCards>Last/All</ListAllCards><NewCard1Try/><NewCardOnFail/><PromptCVV/><PromptExpiry/></FastPay><ExtendedErr/><ActionType>1</ActionType><WYCB/><TEST/><status_url urlEncode=\"true\">${Urls.BASE_URL}api/v1/public/statusUrl</status_url><RegName>APCO Test</RegName></Transaction>"
                            )
                            /*jsonData.put(
                                "XMLParam",
                                "<Transaction hash=\"password\"><ProfileID>EF459CE6111C4C7387C29A902476A737</ProfileID><Value>$finalTotalAmount</Value><Curr>978</Curr><Lang>en</Lang><ORef>$bookingId</ORef><ClientAcc>${user_obj?.firstName} ${user_obj?.lastName}</ClientAcc><MobileNo>${user_obj?.countryCode + "" + user_obj?.phone}</MobileNo><Email>${user_obj?.email}</Email><RedirectionURL>${Urls.BASE_URL}api/v1/public/paymentCallback</RedirectionURL><UDF1/><UDF2/><UDF3/><FastPay><CardRestrict/><ListAllCards>Last/All</ListAllCards><NewCard1Try/><NewCardOnFail/><PromptCVV/><PromptExpiry/></FastPay><ExtendedErr/><ActionType>1</ActionType><WYCB/><status_url urlEncode=\"true\">${Urls.BASE_URL}api/v1/public/statusUrl</status_url></Transaction>"
                            )*/
//                            jsonData.put("XMLParam", "<Transaction hash=\"password\"><ProfileID>EF459CE6111C4C7387C29A902476A737</ProfileID><Value>$finalTotalAmount</Value><Curr>978</Curr><Lang>en</Lang><ORef>$bookingId</ORef><ClientAcc>${""+user_obj?.firstName+""+?user_obj?.lastName}</ClientAcc><MobileNo>${""+user_obj?.countryCode+""+user_obj?.phone}</MobileNo><Email>${user_obj?.email?:""}</Email><RedirectionURL>${Urls.BASE_URL}api/v1/public/paymentCallback</RedirectionURL><UDF1/><UDF2/><UDF3/><FastPay><CardRestrict/><ListAllCards>Last/All</ListAllCards><NewCard1Try/><NewCardOnFail/><PromptCVV/><PromptExpiry/></FastPay><ExtendedErr/><ActionType>1</ActionType><WYCB/><status_url urlEncode=\"true\">${Urls.BASE_URL}api/v1/public/statusUrl</status_url></Transaction>")
                            val requestBody = RequestBody.create(
                                MediaType.parse("application/json"),
                                jsonData.toString()
                            )
                            tvContinue.setOnClickListener {
                                Log.e("BADSHAH", "requestBody :$jsonData")
                                initializeGatewayViewModel.initialGateway(requestBody)
                            }
                            tvCancel.setOnClickListener {
                                clBlackOut.visibility = View.GONE
                            }

                            //Api Hitting.

//                            jsonObject.put("status","Accepted")
                            model.status = "Accepted"
                        } else {
                            model.status = "Rejected"
                            jsonObject.put("status", "Rejected")
                            jsonObject.put("id", bookingId)
                            jsonObject.put("amount", main_list.get(position).amount)
                            jsonObject.put("msgId", main_list.get(position).id)
                            jsonObject.put(
                                "negotiateAmount",
                                main_list.get(position).negotiateAmount
                            )

                            socket.emit("respond", jsonObject)
                            Log.e("RESPONSD", " " + jsonObject)
                            adapter!!.notifyDataSetChanged()
                        }
                    } else {
                        // model.status = "Expired"
                    }
                    main_list.set(i, model)
                }
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        socket.off(Socket.EVENT_CONNECT)
        socket.off(Socket.EVENT_DISCONNECT)
        socket.off("getOffer_" + bookingId, getOffer)
        socket.off("getMessage", onNewMessage)
        socket.off("error", error)
        socket.disconnect()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            if (!socket.connected()) {
                socket.connect()
                Log.e("Socket", " socket ${socket.connected()}")
            }
        } else {
            val messageToUser = getString(R.string.check_internet)
            showSnackBar(messageToUser)
        }
    }

    fun custom_back() {

        if (isTaskRoot) {
            startActivity(
                Intent(this, HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra("type", "fromChat")
            )
            finishAffinity()
        } else {
            if (from_booking)
                startActivity(
                    Intent(
                        this,
                        HomeActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
            finish()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.chat_menu, menu)
//        return true
//    }
//


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.cancel_booking -> {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.cancel_ur_booking))
                    .setPositiveButton(
                        getString(R.string.yes),
                        object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                val jsonData = JSONObject()
                                jsonData.put("id", bookingId)
                                jsonData.put("status", "Canceled")
                                jsonData.put("reason", "testing")

                                val requestBody = RequestBody.create(
                                    MediaType.parse("application/json"),
                                    jsonData.toString()
                                )
                                cancelBookingModel.hitCancelBookingRequest(
                                    user_obj?.token ?: "",
                                    requestBody
                                )
                            }
                        }).setNegativeButton(R.string.no, null)
                    .show()
                true
            }
           /* R.id.report -> {
                true
            }*/
        }
        return false
    }

    override fun onBackPressed() {
        custom_back()
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.tv_days -> {
                val popup = PopupMenu(this, v)
                val inflater = popup.menuInflater
                inflater.inflate(R.menu.chat_menu, popup.menu)
                popup.setOnMenuItemClickListener(this@ConversationActivity)
                popup.show()
            }
            R.id.iv_back -> {
                hideKeyboard()
                custom_back()
            }
            R.id.send_btn -> {

                if (rtCardNumber.text.toString().trim().isEmpty()) {
                    rtCardNumber.setText("")
                    showSnackBar("Please type message")
                    return
                }

                val messageItem = MessageItem(
                    providerId,
                    user_obj?.id,
                    rtCardNumber.text.toString(),
                    bookingId,
                    "",
                    "",
                    "text"
                )
                val jsonObject = JSONObject()
                jsonObject.put("receiver", providerId)
                jsonObject.put("sender", user_obj?.id)
                jsonObject.put("message", rtCardNumber.text.toString())
                jsonObject.put("type", "text")
                jsonObject.put("bookingId", bookingId)

                Log.e("connected", "Socket ${socket.connected()}")


                if (isNetworkConnected()) {
                    if (socket.connected()) {
                        if (roomJoined) {
                            socket.emit("sendMessage", jsonObject)
                            rtCardNumber.setText("")
                        } else {
                            unsendMessageList?.add(messageItem)
                            rtCardNumber.setText("")
                        }
                    } else {
                        socket.connect()
                        Log.e("Socket", " socket ${socket.connected()}")
                        unsendMessageList?.add(messageItem)
                        rtCardNumber.setText("")
                    }
                } else {
                    showSnackBar(resources.getString(R.string.check_internet))
                }
            }
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

    private fun renderSuccessResponse(response: JsonElement, type: Int) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)

            try {
                if (type == 1) {

                    Log.e("fsdfsdfsdfsdf", "BIG TEST:${data}")

                    val model = MyApp.gson.fromJson(data, ChatHistoryResponse::class.java)
                    if (model.success!!) {

                        for (i in 0 until model?.data?.size!!) {
                            val chat_message = model.data.get(i)!!
                            Log.e("BADSHAH", "BIG TEST:${chat_message}")
//                            if (chat_message.status.equals("Accepted") || chat_message.status.equals("Rejected"))
                            if (chat_message.status.equals("Accepted")) {
                                rtCardNumber.visibility = View.VISIBLE
                                send_btn.visibility = View.GONE
                                bottom_view.visibility = View.GONE
                                tv_days.visibility = View.GONE
                            }
                            if (model.data.get(i)?.type.equals("text")) {
                                if (model.data.get(i)?.sender?.id.equals(user_obj?.id)) {
                                    chat_message.typeText = 0
                                    main_list.add(chat_message)
                                } else {
                                    chat_message.typeText = 1
                                    main_list.add(chat_message)
                                }
                            } else {
                                chat_message.typeText = 2
                                main_list.add(chat_message)
                            }
                        }
                        adapter?.notifyDataSetChanged()
                        if (main_list.size > 0)
                        //scroll_view.fullScroll(View.FOCUS_DOWN);
                            recycler_view.scrollToPosition(main_list.size - 1)

                        Log.e("sdcmskdmc", "smcasm     $bookingStatus")

                        if (bookingStatus == "Pending") {
                            tv_wait_for_accept.visibility = View.VISIBLE
                            rtCardNumber.visibility = View.GONE
                            send_btn.visibility = View.GONE
                        } else {
                            tv_wait_for_accept.visibility = View.GONE
                            rtCardNumber.visibility = View.VISIBLE
                            send_btn.visibility = View.VISIBLE
                        }

                        if (model.bookingStatus == "Completed" || model.bookingStatus == "Rejected") {
                            rtCardNumber.visibility = View.GONE
                            send_btn.visibility = View.GONE
                        } else if (model.bookingStatus == "Pending") {
                            tv_wait_for_accept.visibility = View.VISIBLE
                            rtCardNumber.visibility = View.GONE
                            send_btn.visibility = View.GONE
                        } else {
                            rtCardNumber.visibility = View.VISIBLE
                            send_btn.visibility = View.VISIBLE
                        }

                    } else {
                        showSnackBar(model.message ?: "")
                    }

                } else if (type == 6) {
                    val data: String = MyApp.gson.toJson(response)
                    val initializePaymentGateway =
                        MyApp.gson.fromJson(data, InitializePaymentGatewayResponseModel::class.java)
                    Log.e("BADSHAH", "InitializePAymentGateway Response:$initializePaymentGateway")
                    val url = "${initializePaymentGateway.BaseURL}${initializePaymentGateway.Token}"
                    val i = Intent(context, UrlViewer::class.java)
                    i.putExtra("nameTitle", "Payment")
                    i.putExtra("url", url)
                    i.putExtra("paymentAmount", paymentAmount)
                    i.putExtra("paymentMsgID", paymentMsgID)
                    i.putExtra("paymentNegotiableAmount", paymentNegotiableAmount)
                    i.putExtra("bookingID", bookingId)
                    i.putExtra("negoPayment", true)
                    clBlackOut.visibility = View.GONE
                    startActivityForResult(i, 606)
//                finish()
                } else if (type == 7) {
                    try {
                        val data: String = MyApp.gson.toJson(response)
                        val dataModel = Gson().fromJson(data, BankListResponseModel::class.java)
                        if (dataModel?.success == true) {
                            taxInPercentage = dataModel.data.tax
                        }
                    } catch (e: Exception) {
                        Log.e("BADSHAH", "Exception Occurred:$e")
                    }
                } else {

                    val json = JSONObject(data)
                    if (json.getBoolean("success")) {
                        showToast("Cancelled booking successfully")
                        rtCardNumber.visibility = View.GONE
                        send_btn.visibility = View.GONE
                        bottom_view.visibility = View.GONE
                        tv_days.visibility = View.GONE
                    }
                }

            } catch (e: Exception) {
                Log.e("EXCEPTION", " " + e)
            }

        }
    }


    /*
     AlertDialog.Builder(this!!)
                    .setMessage(getString(R.string.cancel_ur_booking))
                    .setPositiveButton(
                        getString(R.string.yes),
                        object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                model.hitCancelBookingRequest(user_obj?.token?:"",booking_details?.id?:"")
                            }
                        }).setNegativeButton(R.string.no, null).show()
    * */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("BADSHAH", "reques code: $requestCode")
        if (requestCode == 606) {
            val paymentStatus = data?.getStringExtra("paymentStatus")
            Log.e("BADSHAH", "Payment Status: $paymentStatus")
            if (paymentStatus == "success") {
                showPaymentStatusPopup("Payment Successful")
                val jsonObject = JSONObject()
                jsonObject.put("status", "Accepted")
                jsonObject.put("id", bookingId)
                jsonObject.put("amount", paymentAmount)
                jsonObject.put("msgId", paymentMsgID)
                jsonObject.put("negotiateAmount", paymentNegotiableAmount)
                socket.emit("respond", jsonObject)
                Log.e("RESPONSD", " " + jsonObject)
                adapter!!.notifyDataSetChanged()

                rtCardNumber.visibility = View.VISIBLE
                send_btn.visibility = View.VISIBLE
                bottom_view.visibility = View.GONE
                tv_days.visibility = View.GONE
            } else {
                showPaymentStatusPopup("Payment Failed")
            }
        }
    }

    @SuppressLint("CutPasteId")
    private fun showPaymentStatusPopup(status: String) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.payment_success_dialog, null)

        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.setCanceledOnTouchOutside(false)
        mAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val okBtn = mDialogView.findViewById<Button>(R.id.okkBtn) as Button
        val statusText = mDialogView.findViewById<TextView>(R.id.confirmAlertAYSTv) as TextView
        statusText.text = status
        okBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }


}