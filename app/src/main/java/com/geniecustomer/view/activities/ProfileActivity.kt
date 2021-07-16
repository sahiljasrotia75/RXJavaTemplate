package com.geniecustomer.view.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.geniecustomer.R
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.edit_profile.EditProfileResponse
import com.geniecustomer.model.signin.Data
import com.geniecustomer.utils.FileUtils1
import com.geniecustomer.utils.GlideApp
import com.geniecustomer.viewmodels.UpdateprofileViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*

class ProfileActivity : BaseActivity() {
    var iseditable: Boolean = true
    var latlng: LatLng? = null
    val model by viewModel<UpdateprofileViewModel>()
    var paramsMap: HashMap<String, RequestBody> = hashMapOf()
    var token = ""
    var imageFile: File? = null
    var my_request_image: MultipartBody.Part? = null
    var image_address = -1
    var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
//        makenoteditable()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        GlideApp.with(this).load(Urls.BASE_URL + user_obj?.image)
            .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_dummy_dp))
            .fallback(ContextCompat.getDrawable(this, R.drawable.ic_dummy_dp))
            .thumbnail(0.1f)
            .into(ivProfile)
        model.observeEditProfile().observe(this, androidx.lifecycle.Observer<ApiResponse> {
            this.consumeResponse(it!!)
        })
    }

    override fun onResume() {
        super.onResume()
        if (sharedPref.contains("user_obj")) {
            user_obj = MyApp.gson.fromJson(sharedPref.getString("user_obj", ""), Data::class.java)
        }
        inits()
    }

    fun inits() {
        edtFirstName.text = user_obj?.firstName
        edtLastName.text = user_obj?.lastName ?: ""
        edtAddress.text = user_obj?.address
        if (user_obj?.phone != null)
            edtPhone.text = user_obj?.countryCode + " " + user_obj?.phone
        edtEmail.text = user_obj?.email
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

    fun addToHashMap(key: String, my_value: String) {
        paramsMap.put(key, RequestBody.create(MediaType.parse("multipart/form-data"), my_value))
    }

    private fun renderSuccessResponse(response: JsonElement) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)
            Log.e("response=", data)
            val signupResponse = MyApp.gson.fromJson(data, EditProfileResponse::class.java)
//            if(signupResponse.response.logout==1){
//                showLogoutAlert()
//            }
            if (signupResponse.success != null && signupResponse.success.toString().equals(
                    "TRUE",
                    true
                )
            ) {

                val user_obj = MyApp.gson.toJson(signupResponse.data)
                editor.putString("user_obj", user_obj).apply()
                if (image_address == 0) {
                    edtAddress.text = address
                    showToast("Address changed successfully")
                }else if (image_address == 1) {
                    GlideApp.with(this).load(imageFile).into(ivProfile!!)
                    showToast("Profile image changed successfully")
                }
            } else {
                signupResponse.message?.let { showSnackBar(it) }
            }
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
//            R.id.tvEdit -> {
//                if (!iseditable) makeeditable()
//                else makenoteditable()
//            }
            R.id.edtEmail -> {
                editEmail()
            }
            R.id.edtFirstName -> {
                editFName()
            }
            R.id.edtLastName -> {
                editLname()
            }
            R.id.ivEdit -> {
                image_address = 1
                showPictureDialog()
            }
            R.id.edtAddress -> {
                image_address = 0
                Places.initialize(this, getString(R.string.places_api_key))
                val placesClient = Places.createClient(this)
                val fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS_COMPONENTS
                )
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
                startActivityForResult(intent, LOCATIONPERMISSIONCODE)
            }
            R.id.edtPhone -> {
                editPhone()
            }
            R.id.ivProfile -> {
                image_address = 1
                showPictureDialog()
            }
        }
    }

    override fun getRequestCode(requestcode: Int, data: Intent?) {
        super.getRequestCode(requestcode, data)
        when (requestcode) {
            CAMERAPERMISSIONCODE -> {

                val thumbnail = data!!.extras!!.get("data") as Bitmap
                val file = File(saveImage(thumbnail))
                imageFile = file
//                ivEdit.visibility = View.VISIBLE


                val reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile)
                my_request_image = MultipartBody.Part.createFormData(
                    "image",
                    imageFile?.absolutePath ?: "",
                    reqFile
                )

                token = user_obj?.token ?: ""
                Log.e("CAMERAPERMISSIONCODE", "Here")
                model.hitEditProfileWithImage(token, paramsMap, my_request_image!!)
            }
            GALLERYREQUESTCODE -> {
                Log.e("GALLERYREQUESTCODE", "Here")
                if (data != null) {
                    Log.e("call", "data: " + data.data)
                    // val selectedImageURI = data.getData()
                    imageFile = File(FileUtils1.getRealPath(this, data.data))
//                    ivEdit.visibility = View.VISIBLE


                    val reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile)
                    my_request_image = MultipartBody.Part.createFormData(
                        "image",
                        imageFile?.absolutePath ?: "",
                        reqFile
                    )

                    token = user_obj?.token ?: ""
                    Log.e("CAMERAPERMISSIONCODE", "$token")
                    model.hitEditProfileWithImage(token, paramsMap, my_request_image!!)
                }
            }
            LOCATIONPERMISSIONCODE -> {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Log.i(
                    "call",
                    "Place: " + place.name + ", " + place.id + " , " + place.latLng + " , " + place.addressComponents
                )
                edtAddress.text = place.name
                latlng = LatLng(place.latLng?.latitude ?: 0.0, place.latLng?.longitude ?: 0.0)
                hideKeyboard()
                address = place.name ?: ""
                addToHashMap("address", place.name ?: "")
                addToHashMap("latitude", latlng?.latitude!!.toString())
                addToHashMap("longitude", latlng?.longitude!!.toString())
                token = user_obj?.token ?: ""
                model.hitEditProfile(token, paramsMap)
            }
        }
    }

//    private fun makenoteditable() {
//        iseditable = false
//        tvEdit.text = "Edit"
//    }
//
//    private fun makeeditable() {
//        iseditable = true
//        tvEdit.text = "Update"
//    }


    fun editEmail() {
        if (iseditable) {
            startActivity(Intent(this, EditFieldsActivity::class.java).putExtra("extras", "email"))
        }
    }

    fun editPhone() {
        if (iseditable) {
            startActivity(Intent(this, EditFieldsActivity::class.java).putExtra("extras", "phone"))
        }
    }

    fun editFName() {
        if (iseditable) {
            startActivity(Intent(this, EditFieldsActivity::class.java).putExtra("extras", "fname"))
        }
    }

    fun editLname() {
        if (iseditable) {
            startActivity(Intent(this, EditFieldsActivity::class.java).putExtra("extras", "lname"))
        }
    }
}
