package com.geniecustomer.view.register

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.geniecustomer.R
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.view.activities.ConfirmBookingActivity
import com.geniecustomer.view.activities.HomeActivity
import kotlinx.android.synthetic.main.activity_congratulation.*

class CongratulationActivity : BaseActivity()
{
    var token = ""
    var email_phone = ""
    var type = -1
    var code = ""
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congratulation)

            if (intent!=null && intent.hasExtra("type"))
            {
                type = intent.getIntExtra("type",0)
                email_phone = intent.getStringExtra("content")!!
                token = intent.getStringExtra("token")!!
                code = intent.getStringExtra("code")!!

                if(type==1){
                    tv_subtitle.text = getString(R.string.congrats_verifiedEmail)
                }else if(type == 0){
                    tv_subtitle.text = getString(R.string.congrats_verifiedPhone)

                }
                Handler().postDelayed({
                    startActivity(
                        Intent(this,SignUpActivity::class.java).putExtra("token",token)
                            .putExtra("type",type)
                            .putExtra("content",email_phone)
                            .putExtra("code",code)
                    )
                },1500)
            }
//            else if (sharedPref.getString("intent","phone").equals("phone",true))
//            {
//                tv_subtitle.text = getString(R.string.congrats_verifiedPhone)
//                Handler().postDelayed({
//                    navigate(SignUpActivity::class.java)
//                },1500)
//            }
            else
            {
                tv_subtitle.text = getString(R.string.congrats_setUp)
                Handler().postDelayed({
                    if (sharedPref.contains("guest") && sharedPref.getBoolean(
                            "guest",
                            false
                        ) == true
                    ) {
                        editor.remove("guest").apply()
                        startActivity(
                            Intent(this, ConfirmBookingActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        finish()
                    }else {
                        navigateFinishAffinity(HomeActivity::class.java)
                    }
                },1500)
            }

    }

    override fun onBackPressed() {
    }
}
