package com.geniecustomer.view.register

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.geniecustomer.R
import com.geniecustomer.base.BaseActivity
import kotlinx.android.synthetic.main.activity_alowlocation.*

class AllowLocationActivity : BaseActivity() {

    var myIntent : Intent?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alowlocation)

        myIntent = Intent(this,AllowPushActivity::class.java)
        if(intent!=null){
            myIntent?.putExtras(intent)
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when(v!!.id){
            R.id.ivBack -> {
                finish()
            }
            R.id.tv_yourLoc -> {
                if (tv_yourLoc.isEnabled) {
                    setLocation()
                }
            }
            R.id.tvCancel -> {
                startActivity(myIntent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            tv_yourLoc.isEnabled = true
            tv_yourLoc.background = ContextCompat.getDrawable(this,R.drawable.round_corner_bg)
        }
    }

    override fun onLocationFetched() {
        super.onLocationFetched()
        startActivity(myIntent)
    }

    override fun isPermanentlyDenied() {
        super.isPermanentlyDenied()
        tv_yourLoc.isEnabled = false
        tv_yourLoc.background = ContextCompat.getDrawable(this,R.drawable.round_corner_bg_disabled)
    }
}
