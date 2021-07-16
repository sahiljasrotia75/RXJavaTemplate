package com.geniecustomer.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.LocationManager
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.geniecustomer.R
import com.geniecustomer.interfaces.RequestCode
import com.geniecustomer.model.Address
import com.geniecustomer.model.signin.Data
import com.geniecustomer.receivers.MyReceiver
import com.geniecustomer.utils.CustomProgressDialog
import com.geniecustomer.utils.NetworkUtils
import com.geniecustomer.view.activities.ContactUsActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

open class BaseActivity : AppCompatActivity(),View.OnClickListener,BaseView,RequestCode,
    MyReceiver.ConnectivityReceiverListener {

    private var mSnackBar: Snackbar? = null
    private var receiver: BroadcastReceiver? = null

    override fun onGetPermissionCode(requestCode: Int) {
    }

    override fun onLocationFetched() {

    }

    override fun isPermanentlyDenied() {

    }

    val READSTORAGEPERMISSIONCODE = 2
    val CALENDARPERMISSION = 3
    val CAMERAPERMISSIONCODE = 1
    val GALLERYREQUESTCODE = 5
    val LOCATIONPERMISSIONCODE = 15
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    lateinit var my_placesClient: PlacesClient
    private val IMAGE_DIRECTORY = "/Dash"

    override fun onClick(v: View?) {

    }
    var mProgress: CustomProgressDialog? = null
    lateinit var editor: SharedPreferences.Editor
    lateinit var sharedPref: SharedPreferences
    lateinit var context: Context
    var user_obj : Data?=null
    var manager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        window.statusBarColor = Color.parseColor("#CC0575E6")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.places_api_key))
        }
        my_placesClient = Places.createClient(this)
        context = this
        sharedPref = this.getSharedPreferences("com.genie", Context.MODE_PRIVATE)
        editor = sharedPref.edit()

        if(sharedPref.contains("user_obj")){
            user_obj = MyApp.gson.fromJson(sharedPref.getString("user_obj",""),Data::class.java)
        }

        val filter = IntentFilter()
        filter.addAction((ConnectivityManager.CONNECTIVITY_ACTION))
        receiver = MyReceiver()
        registerReceiver(receiver, filter)

        mProgress = CustomProgressDialog(this)

    }


    fun getTime(x: String?): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        //. originalFormat.timeZone = TimeZone.getTimeZone("UTC")
        // var targetFormat = SimpleDateFormat("dd/MM/yyyy")
        val targetFormat = SimpleDateFormat("MMM d, yyyy")
        originalFormat.timeZone = TimeZone.getDefault()
        originalFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = originalFormat.parse(x!!)
        val past = date
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past!!.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
        val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time)

        var sendTIme = ""
        if (seconds < 60) {
            sendTIme = seconds.toString() + " seconds ago"
        } else if (minutes < 60) {
            sendTIme = minutes.toString() + " minutes ago"
        } else if (hours < 24) {
            sendTIme = hours.toString() + " hours ago"
        } else {
            sendTIme = days.toString() + " days ago"
        }

        return sendTIme

    }

    fun setLocation() {
        val placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (!mProgress!!.isShowing)
                showProgress()

            val placeResponse = my_placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result

                    if (response != null && response.placeLikelihoods.size > 0) {

                        Log.e(
                            "sfdsfsdfdsf",
                            "fddfgfdg    ${response.placeLikelihoods.get(0).place.address}"
                        )
                        Log.e(
                            "sfdsfsdfdsf",
                            "fddfgfdg    ${response.placeLikelihoods.get(0).place}"
                        )

                        val address = Address(
                            response.placeLikelihoods.get(0).place.address,
                            response.placeLikelihoods[0].place.latLng!!.latitude,
                            response.placeLikelihoods[0].place.latLng!!.longitude
                        )

                        /*    val address = Address(response.placeLikelihoods.get(0).place.address,
                                response.placeLikelihoods[0].place.latLng!!.latitude,
                                response.placeLikelihoods[0].place.latLng!!.longitude)

                            try {
                                val geocoder = Geocoder(activity!!, Locale.getDefault())
                                val addresses =
                                    geocoder.getFromLocation(address.latitude ?: 0.0, address.longitude ?: 0.0, 1)
                                val cityName = addresses[0].locality
                               // (activity as BaseActivity).editor.putString("location", cityName).apply()

                            }catch (e:java.lang.Exception){
                                Log.e("error","city"+e.message)
                            }*/


                        Log.e(
                            "sfdsfsdfdsf",
                            "fddfgfdg    ${response.placeLikelihoods.get(0).place.address}"
                        )
                        val gson = MyApp.gson.toJson(address)
                        editor.putString("address", gson).apply()

                        Log.e("cssafsaflcmsd", "${response}")
                        Log.e("my_loc", "" + gson)
                        onLocationFetched()
                    }
                    hideProgress()

                } else {
                    hideProgress()

                    if (isNetworkConnected()) {
                        manager =
                            getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            showSettingsAlert()
                        } else {
                            val exception = task.exception

                            AlertDialog.Builder(this)
                                .setMessage("" + exception.toString())
                                .setPositiveButton(
                                    android.R.string.ok,
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(arg0: DialogInterface, arg1: Int) {
                                            arg0.dismiss()
                                        }
                                    })
                                .setOnDismissListener(object : DialogInterface.OnDismissListener {
                                    override fun onDismiss(dialog: DialogInterface?) {
                                        finish()
                                    }

                                }).create().show()
                        }
                    } else {
                        AlertDialog.Builder(this)
                            .setMessage("Please enable internet connection first")
                            .setPositiveButton(
                                android.R.string.ok,
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(arg0: DialogInterface, arg1: Int) {
                                        arg0.dismiss()
                                    }
                                }).setOnDismissListener(object : DialogInterface.OnDismissListener {
                                override fun onDismiss(dialog: DialogInterface?) {
                                    finish()
                                }

                            }).create().show()
                    }


                }
            }
        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )

