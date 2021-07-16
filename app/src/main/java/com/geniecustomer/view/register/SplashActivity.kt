package com.geniecustomer.view.register

import android.os.Bundle
import android.os.Handler
import com.geniecustomer.R
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.view.activities.HomeActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (sharedPref.contains("guest") && sharedPref.getBoolean(
                "guest",
                false
            ) == true
        ) {
            editor.remove("guest").apply()
        }

        //######################To Get the KeyHash for Facebook#################################################
//        try {
//            val info = packageManager.getPackageInfo(
//                "com.geniecustomer",
//                PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//
//        } catch (e: NoSuchAlgorithmException) {
//
//        }
//===========================To Get the KeyHash for Facebook==================================================

        Handler().postDelayed({
            if(sharedPref.contains("user_obj")){
                navigateWithFinish(HomeActivity::class.java)
            }
            else
                navigateWithFinish(IntroActivity::class.java)
        },2000)


    }
}
