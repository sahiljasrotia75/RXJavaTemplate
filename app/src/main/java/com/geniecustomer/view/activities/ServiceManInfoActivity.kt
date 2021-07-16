package com.geniecustomer.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.geniecustomer.R
import com.geniecustomer.adapter.OtherServicesAdapter
import com.geniecustomer.adapters.SelectServiceAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.interfaces.SelectServiceClick
import com.geniecustomer.model.ReviewDetailModel
import com.geniecustomer.model.TaskObject
import com.geniecustomer.model.service_providers.info.*
import com.geniecustomer.utils.GlideApp
import com.geniecustomer.view.activities.imageShow.ImageShow
import com.geniecustomer.viewmodels.GetDashboardDataViewModel
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_service_man_info.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.Serializable

class ServiceManInfoActivity : BaseActivity() {

    val model by viewModel<GetDashboardDataViewModel>()
    var service_adapter: SelectServiceAdapter? = null

    //var list: ArrayList<ServiceListItem> = ArrayList()
    var otherServicesAdapter: OtherServicesAdapter? = null
    var otherServicesList: ArrayList<OtherServices> = ArrayList()
    var services_list: ArrayList<ServiceListItem> = ArrayList()
    var adapter_list: ArrayList<Any> = ArrayList()
    var review_list: ArrayList<ReviewItem> = ArrayList()
    var isDifferent = false
    var id = ""
    var service_id: String = ""
    var service_nameforcheck: String = ""
    var review_detail_model: ReviewDetailModel? = null
    var headersList: MutableList<String> = ArrayList()
    private lateinit var adapter: MyViewPagerAdapter
    private var timeSlotsList: ArrayList<TimeSlotListItem?>? = null
    var isFixed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_man_info)

        if (intent.hasExtra("service_name")) {
            service_nameforcheck = intent.getStringExtra("service_name") ?: ""
        }

        inits()

        tab_layout.addTab(tab_layout.newTab().setText("Hire me"))
        tab_layout.addTab(tab_layout.newTab().setText("Reviews"))
        tab_layout.addTab(tab_layout.newTab().setText("About"))


        //list.clear()


        if (intent != null) id = intent.getStringExtra("id")!!

