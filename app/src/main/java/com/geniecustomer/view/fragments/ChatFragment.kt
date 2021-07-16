package com.geniecustomer.view.fragments


import android.content.DialogInterface
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
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.adapters.ChatListAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.chat.chat.ChatListResponse
import com.geniecustomer.model.chat.chat.DataItem
import com.geniecustomer.view.register.LoginActivity
import com.geniecustomer.viewmodels.BookingViewModel
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_chat.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    var adapter : ChatListAdapter?= null
    var list :  MutableList<DataItem> = ArrayList()
    val model by viewModel<BookingViewModel>()

    var count :Int = 0
    var isLoading = false
    var skip = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.bookingResponse().observe(viewLifecycleOwner, Observer<ApiResponse> {
            this.consumeResponse(
                it!!
            )
        })
        list = ArrayList()
        adapter = ChatListAdapter(activity!!, list as ArrayList<DataItem>)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(activity!!)

    }

    fun pagination(){
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            // for pagination
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val totalItemCount = layoutManager!!.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val endHasBeenReached = lastVisible + 2 >= totalItemCount
                if (totalItemCount > 0 && endHasBeenReached) {
                    Log.d("last_item", "$lastVisible")
                    if (recycler_view.adapter?.itemCount!! < count && !isLoading) {
                        Log.e("last_item", recycler_view.adapter?.itemCount.toString())
                        // hitRatingApi(10,skip)

                        hitApi()

                    }
                }
            }
        })
    }

    fun hitApi(){
        if((activity as BaseActivity).user_obj == null){

            AlertDialog.Builder(activity!!)
                .setMessage(getString(R.string.login_proceed))
                .setPositiveButton(
                    getString(R.string.yes),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            //(context as BaseActivity).editor.putBoolean("guest",false).apply()
                            startActivity(Intent(activity!!, LoginActivity::class.java))
                            activity!!.finishAffinity()
                        }
                    }).setNegativeButton(R.string.no, null).show()

        }else{
            Log.e("API hit", "tin success    ${(activity as BaseActivity).user_obj?.token}   $skip")
            model.hitGetChatList((activity as BaseActivity).user_obj?.token ?: "", skip, 10)
        }

    }

    private fun consumeResponse(apiResponse: ApiResponse) {

        when (apiResponse.status) {

            Status.LOADING -> {
                isLoading = true
                (activity as BaseActivity).showProgress()
            }

            Status.SUCCESS -> {
                isLoading = false
                (activity as BaseActivity).hideProgress()

                try {
                    renderSuccessResponse(apiResponse.data!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Status.ERROR -> {
                isLoading = false
                (activity as BaseActivity).hideProgress()
                Log.e("FAILURE", apiResponse.error?.message ?: "")
                (activity as BaseActivity).showSnackBar(apiResponse.error?.message ?: "")
            }

            else -> {
            }
        }
    }

    private fun renderSuccessResponse(response: JsonElement) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)

            val maxLogSize = 1000
            for (i in 0..data.length / maxLogSize) {
                val start = i * maxLogSize
                var end = (i + 1) * maxLogSize
                end = if (end > data.length) data.length else end
                Log.e("get data from response", data.substring(start, end))
            }

//            try{
                Log.e("Get data", "$data")

                val signupResponse = MyApp.gson.fromJson(data, ChatListResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }

                if (signupResponse.data!!.isEmpty()){
                    clNoDataLogo.visibility = View.VISIBLE
                }else{
                    clNoDataLogo.visibility = View.GONE
                }

                if (signupResponse.success != null && signupResponse.success.toString().equals(
                        "TRUE",
                        true
                    )) {
                 /*   for(i in 0 until signupResponse?.data?.size!!){
                        list.add(signupResponse?.data.get(i)!!)
                    }*/

                    if(skip == 0) {
                        list.clear()
                        list.addAll(signupResponse.data)
                        adapter!!.notifyDataSetChanged()
                    }else{
                        val tempList = ArrayList<DataItem>()
                        tempList.addAll(signupResponse.data)
                        list.addAll(tempList)
                        adapter!!.notifyDataSetChanged()
                    }

                    count = signupResponse.count!!
                    skip++
                    adapter?.notifyDataSetChanged()
                } else {
                    (activity as BaseActivity).showSnackBar(signupResponse.message ?: "")
                }
//            }catch (e: Exception){
//                Log.e("EXCEPTION", " " + e)
//            }

        }
    }


    override fun onResume() {
        super.onResume()
        list.clear()

        skip = 0
        pagination()
        hitApi()

        //hitApi()
        /*if(context.sharedPref.contains("lastmessage") && context.sharedPref.getString("lastmessage","")!!.isNotEmpty()){

        }*/
    }

    override fun onStop() {
        super.onStop()
        model.disposable.clear()
    }
}