package com.geniecustomer.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.adapters.ServiceManListItem
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.filter.FilterActivity
import com.geniecustomer.interfaces.RvClickPostion
import com.geniecustomer.model.Address
import com.geniecustomer.model.ReviewDetailModel
import com.geniecustomer.model.service_providers.DataItem
import com.geniecustomer.model.service_providers.ServiceProvidersListRequest
import com.geniecustomer.model.service_providers.ServiceProvidersListResponse
import com.geniecustomer.viewmodels.FliterDataViewModel
import com.geniecustomer.viewmodels.GetDashboardDataViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_service_man_list.*
import kotlinx.android.synthetic.main.sort_dialog.*
import org.koin.android.viewmodel.ext.android.viewModel

class ServiceManListActivity : BaseActivity() {

    val model by viewModel<GetDashboardDataViewModel>()
    val filterViewModel by viewModel<FliterDataViewModel>()
    var adapter: ServiceManListItem? = null
    val list: MutableList<DataItem> = ArrayList()
    var category_id: String = ""
    var service_id: String = ""
    var category_name: String = ""
    var city: String = ""
    var review_detail_model: ReviewDetailModel? = null
    //var service_name : String = ""
    var page :Int = 1
    var count :Int = 0
    var isLoading = false
    var skip = 0
    var serviceType = 1
    var sortingType = "nameAsc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_man_list)

        adapter = ServiceManListItem(this, list as ArrayList<DataItem>)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        review_detail_model = ReviewDetailModel()
        review_detail_model?.bookingType = "Flexible"
        adapter?.setOnClick(object : RvClickPostion {
            override fun onRvItemClicked(position: Int) {
                Log.e("Serviceid", "service id :- " + tv_category.text.toString().trim())

                startActivity(
                    Intent(this@ServiceManListActivity, ServiceManInfoActivity::class.java)
                        .putExtra("id", list.get(position).id)
                        .putExtra("review_model", review_detail_model)
                        .putExtra("service_id", category_id)
                        .putExtra("service_name", tv_category.text.toString().trim())
                )


            }

        })

