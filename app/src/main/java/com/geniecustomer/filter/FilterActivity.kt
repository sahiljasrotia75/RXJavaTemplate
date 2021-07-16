package com.geniecustomer.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.adapter.FilterLeftRvAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.interfaces.HitFilterApi
import com.geniecustomer.interfaces.RvClickPostion
import com.geniecustomer.model.Address
import com.geniecustomer.model.FilterListDataModel
import com.geniecustomer.model.dashboard.DashboardServicesListResponse
import com.geniecustomer.model.dashboard.DataItem
import com.geniecustomer.model.service_providers.ServiceProvidersListRequest
import com.geniecustomer.model.service_providers.ServiceProvidersListResponse
import com.geniecustomer.model.service_providers.service_obj
import com.geniecustomer.viewmodels.FliterDataViewModel
import com.geniecustomer.viewmodels.GetDashboardDataViewModel
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_filter.*
import org.koin.android.viewmodel.ext.android.viewModel

class FilterActivity : BaseActivity(),RvClickPostion, HitFilterApi
{

    private var catItems = ArrayList<FilterListDataModel>()
    private lateinit var filterListDataModel:FilterListDataModel
    private var leftcateAdapter: FilterLeftRvAdapter? = null
    lateinit var filterList: ArrayList<String>
    private var filterAdapter: CheckBoxApdater? = null
    var  prevpos :Int = 0

    val model by viewModel<GetDashboardDataViewModel>()
    val filterViewModel by viewModel<FliterDataViewModel>()


    var list : MutableList<DataItem> = ArrayList()
    var listForItems : MutableList<service_obj> = ArrayList()

    val list_of_sub_items : MutableList<service_obj> = ArrayList()
    var category_id: String = ""
    var categoryName: String = ""
    var service_id: String = ""
    var city: String = ""
    var serviceType: String = ""
    var type: String = "category"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        if (intent != null && intent.hasExtra("category_id")) {
            service_id = intent.getStringExtra("category_id")!!
            category_id = intent.getStringExtra("service_id")!!
            city = intent.getStringExtra("city")!!
            serviceType = intent.getStringExtra("serviceType")!!
            Log.e("CATE_ID"," "+category_id)
            Log.e("SERVICE_ID"," "+service_id)
        }



