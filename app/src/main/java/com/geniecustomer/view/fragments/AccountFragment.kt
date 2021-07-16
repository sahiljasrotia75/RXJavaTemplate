package com.geniecustomer.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.geniecustomer.R
import com.geniecustomer.adapter.AccountRvAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.interfaces.RvClickPostion
import com.geniecustomer.model.AccountDataModel
import com.geniecustomer.model.edit_profile.EditProfileResponse
import com.geniecustomer.model.signin.Data
import com.geniecustomer.model.signin.SigninResponse
import com.geniecustomer.utils.GlideApp
import com.geniecustomer.view.activities.ChangePasswordActivity
import com.geniecustomer.view.activities.ContactUsActivity
import com.geniecustomer.view.activities.ProfileActivity
import com.geniecustomer.view.activities.SuggestionActivity
import com.geniecustomer.view.activities.urlViewer.UrlViewer
import com.geniecustomer.view.register.LoginActivity
import com.geniecustomer.viewmodels.BookingViewModel
import com.geniecustomer.viewmodels.UpdateprofileViewModel
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_account.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel


class AccountFragment : Fragment(), RvClickPostion, View.OnClickListener {

    lateinit var adapter: AccountRvAdapter
    lateinit var accountDataModel: AccountDataModel
    lateinit var accountData: ArrayList<AccountDataModel>
    val model by viewModel<UpdateprofileViewModel>()
    val profileViewModel by viewModel<BookingViewModel>()
    var user_obj: Data? = null
    var paramsMap: HashMap<String, RequestBody> = hashMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inits()
        switchNoti.setOnCheckedChangeListener { _, isChecked ->
            paramsMap = hashMapOf()
            addToHashMap("isPushEnabled", isChecked.toString())
            model.hitEditProfile(user_obj?.token ?: "", paramsMap)
        }
    }

    private fun addToHashMap(key: String, my_value: String) {
        paramsMap[key] = RequestBody.create(MediaType.parse("multipart/form-data"), my_value)
    }

    private fun inits() {
        iv_logout.setOnClickListener(this)
        tvUserName.setOnClickListener(this)
        tv_signin.setOnClickListener(this)
        tvRating.setOnClickListener(this)
        clHeader.setOnClickListener(this)
        model.observeEditProfile().observe(viewLifecycleOwner, Observer<ApiResponse> {
            this.consumeResponse(it!!, 1)
        })

        profileViewModel.bookingResponse().observe(viewLifecycleOwner, Observer<ApiResponse> {
            this.consumeResponse(it!!, 2)
        })

        profileViewModel.logout().observe(viewLifecycleOwner, Observer<ApiResponse> {
            this.consumeResponse(it!!, 3)
        })




        switchCal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!(activity as BaseActivity).isCalendarPermissions()) {
                    (activity as BaseActivity).isCalendarPermissions()
                }
            }
        }

