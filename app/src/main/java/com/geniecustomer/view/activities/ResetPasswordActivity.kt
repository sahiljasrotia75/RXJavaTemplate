package com.geniecustomer.view.register

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
import com.geniecustomer.model.edit_profile.ChangePasswordResponse
import com.geniecustomer.viewmodels.UpdateprofileViewModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_resetpassword.*
import org.koin.android.viewmodel.ext.android.viewModel

class ResetPasswordActivity : BaseActivity(), TextWatcher {

    val model by viewModel<UpdateprofileViewModel>()
    var token = ""
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

        ivBack.setOnClickListener {
            finish()
        }

        if(intent!=null && intent.hasExtra("token")){
            token = intent.getStringExtra("token")!!
        }

        edtPass.addTextChangedListener(this)
        edtRePass.addTextChangedListener(this)
        model.observeEditProfile().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!)
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
            val signupResponse = MyApp.gson.fromJson(data, ChangePasswordResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
            if (signupResponse.success != null && signupResponse.success.toString().equals(
                    "TRUE",
                    true)){
                showToast(resources.getString(R.string.change_pass_success))
                finish()
            }else{
                signupResponse.message?.let { showSnackBar(it) }
            }
        }
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        when(v!!.id){
            R.id.tvUpdate -> {
                if(edtPass.text.toString().trim().length < 8 ||edtRePass.text.toString().trim().length < 8 ){
                    showToast(resources.getString(R.string.password_must))
                }else if(edtPass.text.toString().trim().equals(edtRePass.text.toString().trim())){
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("password",edtPass.text.toString().trim())
                    model.hitResetPassword(token,jsonObject)
                }else{
                    showToast("Passwords mismatch")
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        if(!edtPass.text.trim().isEmpty() && !edtRePass.text.trim().isEmpty()){
            tvUpdate.background = resources.getDrawable(R.drawable.round_corner_bg)
            tvUpdate.isEnabled = true
        }else{
            tvUpdate.background = resources.getDrawable(R.drawable.round_corner_bg_disabled)
            tvUpdate.isEnabled = false
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}