        model.observeDataResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!,1)
        })

        filterViewModel.observeDataResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!,2)
        })
        Handler().postDelayed({
            model.hitGetDashboarddata(user_obj?.token?:"")
        },200)

        loadLeftRv()
        loadRightRv()
    }

    fun loadLeftRv()
    {

        filterListDataModel = FilterListDataModel("CATEGORIES","#ffffff")
        catItems.add(filterListDataModel)

        filterListDataModel = FilterListDataModel("SERVICES","#f2f2f2")
        catItems.add(filterListDataModel)


        leftcateAdapter = FilterLeftRvAdapter(this, catItems, this)

        rvCategory.adapter = leftcateAdapter
        rvCategory.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    fun loadRightRv() {
        filterList = ArrayList()
        filterAdapter = CheckBoxApdater(listForItems as ArrayList<service_obj>, this, this)
        rvItems.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvItems.adapter = filterAdapter
        filterCatListerner()
    }

    override fun onRvItemClicked(position: Int) {
        catItems[prevpos].bgcolor = "#f2f2f2"
        catItems[position].bgcolor = "#ffffff"
        prevpos = position

        if (position == 0) {
            type = "category"
            listForItems.clear()
            for(i in 0 until list.size){
                val model = service_obj()
                model.name = list.get(i).name?:""
                model.id = list.get(i).id?:""
                model.selected = category_id.equals(list.get(i).id)
                listForItems.add(model)
            }
        } else if (position == 1) {
            type = "service"
            listForItems.clear()
            for( i in list_of_sub_items.indices) {
                val model = service_obj()
                model.name = list_of_sub_items.get(i).name
                model.id = list_of_sub_items.get(i).id
                model.selected = service_id.equals(list_of_sub_items.get(i).id)
                listForItems.add(model)
            }
        }
        leftcateAdapter!!.notifyDataSetChanged()
        filterAdapter!!.notifyDataSetChanged()
    }


    private fun filterCatListerner() {
        filterAdapter!!.setOnclickItem(object : CheckBoxApdater.Clicklisterner {
            override fun ClickListerner(position: Int, checked: Boolean) {

                for (i in 0 until listForItems.size) {
                    //val model = list.get(i)
                    val itemsList = listForItems.get(i)
                    if (checked) {
                        if (i == position) {
                            itemsList.selected = true
                            if (type.equals("category")) {
                                list_of_sub_items.clear()
                                category_id = listForItems.get(position).id
                                categoryName = listForItems.get(position).name
                                Log.e("CATEORY", "ID " + category_id)
                                service_id = ""
                                for (i in 0 until list.get(position).serviceList?.size!!) {
                                    val addlist = service_obj()
                                    addlist.id = list.get(position).serviceList?.get(i)?.id!!
                                    addlist.name = list.get(position).serviceList?.get(i)?.name!!
                                    addlist.selected = true
                                    list_of_sub_items.add(addlist)
                                }
                            } else {
                                service_id = listForItems.get(position).id
                                Log.e("SERCIE", "ID " + service_id)
                            }

                        } else {
                            itemsList.selected = false
                        }
                    } else {
                        itemsList.selected = false
                    }

                    listForItems.set(i, itemsList)
                }
                if (!rvItems.isComputingLayout) {
                    filterAdapter!!.notifyDataSetChanged()
                }

            }

        })
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when(v!!.id){
            R.id.ivBack -> {
                finish()
            }
            R.id.applyBtn -> {
                if(service_id.isNotEmpty()){
                    val i = Intent()
                        i.putExtra("category_id",category_id)
                        i.putExtra("service_id",service_id)
                        i.putExtra("category_name",categoryName)
                        setResult(Activity.RESULT_OK,i)
                        finish()
                    }
                else
                    showToast("Please select service")
            }
        }
    }


    private fun consumeResponse(apiResponse: ApiResponse, i: Int) {

        when (apiResponse.status) {

            Status.LOADING -> showProgress()

            Status.SUCCESS -> {
              hideProgress()
                try {
                    renderSuccessResponse(apiResponse.data!!,i)
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

            if(type == 1){
                val signupResponse = MyApp.gson.fromJson(data, DashboardServicesListResponse::class.java)
                list.clear()
                listForItems.clear()
                if (signupResponse.success != null && signupResponse.success.toString().equals("TRUE", true)) {
                    for(i in 0 until signupResponse?.data?.size!!){

                        list.add(signupResponse.data.get(i)!!)

                        val model = service_obj()
                        model.name = signupResponse.data.get(i)?.name?:""
                        model.id = signupResponse.data.get(i)?.id?:""
                        model.selected = category_id.equals(signupResponse.data.get(i)?.id)
                        listForItems.add(model)
                        // Log.e("ID","CATE "+signupResponse.data.get(i)?.id!!)

                        if(category_id.equals(signupResponse.data.get(i)?.id)){
                        categoryName = signupResponse.data.get(i)?.name!!
                            for(j in 0 until list.get(i).serviceList?.size!!){
                                Log.e("ID","SERVICE "+list.get(i).serviceList?.get(j)?.id!!)
                                val addlist = service_obj()
                                addlist.id = list.get(i).serviceList?.get(j)?.id!!
                                addlist.name = list.get(i).serviceList?.get(j)?.name!!
                                addlist.selected = service_id.equals(list.get(i).serviceList?.get(j)?.id!!)
                                list_of_sub_items.add(addlist)
                            }
                            Log.e("Service ","LISt " +list_of_sub_items.size)
                        }
                    }
                    filterAdapter!!.notifyDataSetChanged()
                } else {
                    showSnackBar(signupResponse.message!!)
                }
            }else{
                val signupResponse = MyApp.gson.fromJson(data, ServiceProvidersListResponse::class.java)
                tvFilterCount.text = "" + signupResponse?.count

//                Handler().postDelayed({
//
//                    val i = Intent()
//                    i.putExtra("category_id",category_id)
//                    i.putExtra("service_id",service_id)
//                    i.putExtra("category_name",categoryName)
//                    setResult(Activity.RESULT_OK,i)
//                    finish()
//                },200)
            }

        }
    }

    fun hitApi(type : String){
        val address = MyApp.gson.fromJson((this as BaseActivity).sharedPref.getString("address",""),
            Address::class.java)
        val providersListRequest = ServiceProvidersListRequest("", city, service_id, 10,address.latitude,address.longitude, 0, category_id,type , "nameAsc")
        filterViewModel.hitGetProvidersList(user_obj?.token ?: "", providersListRequest)
    }

    override fun onHitFilter(hit: Boolean) {
        if (type == "service")
        hitApi(serviceType)
    }

}