//        list.add("Carpentry & Construction")
//        list.add("Delivery")
//        list.add("Electrical help")
        service_adapter = SelectServiceAdapter(this, adapter_list)
        recycler_view.adapter = service_adapter
        recycler_view.layoutManager = LinearLayoutManager(this)

        model.observeDataResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!)
        })

        Handler().postDelayed({
            model.hitGetProviderData(user_obj?.token ?: "", id, service_id)
        }, 200)


        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    adapter_list.clear()

                    Log.e("servicesList", "nklcnaslc    ----------   $services_list")
                    for (i in 0 until services_list.size) {
                        if (isFixed) {
                            if (services_list.get(i).priceType.equals("fixed", true)) {
                                adapter_list.add(services_list.get(i))
                                // review_detail_model?.price_type = "Fixed"
                            }
                        } else {
                            if (!services_list.get(i).priceType.equals("fixed", true)) {
                                adapter_list.add(services_list.get(i))
                                //review_detail_model?.price_type = "Negotiate"
                            }
                        }
                    }

                    if (isFixed) {
                        val fixedOtherServices = otherServicesList.filter {
                            it.priceType.equals("fixed", true)
                        }
                        if (fixedOtherServices.isNotEmpty()) {
                            otherServices.visibility = View.VISIBLE
                            otherServicesRV.visibility = View.VISIBLE
                        } else {
                            otherServices.visibility = View.GONE
                            otherServicesRV.visibility = View.GONE
                        }
                        otherServicesAdapter?.submitList(fixedOtherServices as ArrayList<OtherServices>)
                    } else {
                        val fixedOtherServices = otherServicesList.filter {
                            !it.priceType.equals("fixed", true)
                        }
                        if (fixedOtherServices.isNotEmpty()) {
                            otherServices.visibility = View.VISIBLE
                            otherServicesRV.visibility = View.VISIBLE
                        } else {
                            otherServices.visibility = View.GONE
                            otherServicesRV.visibility = View.GONE
                        }
                        otherServicesAdapter?.submitList(fixedOtherServices as ArrayList<OtherServices>)
                    }

                    service_adapter?.notifyDataSetChanged()

                    tv_no_reviews.visibility = View.GONE
                    recycler_view.visibility = View.VISIBLE
                    nested_tv_no_about.visibility = View.GONE
                } else if (tab?.position == 1) {
                    otherServices.visibility = View.GONE
                    otherServicesRV.visibility = View.GONE
                    if (review_list.size > 0) {
                        adapter_list.clear()
                        for (i in 0 until review_list.size) {
                            adapter_list.add(review_list.get(i))
                        }
                        service_adapter?.notifyDataSetChanged()
                        tv_no_reviews.visibility = View.GONE
                        recycler_view.visibility = View.VISIBLE
                        nested_tv_no_about.visibility = View.GONE
                    } else {
                        tv_no_reviews.visibility = View.VISIBLE
                        recycler_view.visibility = View.GONE
                        nested_tv_no_about.visibility = View.GONE
                    }
                } else if (tab?.position == 2) {
                    otherServices.visibility = View.GONE
                    otherServicesRV.visibility = View.GONE
                    tv_no_reviews.visibility = View.GONE
                    recycler_view.visibility = View.GONE
                    nested_tv_no_about.visibility = View.VISIBLE
                }
            }
        })

        service_adapter?.selectServiceClick(object : SelectServiceClick {
            override fun onClick(position: Int, isSelected: Boolean) {
                //(list.get(position) as ServiceListItem).isSelected = !isSelected
                services_list.get((adapter_list.get(position) as ServiceListItem).position!!).isSelected =
                    !isSelected
                (adapter_list.get(position) as ServiceListItem).isSelected = !isSelected
                service_adapter?.notifyDataSetChanged()
            }

        })
    }

    fun toggle_services() {
        if (tab_layout.selectedTabPosition == 0) {
            adapter_list.clear()

            otherServicesAdapter = OtherServicesAdapter(context)
            otherServicesRV.adapter = otherServicesAdapter

            for (i in 0 until services_list.size) {
                if (isFixed) {
                    if (services_list.get(i).priceType.equals("fixed", true)) {
                        adapter_list.add(services_list.get(i))
                        //review_detail_model?.price_type = "Fixed"
                    }
                } else {
                    if (!services_list.get(i).priceType.equals("fixed", true)) {
                        adapter_list.add(services_list.get(i))

                    }
                }

            }


            if (adapter_list.isEmpty()) {
                no_services.visibility = View.GONE
            } else {
                no_services.visibility = View.GONE
            }

            if (isFixed) {
                val fixedOtherServices = otherServicesList.filter {
                    it.priceType.equals("fixed", true)
                }
                if (fixedOtherServices.isNotEmpty()) {
                    otherServices.visibility = View.VISIBLE
                    otherServicesRV.visibility = View.VISIBLE
                } else {
                    otherServices.visibility = View.GONE
                    otherServicesRV.visibility = View.GONE
                }
                otherServicesAdapter?.submitList(fixedOtherServices as ArrayList<OtherServices>)
            } else {
                val fixedOtherServices = otherServicesList.filter {
                    !it.priceType.equals("fixed", true)
                }
                if (fixedOtherServices.isNotEmpty()) {
                    otherServices.visibility = View.VISIBLE
                    otherServicesRV.visibility = View.VISIBLE
                } else {
                    otherServices.visibility = View.GONE
                    otherServicesRV.visibility = View.GONE
                }
                otherServicesAdapter?.submitList(fixedOtherServices as ArrayList<OtherServices>)
            }

            service_adapter?.notifyDataSetChanged()
        }
    }


    fun inits() {
        if (intent != null && intent.hasExtra("review_model")) {
            review_detail_model = intent.getSerializableExtra("review_model") as ReviewDetailModel
            service_id = intent.getStringExtra("service_id")!!
            //review_detail_model?.price_type = "Fixed"
        }

        adapter = MyViewPagerAdapter(this, headersList as ArrayList<String>)
        viewPager_intro.adapter = adapter
        tabLayout.setupWithViewPager(viewPager_intro, true)
    }

    private class MyViewPagerAdapter(var context: Context, var list: ArrayList<String>) :
        PagerAdapter() {

        var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            layoutInflater = LayoutInflater.from(context)
            val view = layoutInflater!!.inflate(R.layout.imageview_layout, container, false)
            val imageView = view.findViewById<ImageView>(R.id.imageview)

            GlideApp.with(context).load(Urls.BASE_URL + list.get(position))
                .error(R.drawable.ic_placeholder)
                .into(imageView)

            container.addView(view)

            imageView.setOnClickListener {
                clickImage(position)
            }

            return view
        }

        /** Click Image */
        private fun clickImage(position: Int) {
            try {
                Intent(context, ImageShow::class.java).also {
                    it.putExtra("imageUrl", list)
                    it.putExtra("position", position)
                    context.startActivity(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

    private fun switch_to_fixed() {
        val view = tv_fixed_btn.background as GradientDrawable
        view.setColor(resources.getColor(R.color._649ff8))
        val view1 = tv_nego_btn.background as GradientDrawable
        view1.setColor(resources.getColor(R.color.colorWhite))
        tv_fixed_btn.setTextColor(resources.getColor(R.color.colorWhite))
        tv_nego_btn.setTextColor(resources.getColor(R.color._649ff8))
        view.setStroke(1, resources.getColor(R.color._649ff8))
        view1.setStroke(1, resources.getColor(R.color._649ff8))
        isFixed = true
        toggle_services()
    }

    private fun switch_to_negotiable() {
        val view = tv_fixed_btn.background as GradientDrawable
        view.setColor(resources.getColor(R.color.colorWhite))
        val view1 = tv_nego_btn.background as GradientDrawable
        view1.setColor(resources.getColor(R.color._649ff8))
        tv_nego_btn.setTextColor(resources.getColor(R.color.colorWhite))
        tv_fixed_btn.setTextColor(resources.getColor(R.color._649ff8))
        view1.setStroke(1, resources.getColor(R.color._649ff8))
        view.setStroke(1, resources.getColor(R.color._649ff8))
        isFixed = false
        toggle_services()
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.tv_fixed_btn -> {
                if (!isFixed) {
                    switch_to_fixed()
                }
            }
            R.id.tv_nego_btn -> {
                if (isFixed) {
                    switch_to_negotiable()
                }
            }

            R.id.tv_next -> {
                val task_list: MutableList<TaskObject> = ArrayList()
                var priceType = ""
                isDifferent = false
                for (i in 0 until services_list.size) {
                    if (services_list.get(i).isSelected) {
                        val taskObject = TaskObject(
                            services_list.get(i).id ?: "",
                            services_list.get(i).name ?: "",
                            services_list.get(i).desc ?: "",
                            services_list.get(i).price ?: 0.0,
                            services_list.get(i).payType ?: ""
                        )
                        task_list.add(taskObject)
                        if (!priceType.equals("") && !priceType.equals(services_list.get(i).priceType)) {
                            isDifferent = true
                        } else {
                            priceType = services_list.get(i).priceType ?: ""
                        }

                    }
                }
                when {
                    task_list.size == 0 -> {
                        showToast("Please select at least 1 service")
                    }
                    isDifferent -> {
                        showToast("Choose either 'Fixed' or 'Negotiable' services at a time.")
                    }
                    else -> {
                        review_detail_model?.price_type = priceType
                        review_detail_model?.task_list = task_list
                        Log.e("sdknjlvsdnj", "dinsvsvdndi=====${review_detail_model}")
                        val intent = Intent(this, RequireServiceActivity::class.java)
                        intent.putExtra("review_model", review_detail_model as Serializable)
                        intent.putParcelableArrayListExtra("time_slots", timeSlotsList)
                        intent.putExtra("providerCity", review_detail_model?.provider_city)
                        startActivity(intent)

                    }
                }
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

    @SuppressLint("SetTextI18n")
    private fun renderSuccessResponse(response: JsonElement) {
        if (!response.isJsonNull) {
            otherServicesList.clear()
            Log.e("renderSuccessResponse", "$response")
            val data: String = MyApp.gson.toJson(response)
            val signupResponse = MyApp.gson.fromJson(data, ServiceProviderInfoResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }

            if (signupResponse.success != null && signupResponse.success.toString().equals(
                    "TRUE",
                    true
                )
            ) {
                headersList.clear()
                for (i in 0 until signupResponse?.data?.gallery?.size!!) {
                    headersList.add(signupResponse.data.gallery.get(i)!!)
                }
                for (i in 0 until signupResponse.data.ratingList?.size!!) {
                    review_list.add(signupResponse.data.ratingList.get(i)!!)
                }
//                var service_position = 0
                adapter_list.clear()
                for (i in 0 until signupResponse.data.serviceList?.size!!) {
//                    if(signupResponse?.data?.serviceList?.get(i)?.service?.get(0)?.id.equals(service_id)) {
                    services_list.add(signupResponse.data.serviceList.get(i)!!)
                    services_list.get(i).position = i

                    if (signupResponse.data.serviceList.get(i)?.priceType.equals("fixed", true)) {
                        //list.add(signupResponse.data.serviceList.get(i)!!)
                        adapter_list.add(signupResponse.data.serviceList.get(i)!!)
                    }
//                        service_position++
//                    }
                }

                var seletedpricetype: String = ""

                for (i in services_list.indices) {
                    for (j in services_list.get(i).service!!.indices) {
                        if (services_list.get(i).service?.get(j)?.name.equals(
                                service_nameforcheck,
                                true
                            )
                        )
                            seletedpricetype = services_list.get(i).priceType ?: ""
                    }
                }

                val fixedList = services_list.filter { it.priceType == "Fixed" }


                otherServicesList.addAll(signupResponse.data.otherServices)
                /*Log.e("kmcksdncksdk", "sdasdsadasd   ${signupResponse.data.name}")
                otherServices.text = "${signupResponse.data.name} provides other services"
*/

                if (fixedList.isNotEmpty()) {
                    switch_to_fixed()
                } else {
                    switch_to_negotiable()
                }

                /*if (seletedpricetype.isNotEmpty())
                {
                    if (seletedpricetype.equals("fixed", true))
                        switch_to_fixed()
                    else
                        switch_to_negotiable()
                }
                else {
                    if (adapter_list.size == 0) {
                        switch_to_negotiable()
                    } else {
                        switch_to_fixed()
                    }
                }*/

                tv_name.text = signupResponse.data.name
                /*otherServices.text = "${signupResponse.data.name} provides other services"
                otherServices.setBackgroundResource(R.drawable.delivery_back)*/
                rating_bar.rating = (signupResponse.data.avgRating ?: 0f)
                if (signupResponse.data.ratingList.size < 2) {
                    tv_ratings_num.text = "(" + signupResponse.data.ratingList.size + " Review)"
                } else {
                    tv_ratings_num.text = "(" + signupResponse.data.ratingList.size + " Reviews)"
                }
//                tv_exp.setText(signupResponse.data.firstName)
                tv_desc.text = signupResponse.data.bio
                tv_no_about.text = signupResponse.data.bio
                tv_name.text = signupResponse.data.name
                review_detail_model?.provider_id = signupResponse.data.id.toString()
                review_detail_model?.provider_name = tv_name.text.toString()
                review_detail_model?.provider_address = signupResponse.data.address ?: ""
                review_detail_model?.provider_city = signupResponse.data.city ?: ""
                review_detail_model?.provider_rating = signupResponse.data.avgRating ?: 0f
                review_detail_model?.provider_review_num = signupResponse.data.ratingList.size
                timeSlotsList =
                    (signupResponse.data.timeSlotList as ArrayList<TimeSlotListItem?>?)!!

                otherServices1.text = "${signupResponse.data.name} provides other services"
                adapter.notifyDataSetChanged()
                service_adapter?.notifyDataSetChanged()

            } else {
                showSnackBar(signupResponse.message!!)
            }
        }
    }
}