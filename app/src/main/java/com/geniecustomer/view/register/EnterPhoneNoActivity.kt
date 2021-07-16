package com.geniecustomer.view.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Observer
import com.geniecustomer.R
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.signup.SignupResponse
import com.geniecustomer.viewmodels.SignupViewModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_enterphoneno.*
import org.koin.android.viewmodel.ext.android.viewModel

class EnterPhoneNoActivity : BaseActivity() {

    val model by viewModel<SignupViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterphoneno)

        model.signupResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!)
        })

        ivBack.setOnClickListener {
            finish()
        }

        tv_NextPhone.setOnClickListener {
            val request = JsonObject()
            request.addProperty(
                "phone",
                "" +edtPhoneNo.text.toString()
            )
            request.addProperty("role", "CU")
            request.addProperty("countryCode", ""+ccp.selectedCountryCodeWithPlus)
            request.addProperty("deviceType", "ANDROID")
            request.addProperty("pushToken", sharedPref.getString("fcmToken",""))
            model.hitRegisterUser(request)
        }

        edtPhoneNo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty() && s.length > 6) {
                    tv_NextPhone.setBackgroundResource(R.drawable.round_corner_bg)
                    tv_NextPhone.isEnabled = true
                } else {
                    tv_NextPhone.setBackgroundResource(R.drawable.round_corner_bg_disabled)
                    tv_NextPhone.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun consumeResponse(apiResponse: ApiResponse) {

        when (apiResponse.status) {

            Status.LOADING -> showProgress()

            Status.SUCCESS -> {
                hideProgress()
                try {
                    renderSuccessResponse(apiResponse.data!!)
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

    private fun renderSuccessResponse(response: JsonElement) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)
            Log.e("response=", data)
            val signupResponse = MyApp.gson.fromJson(data, SignupResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
            if (signupResponse.success != null && signupResponse.success.toString().equals(
                    "TRUE",
                    true
                )
            ) {
                if(signupResponse.data!=null) {
                    if (signupResponse.data.token != null) {
                        startActivity(
                            Intent(this, SignUpActivity::class.java).putExtra(
                                "token",
                                signupResponse.data.token
                            ).putExtra(
                                "content",
                                "" + edtPhoneNo.text.toString()
                            ).putExtra(
                                "code",
                                "" + ccp.selectedCountryCodeWithPlus
                            )
                                .putExtra("type", 0)
                        )
                    } else if (signupResponse.data.otpId != null) {
                        startActivity(
                            Intent(this, VerifyOTPActivity::class.java)
                                .putExtra("otpId", signupResponse.data.otpId)
                                .putExtra(
                                    "content",
                                    "" + edtPhoneNo.text.toString()
                                ).putExtra(
                                    "code",
                                    "" + ccp.selectedCountryCodeWithPlus
                                )
                                .putExtra("type", 0)
                        )
                        //navigateWithExtras(VerifyOTPActivity::class.java, 0)
                    }
                }else{
                    startActivity(Intent(this,EnterPasswordActivity::class.java)
                        .putExtra("content", ""+edtPhoneNo.text.toString())
                        .putExtra("type", 0)
                        .putExtra(
                            "code",
                            "" + ccp.selectedCountryCodeWithPlus
                        ))
                }
            } else {
                signupResponse.message?.let { showSnackBar(it) }
                Log.e("true", "false")
            }
        } else {
            Log.e("true", "null")
        }
    }

}