//            (activity as MainActivity).showSettingsAlert()
        }
    }

    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(
            this
        )
        alertDialog.setTitle(getString(R.string.settings))
        alertDialog.setMessage(getString(R.string.enable_location_provider_message))
        alertDialog.setPositiveButton(getString(R.string.settings),
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    //(activity as BaseActivity).hideProgress()
                   // finish()
                    startActivity(intent)
                }
            })
        alertDialog.setNegativeButton(getString(R.string.cancel),
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    hideProgress()
                    finish()
                    dialog.cancel()
                }
            })
        alertDialog.show()
    }


    @SuppressLint("SimpleDateFormat")
    fun getDateTime(x: String?): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        //. originalFormat.timeZone = TimeZone.getTimeZone("UTC")
        // var targetFormat = SimpleDateFormat("dd/MM/yyyy")
        val targetFormat = SimpleDateFormat(" d MMM, yy")
        originalFormat.timeZone = TimeZone.getDefault()
        originalFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = originalFormat.parse(x!!)
        val formattedDate = targetFormat.format(date!!)
        Log.e("143"," date "+formattedDate)
        val targetTimeFormat = SimpleDateFormat("hh:mm a")
        //originalFormat.timeZoMyne = TimeZone.getDefault()
        originalFormat.timeZone = TimeZone.getTimeZone("UTC")
        val time = originalFormat.parse(x)
        val formattedTime = targetTimeFormat.format(time!!)
        Log.e("148"," time "+formattedTime)

        val allDate = formattedDate+" "+formattedTime.toString().toUpperCase()
        return allDate
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(x: String?): String {
        if (!x.isNullOrEmpty()) {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            //. originalFormat.timeZone = TimeZone.getTimeZone("UTC")
            // var targetFormat = SimpleDateFormat("dd/MM/yyyy")
            val targetFormat = SimpleDateFormat(" d MMM, yy")
            originalFormat.timeZone = TimeZone.getDefault()
            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = originalFormat.parse(x)
            val formattedDate = targetFormat.format(date!!)
            Log.e("143", " date " + formattedDate)
            val targetTimeFormat = SimpleDateFormat("hh:mm a")
            //originalFormat.timeZoMyne = TimeZone.getDefault()
            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
            val time = originalFormat.parse(x)
            val formattedTime = targetTimeFormat.format(time!!)
            Log.e("148", " time " + formattedTime)

            val allDate = formattedDate
            return allDate
        } else {
            return ""
        }
    }


    @SuppressLint("SimpleDateFormat")
    fun getDateBooking(x: String?): String {
        if (!x.isNullOrEmpty()) {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            //. originalFormat.timeZone = TimeZone.getTimeZone("UTC")
            // var targetFormat = SimpleDateFormat("dd/MM/yyyy")
            val targetFormat = SimpleDateFormat(" d MMM, yyyy")
            originalFormat.timeZone = TimeZone.getDefault()
            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = originalFormat.parse(x)
            val formattedDate = targetFormat.format(date!!)
            Log.e("143", " date " + formattedDate)
            val targetTimeFormat = SimpleDateFormat("hh:mm:ss a")
            //originalFormat.timeZoMyne = TimeZone.getDefault()
            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
            val time = originalFormat.parse(x)
            val formattedTime = targetTimeFormat.format(time!!)
            Log.e("148", " time " + formattedTime)

            val allDate = formattedDate + " " + formattedTime
            return allDate
        } else {
            return ""
        }

    }


    @SuppressLint("SimpleDateFormat")
    fun getDateBookingWithDay(x: String?): String {
        if (!x.isNullOrEmpty()) {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            //. originalFormat.timeZone = TimeZone.getTimeZone("UTC")
            // var targetFormat = SimpleDateFormat("dd/MM/yyyy")
            val targetFormat = SimpleDateFormat("EEEE, MMM dd, yyyy")
            originalFormat.timeZone = TimeZone.getDefault()
            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = originalFormat.parse(x)
            val formattedDate = targetFormat.format(date!!)
            Log.e("143", " date " + formattedDate)
            val targetTimeFormat = SimpleDateFormat("hh:mm:ss a")
            //originalFormat.timeZoMyne = TimeZone.getDefault()
            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
            val time = originalFormat.parse(x)
            val formattedTime = targetTimeFormat.format(time!!)
            Log.e("148", " time " + formattedTime)

            val allDate = formattedDate
            return allDate
        } else {
            return ""
        }

    }


    override fun showProgress() {
        mProgress!!.show()
    }

    override fun hideProgress() {
        mProgress!!.cancel()
    }

    fun showSnackBar(message: String) {
        showSnackbarMessage(message, this)
    }

    fun showSnackbarMessage(message: String, activity: Activity) {
        val snackbar = Snackbar.make(
            activity.window.decorView.findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )
        val view = snackbar.view
        view.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent))
        val tv = view.findViewById<TextView>(R.id.snackbar_text)
        tv.setTextColor(Color.WHITE)
        snackbar.show()
    }

    override fun isNetworkConnected(): Boolean {
        return NetworkUtils.isNetworkConnected(applicationContext)
    }

    override fun showServerError() {
    }

    override fun onSuccess(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun navigate(destination : Class<*>)
    {
        startActivity(Intent(this@BaseActivity,destination))
    }

    fun navigateWithExtras(destination : Class<*>,extras : Int)
    {
        startActivity(Intent(this@BaseActivity,destination).putExtra("type",extras))
    }
    fun navigateWithFinish(destination : Class<*>)
    {
        startActivity(Intent(this@BaseActivity,destination))
        finish()
    }
    fun navigateFinishAffinity(destination : Class<*>)
    {
        startActivity(Intent(this@BaseActivity,destination))
        finishAffinity()
    }
    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun hideKeyboard() {

        if (currentFocus != null)
        {
            val inputMethodManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }


    fun openGallery() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            if (isGalleryPermissions()) {
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, GALLERYREQUESTCODE)//one can be replaced with any action code
            }
        }
        else {
            // do something for phones running an SDK before lollipop
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(
                pickPhoto, GALLERYREQUESTCODE
            )//one can be replaced with any action code
        }
    }

    fun openCamera() {
        if (checkAndRequestPermissions(CAMERAPERMISSIONCODE)) {
            camera()
        }
    }


    fun isCalendarPermissions(): Boolean {
        var isGranted: Boolean = false
        val permission = ContextCompat.checkSelfPermission(
            this@BaseActivity,
            Manifest.permission.READ_CALENDAR
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("GalleryPermissions", "Permission to get image from gallery denied")
            isGranted = false
            checkAndRequestPermissionsForCalendar(CALENDARPERMISSION)
        } else {
            isGranted = true
        }
        return isGranted
    }

    fun isGalleryPermissions(): Boolean {
        var isGranted: Boolean = false
        val permission = ContextCompat.checkSelfPermission(
            this@BaseActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("GalleryPermissions", "Permission to get image from gallery denied")
            makeGalleryRequest()
            isGranted = false
        } else {
            isGranted = true
        }
        return isGranted
    }

    fun makeGalleryRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READSTORAGEPERMISSIONCODE
        )
    }


    private fun checkAndRequestPermissionsForCalendar(REQUEST_ID_MULTIPLE_PERMISSIONS: Int): Boolean {

        val read_Calendar = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CALENDAR
        )

        val write_Calendar = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_CALENDAR
        )
        val listPermissionsNeeded = ArrayList<String>()

        if (read_Calendar != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_CALENDAR)
        }
        if (write_Calendar != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_CALENDAR)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toArray(arrayOfNulls<String>(listPermissionsNeeded.size)),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }


    private fun checkAndRequestPermissions(REQUEST_ID_MULTIPLE_PERMISSIONS: Int): Boolean {
        val camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        val storage = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val read = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val read_Calendar = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CALENDAR
        )

        val write_Calendar = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_CALENDAR
        )
        val listPermissionsNeeded = ArrayList<String>()
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA)
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toArray(arrayOfNulls<String>(listPermissionsNeeded.size)),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            106 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startActivity(ContactUsActivity.callIntent)
                }
            }
            CAMERAPERMISSIONCODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("baseactivity", "Permission has been denied by user")
                } else {
                    if (checkAndRequestPermissions(CAMERAPERMISSIONCODE)) {
                        openCamera()
                        Log.i("baseactivity", "Permission has been granted by user")
                    }
                }
            }
            READSTORAGEPERMISSIONCODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("baseactivity", "Permission has been denied by user")
                } else {
                    openGallery()
                    Log.i("baseactivity", "Permission has been granted by user")
                }
            }

            CALENDARPERMISSION -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("baseactivity", "Permission has been denied by user")
                } else {

                    Log.i("baseactivity", "Permission has been granted by user")
                }
            }
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocation()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Log.e("baseactivity", "pemanenet "+shouldShowRequestPermissionRationale(permissions[0]))
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(shouldShowRequestPermissionRationale(permissions[0]) == false){
                            isPermanentlyDenied()
                        }
                    }
                }
            }
        }
    }

    fun openLocationPicker(){
        Places.initialize(this, getString(R.string.places_api_key))
        val placesClient = Places.createClient(this)
        val fields = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS
        )
        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
        startActivityForResult(intent, LOCATIONPERMISSIONCODE)
    }

    private fun camera() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(
            takePicture, CAMERAPERMISSIONCODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getRequestCode(requestCode, data)
        } else {
        }
    }

    fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this@BaseActivity)
        pictureDialog.setTitle("Select")
        val pictureDialogItems = arrayOf<String>(
            "Gallery",
            "Camera",
            "Cancel"
        )
        pictureDialog.setItems(
            pictureDialogItems,
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    when (which) {
                        0 -> openGallery()
                        1 -> openCamera()
                        2 -> dialog.dismiss()
                    }
                }
            })
        pictureDialog.show()
    }
    override fun getRequestCode(requestcode: Int, data: Intent?) {

    }
    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY
        )
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg")
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                this,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    fun showToast2(message: String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("WrongConstant")
    private fun showMessage(isConnected: Boolean) {

        if (!isConnected) {
            val messageToUser = "Internet is not Connected!"
            val customlayout = findViewById<View>(android.R.id.content)
            mSnackBar = Snackbar.make(
                customlayout,
                messageToUser,
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.
            mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
            mSnackBar?.view?.setBackgroundColor(Color.RED)
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
        }

    }


    fun isNetworkConnected2(): Boolean {
        return NetworkUtils.isNetworkConnected(applicationContext)
    }

    override fun onResume() {
        super.onResume()

        MyReceiver.connectivityReceiverListener = this

//        Log.d("Notidata", "notidata :- BaseActivity running")
//        LocalBroadcastManager.getInstance(this).registerReceiver((mNotiDataReceiver ),
//            IntentFilter("mNotiDataReceiver"))
    }

    override fun onDestroy() {
        try {
            if(receiver!=null){
                unregisterReceiver(receiver)}
        } catch (e: Exception) {
            Log.w("BADSHAH", "Exception aya snackbar receiver mai from onDestroy: $e")
        }
        super.onDestroy()
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }

//    override fun onStop() {
//        super.onStop()
//        Log.d("Notidata", "notidata :- BaseActivity stopped")
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotiDataReceiver)
//    }
//
//    private val mNotiDataReceiver = object :BroadcastReceiver()
//    {
//        override fun onReceive(context: Context?, intent: Intent?)
//        {
//            val data = intent!!.extras!!.getString("notidata")
//            Log.d("Notidata", "notidata :- $data")
//
//
////            val nvi_intent = Intent(this, RatingActivity::class.java)
////            nvi_intent.putExtra("bookingId",booking_details?.id?:"")
////            nvi_intent.putExtra("provider",booking_details?.sentTo?.id?:"")
////            nvi_intent.putExtra("providerImage",booking_details!!.sentTo!!.image?:"")
////            nvi_intent.putExtra("providerName",booking_details!!.sentTo!!.name?:"")
////            startActivityForResult(nvi_intent,10)
//
//        }
//
//    }


    @SuppressLint("SimpleDateFormat")
    fun getMilliFromDate(dateFormat: String?): Long {
        var date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        try {
            date = formatter.parse(dateFormat!!)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date.time
    }


}
