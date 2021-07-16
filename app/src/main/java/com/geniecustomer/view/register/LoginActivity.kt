package com.geniecustomer.view.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.dash.model.login.FacebookEventObject
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.geniecustomer.R
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.model.signin.SigninResponse
import com.geniecustomer.model.social.FacebookPicResponse
import com.geniecustomer.model.social.SocialRequest
import com.geniecustomer.view.activities.HomeActivity
import com.geniecustomer.viewmodels.SigninViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_login.*
import org.apache.commons.lang3.StringEscapeUtils
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class LoginActivity : BaseActivity() {

    var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var gso: GoogleSignInOptions
    lateinit var callbackManager: CallbackManager
    private var str_firstname: String = ""
    private var str_lastname: String = ""
    private var str_email = ""
    private var str_id = ""
    private var str_photo = ""
    val RC_SIGN_IN: Int = 234
    var jsonObject: FacebookEventObject? = null
    val model by viewModel<SigninViewModel>()

    var socialLogin: Boolean = false
    var socialProvider: String = ""
    var fcmToken: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if ((sharedPref.contains("guest") && sharedPref.getBoolean("guest",false)==true)) {
            tvSkip.visibility = View.GONE
        }

        callbackManager = CallbackManager.Factory.create()

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        model.signinResponse().observe(this, Observer<ApiResponse> {
            this.consumeResponse(it!!)
        })
        getFcmToken()

    }


    private fun googleSignIn() {

        val signInIntent: Intent = mGoogleSignInClient!!.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun facebookLogin() {
//        FacebookSdk.sdkInitialize(this)
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile,email"))
        LoginManager.getInstance().loginBehavior = LoginBehavior.WEB_ONLY

        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(
                    loginResult.accessToken
                ) { `object`, response ->

                    socialLogin = true

                    Log.e("fbDAta", MyApp.gson.toJson(`object`))

                    try {
                        str_firstname = `object`.getString("first_name")
                    } catch (e: Exception) {

                        str_firstname = ""
                    }

                    try {
                        str_lastname = `object`.getString("last_name")
                    } catch (e: Exception) {
                        str_lastname = ""
                    }
                    try {
                        str_email = `object`.getString("email")
                    } catch (e: Exception) {
                        str_email = ""
                    }
                    try {
                        str_id = `object`.getString("id")
                    } catch (e: Exception) {
                        str_id = ""
                    }

                    try {
                        val profile_pic = `object`.getString("picture")
                        Log.e("profile_pic", "" + profile_pic)
                        val pic_url_obj = StringEscapeUtils.unescapeJava(profile_pic)
                        val picResponse =
                            MyApp.gson.fromJson(pic_url_obj, FacebookPicResponse::class.java)
                        str_photo = picResponse.data?.url ?: ""
                        Log.e("str_photo", str_photo)
                    } catch (e: Exception) {
                        str_photo = ""
                    }

                    jsonObject = FacebookEventObject(
                        str_firstname,
                        str_lastname,
                        str_id,
                        str_email,
                        "",
                        ""
                    )

                    if (jsonObject != null && str_id.length > 0) {
                        socialProvider = "FB"
                        val socialRequest = SocialRequest(
                            str_firstname,
                            str_lastname,
                            "",
                            str_id,
                            "googleId",
                            str_email, str_photo, "Android", fcmToken
                        )
                        if (str_email.isEmpty()) {
                            showSnackBar("Please add an email in your facebook account.")
                        } else {
                            model.hitSocialSignin(socialRequest)
                        }
                    }

                }

                val parameters = Bundle()
                parameters.putString(
                    "fields",
                    "id,name,email,first_name,last_name,gender,picture.type(large)"
                )
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                if (AccessToken.getCurrentAccessToken() == null) {
                    return
                    // already logged out
                }

                GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/permissions/",
                    null,
                    HttpMethod.DELETE
                ) {
                    LoginManager.getInstance().logOut()
                    facebookLogin()
                }.executeAsync()
            }

            override fun onError(e: FacebookException) {
                showSnackBar("" + e.message.toString())
                Log.e("5656", e.message.toString())
                AccessToken.setCurrentAccessToken(null)
                LoginManager.getInstance()
                    .logInWithReadPermissions(
                        this@LoginActivity,
                        Arrays.asList("public_profile,email")
                    )
            }
        })
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

    private fun renderSuccessResponse(response: JsonElement) {
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)

            val signupResponse = MyApp.gson.fromJson(data, SigninResponse::class.java)
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

                if (sharedPref.contains("guest") && sharedPref.getBoolean("guest", false) == true) {
                    editor.remove("guest").apply()
                    finish()
                } else {
                    navigateFinishAffinity(HomeActivity::class.java)
                }
            } else {
                signupResponse.message?.let { showSnackBar(it) }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                Log.e("call", "12222")
                //Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                Log.e("call", "acoount email--->>>   " + account!!.email!!)

                Log.e("displayName ", account.displayName!!)
                Log.e("FamilyName ", account.familyName!!)
                Log.e("GivenName ", account.givenName!!)
                Log.e("google id ", account.id!!)
                socialLogin = true

                try {
                    str_firstname = account.givenName!!
                } catch (e: Exception) {
                    str_firstname = ""
                }

                try {
                    str_lastname = account.familyName!!
                } catch (e: Exception) {
                    str_lastname = ""
                }
                try {
                    str_email = account.email!!
                } catch (e: Exception) {
                    str_email = ""
                }
                try {
                    str_id = account.id!!
                } catch (e: Exception) {
                    str_id = ""
                }

                try {
                    str_photo = account.photoUrl!!.toString()
                    Log.e("url_photo", str_photo)
                } catch (e: Exception) {
                    str_photo = ""
                }

                jsonObject =
                    FacebookEventObject(str_firstname, str_lastname, str_id, str_email, "", "")
                Log.e("jsonObject", "" + jsonObject)
                // viewModel.googleLoginUser(account.email, "", account.givenName, account.familyName, account.id)
                if (jsonObject != null && str_id.length > 0) {
                    //socialProvider = "GOOGLE"
                    val socialRequest = SocialRequest(
                        str_firstname,
                        str_lastname,
                        "",
                        str_id,
                        "googleId",
                        str_email, str_photo,"Android",fcmToken
                    )
                    Log.e("samcasm", "sacnasmklc")
                    model.hitSocialSignin(socialRequest)
                }


                GoogleSignIn.getClient(this, gso).signOut()

                //authenticating with firebase
                //firebaseAuthWithGoogle(account);
            } catch (e: ApiException) {
                showSnackBar(GoogleSignInStatusCodes.getStatusCodeString(e.statusCode))

                Log.e("call", "google response--> " + e.message)
//                showSnackBar(""+e.message)
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.tvLoginWEmail -> {
                navigateWithExtras(EnterEmailActivity::class.java, 0)
            }
            R.id.tvLoginWPhone -> {
                navigateWithExtras(EnterPhoneNoActivity::class.java, 1)
            }
            R.id.tvSkip -> {
                navigateFinishAffinity(HomeActivity::class.java)
            }
            R.id.ivFacebook -> facebookLogin()
            R.id.ivGmail -> googleSignIn()
        }
    }

    private fun getFcmToken() {
        //FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("fcm_task", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                fcmToken = task.result?.token!!
                editor.putString("fcmToken", fcmToken).apply()
                Log.e("device Token", " " + fcmToken)
            })
    }
}



