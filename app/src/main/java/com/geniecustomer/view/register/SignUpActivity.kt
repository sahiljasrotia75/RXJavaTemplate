package com.geniecustomer.view.register

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.geniecustomer.R
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.edit_profile.EditProfileRequest
import com.geniecustomer.model.signup.SignupResponse
import com.geniecustomer.utils.FileUtils1
import com.geniecustomer.utils.GlideApp
import com.geniecustomer.viewmodels.SignupViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_signup.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*

class SignUpActivity : BaseActivity(), TextWatcher {


    var imageFile: File? = null
    var content = ""
    var type = -1
    var token = ""
    var code = ""
    var latlng : LatLng?=null
    val model by viewModel<SignupViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        content = intent.getStringExtra("content")!!
        type = intent.getIntExtra("type", -1)
        token = intent.getStringExtra("token")!!
        code = intent.getStringExtra("code")!!

        when (type) {
            0 -> {
                //Phone Number
                ccp02.setCountryForPhoneCode(code.toInt())
                edtPhoneNo.setText(content)
                edtPhoneNo.isEnabled = false
                edtEmail.isEnabled = true
            }
            1 -> {
                //Email
                code = ccp02.defaultCountryCodeWithPlus.toString()
                edtEmail.setText(content)
                edtEmail.isEnabled = false
                edtPhoneNo.isEnabled = true
            }
        }

