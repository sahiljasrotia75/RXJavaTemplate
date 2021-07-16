package com.geniecustomer.view.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.geniecustomer.R
import com.geniecustomer.adapters.BookingAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.booking.Data
import com.geniecustomer.model.booking_history.BookingHistoryResponse
import com.geniecustomer.view.register.LoginActivity
import com.geniecustomer.viewmodels.BookingViewModel
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_booking.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class BookingFragment : Fragment() {

    var list: MutableList<Data> = ArrayList()
    val onGoinglist: MutableList<Data> = ArrayList()
    val pastlist: MutableList<Data> = ArrayList()
    var adapter: BookingAdapter? = null
    val viewModel by viewModel<BookingViewModel>()
    var type = 0
    var debouncePeriod: Long = 300

    // private val coroutineScope = lifecycle.coroutineScope
    private var searchJob: Job? = null

    /** @Mukesh New Design Change */


    companion object {
        var backfromPast = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BookingAdapter(activity!!, list, type)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        observer()
        hitApi()

        tab_layout.addTab(tab_layout.newTab().setText("Ongoing"))
        tab_layout.addTab(tab_layout.newTab().setText("Past"))
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                clNoDataLogo.visibility = View.GONE
                type = tab?.position!!
                adapter?.updateType(type)

                searchJob = CoroutineScope(Main).launch {
                    delay(debouncePeriod)
                    if (type == 0) {
                        list.clear()
                        list.addAll(onGoinglist)
                        if (list.isEmpty()) {
                            clNoDataLogo.visibility = View.VISIBLE
                        } else {
                            clNoDataLogo.visibility = View.GONE
                        }
                        adapter!!.notifyDataSetChanged()

                    } else {
                        list.clear()
                        list.addAll(pastlist)
                        if (list.isEmpty()) {
                            clNoDataLogo.visibility = View.VISIBLE
                        } else {
                            clNoDataLogo.visibility = View.GONE
                        }
                        adapter!!.notifyDataSetChanged()
                    }
                    searchJob?.cancel()
                }


            }

        })
    }

    private fun observer() {
        viewModel.bookingResponse().observe(activity!!, Observer { this.consumeResponse(it!!) })
    }

    private fun hitApi() {
        Log.e("BOOKING_FRAGMENT", " " + (context as BaseActivity).user_obj?.id)
        //(context as BaseActivity).user_obj

        if ((context as BaseActivity).user_obj == null) {
            AlertDialog.Builder(activity!!)
                .setMessage(getString(R.string.login_proceed))
                .setPositiveButton(
                    getString(R.string.yes)
                ) { p0, p1 ->
                    (context as BaseActivity).editor.putBoolean("guest", true).apply()
                    startActivity(Intent(activity!!, LoginActivity::class.java))
                    activity!!.finishAffinity()
                }.setNegativeButton(R.string.no, null).show()


        } else {
            val token = (context as BaseActivity).user_obj?.token
            val jsonObject = JsonObject()
            jsonObject.addProperty("limit", 10)
            jsonObject.addProperty("page", 1)
            jsonObject.addProperty("skip", "")
            viewModel.historyBooking(token!!, jsonObject)
        }
    }

    private fun consumeResponse(apiResponse: ApiResponse) {

        when (apiResponse.status) {

            Status.LOADING -> (activity as BaseActivity).showProgress()

            Status.SUCCESS -> {
                try {
                    renderSuccessResponse(apiResponse.data!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Status.ERROR -> {
                (activity as BaseActivity).hideProgress()
                Log.e("FAILURE", apiResponse.error?.message ?: "")
                (activity as BaseActivity).showSnackBar(apiResponse.error?.message ?: "")
            }
        }
    }

    private fun renderSuccessResponse(response: JsonElement) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)
            Log.e("response=", data)

            try {
                val model = MyApp.gson.fromJson(data, BookingHistoryResponse::class.java)

              if (model.success!!) {
                    list.clear()
                    pastlist.clear()
                    onGoinglist.clear()

                    if(type == 0)
                       list.addAll(model.data?.ongoing!!)
                    else
                        list.addAll(model.data?.pastOrder!!)

                    onGoinglist.addAll(model.data.ongoing!!)
                    pastlist.addAll(model.data.pastOrder!!)

                    if (type == 0) {
                        if (onGoinglist.isEmpty()) {
                            clNoDataLogo.visibility = View.VISIBLE
                        } else {
                            clNoDataLogo.visibility = View.GONE
                        }
                    } else {
                        if (pastlist.isEmpty()) {
                            clNoDataLogo.visibility = View.VISIBLE
                        } else {
                            clNoDataLogo.visibility = View.GONE
                        }
                    }

                    Log.e("SIZE", " " + list.size)
                    adapter!!.notifyDataSetChanged()
                    (activity as BaseActivity).hideProgress()
                } else {
                    (activity as BaseActivity).showSnackBar(model.message!!)
                }
            } catch (e: Exception) {
                Log.e("Exception = ", " $e")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!backfromPast) {
            adapter?.main_list!!.clear()
            adapter!!.notifyDataSetChanged()
            hitApi()
        } else {
            backfromPast = false
        }
    }
}
