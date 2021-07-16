package com.geniecustomer.view.activities

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.geniecustomer.R
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.viewmodels.BookingViewModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_suggestion.*
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel

class SuggestionActivity : BaseActivity() {

    val suggestionViewModel by viewModel<BookingViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestion)
        suggestionViewModel.bookingResponse().observe(this, Observer<ApiResponse> { this.consumeResponse(it!!,1) })
        tvSubmit.setOnClickListener {
            if(ed_issue_name.text.toString().trim().isEmpty()){
                showSnackBar("Please enter your suggestion")
            }else if(ed_issue_des.text.toString().trim().isEmpty()){
                showSnackBar("Please describe your suggestion")
            }else{
                val json = JsonObject()
                json.addProperty("suggestion",ed_issue_name.text.toString())
                json.addProperty("desc",ed_issue_des.text.toString())
                suggestionViewModel.suggestion(user_obj?.token?:"",json)
            }
        }
        back_click.setOnClickListener {
            finish()
        }
    }

    private fun consumeResponse(apiResponse: ApiResponse, type: Int) {

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

    private fun renderSuccessResponse(response: JsonElement, type: Int) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)
            Log.e("response=", data)
            var json = JSONObject(data)

            if (json.getBoolean("success")) {
               showToast("Submitted your suggestion")
               finish()
            } else {
                showSnackBar(json.getString("message"))

            }
        }
    }
}
