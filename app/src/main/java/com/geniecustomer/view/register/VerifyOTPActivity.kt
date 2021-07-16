package com.geniecustomer.view.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.geniecustomer.model.otp.OtpRequest
import com.geniecustomer.model.otp.OtpResponse
import com.geniecustomer.viewmodels.OtpViewModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_verifyotp.*
import org.koin.android.viewmodel.ext.android.viewModel

class VerifyOTPActivity : BaseActivity() {

    var otpId = ""
    var email_phone = ""
    var type = -1
    var verify_type = "register"
    var code = ""
    val model by viewModel<OtpViewModel>()
    var resend = false
    var resend_me_otp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifyotp)

        if (intent != null && intent.hasExtra("type")) {
            type = intent.getIntExtra("type", 0)
            email_phone = intent.getStringExtra("content")!!
            otpId = intent.getStringExtra("otpId")!!
            code = intent.getStringExtra("code")!!
            tv_EnterCode.text = "Please enter the 6-digit code sent to you at " + code + email_phone
            if (intent.hasExtra("from") && intent.getStringExtra("from").equals("forgot", true)) {
                verify_type = "password"
            }
            if (intent.hasExtra("resend") && intent.getBooleanExtra("resend", false) == true) {
                verify_type = "profile"
                resend = true
            }
        }

        tv_notRecieved.text = resources.getString(R.string.codenot_recieved, 30)
        startTimer()

        model.otpResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!)
        })


        ivBack.setOnClickListener {
            finish()
        }
        /*clOTPCode?.setOnClickListener {
            *//*digit01.text.clear()
            digit02.text.clear()
            digit03.text.clear()
            digit04.text.clear()
            digit05.text.clear()
            digit06.text.clear()
            onTypeOTP()*//*
        }*/

        tv_verify.setOnClickListener {

            if (/*!digit01.text.isNullOrEmpty() && !digit02.text.isNullOrEmpty() && !digit03.text.isNullOrEmpty() && !digit04.text.isNullOrEmpty()
                && !digit05.text.isNullOrEmpty() && !digit06.text.isNullOrEmpty()*/mukeshOtpView.text?.trim().toString().isNotEmpty()
            ) {
                resend_me_otp = false
                Log.e("nkasnclkns", "dvsdvsd   $type")
//                val otp = digit01.text.toString()+digit02.text.toString()+digit03.text.toString()+digit04.text.toString()+digit05.text.toString()+digit06.text.toString()
                val otp = mukeshOtpView.text?.trim().toString()
                if (type == 0) {
                    val otpRequest = OtpRequest("CU", otpId, email_phone, otp, verify_type, "", code,"ANDROID",sharedPref.getString("fcmToken",""))
                    model.hitVerifyOtp(otpRequest)
                } else if (type == 1) {
                    val otpRequest =
                        OtpRequest("CU", otpId, "", otp, verify_type, email_phone, code,"ANDROID",sharedPref.getString("fcmToken",""))
                    model.hitVerifyOtp(otpRequest)
                }


            }
        }


        onTypeOTP()
    }

    fun startTimer(){
        var count = 30
        Handler().post(object : Runnable {
            override fun run() {
                if (count != 0) {
                    count--
                    tv_notRecieved.text = resources.getString(R.string.codenot_recieved, count)
                    Handler().postDelayed(this, 1000)
                } else {
                    tv_notRecieved.text = "tap below to resend it"
                    tv_resend.setTextColor(resources.getColor(R.color.colorAccent))
                    tv_resend.isEnabled = true
                }
            }
        })
    }

    fun onTypeOTP() {


        mukeshOtpView?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.e("csacs","${s?.length}")
                if (s!!.length == 6) {
                    tv_verify.setBackgroundResource(R.drawable.round_corner_bg_green)
                    tv_verify.isEnabled = true
                } else {
                    tv_verify.setBackgroundResource(R.drawable.round_corner_bg_disabled)
                    tv_verify.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        //Managing focus (Input and Removal) of OTP DIGITS

       /* digit01.isCursorVisible = true
        digit01.requestFocus()
        digit01.setSelection(digit01.text.length)
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(digit01, InputMethodManager.SHOW_IMPLICIT)

        digit01?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
//                    digit02.isFocusable = true
//                    digit02.isFocusableInTouchMode = true
                    clOTPCode.visibility = View.GONE
                    digit02?.requestFocus()
                }else if (s.isEmpty()) {
                    hideKeyboard()
                    clOTPCode.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        digit02?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    if (digit02.text.toString().length == 1) {
                        digit03?.requestFocus()
                    }
                } else if (s.isEmpty()) {
                    digit01?.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        digit03?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    if (digit03.text.toString().length == 1) {
//                    digit04.isFocusable = true
//                    digit04.isFocusableInTouchMode = true
                        digit04?.requestFocus()
                    }
                } else if (s!!.length == 0) {
                    digit02?.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        digit04?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    if (digit04.text.toString().length == 1) {
//                    digit04.isFocusable = true
//                    digit04.isFocusableInTouchMode = true
                        digit05?.requestFocus()
                    }
                } else if (s!!.length == 0) {
                    digit03?.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        digit05?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    if (digit05.text.toString().length == 1) {
//                    digit04.isFocusable = true
//                    digit04.isFocusableInTouchMode = true
                        digit06?.requestFocus()
                    }
                } else if (s!!.length == 0) {
                    digit04?.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        digit06?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 0) {
                    digit05?.requestFocus()
                    if (!digit01.text.isNullOrEmpty() && !digit02.text.isNullOrEmpty() && !digit03.text.isNullOrEmpty() && !digit04.text.isNullOrEmpty()
                        && !digit05.text.isNullOrEmpty() && !digit06.text.isNullOrEmpty()
                    ) {
                        tv_verify.setBackgroundResource(R.drawable.round_corner_bg_green)
                        tv_verify.isEnabled = true
                    } else {
                        tv_verify.setBackgroundResource(R.drawable.round_corner_bg_disabled)
                        tv_verify.isEnabled = false
                    }
                } else {
                    hideKeyboard()
                    if (!digit01.text.isNullOrEmpty() && !digit02.text.isNullOrEmpty() && !digit03.text.isNullOrEmpty() && !digit04.text.isNullOrEmpty()
                        && !digit05.text.isNullOrEmpty() && !digit06.text.isNullOrEmpty()
                    ) {
                        tv_verify.setBackgroundResource(R.drawable.round_corner_bg_green)
                        tv_verify.isEnabled = true
                    } else {
                        tv_verify.setBackgroundResource(R.drawable.round_corner_bg_disabled)
                        tv_verify.isEnabled = false
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
*/





    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.tv_resend -> {
                val request = JsonObject()
                if (otpId.isNotEmpty())
                    request.addProperty("otpId", otpId)
                if (type == 0) {
                    request.addProperty("countryCode", code)
                    request.addProperty("phone", email_phone)
                    request.addProperty("role","CU")
                } else {
                    request.addProperty("email", email_phone)
                    request.addProperty("role","CU")
                }
                resend_me_otp = true
                model.hitResendOtp(request)
            }
        }
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
            val signupResponse = MyApp.gson.fromJson(data, OtpResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
            if (signupResponse.success != null && signupResponse.success.toString().equals(
                    "TRUE",
                    true
                )
            ) {
                // if(signupResponse.data?.token !=null){


                if (resend_me_otp) {
                    resend_me_otp = false
                    tv_resend.isEnabled = false
                    startTimer()
                    tv_resend.setTextColor(resources.getColor(R.color._e5e5e5))
                    showToast("Resend otp successfully")
                    otpId = signupResponse?.data?.otpId?:""
                } else {

                    if (resend) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    else
                    {
                        if (verify_type.equals("password")) {
                            startActivity(
                                Intent(this, ResetPasswordActivity::class.java).putExtra(
                                    "token",
                                    signupResponse.data?.token
                                ).putExtra("type", type)
                                    .putExtra("content", email_phone)
                                    .putExtra("code", code)
                            )
                            finish()
                        } else {
                            startActivity(
                                Intent(this, CongratulationActivity::class.java).putExtra("token",
                                    signupResponse.data?.token
                                ).putExtra("type", type)
                                    .putExtra("content", email_phone)
                                    .putExtra("code", code)
                            )
                        }
                    }
                }
                // }
            } else {
                signupResponse.message?.let { showSnackBar(it) }
                Log.e("true", "false")
            }
        } else {
            Log.e("true", "null")
        }
    }

}
