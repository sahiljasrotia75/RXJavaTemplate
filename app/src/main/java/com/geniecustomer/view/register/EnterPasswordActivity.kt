package com.geniecustomer.view.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.geniecustomer.R
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.forgot_pass.ForgotResponse
import com.geniecustomer.model.signin.Data
import com.geniecustomer.model.signin.SigninRequest
import com.geniecustomer.model.signin.SigninResponse
import com.geniecustomer.view.activities.ConfirmBookingActivity
import com.geniecustomer.view.activities.HomeActivity
import com.geniecustomer.viewmodels.SigninViewModel
import com.geniecustomer.viewmodels.UpdateprofileViewModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_enterpassword.*
import org.koin.android.viewmodel.ext.android.viewModel

class EnterPasswordActivity : BaseActivity() {

    //var veri_type = 0
    var type = -1
    var content = ""
    var code = ""
    val model by viewModel<SigninViewModel>()
    val update_model by viewModel<UpdateprofileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterpassword)

        if (intent != null && intent.hasExtra("content")) {
            content = intent.getStringExtra("content")!!
            type = intent.getIntExtra("type", -1)
            code = intent.getStringExtra("code")!!
        }

        model.signinResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!, 0)
        })

        update_model.observeEditProfile().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!, 1)
        })

        edtPassId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty()) {
                    tv_Login.setBackgroundResource(R.drawable.round_corner_bg)
                    tv_Login.isEnabled = true
                } else if (s.isEmpty()) {
                    tv_Login.setBackgroundResource(R.drawable.round_corner_bg_disabled)
                    tv_Login.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
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

    private fun renderSuccessResponse(response: JsonElement, type_v: Int) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)
            if (type_v == 0) {
                val signupResponse = MyApp.gson.fromJson(data, SigninResponse::class.java)
                if (signupResponse.success != null && signupResponse.success.toString().equals("TRUE", true))
                {
                    val user_obj2 = MyApp.gson.toJson(signupResponse.data)
                    editor.putString("user_obj", user_obj2).apply()
                    user_obj = MyApp.gson.fromJson(sharedPref.getString("user_obj",""), Data::class.java)

                    Log.e("asdjkcasnkc", "asknclkasc           $user_obj")

                    if (sharedPref.contains("guest") && sharedPref.getBoolean("guest", false) == true)
                    {
                        editor.remove("guest").apply()
                        startActivity(Intent(this, ConfirmBookingActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                    else {
                        navigateFinishAffinity(HomeActivity::class.java)
                    }
                } else {
                    signupResponse.message?.let { showSnackBar(it) }
                }
            }
            else if (type_v == 1)
            {

                val signupResponse = MyApp.gson.fromJson(data, ForgotResponse::class.java)

                if (signupResponse.success != null && signupResponse.success.toString().equals("TRUE", true)) {
                    val intent = Intent(
                        this,
                        VerifyOTPActivity::class.java
                    ).putExtra("type", type).putExtra("code", code).putExtra("content", content)
                        .putExtra("otpId", signupResponse.data?.otpId).putExtra("from", "forgot")
                    startActivity(intent)
                } else {
                    signupResponse.message?.let { showSnackBar(it) }
                }

            }
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.tvForgotPass -> {
                val jsonObject = JsonObject()
                if (type == 0) {
                    jsonObject.addProperty("phone", content)
                    jsonObject.addProperty("email", "")
                    jsonObject.addProperty("countryCode", code)
                } else if (type == 1) {
                    jsonObject.addProperty("phone", "")
                    jsonObject.addProperty("email", content)
                    jsonObject.addProperty("countryCode", code)
                }
                jsonObject.addProperty("role", "CU")
                update_model.hitForgotPassword(jsonObject)
                //if (veri_type == 0) {



//                val dialog = Dialog(this, R.style.TransparentDialog1)
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                dialog.setContentView(R.layout.choose_alert)
//
//
//                var btn_update = dialog.findViewById<Button>(R.id.btn_next)
//                var tv_email = dialog.findViewById<TextView>(R.id.tv_email)
//                var tv_ph9 = dialog.findViewById<TextView>(R.id.tv_ph9)
//
//                tv_email.setOnClickListener {
//                    if (veri_type != 0) {
//                        tv_email.setTextColor(resources.getColor(R.color.colorWhite))
//                        tv_email.setCompoundDrawablesWithIntrinsicBounds(
//                            0,
//                            0,
//                            R.drawable.filled_radio,
//                            0
//                        )
//                        tv_ph9.setCompoundDrawablesWithIntrinsicBounds(
//                            0,
//                            0,
//                            R.drawable.ic_radio_unfilled,
//                            0
//                        )
//                        tv_ph9.setTextColor(resources.getColor(R.color._3b5999))
//                        veri_type = 0
//                    }
//                }
//                tv_ph9.setOnClickListener {
//                    if (veri_type != 1) {
//                        tv_ph9.setTextColor(resources.getColor(R.color.colorWhite))
//                        tv_email.setCompoundDrawablesWithIntrinsicBounds(
//                            0,
//                            0,
//                            R.drawable.ic_radio_unfilled,
//                            0
//                        )
//                        tv_ph9.setCompoundDrawablesWithIntrinsicBounds(
//                            0,
//                            0,
//                            R.drawable.filled_radio,
//                            0
//                        )
//                        tv_email.setTextColor(resources.getColor(R.color._3b5999))
//                        veri_type = 1
//                    }
//                }
//
//                btn_update.setOnClickListener {
//                    dialog.dismiss()
//                    if (veri_type == 0) {
//                        val intent = Intent(
//                            applicationContext,
//                            VerifyOTPActivity::class.java
//                        ).putExtra("type", 1).putExtra("from","forgot")
//                        startActivity(intent)
//                    } else {
//                        val intent = Intent(
//                            applicationContext,
//                            VerifyOTPActivity::class.java
//                        ).putExtra("type", 0).putExtra("from","forgot")
//                        startActivity(intent)
//                    }
//                }
//
//                dialog.show()

            }
            R.id.ivBack -> {
                finish()
            }
            R.id.tv_Login -> {
                if (edtPassId.text.toString().trim().length < 8) {
                    showToast("Password must be at least 8 characters long.")
                } else {

                    if (type == 0) {
                        var signinRequest =
                            SigninRequest(edtPassId.text.toString().trim(), "CU", content, "", code,"ANDROID", sharedPref.getString("fcmToken",""))
                        model.hitSigninUser(signinRequest)
                    } else if (type == 1) {
                        var signinRequest =
                            SigninRequest(edtPassId.text.toString().trim(), "CU", "", content, code,"ANDROID", sharedPref.getString("fcmToken",""))
                        model.hitSigninUser(signinRequest)
                    }

                }
            }
        }
    }
}
