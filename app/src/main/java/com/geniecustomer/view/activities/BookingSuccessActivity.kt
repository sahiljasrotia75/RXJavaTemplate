package com.geniecustomer.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.geniecustomer.R
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.model.booking.Data

class BookingSuccessActivity : BaseActivity() {

    var booking_details : Data?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookingsuccess)

        if(intent!=null && intent.hasExtra("details")){
            booking_details = intent.getSerializableExtra("details") as Data
        }

        Handler().postDelayed({
            startActivity(Intent(this,HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        },3000)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when(v!!.id){
            R.id.tvPreviewDetails -> {
                startActivity(Intent(this,BookingDetailActivity::class.java)
                    .putExtra("success",true)
                    .putExtra("details",booking_details))
                finish()
//                startActivity(Intent(this,HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//                finish()
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }
}
