package com.geniecustomer.view.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.geniecustomer.R
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.edit_profile.EditProfileResponse
import com.geniecustomer.model.forgot_pass.ForgotResponse
import com.geniecustomer.model.signin.Data
import com.geniecustomer.utils.AppConstants
import com.geniecustomer.utils.AppConstants.ACCOUNT
import com.geniecustomer.utils.AppConstants.CHAT
import com.geniecustomer.utils.AppConstants.HOME
import com.geniecustomer.utils.AppConstants.MYBOOKING
import com.geniecustomer.utils.AppConstants.TRENDING
import com.geniecustomer.view.fragments.*
import com.geniecustomer.view.register.LoginActivity
import com.geniecustomer.view.register.VerifyOTPActivity
import com.geniecustomer.viewmodels.OtpViewModel
import com.geniecustomer.viewmodels.UpdateprofileViewModel
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_edit_fields.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.navigation_footer.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity() {

    var FragmentTag: String = ""
    var token = ""
    val otp_model by viewModel<OtpViewModel>()
    var paramsMap: HashMap<String, RequestBody> = hashMapOf()
    val model by viewModel<UpdateprofileViewModel>()
    private lateinit var appUpdateManager: AppUpdateManager

    companion object{
        var phonePopUp:ConstraintLayout? = null
        var emailPopUp:ConstraintLayout? = null
        var extras = ""
    }
    private var mFragmentManager: FragmentManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkUpdate()
        if (intent != null && intent.hasExtra("extras")) {
            extras = intent.getStringExtra("extras")!!
        }
        phonePopUp = findViewById(R.id.clBlackOut4phone)
        emailPopUp = findViewById(R.id.clBlackOut4email)

        phonePopUp!!.setOnClickListener {
            phonePopUp!!.visibility = View.GONE
        }
        emailPopUp!!.setOnClickListener {
            emailPopUp!!.visibility = View.GONE
        }
        token = user_obj?.token?:""
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        mFragmentManager = supportFragmentManager

        model.observeEditProfile().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!,0)
        })
        otp_model.otpResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!,1)
        })

        showinitialFragment(HomeFragment(), HOME)

        edtPhoneNo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty() && s.length > 6) {
                    tv_NextPhone.setBackgroundResource(R.drawable.round_corner_bg)
                    tv_NextPhone.isEnabled = true
                } else {
                    tv_NextPhone.setBackgroundResource(R.drawable.round_corner_bg_disabled)
                    tv_NextPhone.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isValidEmail()) {
                    tv_NextEmail.setBackgroundResource(R.drawable.round_corner_bg)
                    tv_NextEmail.isEnabled = true
                } else {
                    tv_NextEmail.setBackgroundResource(R.drawable.active_dot)
                    tv_NextEmail.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        tv_NextPhone.setOnClickListener {
            val request = JsonObject()
            request.addProperty("code",ccp02.selectedCountryCodeWithPlus.toString())
            request.addProperty("phone",edtPhoneNo.text.toString())
            request.addProperty("role","CU")
            otp_model.hitResendOtp(request)
        }
        tv_NextEmail.setOnClickListener {
            val request = JsonObject()
            request.addProperty("email",etEmail.text.toString())
            request.addProperty("role","CU")
            otp_model.hitResendOtp(request)
        }


      /*  if(intent.hasExtra("notification")){

            val clickAction = intent.getStringExtra("clickAction")
            if(clickAction.equals("New_Message")){
                 if (!FragmentTag.equals(CHAT)) {
                      replaceFragment(ChatFragment(), CHAT)
                 }
               val intent1 = Intent(applicationContext,HomeActivity::class.java)
                intent1.putExtra("notification","")
                intent1.putExtra("clickAction",clickAction)
                intent1.putExtra("name",intent.getStringExtra("name"))
                intent1.putExtra("image",intent.getStringExtra("image"))
                intent1.putExtra("providerId",intent.getStringExtra("senderId"))
                intent1.putExtra("bookingId",intent.getStringExtra("bookingId"))
                startActivity(intent1)
            }


        }else{

        }*/

        /*if(intent.hasExtra("type")){
            val type = intent.getStringExtra("type")
            if(type?.equals("fromChat")!!){
                if (!FragmentTag.equals(CHAT)) {
                    replaceFragment(ChatFragment(), CHAT)
                }
            }
        }*/
    }

    private fun checkUpdate() {
        // Returns an intent object that you use to check for an update.
        // Checks that the platform will allow the specified type of update.
        Log.d("zcxzc", "Checking for updates")
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.UPDATE_AVAILABLE
                ) {
                    // If an in-app update is already running, resume the update.
                    Log.e("sdkfnlksd", "dfsdf")
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        1234
                    )
                } else {
                    Log.e("sdkfnlksd", "saknsak")
                }
            }

    }

    fun addToHashMap(key: String, my_value: String) {
        paramsMap.put(key, RequestBody.create(MediaType.parse("multipart/form-data"), my_value))
    }

    private fun consumeResponse(apiResponse: ApiResponse, type: Int) {

        when (apiResponse.status) {

            Status.LOADING -> showProgress()

            Status.SUCCESS -> {
                hideProgress()
                try {
                    renderSuccessResponse(apiResponse.data!!, type)
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

    private fun renderSuccessResponse(response: JsonElement, my_type : Int) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)
            Log.e("response=", data)
            if(my_type == 0) {
                val signupResponse = MyApp.gson.fromJson(data, EditProfileResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
                if (signupResponse.success != null && signupResponse.success.toString().equals(
                        "TRUE",
                        true
                    )
                ) {
                    val user_obj2 = MyApp.gson.toJson(signupResponse.data)
                    editor.putString("user_obj", user_obj2).apply()
                    hideKeyboard()
                    user_obj = MyApp.gson.fromJson(sharedPref.getString("user_obj",""), Data::class.java)
                    when (extras) {
                        "email" -> {
                            showToast("Email Updated Successfully")
                            emailPopUp!!.visibility = View.GONE
                        }
                        "phone" -> {
                            showToast("Phone Updated Successfully")
                            phonePopUp!!.visibility = View.GONE
                        }
                    }
                } else {
                    signupResponse.message?.let { showSnackBar(it) }
                }
            }
           else if(my_type == 1) {
                val signupResponse = MyApp.gson.fromJson(data, ForgotResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
                if (signupResponse.success != null && signupResponse.success.toString().equals(
                        "TRUE",
                        true
                    )
                ) {
                    val intent = Intent(this, VerifyOTPActivity::class.java)
                    if(extras.equals("email",true)){
                        intent.putExtra("type",1)
                        intent.putExtra("code","")
                        intent.putExtra("content",etEmail.text.toString())
                    }else if(extras.equals("phone",true)){
                        intent.putExtra("type",0)
                        intent.putExtra("code",ccp02.selectedCountryCodeWithPlus)
                        intent.putExtra("content",edtPhoneNo.text.toString())
                    }
                    intent.putExtra("otpId",signupResponse.data?.otpId)
                    intent.putExtra("resend",true)
                    startActivityForResult(intent,101)

                } else {
                    signupResponse.message?.let { showSnackBar(it) }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){

            token = user_obj?.token?:""
            phonePopUp!!.visibility = View.GONE
            user_obj?.phone = edtPhoneNo.text.toString()
            model.hitEditProfile(token, paramsMap)

            if (extras.equals("email")) {
                addToHashMap("email", etEmail.text.toString())
            } else {
                addToHashMap("phone", edtPhoneNo.text.toString())
                addToHashMap("countryCode", ccp02.selectedCountryCodeWithPlus)
            }
            token = user_obj?.token ?: ""
            model.hitEditProfile(token, paramsMap)
        } else if (requestCode == 1234) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d("ascsac", "" + "Result Ok")
                    //  handle user's approval }
                }
                Activity.RESULT_CANCELED -> {
                    Log.d("ascsac", "" + "Result Cancelled")
                    //  handle user's rejection  }
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    //if you want to request the update again just call checkUpdate()
                    Log.d("ascsac", "" + "Update Failure")
                    //  handle update failure
                }
            }
        }
    }

    fun showinitialFragment(fragment: Fragment, tag: String) {
        changeDrawableColor(tv_home, R.drawable.ic_home_selected, R.color._ff8a74)
        changeDrawableColor(tv_trending, R.drawable.ic_trending_selected, R.color._d4d8df)
        changeDrawableColor(tv_booking, R.drawable.ic_bookng_selected, R.color._d4d8df)
        changeDrawableColor(tv_chat, R.drawable.ic_chat_selected, R.color._d4d8df)
        changeDrawableColor(tv_account, R.drawable.ic_account_selected, R.color._d4d8df)

        FragmentTag = tag
        val transaction = mFragmentManager?.beginTransaction()
        transaction!!.replace(R.id.framelayout, fragment, tag)

//        (mFragmentStack as Stack<Fragment>).push(fragment)
        transaction.commitAllowingStateLoss()
    }

    fun replaceFragment(fragment: Fragment, tag: String) {


        if (tag.equals(AppConstants.HOME)) {
            changeDrawableColor(tv_home, R.drawable.ic_home_selected, R.color._ff8a74)
            changeDrawableColor(tv_trending, R.drawable.ic_trending_selected, R.color._d4d8df)
            changeDrawableColor(tv_booking, R.drawable.ic_bookng_selected, R.color._d4d8df)
            changeDrawableColor(tv_chat, R.drawable.ic_chat_selected, R.color._d4d8df)
            changeDrawableColor(tv_account, R.drawable.ic_account_selected, R.color._d4d8df)
        } else if (tag.equals(AppConstants.TRENDING)) {
            changeDrawableColor(tv_trending, R.drawable.ic_trending_selected, R.color._ff8a74)
            changeDrawableColor(tv_home, R.drawable.ic_home_selected, R.color._d4d8df)
            changeDrawableColor(tv_booking, R.drawable.ic_bookng_selected, R.color._d4d8df)
            changeDrawableColor(tv_chat, R.drawable.ic_chat_selected, R.color._d4d8df)
            changeDrawableColor(tv_account, R.drawable.ic_account_selected, R.color._d4d8df)
        } else if (tag.equals(AppConstants.MYBOOKING)) {
            changeDrawableColor(tv_booking, R.drawable.ic_bookng_selected, R.color._ff8a74)
            changeDrawableColor(tv_trending, R.drawable.ic_trending_selected, R.color._d4d8df)
            changeDrawableColor(tv_home, R.drawable.ic_home_selected, R.color._d4d8df)
            changeDrawableColor(tv_chat, R.drawable.ic_chat_selected, R.color._d4d8df)
            changeDrawableColor(tv_account, R.drawable.ic_account_selected, R.color._d4d8df)
        } else if (tag.equals(AppConstants.CHAT)) {
            changeDrawableColor(tv_chat, R.drawable.ic_chat_selected, R.color._ff8a74)
            changeDrawableColor(tv_trending, R.drawable.ic_trending_selected, R.color._d4d8df)
            changeDrawableColor(tv_booking, R.drawable.ic_bookng_selected, R.color._d4d8df)
            changeDrawableColor(tv_home, R.drawable.ic_home_selected, R.color._d4d8df)
            changeDrawableColor(tv_account, R.drawable.ic_account_selected, R.color._d4d8df)
        } else if (tag.equals(AppConstants.ACCOUNT)) {
            changeDrawableColor(tv_account, R.drawable.ic_account_selected, R.color._ff8a74)
            changeDrawableColor(tv_trending, R.drawable.ic_trending_selected, R.color._d4d8df)
            changeDrawableColor(tv_booking, R.drawable.ic_bookng_selected, R.color._d4d8df)
            changeDrawableColor(tv_chat, R.drawable.ic_chat_selected, R.color._d4d8df)
            changeDrawableColor(tv_home, R.drawable.ic_home_selected, R.color._d4d8df)
        }

        FragmentTag = tag
//        mFragmentStack = Stack()
        val transaction = mFragmentManager?.beginTransaction()
//        if(!intent.hasExtra("type"))
        transaction!!.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)

        transaction.replace(R.id.framelayout, fragment, tag)

//        (mFragmentStack as Stack<Fragment>).push(fragment)
        transaction.commitAllowingStateLoss()

    }

    fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun changeDrawableColor(textView: TextView, my_drawable: Int, color: Int) {
//        if (color == R.color.colorAccent)
        textView.setTextColor(resources.getColor(color))
//        else
//            textView.setTextColor(resources.getColor(R.color._d4d8df))

        val drawable = resources.getDrawable(my_drawable)
        drawable.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(textView.context, color),
            PorterDuff.Mode.SRC_IN
        )
    }

    override fun onLocationFetched() {
        super.onLocationFetched()
        if (FragmentTag.equals(HOME))
            (mFragmentManager?.findFragmentByTag(HOME) as HomeFragment).onFragmentLocationFetched()

        if (FragmentTag.equals(TRENDING))
            (mFragmentManager?.findFragmentByTag(TRENDING) as TrendingFragment).onFragmentLocationFetched()
    }

    override fun onBackPressed() {
        if (phonePopUp?.visibility == View.VISIBLE)
        {
            phonePopUp?.visibility = View.GONE
        }
        else if (emailPopUp?.visibility == View.VISIBLE){
            emailPopUp?.visibility = View.GONE
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.tv_home -> {
                if (!FragmentTag.equals(HOME)) {
                    replaceFragment(HomeFragment(), HOME)
                }
            }
            R.id.tv_trending -> {
                if (!FragmentTag.equals(TRENDING)) {
                    replaceFragment(TrendingFragment(), TRENDING)
                }
            }
            R.id.tv_booking -> {
                if(user_obj == null){
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.login_proceed))
                        .setCancelable(false)
                        .setPositiveButton(
                            getString(R.string.yes),
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                                    finishAffinity()
                                }
                            }).setNegativeButton(R.string.no, null).show()

                }else {
                    if (!FragmentTag.equals(MYBOOKING)) {
                        replaceFragment(BookingFragment(), MYBOOKING)
                    }
                }
            }
            R.id.tv_chat -> {

                if(user_obj == null){

                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.login_proceed))
                        .setCancelable(false)
                        .setPositiveButton(
                            getString(R.string.yes),
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    /*(context as BaseActivity).editor.putBoolean("guest",true).apply()*/
                                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                                    finishAffinity()
                                }
                            }).setNegativeButton(R.string.no, null).show()

                }else{
                    if (!FragmentTag.equals(CHAT)) {
                        replaceFragment(ChatFragment(), CHAT)
                    }
                }
            }
            R.id.tv_account -> {
                if (!FragmentTag.equals(ACCOUNT)) {
                    replaceFragment(AccountFragment(), ACCOUNT)
                }
            }
        }
    }
}
