package com.geniecustomer.view.fragments


import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.geniecustomer.R
import com.geniecustomer.adapters.TrendingMain
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.interfaces.OnCategoryClicked
import com.geniecustomer.model.Address
import com.geniecustomer.view.activities.ServiceManListActivity
import com.geniecustomer.view.fragments.trendingResponse.Data
import com.geniecustomer.view.fragments.trendingResponse.TrendingResponse
import com.geniecustomer.viewmodels.GetDashboardDataViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_trending.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class TrendingFragment : Fragment() {

    var adapter: TrendingMain? = null
    val list: ArrayList<Data> = ArrayList()
    var iv_logo_width = 0f
    val model by viewModel<GetDashboardDataViewModel>()
    var service_id1 = ""
    var category_id1 = ""
    var service_name1 = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_logo.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                iv_logo_width = iv_logo.measuredWidth.toFloat()
                Log.e("iv_logo_width",""+iv_logo.measuredWidth)

                iv_logo.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })

       /* list_of_headers.add(service_obj("id","Handyman"))
        list_of_headers.add(service_obj("id","Parties & Events"))


        val arraylist1 = arrayListOf<service_obj>(service_obj("id","Home Repairs")
            ,service_obj("id","Furniture Assembly"))
        val arraylist2 = arrayListOf<service_obj>(service_obj("id","Party Poppers"))*/

        //list_of_sub_items.add(arraylist1)
        //list_of_sub_items.add(arraylist2)

        adapter = TrendingMain(activity!!, list)

        listerner()
        model.observeDataResponse().observe(activity!!, Observer<ApiResponse> { this.consumeResponse(it!!) })
        model.getTrending(10,0)

        recycler_view.adapter = adapter
        recycler_view.layoutManager = GridLayoutManager(activity!!, 2)

        appbarlayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                appBarLayout?.post {
                    if (iv_logo != null) {
                        iv_logo.layoutParams.width =
                            (iv_logo_width - ((iv_logo_width * (Math.abs(verticalOffset / appBarLayout.totalScrollRange.toFloat()))) / 1.7)).toInt()
                        //Log.e("value",""+((iv_logo_width * (Math.abs(verticalOffset/ appBarLayout?.totalScrollRange!!)))/2))
                        // matrix.setScale(Math.abs(verticalOffset/ appBarLayout?.totalScrollRange!!).toFloat()/2f,1f)
                        iv_logo.requestLayout()
                        //})
                    }
                }
            }

        })
        tv_loc.setOnClickListener {
            Places.initialize(activity!!, getString(R.string.places_api_key))
            val placesClient = Places.createClient(activity!!)
            val fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS_COMPONENTS
            )
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(activity!!)
            startActivityForResult(intent, 178)
        }
    }


    fun listerner(){
       adapter!!.onCategoryClicked(object : OnCategoryClicked {
           override fun onClick(service_id: String, category_id: String, service_name: String) {
               if (tv_loc.text.toString().isEmpty()) {

                   if (tv_loc.text.isEmpty()) {
                       (activity as BaseActivity).setLocation()
                       service_id1 = service_id
                       category_id1 = category_id
                       service_name1 = service_name
                   }
               } else {
                   startActivity(
                       Intent(context, ServiceManListActivity::class.java)
                           .putExtra("service_id", category_id)
                           .putExtra("category_id", service_id)
                           .putExtra("city", tv_loc.text)
                           .putExtra("service_name", service_name)
                   )
               }


           }
       })

    }

    fun onFragmentLocationFetched(){
        Log.e("aagya","yhaaaa")
        if((activity as BaseActivity).sharedPref.contains("address")){
            val address = MyApp.gson.fromJson((activity as BaseActivity).sharedPref.getString("address",""),Address::class.java)
            try {
                val geocoder = Geocoder(activity!!, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(address.latitude ?: 0.0, address.longitude ?: 0.0, 1)
                val cityName = addresses[0].locality
                (activity as BaseActivity).editor.putString("location", cityName).apply()
                tv_loc.text = cityName

                startActivity(
                    Intent(context, ServiceManListActivity::class.java)
                        .putExtra("service_id", service_id1)
                        .putExtra("category_id", category_id1)
                        .putExtra("city", tv_loc.text.toString())
                        .putExtra("service_name", service_name1)
                )


            }catch (e:java.lang.Exception){
                Log.e("error","city"+e.message)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("activity_result","true")
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == 178){
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Log.e(
                    "call",
                    "Place: " + place.name + ", " + place.id + " , " + place.latLng + " , " + place.addressComponents
                )

                val latlng = LatLng(place.latLng?.latitude ?: 0.0, place.latLng?.longitude ?: 0.0)
                val address = Address(place.name, latlng.latitude, latlng.longitude)
                val gson = MyApp.gson.toJson(address)
                (activity as BaseActivity).editor.putString("address", gson).apply()

                try {
                    val geocoder = Geocoder(activity!!, Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(address.latitude ?: 0.0, address.longitude ?: 0.0, 1)
                    val cityName = addresses[0].locality
                    tv_loc.text = cityName

                } catch (e: java.lang.Exception) {
                    Log.e("error", "city" + e.message)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if((activity as BaseActivity).sharedPref.contains("address")){
            val address = MyApp.gson.fromJson((activity as BaseActivity).sharedPref.getString("address",""),Address::class.java)
            try {
                val geocoder = Geocoder(activity!!, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(address.latitude ?: 0.0, address.longitude ?: 0.0, 1)
                val cityName = addresses[0].locality
                (activity as BaseActivity).editor.putString("location", cityName).apply()
                tv_loc.text = cityName
            }catch (e:java.lang.Exception){
                Log.e("error","city"+e.message)
            }
        }
    }

    private fun consumeResponse(apiResponse: ApiResponse) {

        when (apiResponse.status) {

            Status.LOADING -> (activity!! as BaseActivity).showProgress()

            Status.SUCCESS -> {
                (activity!! as BaseActivity).hideProgress()
                try {
                    renderSuccessResponse(apiResponse.data!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Status.ERROR -> {
                (activity!! as BaseActivity).hideProgress()
                Log.e("FAILURE", apiResponse.error?.message ?: "")
                (activity!! as BaseActivity).showSnackBar(apiResponse.error?.message ?: "")
            }

            else -> {
            }
        }
    }

    private fun renderSuccessResponse(response: JsonElement) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)
            Log.e("DATA"," "+data)
            val trendingmodel = MyApp.gson.fromJson(data, TrendingResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
            if (trendingmodel.data!!.isEmpty()){
                tvNoData?.visibility = View.VISIBLE
            }else{
                tvNoData?.visibility = View.GONE
            }
            if (trendingmodel.success!!) {
                list.addAll(trendingmodel.data!!)
                adapter?.notifyDataSetChanged()
            } else {
                (activity!! as BaseActivity).showSnackBar(trendingmodel.message!!)
            }

        }
    }


}
