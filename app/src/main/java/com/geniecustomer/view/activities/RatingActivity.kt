package com.geniecustomer.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import androidx.lifecycle.Observer
import com.geniecustomer.R
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.utils.GlideApp
import com.geniecustomer.viewmodels.BookingViewModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_rating.*
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel

class RatingActivity : BaseActivity() {

    val viewModel by viewModel<BookingViewModel>()
    var name = ""
    var provider = ""
    var bookingId = ""
    var image = ""
    var isRating = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        if(intent.hasExtra("bookingId")){
            provider = intent.getStringExtra("provider")!!
            name = intent.getStringExtra("providerName")!!
            image = intent.getStringExtra("providerImage")!!
            bookingId = intent.getStringExtra("bookingId")!!
            val orderId = intent.getStringExtra("orderId")!!
            tv_name.text = name
            tv_id.text = "#$orderId"
            GlideApp.with(context).load(Urls.BASE_URL+image).into(iv_profyl)
        }
        viewModel.bookingResponse().observe(this, Observer<ApiResponse> { this.consumeResponse(it!! ) })
        rating_bar?.rating = 1f
        rating_bar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, _ ->
                if(rating<1f){
                    ratingBar?.rating = 1f
                }
            }

        rating_bar.setOnTouchListener { v, event ->
            when {
                rating_bar.rating > 4f -> {
                    iv_review_type.setImageDrawable(resources.getDrawable(R.drawable.ic_rating_5))
                    tv_review.text = getString(R.string.great)
                }
                rating_bar.rating > 3f -> {
                    iv_review_type.setImageDrawable(resources.getDrawable(R.drawable.ic_rating_4))
                    tv_review.text = getString(R.string.good)
                }
                rating_bar.rating > 2f -> {
                    iv_review_type.setImageDrawable(resources.getDrawable(R.drawable.ic_rating_3))
                    tv_review.text = getString(R.string.okay)
                }
                rating_bar.rating > 1f -> {
                    iv_review_type.setImageDrawable(resources.getDrawable(R.drawable.ic_rating_2))
                    tv_review.text = getString(R.string.bad)
                }
                else -> {
                    iv_review_type.setImageDrawable(resources.getDrawable(R.drawable.ic_rating_1))
                    tv_review.text = getString(R.string.terrible)
                }
            }
            rating_bar.onTouchEvent(event)
        }

    }

    override fun onClick(v: View?) {
        super.onClick(v)
            when (v!!.id) {
                R.id.iv_back -> {
                    hideKeyboard()
                    finish()
                    /*if(isRating){
                        val i = Intent()
                        i.putExtra("rating","done")
                        setResult(Activity.RESULT_OK,i)
                        finish()
                    }else{
                        finish()
                    }*/
                }
                R.id.tv_submit -> {

                    if(et_feedback.text.toString().isEmpty()){
                        showToast("Please add feedback")
                     return
                    }

                    showProgress()
                    val jsonElement = JsonObject()
                    jsonElement.addProperty("provider",provider)
                    jsonElement.addProperty("bookingId",bookingId)
                    jsonElement.addProperty("rating",rating_bar?.rating)
                    jsonElement.addProperty("review",et_feedback.text.toString())
                    Log.e("Json", " sent $jsonElement")
                    viewModel.postRating(user_obj?.token?:"" ,jsonElement)

                }
            }
        }


    private fun consumeResponse(apiResponse: ApiResponse) {

        when (apiResponse.status) {

            Status.LOADING -> {
                //showProgress()
            }

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
                Log.e("response ", " $response")
                try{
                    val obj = JSONObject(data)
                    if(obj.getBoolean("success")){
                        isRating = true
                        hideKeyboard()
                        val i = Intent()
                        i.putExtra("rating","done")
                        setResult(Activity.RESULT_OK,i)
                        finish()
                    }
                }catch (e : Exception){
                    Log.e("EXCEPTION ", " $e")
                }

        }
    }

    /*override fun onBackPressed() {
        if(isRating){
            val i = Intent()
            i.putExtra("rating","done")
            setResult(Activity.RESULT_OK,i)
            finish()
        }else{
            finish()
        }
    }*/
}