        model.observeDataResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!)
        })

        if (intent != null && intent.hasExtra("category_id")) {
            category_id = intent.getStringExtra("category_id")!!
            service_id = intent.getStringExtra("service_id")!!
            city = intent.getStringExtra("city")!!
            tv_category.text = intent.getStringExtra("service_name")!!
            Log.e("CATE_ID"," "+category_id)
            Log.e("SERVICE_ID"," "+service_id)
            Log.e("city"," "+city)
        }

        pagination()

        Handler().postDelayed({
            hitApi("Flexible")
        }, 200)
    }

    fun hitApi(type: String){
        if (sharedPref.contains("address")) {
            val address = MyApp.gson.fromJson(
                (this as BaseActivity).sharedPref.getString("address", ""),
                Address::class.java
            )
            val providersListRequest = ServiceProvidersListRequest(
                "",
                city,
                category_id,
                10,
                address.latitude ?: 0.0,
                address.longitude ?: 0.0,
                skip,
                service_id,
                type,
                sortingType
            )
            Log.e("hit", "DATA $providersListRequest ${user_obj?.token}")
            model.hitGetProvidersList(user_obj?.token ?: "", providersListRequest)
        } else {

            val providersListRequest = ServiceProvidersListRequest(
                "",
                city,
                category_id,
                10,
                0.0,
                0.0,
                skip,
                service_id,
                type,
                sortingType
            )
            Log.e("hit", "DATA $providersListRequest ${user_obj?.token}")
            model.hitGetProvidersList(user_obj?.token ?: "", providersListRequest)
        }

    }

    fun pagination() {
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            // for pagination
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val totalItemCount = layoutManager!!.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val endHasBeenReached = lastVisible + 2 >= totalItemCount
                if (totalItemCount > 0 && endHasBeenReached) {
                    Log.d("last_item", " outer $lastVisible")

                    if (list.size < count && !isLoading) {
                        Log.e("last_item", " inner " + recycler_view.adapter?.itemCount.toString())
                        // hitRatingApi(10,skip)
                        if (serviceType == 1)
                            hitApi("now")
                        else if (serviceType == 2)
                            hitApi("schedule")
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.tv_now -> {
                if (!review_detail_model?.bookingType.equals("Now", true)) {
                    tv_now.setTextColor(resources.getColor(R.color.colorWhite))
                    tv_flexible.setTextColor(resources.getColor(R.color._d4d8df))
                    tv_later.setTextColor(resources.getColor(R.color._d4d8df))
                    tv_now.background = resources.getDrawable(R.drawable.ongoing_back)
                    tv_later.background = resources.getDrawable(R.drawable.past_back)
                    tv_flexible.background = resources.getDrawable(R.drawable.past_back)
//                tv_select_date.visibility = View.GONE
//                date_recycler_view.visibility = View.GONE
//                tv_select_time.visibility = View.GONE
//                time_recycler_view.visibility = View.GONE
                    review_detail_model?.bookingType = "Now"
                    serviceType = 1
                    skip = 0
                    hitApi("now")
                    /*val providersListRequest =
                        ServiceProvidersListRequest("", city, category_id, 10, 0, service_id, "now")
                    model.hitGetProvidersList(user_obj?.token ?: "", providersListRequest)*/
                }
            }

            R.id.tv_later -> {
                if (!review_detail_model?.bookingType.equals("Schedule", true)) {
                    tv_later.setTextColor(resources.getColor(R.color.colorWhite))
                    tv_now.setTextColor(resources.getColor(R.color._d4d8df))
                    tv_flexible.setTextColor(resources.getColor(R.color._d4d8df))
                    tv_later.background = resources.getDrawable(R.drawable.ongoing_back)
                    tv_now.background = resources.getDrawable(R.drawable.past_back)
                    tv_flexible.background = resources.getDrawable(R.drawable.past_back)
//                tv_select_date.visibility = View.VISIBLE
//                date_recycler_view.visibility = View.VISIBLE
//                tv_select_time.visibility = View.VISIBLE
//                time_recycler_view.visibility = View.VISIBLE
                    review_detail_model?.bookingType = "Schedule"
                    serviceType = 2
                    skip = 0
                    hitApi("schedule")

                    /* val providersListRequest =
                        ServiceProvidersListRequest(
                            "",
                            city,
                            category_id,
                            10,
                            skip,
                            service_id,
                            "schedule"
                        )
                    model.hitGetProvidersList(user_obj?.token ?: "", providersListRequest)*/
                }
            }
            R.id.tv_flexible -> {
                if (!review_detail_model?.bookingType.equals("Flexible", true)) {
                    tv_later.setTextColor(resources.getColor(R.color._d4d8df))
                    tv_now.setTextColor(resources.getColor(R.color._d4d8df))
                    tv_flexible.setTextColor(resources.getColor(R.color.colorWhite))
                    tv_flexible.background = resources.getDrawable(R.drawable.ongoing_back)
                    tv_later.background = resources.getDrawable(R.drawable.past_back)
                    tv_now.background = resources.getDrawable(R.drawable.past_back)
//                tv_select_date.visibility = View.VISIBLE
//                date_recycler_view.visibility = View.VISIBLE
//                tv_select_time.visibility = View.VISIBLE
//                time_recycler_view.visibility = View.VISIBLE
                    review_detail_model?.bookingType = "Flexible"
                    serviceType = 2
                    skip = 0
                    hitApi("Flexible")
                }
            }
            R.id.iv_back -> {
                finish()
            }
            R.id.tv_filter -> {

                val i = Intent(this, FilterActivity::class.java)
                i.putExtra("category_id", category_id)
                i.putExtra("service_id", service_id)
                i.putExtra("city", city)

                if (serviceType == 1)
                    i.putExtra("serviceType", "now")
                else
                    i.putExtra("serviceType", "schedule")
                startActivityForResult(i, 10)

            }
            R.id.tv_sortby -> {

                sortDialog()

            }
        }
    }

    private fun consumeResponse(apiResponse: ApiResponse) {

        when (apiResponse.status) {

            Status.LOADING -> {
                isLoading = true
                if(skip == 0)
                    showProgress()
            }

            Status.SUCCESS -> {
                isLoading = false
                hideProgress()
                try {
                    renderSuccessResponse(apiResponse.data!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Status.ERROR -> {
                isLoading = false
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

            Log.e("dnvcsdnv", "$response")

            val data: String = MyApp.gson.toJson(response)

            val signupResponse = MyApp.gson.fromJson(data, ServiceProvidersListResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
            if (signupResponse.success != null && signupResponse.success.toString().equals(
                    "TRUE",
                    true
                )
            ) {

                Log.e("ITEMs", " " + signupResponse)
                Log.e("ITEMsData", "${Gson().toJson(signupResponse)}")

                if(skip == 0) {
                    list.clear()
                    list.addAll(signupResponse.data!!)
                    adapter!!.notifyDataSetChanged()
                }else{
                    val tempList = ArrayList<DataItem>()
                    tempList.addAll(signupResponse.data!!)
                    list.addAll(tempList)
                    adapter!!.notifyDataSetChanged()
                }
                count = signupResponse.count!!
                skip++
                adapter?.notifyDataSetChanged()

                pro_num.text = "" + signupResponse.count + " Professionals"
                if (list.size == 0) {
                    tv_no_providers.visibility = View.VISIBLE
                    recycler_view.visibility = View.GONE
                }else {
                    tv_no_providers.visibility = View.GONE
                    recycler_view.visibility = View.VISIBLE
                }
            } else {
                showSnackBar(signupResponse.message!!)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 10){
                 if(data?.hasExtra("category_id")!!) {
                     service_id = data.getStringExtra("category_id")!!
                     category_id = data.getStringExtra("service_id")!!
                     category_name = data.getStringExtra("category_name")!!
                     tv_category.text = category_name
                     skip = 0
                     if (serviceType == 1) {
                         hitApi("now")
                     } else if (serviceType == 2) {
                         hitApi("schedule")
                     }

                 }
            }
        }
    }

        fun sortDialog() {

        val view = LayoutInflater.from(this).inflate(R.layout.sort_dialog, null)
        val dialog = BottomSheetDialog(this, R.style.SheetDialog)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.setContentView(view)

            if(sortingType.equals("nameAsc")){
                dialog.rdioNameAsc.isChecked = true
            }else if(sortingType.equals("nameDesc")){
                dialog.rdioNameDesc.isChecked = true
            }else if(sortingType.equals("ratingAsc")){
                dialog.rdioRatingLow.isChecked = true
            }else if(sortingType.equals("ratingDesc")){
                dialog.rdioRatingHigh.isChecked = true
            }else if(sortingType.equals("priceAsc")){
                dialog.rdioPrizeLow.isChecked = true
            }else if(sortingType.equals("priceDesc")){
                dialog.rdioPrizeHigh.isChecked = true
            }
            else if(sortingType.equals("distAsc")){
                dialog.rdioDistanceLow.isChecked = true
            }
            else if(sortingType.equals("distDesc")){
                dialog.rdioDistanceHigh.isChecked = true
            }

        dialog.rdioGrpHome.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                val radioButton = group!!.findViewById<RadioButton>(checkedId)

                Log.e("sortingType"," "+sortingType)



                    if(radioButton.text.equals("Name - Ascending")){
                        skip = 0
                        sortingType = "nameAsc"
                    }else if(radioButton.text.equals("Name - Descending")){
                        skip = 0
                        sortingType = "nameDesc"
                    }else if(radioButton.text.equals("Rating - Low to High")){
                        skip = 0
                        sortingType = "ratingAsc"
                    }else if(radioButton.text.equals("Rating - High to Low")){
                        skip = 0
                        sortingType = "ratingDesc"
                    }else if(radioButton.text.equals("Price - Low to High")){
                        skip = 0
                        sortingType = "priceAsc"
                    }else if(radioButton.text.equals("Price - High to Low")){
                        skip = 0
                        sortingType = "priceDesc"
                    }else if(radioButton.text.equals("Distance - Low to High")){
                        skip = 0
                        sortingType = "distAsc"
                    }else if(radioButton.text.equals("Distance - High to Low")){
                        skip = 0
                        sortingType = "distDesc"
                    }


                if(serviceType == 1)
                    hitApi("now")
                else if(serviceType == 2)
                    hitApi("schedule")

                //Toast.makeText(this@CategoryList, "Sorted according to " + radioButton.text, Toast.LENGTH_LONG).show()
                Handler().postDelayed({
                    dialog.dismiss()
                }, 400)
            }
        })
        dialog.show()
    }

}