        addListener()
    }


    private fun validations() : Boolean{
        return when{
            /*imageFile == null->{
                showToastMessage("Please upload a profile image.")
                false
            }*/
            edtFirstName.text.toString().trim().isEmpty()->{
                showToastMessage("Please enter a first name.")
                false
            }
            edtLastName.text.toString().trim().isEmpty()->{
                showToastMessage("Please enter a last name.")
                false
            }
            /*edtAddress.text.toString().trim().isEmpty()->{
                showToastMessage("Please enter a address.")
                false
            }*/
            edtPassword.text.toString().trim().isEmpty()->{
                showToastMessage("Please enter a password.")
                false
            }
            edtPassword.text.toString().length < 8 ->{
                showToastMessage("Password must be greater then 8 character.")
                false
            }
            edtRePassword.text.toString().trim().isEmpty()->{
                showToastMessage("Please enter Re-enter Password")
                false
            }
            edtRePassword.text.toString().length < 8 ->{
                showToastMessage("Re-enter Password must be greater then 8 character.")
                false
            }
            edtPhoneNo.text.toString().trim().isEmpty()->{
                showToastMessage("Please enter a phone number.")
                false
            }
            edtPhoneNo.text.toString().length < 7->{
                showToastMessage("Please enter a valid phone number.")
                false
            }
            edtEmail.text.toString().trim().isEmpty() -> {
                showToastMessage("Please enter a email address.")
                false
            }
            !edtEmail.text.isValidEmail() -> {
                showToastMessage("Please enter a valid email address.")
                false
            }
            edtPassword.text.toString() != edtRePassword.text.toString() -> {
                showToastMessage("Password not match.")
                false
            }
            else -> true
        }
    }


    /***/
    private fun showToastMessage(message: String){
        try {
            Toast.makeText(context,message,Toast.LENGTH_LONG).show()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    fun addListener() {
        edtFirstName.addTextChangedListener(this)
        edtLastName.addTextChangedListener(this)
        edtAddress.addTextChangedListener(this)
        edtPhoneNo.addTextChangedListener(this)
        edtPassword.addTextChangedListener(this)
        edtRePassword.addTextChangedListener(this)

        model.signupResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!)
        })

        ccp02.setOnCountryChangeListener {
            code = ccp02.selectedCountryCodeWithPlus
            Log.e("cndkncvkd","$code")
        }

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.ivProfile -> {
                showPictureDialog()
            }
            R.id.edtAddress -> {
                Places.initialize(this, getString(R.string.places_api_key))
                val placesClient = Places.createClient(this)
                val fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS_COMPONENTS
                )
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
                startActivityForResult(intent, LOCATIONPERMISSIONCODE)
            }
        }
    }


    override fun onBackPressed() {
        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }

    override fun afterTextChanged(s: Editable?) {
        if (edtFirstName.text.trim().isNotEmpty() &&
            edtLastName.text.trim().isNotEmpty() &&
            /*edtAddress.text.trim().isNotEmpty() &&*/
            edtPassword.text.trim().isNotEmpty() &&
            edtRePassword.text.trim().isNotEmpty() &&
            edtPhoneNo.text.trim().isNotEmpty() &&
            edtEmail.text.trim().isNotEmpty()
        ) {
            tvNext.background = resources.getDrawable(R.drawable.round_corner_bg)
            tvNext.setOnClickListener {
               /* if(edtPassword.text.toString().trim().length < 8){
                    showToast("Password must be at least 8 characters long.")
                }
                else if (edtPassword.text.toString().trim().equals(edtRePassword.text.toString().trim())){
//                    paramsMap = HashMap()
                    val intent = Intent(this,AllowLocationActivity::class.java)
                    intent.putExtra("token",token)
                    if(imageFile!=null){
                        intent.putExtra("imageFile",imageFile)
                    }
                    if(type == 0) {
                        val editProfileRequest = EditProfileRequest(
                            edtFirstName.text.trim().toString(),
                            edtLastName.text.trim().toString(),
                            edtPassword.text.trim().toString(),
                            edtAddress.text.trim().toString(),
                            "CU",
                            content,
                            0.0,
                            "etEmail",
                            0.0
                            )
                        intent.putExtra("data",editProfileRequest)
                    }
                    else if(type == 1)
                    {
                        val editProfileRequest = EditProfileRequest(
                            edtFirstName.text.trim().toString(),
                            edtLastName.text.trim().toString(),
                            edtPassword.text.trim().toString(),
                            edtAddress.text.trim().toString(),
                            "CU",
                            edtPhoneNo.text.trim().toString(),
                            latlng?.latitude,
                            content,
                            latlng?.longitude
                        )
                        intent.putExtra("data",editProfileRequest)
                    }
                    startActivity(intent)
                }
                else
                    showToast("Passwords mismatch.")*/
                if (validations()){
                    hitOtpService()
                }




                /*if (validations()){
                    val intent = Intent(this,VerifyOTPActivity::class.java)
                    intent.putExtra("type",0)
                    intent.putExtra("code",ccp.selectedCountryCodeWithPlus)
                    intent.putExtra("content",edtCurrentPass.text.toString())
                    intent.putExtra("otpId", signupResponse.data.otpId)
                    intent.putExtra("resend",true)
                    startActivityForResult(intent,101)
                }*/



            }
        } else {
            tvNext.background = resources.getDrawable(R.drawable.round_corner_bg_disabled)
            tvNext.setOnClickListener {}
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }


    fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()


    override fun getRequestCode(requestcode: Int, data: Intent?) {
        super.getRequestCode(requestcode, data)
        when (requestcode) {
            CAMERAPERMISSIONCODE -> {
                Log.e("CAMERAPERMISSIONCODE", "Here")
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                val file = File(saveImage(thumbnail))
                imageFile = file
                ivEdit.visibility = View.VISIBLE
                GlideApp.with(this).load(imageFile).into(ivProfile!!)
            }
            GALLERYREQUESTCODE -> {
                Log.e("GALLERYREQUESTCODE", "Here")
                if (data != null) {
                    Log.e("call", "data: " + data.data)
                    val selectedImageURI = data.data
                    imageFile = File(FileUtils1.getRealPath(this, data.data))
                    ivEdit.visibility = View.VISIBLE
                    GlideApp.with(this).load(imageFile).into(ivProfile!!)
                }
            }
            LOCATIONPERMISSIONCODE -> {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Log.i(
                    "call",
                    "Place: " + place.name + ", " + place.id + " , " + place.latLng + " , " + place.address
                )
//                edtAddress.text = place.name
                edtAddress.text = place.address
                latlng = LatLng(place.latLng?.latitude ?: 0.0, place.latLng?.longitude ?: 0.0)
            }
        }
    }



    /**
     * @START_WORK
     * MUKESH RAJPUT
     * @29/10/2020
     * */

    private fun hitOtpService() {
        try {
            val request = JsonObject()

            when(type){
                0->{
                    //Email verify pending
                    request.addProperty("email", "" + edtEmail.text.toString())
                    request.addProperty("role", "CU")
                    request.addProperty("deviceType", "ANDROID")
                    request.addProperty("pushToken", sharedPref.getString("fcmToken",""))
                }
                1->{
                    //Phone verify pending
                    request.addProperty(
                        "phone",
                        "" +edtPhoneNo.text.toString()
                    )
                    request.addProperty("role", "CU")
                    request.addProperty("countryCode", ""+code)
                    request.addProperty("deviceType", "ANDROID")
                    request.addProperty("pushToken", sharedPref.getString("fcmToken",""))
                }
            }
            Log.e("lcmslcmlsmc","$request")
            model.hitRegisterUser(request)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    private fun hitService(){
        try {

            val intent = Intent(this,AllowLocationActivity::class.java)
            intent.putExtra("token",token)
            if(imageFile!=null){
                intent.putExtra("imageFile",imageFile)
            }
            if(type == 0) {
                val editProfileRequest = EditProfileRequest(
                    edtFirstName.text.trim().toString(),
                    edtLastName.text.trim().toString(),
                    edtPassword.text.trim().toString(),
                    edtAddress.text.trim().toString(),
                    "CU",
                    content,
                    0.0,
                    edtEmail.text.trim().toString(),
                    0.0,
                    code
                )
                intent.putExtra("data",editProfileRequest)
            }
            else if(type == 1)
            {
                val editProfileRequest = EditProfileRequest(
                    edtFirstName.text.trim().toString(),
                    edtLastName.text.trim().toString(),
                    edtPassword.text.trim().toString(),
                    edtAddress.text.trim().toString(),
                    "CU",
                    edtPhoneNo.text.trim().toString(),
                    latlng?.latitude,
                    content,
                    latlng?.longitude,
                    code
                )
                intent.putExtra("data",editProfileRequest)
            }
            startActivity(intent)
        }catch (e:Exception){
            e.printStackTrace()
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


    //Render Success Response
    private fun renderSuccessResponse(response: JsonElement) {
        if (!response.isJsonNull) {

            val data: String = MyApp.gson.toJson(response)
            Log.e("response=", data)
            val signupResponse = MyApp.gson.fromJson(data, SignupResponse::class.java)

            if (signupResponse.data?.otpId != null) {
                val intent = Intent(this, VerifyOTPActivity::class.java)
                /*intent.putExtra("type", type)*/
                when (type) {
                    0 -> {
                        intent.putExtra("code", "")
                        intent.putExtra("content", edtEmail.text.toString())
                        intent.putExtra("type", 1)
                    }
                    1 -> {
                        intent.putExtra("code", ccp02.selectedCountryCodeWithPlus)
                        intent.putExtra("content", edtPhoneNo.text.toString())
                        intent.putExtra("type", 0)
                    }
                }
                intent.putExtra("otpId", signupResponse.data.otpId)
                intent.putExtra("resend", true)
                startActivityForResult(intent, 102)
            }
            else{
                showToastMessage(signupResponse.message ?: "Something went wrong.")
            }


        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("asldlasdas","cmdkscmkdsmck")
        Log.e("asldlasdas","$requestCode   $resultCode   $data")
        if(requestCode == 102){
            if(resultCode==Activity.RESULT_OK){
                hitService()
            }
        }
    }


}
