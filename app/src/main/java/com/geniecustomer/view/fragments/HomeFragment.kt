package com.geniecustomer.view.fragments


import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.adapters.HomeGridAdapter
import com.geniecustomer.adapters.TrendingMainAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.interfaces.HomeItemClick
import com.geniecustomer.interfaces.OnCategoryClicked
import com.geniecustomer.model.Address
import com.geniecustomer.model.dashboard.DashboardServicesListResponse
import com.geniecustomer.model.dashboard.DataItem
import com.geniecustomer.model.service_providers.service_obj
import com.geniecustomer.view.activities.SearchActivity
import com.geniecustomer.view.activities.ServiceManListActivity
import com.geniecustomer.view.register.LoginActivity
import com.geniecustomer.viewmodels.GetDashboardDataViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(),View.OnClickListener {

    var adapter : HomeGridAdapter? =null
    var dialog : BottomSheetDialog?=null
    var list : MutableList<DataItem> = ArrayList()
    var item_adapter : TrendingMainAdapter? = null
    val list_of_headers : MutableList<service_obj> = ArrayList()
    val list_of_sub_items : MutableList<ArrayList<service_obj>> = ArrayList()
    var iv_logo_width = 0f
    var service_id1 = ""
    var category_id1 = ""
    var service_name1 = ""

    val model by viewModel<GetDashboardDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        Log.e(
            "LocationFetch",
            " ssssssssssssssssss      ${
                (activity as BaseActivity).sharedPref.getString(
                    "address",
                    ""
                )
            }"
        )
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.observeDataResponse().observe(viewLifecycleOwner, Observer<ApiResponse> {
            this.consumeResponse(it!!)
        })

        inits()

        iv_logo.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                iv_logo_width = iv_logo.measuredWidth.toFloat()
                Log.e("iv_logo_width",""+iv_logo.measuredWidth)
                iv_logo.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
    }

    fun inits(){

        adapter = HomeGridAdapter(activity!!,list as ArrayList<DataItem>)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = GridLayoutManager(activity!!,2)

        adapter?.setOnHomeItemClick(object : HomeItemClick {
            override fun onClick(position: Int) {
                if ((activity as BaseActivity).user_obj != null) {
//                    if ((activity as BaseActivity).user_obj?.phone != null && (activity as BaseActivity).user_obj?.email != null)
//                    {
                    val dialogView = (context as BaseActivity).layoutInflater.inflate(
                        R.layout.select_home_grid_item,
                        null,
                        false
                    )
                    dialog = BottomSheetDialog(activity!!, R.style.TransparentDialog)
                    val recycler_view1 = dialogView.findViewById<RecyclerView>(R.id.recycler_view)
                    val iv_close_dialog = dialogView.findViewById<ImageView>(R.id.iv_close_dialog)

                    iv_close_dialog.setOnClickListener {
                        dialog?.dismiss()
                    }
                    list_of_headers.clear()
                    list_of_sub_items.clear()

                    list_of_headers.add(
                        service_obj(
                            list.get(position).id ?: "",
                            list.get(position).name ?: ""
                        )
                    )
                    val arraylist = ArrayList<service_obj>()
                    for (i in 0 until list.get(position).serviceList?.size!!)
                        arraylist.add(
                            service_obj(
                                list.get(position).serviceList?.get(i)?.id!!,
                                list.get(position).serviceList?.get(i)?.name!!
                            )
                        )

                    list_of_sub_items.add(arraylist)

                    item_adapter = TrendingMainAdapter(
                        activity!!, list_of_headers as ArrayList<service_obj>,
                        list_of_sub_items as ArrayList<ArrayList<service_obj>>, true
                    )

                    item_adapter?.onCategoryClicked(object : OnCategoryClicked {
                        override fun onClick(
                            service_id: String,
                            category_id: String,
                            service_name: String
                        ) {
                            dialog?.dismiss()

                            if (tv_loc.text.isEmpty()) {
                                (activity as BaseActivity).setLocation()
                                service_id1 = service_id
                                category_id1 = category_id
                                service_name1 = service_name
                            } else {
                                startActivity(
                                    Intent(context, ServiceManListActivity::class.java)
                                        .putExtra("service_id", service_id)
                                        .putExtra("category_id", category_id)
                                        .putExtra("city", tv_loc.text)
                                        .putExtra("service_name", service_name)
                                )
                            }
                        }
                    })

                    recycler_view1.adapter = item_adapter
                    recycler_view1.layoutManager = LinearLayoutManager(
                        activity!!,
                        LinearLayoutManager.VERTICAL, false
                    )

                    dialog?.setContentView(dialogView)
                    dialog?.show()
//                    }
//                    else if ((activity as BaseActivity).user_obj?.phone == null)
//                    {
//                        if (HomeActivity.phonePopUp?.visibility == View.GONE)
//                            HomeActivity.phonePopUp?.visibility = View.GONE
//                            HomeActivity.extras = "phone"
//                    }
//                    else if ((activity as BaseActivity).user_obj?.email == null)
//                    {
//                        if (HomeActivity.emailPopUp?.visibility == View.GONE)
//                            HomeActivity.emailPopUp?.visibility = View.GONE
//                        HomeActivity.extras = "email"
//                    }
                } else {
                    val dialogView = (context as BaseActivity).layoutInflater.inflate(
                        R.layout.select_home_grid_item,
                        null
                    )
                    dialog = BottomSheetDialog(activity!!, R.style.TransparentDialog)
                    val recycler_view1 =
                        dialogView.findViewById<RecyclerView>(R.id.recycler_view)
                    val iv_close_dialog =
                        dialogView.findViewById<ImageView>(R.id.iv_close_dialog)

                    iv_close_dialog.setOnClickListener {
                        dialog?.dismiss()
                    }
                    list_of_headers.clear()
                    list_of_sub_items.clear()

                    list_of_headers.add(
                        service_obj(
                            list.get(position).id ?: "",
                            list.get(position).name ?: ""
                        )
                    )
                    val arraylist = ArrayList<service_obj>()
                    for (i in 0 until list.get(position).serviceList?.size!!)
                        arraylist.add(
                            service_obj(
                                list.get(position).serviceList?.get(i)?.id!!,
                                list.get(position).serviceList?.get(i)?.name!!
                            )
                        )

                    list_of_sub_items.add(arraylist)

                    item_adapter = TrendingMainAdapter(
                        activity!!, list_of_headers as ArrayList<service_obj>,
                        list_of_sub_items as ArrayList<ArrayList<service_obj>>, true
                    )

                    item_adapter?.onCategoryClicked(object : OnCategoryClicked {
                        override fun onClick(
                            service_id: String,
                            category_id: String,
                            service_name: String
                        ) {
                            dialog?.dismiss()
                            if (tv_loc.text.isEmpty()) {
                                (activity as BaseActivity).setLocation()
                                service_id1 = service_id
                                category_id1 = category_id
                                service_name1 = service_name
                            } else {
                                startActivity(
                                    Intent(context, ServiceManListActivity::class.java)
                                        .putExtra("service_id", service_id)
                                        .putExtra("category_id", category_id)
                                        .putExtra("city", tv_loc.text)
                                        .putExtra("service_name", service_name)
                                )
                            }
                        }
                    })

                    recycler_view1.adapter = item_adapter
                    recycler_view1.layoutManager = LinearLayoutManager(
                        activity!!,
                        LinearLayoutManager.VERTICAL, false
                    )

                    dialog?.setContentView(dialogView)
                    dialog?.show()
                }
            }

        })

        tv_search.setOnClickListener(this)
        iv_noti.setOnClickListener(this)
        tv_loc.setOnClickListener(this)

        appbarlayout.addOnOffsetChangedListener(object :AppBarLayout.OnOffsetChangedListener{
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                appBarLayout?.post {
                    if(iv_logo!=null) {
                        iv_logo.layoutParams.width = (iv_logo_width - ((iv_logo_width * (abs(verticalOffset / appBarLayout.totalScrollRange.toFloat()))) / 1.7)).toInt()
                        iv_logo.requestLayout()
                    }
                }
            }

        })

        try {
            Handler().postDelayed({
                model.hitGetDashboarddata((activity as BaseActivity).user_obj?.token ?: "")
            }, 200)
        }catch (e:Exception){ }

    }

    fun onFragmentLocationFetched(){
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

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_search ->{
                val search_intent = Intent(activity ,SearchActivity::class.java)
                startActivityForResult(search_intent , 180)
            }
            R.id.iv_noti ->{
                Log.e("clicked","true")
            }
            R.id.tv_loc ->{
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("activity_result","true")
        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == 178)
            {
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
            else if(requestCode == 180)
            {
                  if(data?.hasExtra("id")!!){
                      Log.e("SET_DATA"," Search "+data.getStringExtra("id")!!)

                      var id = data.getStringExtra("id")!!

                      var position  : Int = 0
                      for(i in list.indices){
                          if(list.get(i).id.equals(id)){
                              position = i
                              break
                          }

                      }

                      Handler().postDelayed({
                          val dialogView = (context as BaseActivity).layoutInflater.inflate(
                              R.layout.select_home_grid_item,
                              null
                          )
                          dialog = BottomSheetDialog(activity!!, R.style.TransparentDialog)
                          val recycler_view1 = dialogView.findViewById<RecyclerView>(R.id.recycler_view)
                          val iv_close_dialog = dialogView.findViewById<ImageView>(R.id.iv_close_dialog)

                          iv_close_dialog.setOnClickListener {
                              dialog?.dismiss()
                          }
                          list_of_headers.clear()
                          list_of_sub_items.clear()

                          list_of_headers.add(service_obj(list.get(position).id?:"",list.get(position).name?:""))
                          val arraylist = ArrayList<service_obj>()
                          for(i in 0 until list.get(position).serviceList?.size!!)
                              arraylist.add(service_obj(list.get(position).serviceList?.get(i)?.id!!,list.get(position).serviceList?.get(i)?.name!!))

                          list_of_sub_items.add(arraylist)

                          item_adapter = TrendingMainAdapter(activity!!, list_of_headers as ArrayList<service_obj>,
                              list_of_sub_items as ArrayList<ArrayList<service_obj>>,true
                          )

                          item_adapter?.onCategoryClicked(object:OnCategoryClicked{
                              override fun onClick(service_id: String, category_id: String, service_name : String) {
                                  dialog?.dismiss()
                                  if(tv_loc.text.isEmpty()){
                                      (activity as BaseActivity).setLocation()
                                      service_id1 = service_id
                                      category_id1 = category_id
                                      service_name1 = service_name
                                  }else{
                                      startActivity(Intent(context, ServiceManListActivity::class.java)
                                          .putExtra("service_id",service_id)
                                          .putExtra("category_id",category_id)
                                          .putExtra("city",tv_loc.text)
                                          .putExtra("service_name",service_name))
                                  }
                              }
                          })

                          recycler_view1.adapter = item_adapter
                          recycler_view1.layoutManager = LinearLayoutManager(activity!!,
                              LinearLayoutManager.VERTICAL,false)

                          dialog?.setContentView(dialogView)
                          dialog?.show()

                      },150)


                  }
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

            val signupResponse =
                MyApp.gson.fromJson(data, DashboardServicesListResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
            if (signupResponse.success != null && signupResponse.success.toString().equals(
                    "TRUE",
                    true
                )
            ) {
                for (i in 0 until signupResponse?.data?.size!!) {
                    list.add(signupResponse.data.get(i)!!)
                }
                adapter?.notifyDataSetChanged()
            } else {
                if (signupResponse.message!!.contains("403")){
                    showSessionExpiredDialog()
                }else{
                    (activity!! as BaseActivity).showSnackBar(signupResponse.message)
                }
            }

        }
    }

    private fun showSessionExpiredDialog() {
        AlertDialog.Builder(activity!!)
            .setMessage(getString(R.string.sessionExpired))
            .setPositiveButton(
                getString(android.R.string.ok)
            ) { p0, p1 ->
                (activity!! as BaseActivity).editor.clear().apply()
                activity!!.startActivity(
                    Intent(
                        context!!,
                        LoginActivity::class.java
                    )
                )
                activity!!.finishAffinity()
            }.show()
    }


}
