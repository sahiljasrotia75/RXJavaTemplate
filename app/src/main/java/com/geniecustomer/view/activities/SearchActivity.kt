package com.geniecustomer.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.geniecustomer.R
import com.geniecustomer.adapter.SearchAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.search_model.DataItem
import com.geniecustomer.model.search_model.SearchResponse
import com.geniecustomer.model.service_providers.service_obj
import com.geniecustomer.viewmodels.BookingViewModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_search.*
import org.koin.android.viewmodel.ext.android.viewModel


class SearchActivity : BaseActivity() {

    var adapter : SearchAdapter? = null
   // val list : MutableList<DataItem>? = ArrayList()
    val list : MutableList<DataItem>? = ArrayList()
    val list_of_sub_items : MutableList<ArrayList<service_obj>> = ArrayList()
    val viewModel by viewModel<BookingViewModel>()
   /* val catlist1 : ArrayList<DataItem> = ArrayList()
    val servicelist1 : ArrayList<DataItem> = ArrayList()*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

//        list_of_headers.add("Categories")
//        list_of_headers.add("Service")
//
//        val arraylist1 = arrayListOf<String>("Holidays","Handyman")
//        val arraylist2 = arrayListOf<String>("Home Repairs")
//
//        list_of_sub_items.add(arraylist1)
//        list_of_sub_items.add(arraylist2)

        /*adapter = TrendingMainAdapter(this, list_of_headers as ArrayList<service_obj>,
            list_of_sub_items as ArrayList<ArrayList<service_obj>>,false
        )*/

       // adapter = TrendingMainAdapter(this,list)
        adapter = SearchAdapter(this,list!!)
            viewModel.bookingResponse().observe(this, Observer<ApiResponse> { this.consumeResponse(it!! ) })

        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        listerner()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        search_bar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // shimmer_view_container.visibility = View.VISIBLE
                //  shimmer_view_container.startShimmerAnimation()
                var item :String =  s.toString()
                searchItems(item)
            }
        })
    }

    private fun listerner() {
        adapter!!.setonClickItem(object  : SearchAdapter.ClickItem{
            override fun onClickItem(position: Int) {
                if(list?.size != 0){
                    if(list?.get(0)?.name.equals("Categories")){
                        val i = Intent()
                        i.putExtra("id",list?.get(0)?.list?.get(position)?.id)
                        setResult(Activity.RESULT_OK , i)
                        finish()
                    }
                }

            }
        })
    }

    fun searchItems(s :String) {
        val jsonElement = JsonObject()
        jsonElement.addProperty("searchText",s)
        Log.e("Json"," sent "+jsonElement.toString())
        viewModel.search(jsonElement)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when(v!!.id){
            R.id.iv_back -> {
                hideKeyboard()
                finish()
            }
        }
    }

    private fun consumeResponse(apiResponse: ApiResponse) {

        when (apiResponse.status) {

            Status.LOADING -> {
                //showProgress()
            }

            Status.SUCCESS -> {
                //hideProgress()
                try {
                    renderSuccessResponse(apiResponse.data!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Status.ERROR -> {
                //hideProgress()
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
            Log.e("reponse "," "+response.toString())
            try{
                val model = MyApp.gson.fromJson(data , SearchResponse::class.java)
                if(model.success!!){
                    list!!.clear()
                    list.addAll(model.data!!)
                    adapter!!.notifyDataSetChanged()

                }else{
                    showToast(model.message!!)
                }
            }catch (e : Exception){
                Log.e("EXCEPTION "," "+e)
            }

        }
    }
}