//        tvUserName.setOnClickListener(this)
//        if ((activity!! as BaseActivity).user_obj != null) {
//            user_obj = (activity!! as BaseActivity).user_obj
//            tvUserName.setText(user_obj?.firstName + " " + user_obj?.lastName)
//            GlideApp.with(activity!!).load(Urls.BASE_URL + user_obj?.image)
//                .placeholder(ContextCompat.getDrawable(activity!!, R.drawable.ic_dummy_dp))
//                .fallback(ContextCompat.getDrawable(activity!!, R.drawable.ic_dummy_dp))
//                .thumbnail(0.1f)
//                .into(iv_profile)
//        }

    }


    private fun getProfileApi() {
        if(user_obj!= null)
           profileViewModel.getProfile(user_obj?.token!!)
    }

    /* fun addToCalendar(){








         val beginTime: Calendar = Calendar.getInstance()
         beginTime.set(yearInt, monthInt - 1, dayInt, 7, 30)


         val l_event = ContentValues()
         l_event.put("calendar_id", CalIds.get(0))
         l_event.put("title", "event")
         l_event.put("description", "This is test event")
         l_event.put("eventLocation", "School")
         l_event.put("dtstart", beginTime.getTimeInMillis())
         l_event.put("dtend", beginTime.getTimeInMillis())
         l_event.put("allDay", 0)
         l_event.put("rrule", "FREQ=YEARLY")
         // status: 0~ tentative; 1~ confirmed; 2~ canceled
         // l_event.put("eventStatus", 1);

         // status: 0~ tentative; 1~ confirmed; 2~ canceled
 // l_event.put("eventStatus", 1);
         l_event.put("eventTimezone", "India")
         val l_eventUri: Uri
         l_eventUri = if (Build.VERSION.SDK_INT >= 8) {
             Uri.parse("content://com.android.calendar/events")
         } else {
             Uri.parse("content://calendar/events")
         }
         val l_uri: Uri = (activity as BaseActivity)!!.getContentResolver().insert(l_eventUri, l_event)!!

     }*/
    private fun consumeResponse(apiResponse: ApiResponse, type: Int) {

        when (apiResponse.status) {

            Status.LOADING -> (activity as BaseActivity).showProgress()

            Status.SUCCESS -> {
                (activity as BaseActivity).hideProgress()
                try {
                    renderSuccessResponse(apiResponse.data!!,type)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Status.ERROR -> {
                switchNoti.isChecked = !switchNoti.isChecked
                (activity as BaseActivity).hideProgress()
                Log.e("FAILURE", apiResponse.error?.message ?: "")
                (activity as BaseActivity).showSnackBar(apiResponse.error?.message ?: "")
            }


        }
    }

    private fun renderSuccessResponse(response: JsonElement, type: Int) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)
            Log.e("response=", data)
            Log.e("response=", type.toString())
            if(type == 1){
                val signupResponse = MyApp.gson.fromJson(data, EditProfileResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
                if (signupResponse.success != null && signupResponse.success.toString().equals(
                        "TRUE",
                        true
                    )
                ) {
                    val user_obj1 = MyApp.gson.toJson(signupResponse.data)
                    (activity as BaseActivity).editor.putString("user_obj", user_obj1).apply()


                } else {
                    switchNoti.isChecked = !switchNoti.isChecked
                    signupResponse.message?.let { (activity as BaseActivity).showSnackBar(it) }
                }
            } else if (type == 3) {
                val json = JSONObject(response.toString())
                if (json.getString("success").equals("true", true)) {
                    (activity!! as BaseActivity).editor.clear().apply()
                    activity!!.startActivity(
                        Intent(
                            context!!,
                            LoginActivity::class.java
                        )
                    )
                    activity!!.finishAffinity()
                }
            } else {
                val signupResponse = MyApp.gson.fromJson(data, SigninResponse::class.java)
                if (signupResponse?.success!!) {
                    if (user_obj != null)
                        tvRating.text = "" + signupResponse.data?.avgRating
                    else
                        tvRating.visibility = View.GONE
                }

            }

        }
    }

    override fun onResume() {
        super.onResume()
        if ((activity as BaseActivity).sharedPref.contains("user_obj")) {
            user_obj = MyApp.gson.fromJson(
                (activity as BaseActivity).sharedPref.getString("user_obj", ""),
                Data::class.java
            )
            switchNoti.isChecked = user_obj?.isPushEnabled ?: false
            tvUserName.text = user_obj?.firstName?:"" + " " + user_obj?.lastName?:""
            GlideApp.with(activity!!).load(Urls.BASE_URL + user_obj?.image)
                .placeholder(ContextCompat.getDrawable(activity!!, R.drawable.ic_dummy_dp))
                .fallback(ContextCompat.getDrawable(activity!!, R.drawable.ic_dummy_dp))
                .thumbnail(0.1f)
                .into(iv_profile)

            if(user_obj !=null)
                tvRating.text = ""+user_obj?.avgRating
            else
                tvRating.visibility = View.GONE

            getProfileApi()
        }else{
            iv_logout.visibility = View.GONE
            tv_signin.visibility = View.VISIBLE
            tvNoti.visibility = View.GONE
            ivNoti.visibility = View.GONE
            switchNoti.visibility = View.GONE
            middle_view.visibility = View.GONE
            tvRating.visibility = View.GONE
        }
        loadAcountRv()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_logout -> {
                AlertDialog.Builder(activity!!)
                    .setMessage(getString(R.string.logout_really))
                    .setPositiveButton(
                        getString(R.string.yes)
                    ) { _, _ ->
                        //api for logout
                        logOutApi()
                    }.setNegativeButton(R.string.no, null).show()

            }
            R.id.clHeader -> {
                openProfile()
            }
            R.id.tvUserName -> {
                openProfile()
            }
            R.id.tvRating -> {
                openProfile()
            }
            R.id.tv_signin -> {
                (activity as BaseActivity).navigateFinishAffinity(LoginActivity::class.java)
            }
        }
    }


    private fun logOutApi() {
        try {
            val token = (context as BaseActivity).user_obj?.token
            profileViewModel.logoutService(token ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun openProfile() {
        if (user_obj != null)
            activity!!.startActivity(Intent(context, ProfileActivity::class.java))
    }

    fun loadAcountRv() {
        accountData = ArrayList()
        accountDataModel =
            AccountDataModel(R.drawable.group_11444, getString(R.string.change_password))

        if (user_obj?.canChangePassword == true)
            accountData.add(accountDataModel)



        accountDataModel = AccountDataModel(R.drawable.group_183, getString(R.string.terms_conditions))
        accountData.add(accountDataModel)

        accountDataModel = AccountDataModel(R.drawable.group_11449, getString(R.string.contact_us))
        accountData.add(accountDataModel)

        accountDataModel = AccountDataModel(R.drawable.help_icon, getString(R.string.suggestions))
        if (user_obj != null)
            accountData.add(accountDataModel)

        accountDataModel = AccountDataModel(R.drawable.union_5, getString(R.string.about_app))
        accountData.add(accountDataModel)



        adapter = AccountRvAdapter(context!!.applicationContext, accountData, this)
        rvAccount.adapter = adapter
        rvAccount.layoutManager = LinearLayoutManager(
            context?.applicationContext,
            LinearLayoutManager.VERTICAL, false
        )
    }

    override fun onRvItemClicked(position: Int) {

        if (user_obj == null || (user_obj != null && user_obj?.canChangePassword == false)) {
            when (position) {
                0 -> {
                    val url = "https://genie.mt/link/term?mobile=true"
                    val i = Intent(context, UrlViewer::class.java)
                    i.putExtra("nameTitle", "Terms & conditions")
                    i.putExtra("url", url)
                    context!!.startActivity(i)
                }
               1-> { context!!.startActivity(Intent(context, ContactUsActivity::class.java)) }
                2 -> context!!.startActivity(Intent(context, SuggestionActivity::class.java))
                3 -> {
                    val url = "https://genie.mt/link/about?mobile=true"
                    val i = Intent(context, UrlViewer::class.java)
                    i.putExtra("nameTitle", "About us")
                    i.putExtra("url", url)
                    context!!.startActivity(i)
                }
            }
        } else {
            when (position) {
                0 -> context!!.startActivity(Intent(context, ChangePasswordActivity::class.java))
                2 -> context!!.startActivity(Intent(context, ContactUsActivity::class.java))
                3 -> context!!.startActivity(Intent(context, SuggestionActivity::class.java))
                4 -> {
                    val url = "https://genie.mt/link/about?mobile=true"
                    val i = Intent(context, UrlViewer::class.java)
                    i.putExtra("nameTitle", "About us")
                    i.putExtra("url", url)
                    context!!.startActivity(i)
                }
                1 -> {
                    val url = "https://genie.mt/link/term?mobile=true"
                    val i = Intent(context, UrlViewer::class.java)
                    i.putExtra("nameTitle", "Terms & conditions")
                    i.putExtra("url", url)
                    context!!.startActivity(i)
                }
            }
        }
    }

}
