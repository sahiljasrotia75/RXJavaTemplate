package com.geniecustomer.view.register

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.geniecustomer.R
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.edit_profile.EditProfileRequest
import com.geniecustomer.model.edit_profile.EditProfileResponse
import com.geniecustomer.viewmodels.UpdateprofileViewModel
import com.google.gson.JsonElement
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class AllowPushActivity : BaseActivity() {

    var signupData: EditProfileRequest? = null
    var imageFile: File? = null
    var token: String = ""
    var paramsMap: HashMap<String, RequestBody> = hashMapOf()
    val model by viewModel<UpdateprofileViewModel>()
    var my_request_image: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allowpush)

        if (intent != null) {
            signupData = intent.getSerializableExtra("data") as EditProfileRequest
            paramsMap = hashMapOf()
            addToHashMap("firstName", signupData?.firstName!!)
            addToHashMap("lastName", signupData?.lastName!!)
            addToHashMap("password", signupData?.password!!)
            addToHashMap("address", signupData?.address!!)
            addToHashMap("role", signupData?.role!!)
            if (!signupData?.phone?.isEmpty()!!) {
                addToHashMap("countryCode", signupData?.countryCode!!)
                addToHashMap("phone", signupData?.phone!!)
            }
            addToHashMap("latitude", signupData?.latitude?.toString() ?: "0.0")
            addToHashMap("longitude", signupData?.longitude?.toString() ?: "0.0")
            if (!signupData?.email?.isEmpty()!!)
                addToHashMap("email", signupData?.email!!)

            //paramsMap["data"] =  RequestBody.create(MediaType.parse("application/json; charset=utf-8"), MyApp.gson.toJson(signupData))
            token = intent.getStringExtra("token")!!

            if (intent.hasExtra("imageFile")) {
                imageFile = intent.getSerializableExtra("imageFile") as File
                val reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile)
                my_request_image = MultipartBody.Part.createFormData(
                    "image",
                    imageFile?.absolutePath ?: "",
                    reqFile
                )

            }
        }

        model.observeEditProfile().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!)
        })
    }

    fun addToHashMap(key: String, my_value: String) {
        Log.e("dsvsdgdsgfsdf", "$key ====== $my_value   ")
        paramsMap.put(key, RequestBody.create(MediaType.parse("multipart/form-data"), my_value))
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.tv_allow -> {
                hitApi(true)
            }
            R.id.tvCancel -> {
                hitApi(false)
            }
        }
    }

    fun hitApi(isPush: Boolean) {
        addToHashMap("isPushEnabled", isPush.toString())
        if (my_request_image == null) {
            1
            Log.e("nckdnlvcndslkv  v", "${paramsMap.values}")
            Log.e("nckdnlvcndslkv   k", "${paramsMap.keys}")
            Log.e("nckdnlvcndslkv  e", "${paramsMap.entries}")
            model.hitEditProfile(token, paramsMap)
        } else {
            model.hitEditProfileWithImage(token, paramsMap, my_request_image!!)
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
                navigate(CongratulationActivity::class.java)
            } else {
                signupResponse.message?.let { showSnackBar(it) }
            }
        }
    }
}
