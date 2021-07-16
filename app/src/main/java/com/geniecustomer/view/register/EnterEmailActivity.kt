package com.geniecustomer.view.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
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
import kotlinx.android.synthetic.main.activity_enteremail.*
import org.koin.android.viewmodel.ext.android.viewModel

class EnterEmailActivity : BaseActivity() {

    val model by viewModel<SignupViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enteremail)

        ivBack.setOnClickListener {
            finish()
        }

        tv_Next.setOnClickListener {
            val request = JsonObject()
            request.addProperty("email", "" + edtEmailId.text.toString())
            request.addProperty("role", "CU")
            request.addProperty("deviceType", "ANDROID")
            request.addProperty("pushToken", sharedPref.getString("fcmToken",""))
            model.hitRegisterUser(request)
        }

        model.signupResponse().observe(this, Observer<ApiResponse>{
            this.consumeResponse(it!!)
        })

        edtEmailId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isValidEmail()) {
                    tv_Next.setBackgroundResource(R.drawable.round_corner_bg)
                    tv_Next.isEnabled = true
                } else {
                    tv_Next.setBackgroundResource(R.drawable.round_corner_bg_disabled)
                    tv_Next.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

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

            if (signupResponse.success !=null && signupResponse.success.toString().equals("TRUE", true)) {
                if(signupResponse.data!=null) {
                    if (signupResponse.data.token != null) {
                        startActivity(
                            Intent(this, SignUpActivity::class.java).putExtra(
                                "token",
                                signupResponse.data.token
                            )
                                .putExtra("content", edtEmailId.text.toString())
                                .putExtra("type", 1)
                                .putExtra("code", "")
                        )
                    } else if (signupResponse.data.otpId != null) {
                        startActivity(
                            Intent(this, VerifyOTPActivity::class.java)
                                .putExtra("otpId", signupResponse.data.otpId)
                                .putExtra("content", edtEmailId.text.toString())
                                .putExtra("type", 1)
                                .putExtra("code", "")
                        )
                        //navigateWithExtras(VerifyOTPActivity::class.java, 0)
                    }
                } else {
                    startActivity(
                        Intent(this, EnterPasswordActivity::class.java)
                            .putExtra("content", edtEmailId.text.toString())
                            .putExtra("type", 1)
                            .putExtra("code", "")
                    )

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
