package com.geniecustomer.view.activities

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
import kotlinx.android.synthetic.main.activity_changepassword.*
import org.koin.android.viewmodel.ext.android.viewModel

class ChangePasswordActivity : BaseActivity(), TextWatcher {

    val model by viewModel<UpdateprofileViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

        edtCurrentPass.addTextChangedListener(this)
        edtNewPass.addTextChangedListener(this)
        edtConfirmPass.addTextChangedListener(this)

        model.observeEditProfile().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!)
        })
    }
    override fun onClick(v: View?) {
        super.onClick(v)
        when(v!!.id){
            R.id.tvUpdate -> {
                if(edtCurrentPass.text.toString().trim().length < 8 ||edtNewPass.text.toString().trim().length < 8 ){
                    showToast(resources.getString(R.string.password_must))
                }else if(edtNewPass.text.toString().trim().equals(edtConfirmPass.text.toString().trim())){
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("oldPassword",edtCurrentPass.text.toString().trim())
                    jsonObject.addProperty("password",edtNewPass.text.toString().trim())
                    if(user_obj!=null)
                        model.hitChangePassword(user_obj?.token!!,jsonObject)
                }else{
                    showToast("Passwords mismatch")
                }
            }
            R.id.iv_back -> {
                finish()
            }
        }
    }
    override fun afterTextChanged(s: Editable?) {
        if(!edtCurrentPass.text.trim().isEmpty() && !edtNewPass.text.trim().isEmpty() && !edtConfirmPass.text.trim().isEmpty()){
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
}
