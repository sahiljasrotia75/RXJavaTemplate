package com.geniecustomer.view.activities.urlViewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.ProgressBar
import com.geniecustomer.R
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.model.booking.Data
import com.geniecustomer.view.activities.BookingSuccessActivity
import kotlinx.android.synthetic.main.activity_url_viewer.*


class UrlViewer : BaseActivity() {

    private var mprogressBar: ProgressBar? = null
    private var loadUrl: String? = null
    private var nameTitle: String? = null
    var booking_details : Data?=null
    var paymentAmount = 0.0
    var paymentMsgID = ""
    var paymentNegotiableAmount = 0.0
    private var bookingID = ""
    private var negopayment = false
    private var isPaymentSuccess = false
    private var isPaymentFalse = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url_viewer)
        if(intent!=null && intent.hasExtra("details")){
            booking_details = intent.getSerializableExtra("details") as Data
        }
        if(intent!=null && intent.hasExtra("paymentAmount")){
            paymentAmount = intent.getDoubleExtra("paymentAmount",0.0)
        }
        if(intent!=null && intent.hasExtra("paymentNegotiableAmount")){
            paymentNegotiableAmount = intent.getDoubleExtra("paymentNegotiableAmount",0.0)
        }
        if(intent!=null && intent.hasExtra("paymentMsgID")){
            paymentMsgID = intent.getStringExtra("paymentMsgID")!!
        }
        if(intent!=null && intent.hasExtra("bookingID")){
            bookingID = intent.getStringExtra("bookingID")!!
        }

        negopayment = intent.getBooleanExtra("negoPayment",false)

        initBlock()
    }

    private fun initBlock() {

        loadUrl = intent.getStringExtra("url")!!
        nameTitle = intent.getStringExtra("nameTitle")!!
        tvtitle.text = nameTitle
        Log.e("BADSHAH","PDF URL:$loadUrl")
        mprogressBar = findViewById(R.id.progressBar)
        backBtn.setOnClickListener {
            finish()
        }

        webView.webChromeClient = WebChromeClient()
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        webView.setPadding(0, 0, 0, 0)
        webView.setInitialScale(getScale())
        webView.loadUrl(loadUrl!!)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (mprogressBar?.isShown!!) { mprogressBar?.visibility = View.GONE }

                if (url == "${Urls.BASE_URL}credit-success") {
                    startActivity(
                        Intent(this@UrlViewer, BookingSuccessActivity::class.java)
                            .putExtra("details", booking_details)
                    )
                    finish()
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                try {
                    Log.e("WEBVIEWDATA", "request : " + request?.url)

                    if (request?.url!!.toString()
                            .contains("${Urls.BASE_URL}payment-success?role=cu", true)
                    ) {
                        isPaymentSuccess = true
                        if (!negopayment) {
                            startActivity(
                                Intent(this@UrlViewer, BookingSuccessActivity::class.java)
                                    .putExtra("details", booking_details)
                            )
                            finish()
                        } else {
                            val i = Intent()
                            i.putExtra("paymentStatus", "success")
                            setResult(606, i)
                            finish()
                        }

                    } else if (request.url!!.toString()
                            .contains("${Urls.BASE_URL}payment-fail?role=cu", true)
                    ) {
                        isPaymentFalse = true
                        if (!negopayment) {
                            finish()
                        } else {
                            val i = Intent()
                            i.putExtra("paymentStatus", "un-success")
                            setResult(606, i)
                            finish()
                        }
                    } else if (request.url!!.toString()
                            .contains("https://appgrowthcompany.com/genie/", true)
                    ) {
                        if (isPaymentSuccess) {
                            if (negopayment) {
                                val i = Intent()
                                i.putExtra("paymentStatus", "success")
                                setResult(606, i)
                                finish()
                            }
                        }
                    } else {
                        if (isPaymentFalse) {
                            if (negopayment) {
                                val i = Intent()
                                i.putExtra("paymentStatus", "un-success")
                                setResult(606, i)
                                finish()
                            }
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                try {
                    Log.e("WEBVIEWDATA", "request : $url")

                    if (url!!.toString()
                            .contains("${Urls.BASE_URL}payment-success?role=cu", true)
                    ) {
                        isPaymentSuccess = true
                        if (!negopayment) {
                            startActivity(
                                Intent(this@UrlViewer, BookingSuccessActivity::class.java)
                                    .putExtra("details", booking_details)
                            )
                            finish()
                        } else {
                            val i = Intent()
                            i.putExtra("paymentStatus", "success")
                            setResult(606, i)
                            finish()
                        }

                    } else if (url.toString()
                            .contains("${Urls.BASE_URL}payment-fail?role=cu", true)
                    ) {
                        isPaymentFalse = true
                        if (!negopayment) {
                            finish()
                        } else {
                            val i = Intent()
                            i.putExtra("paymentStatus", "un-success")
                            setResult(606, i)
                            finish()
                        }
                    } else if (url.toString()
                            .contains("https://appgrowthcompany.com/genie/", true)
                    ) {
                        if (isPaymentSuccess) {
                            if (negopayment) {
                                val i = Intent()
                                i.putExtra("paymentStatus", "success")
                                setResult(606, i)
                                finish()
                            }
                        }
                    } else {
                        if (isPaymentFalse) {
                            if (negopayment) {
                                val i = Intent()
                                i.putExtra("paymentStatus", "un-success")
                                setResult(606, i)
                                finish()
                            }
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
                return super.shouldOverrideUrlLoading(view, url)
            }

        }




    }//End of Rex.

    private fun getScale(): Int {
        val display =
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val width = display.width
        var `val`: Double = width / 360.0
        `val` *= 100.0
        return `val`.toInt()
    }

}
