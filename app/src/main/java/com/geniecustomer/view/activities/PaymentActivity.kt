package com.geniecustomer.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.JsonReader
import android.view.View
import com.geniecustomer.R
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.model.booking.Data
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings
import com.oppwa.mobile.connect.provider.Connect
import kotlinx.android.synthetic.main.activity_payment.*
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class PaymentActivity : BaseActivity() {

    var booking_details : Data?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        if(intent!=null && intent.hasExtra("details")){
            booking_details = intent.getSerializableExtra("details") as Data
        }
        tvTotValue.text = "$" + booking_details?.amount

        payment()
    }

    private fun payment() {
        val paymentBrands: MutableSet<String> = LinkedHashSet()

        paymentBrands.add("VISA")
        paymentBrands.add("MASTER")
        paymentBrands.add("DIRECTDEBIT_SEPA")

        if(requestCheckoutId() != null){
            val checkoutSettings = CheckoutSettings(requestCheckoutId()!!, paymentBrands, Connect.ProviderMode.TEST)
            // Set shopper result URL
            // Set shopper result URL
            checkoutSettings.shopperResultUrl = "http://www.apptunix.com://result"

            val intent = checkoutSettings.createCheckoutActivityIntent(this)

            startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT)
        }



    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.tvTitle -> {
                startActivity(Intent(this,HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
            R.id.tvNext -> {
                startActivity(Intent(this,BookingSuccessActivity::class.java)
                    .putExtra("details",booking_details))
                finish()
            }
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this,HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    fun requestCheckoutId(): String? {
        val url: URL
        val urlString: String
        var connection: HttpURLConnection? = null
        var checkoutId: String? = null
        urlString = "https://test.oppwa.com/" + "?amount=48.99&currency=EUR&paymentType=DB"
        try {
            url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            val reader = JsonReader(
                InputStreamReader(connection.inputStream, "UTF-8")
            )
            reader.beginObject()
            while (reader.hasNext()) {
                if (reader.nextName().equals("checkoutId")) {
                    checkoutId = reader.nextString()
                    break
                }
            }
            reader.endObject()
            reader.close()
        } catch (e: Exception) { /* error occurred */
        } finally {
            if (connection != null) {
                connection.disconnect()
            }
        }
        return checkoutId
    }
}
