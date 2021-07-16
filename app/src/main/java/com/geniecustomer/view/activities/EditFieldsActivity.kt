package com.geniecustomer.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.geniecustomer.R
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.edit_profile.EditProfileResponse
import com.geniecustomer.model.forgot_pass.ForgotResponse
import com.geniecustomer.view.register.VerifyOTPActivity
import com.geniecustomer.viewmodels.OtpViewModel
import com.geniecustomer.viewmodels.UpdateprofileViewModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_edit_fields.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.koin.android.viewmodel.ext.android.viewModel


class EditFieldsActivity : BaseActivity() {

    var extras = ""
    var paramsMap: HashMap<String, RequestBody> = hashMapOf()
    val model by viewModel<UpdateprofileViewModel>()
    val otp_model by viewModel<OtpViewModel>()
    var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_fields)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        if (intent != null && intent.hasExtra("extras")) {
            extras = intent.getStringExtra("extras")!!
        }

        model.observeEditProfile().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!,0)
        })

        otp_model.otpResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!,1)
        })

        when (extras) {
            "email" -> {
                tvTitle.text = "Edit Email"
                tvCurrentPass.text = "Enter Email"
                edtCurrentPass.hint = "Enter Email"
                edtCurrentPass.setText(user_obj?.email)
                edtCurrentPass.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                (edtCurrentPass.layoutParams as ConstraintLayout.LayoutParams).marginStart =
                    resources.getDimensionPixelSize(R.dimen._20sdp)
            }
            "phone" -> {
                tvTitle.text = "Edit Phone"
                tvCurrentPass.text = "Enter Phone"
                edtCurrentPass.hint = "Enter Phone"
                edtCurrentPass.setText(user_obj?.phone)
                if (!user_obj?.countryCode.isNullOrEmpty()) {
                    ccp.setCountryForPhoneCode(
                        ((user_obj?.countryCode ?: "+0").replace(
                            "+",
                            ""
                        )).toInt()
                    )
                }
                ccp.visibility = View.VISIBLE
                edtCurrentPass.inputType = InputType.TYPE_CLASS_NUMBER
                edtCurrentPass.keyListener = DigitsKeyListener.getInstance("0123456789")
            }
            "fname" -> {
                tvTitle.text = "Edit First Name"
                tvCurrentPass.text = "Enter First Name"
                edtCurrentPass.hint = "Enter First Name"
                edtCurrentPass.setText(user_obj?.firstName)
                edtCurrentPass.inputType = InputType.TYPE_CLASS_TEXT
                (edtCurrentPass.layoutParams as ConstraintLayout.LayoutParams).marginStart =
                    resources.getDimensionPixelSize(R.dimen._20sdp)
                edtCurrentPass.filters = arrayOf(InputFilter { src, start, end, dst, dstart, dend ->
                    if (src == "") {
                        return@InputFilter src
                    }
                    if (src.toString().matches("[a-zA-Z]+".toRegex())) {
                        src
                    } else ""
                })
                // edtCurrentPass.setKeyListener(DigitsKeyListener.getInstance(resources.getString(R.string.alphabets)))
            }
            "lname" -> {
                tvTitle.text = "Edit Last Name"
                tvCurrentPass.text = "Enter Last Name"
                edtCurrentPass.hint = "Enter Last Name"
                edtCurrentPass.setText(user_obj?.lastName ?: "")
                edtCurrentPass.inputType = InputType.TYPE_CLASS_TEXT
                (edtCurrentPass.layoutParams as ConstraintLayout.LayoutParams).marginStart =
                    resources.getDimensionPixelSize(R.dimen._20sdp)
                edtCurrentPass.filters = arrayOf(InputFilter { src, start, end, dst, dstart, dend ->
                    if (src == "") {
                        return@InputFilter src
                    }
                    if (src.toString().matches("[a-zA-Z]+".toRegex())) {
                        src
                    } else ""
                })
                // edtCurrentPass.setKeyListener(DigitsKeyListener.getInstance(resources.getString(R.string.alphabets)))
            }
        }

        edtCurrentPass.setSelection(edtCurrentPass.text.length)
        edtCurrentPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                when (extras) {
                    "email" -> {
                        if (s.isValidEmail() && s.toString().length < 36 && !s.toString()
                                .equals(user_obj?.email, true)
                        ) {
                            tvUpdate.setBackgroundResource(R.drawable.round_corner_bg)
                            tvUpdate.isEnabled = true
                        } else {
                            tvUpdate.setBackgroundResource(R.drawable.round_corner_bg_disabled)
                            tvUpdate.isEnabled = false
                        }
                    }
                    "phone" -> {
                        if (s!!.isNotEmpty() && s.length > 6 && s.length < 13 && !s.toString()
                                .equals(user_obj?.phone)
                        ) {
                            tvUpdate.setBackgroundResource(R.drawable.round_corner_bg)
                            tvUpdate.isEnabled = true
                        } else {
                            tvUpdate.setBackgroundResource(R.drawable.round_corner_bg_disabled)
                            tvUpdate.isEnabled = false
                        }
                    }
                    else -> {
                        if (s!!.isNotEmpty() && s.toString().length < 26) {
                            tvUpdate.setBackgroundResource(R.drawable.round_corner_bg)
                            tvUpdate.isEnabled = true
                        } else {
                            tvUpdate.setBackgroundResource(R.drawable.round_corner_bg_disabled)
                            tvUpdate.isEnabled = false
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }


    fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.iv_back -> {
                hideKeyboard()
                finish()
            }
            R.id.tvUpdate -> {
                when (extras) {
                    "email" -> {
                        val request = JsonObject()
                        request.addProperty("email",edtCurrentPass.text.toString())
                        request.addProperty("role","CU")
                        otp_model.hitResendOtp(request)
                    }
                    "phone" -> {
                        val request = JsonObject()
                        request.addProperty("code",ccp.selectedCountryCodeWithPlus.toString())
                        request.addProperty("phone",edtCurrentPass.text.toString())
                        request.addProperty("role","CU")
                        otp_model.hitResendOtp(request)
                    }
                    "fname" -> {
                        addToHashMap("firstName",edtCurrentPass.text.toString())
                        token = user_obj?.token?:""
                        model.hitEditProfile(token, paramsMap)
                    }
                    "lname" -> {
                        addToHashMap("lastName",edtCurrentPass.text.toString())
                        token = user_obj?.token?:""
                        model.hitEditProfile(token, paramsMap)
                    }
                }
            }
        }
    }



    fun addToHashMap(key: String, my_value: String) {
        paramsMap.put(key, RequestBody.create(MediaType.parse("multipart/form-data"), my_value))
    }


    private fun consumeResponse(apiResponse: ApiResponse, type : Int) {

        when (apiResponse.status) {

            Status.LOADING -> showProgress()

            Status.SUCCESS -> {
                hideProgress()
                try {
                    renderSuccessResponse(apiResponse.data!!,type)
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

    private fun renderSuccessResponse(response: JsonElement,my_type : Int) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)
            Log.e("response=", data)

            if(my_type == 0) {
                val signupResponse = MyApp.gson.fromJson(data, EditProfileResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
                if (signupResponse.success != null && signupResponse.success.toString().equals(
                        "TRUE",
                        true
                    )
                ) {
                    val user_obj = MyApp.gson.toJson(signupResponse.data)
                    editor.putString("user_obj", user_obj).apply()
                    hideKeyboard()
                    when (extras) {
                        "fname" -> {
                            showToast("First Name changed successfully")
                        }
                        "lname" -> {
                            showToast("Last Name changed successfully")
                        }
                        "email" -> {
                            showToast("Email Updated Successfully")
                        }
                        "phone" -> {
                            showToast("Phone Updated Successfully")
                        }
                    }
                    finish()

                } else {
                    signupResponse.message?.let { showSnackBar(it) }
                }
            }
            else if(my_type == 1) {
                val signupResponse = MyApp.gson.fromJson(data, ForgotResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
                if (signupResponse.success != null && signupResponse.success.toString().equals(
                        "TRUE",
                        true
                    )
                ) {
                    val intent = Intent(this,VerifyOTPActivity::class.java)
                    if(extras.equals("email",true)){
                        intent.putExtra("type",1)
                        intent.putExtra("code","")
                    }else if(extras.equals("phone",true)){
                        intent.putExtra("type",0)
                        intent.putExtra("code",ccp.selectedCountryCodeWithPlus)
                    }
                    intent.putExtra("content",edtCurrentPass.text.toString())
                    intent.putExtra("otpId",signupResponse.data?.otpId)
                    intent.putExtra("resend",true)
                    startActivityForResult(intent,101)

                } else {
                    signupResponse.message?.let { showSnackBar(it) }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK){
            if(extras.equals("email")) {
                addToHashMap("email", edtCurrentPass.text.toString())
            }else {
                addToHashMap("phone", edtCurrentPass.text.toString())
                addToHashMap("countryCode", ccp.selectedCountryCodeWithPlus)
            }
            token = user_obj?.token?:""
            model.hitEditProfile(token, paramsMap)
        }
    }
}
