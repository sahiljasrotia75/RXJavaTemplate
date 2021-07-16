package com.geniecustomer.view.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.geniecustomer.R
import com.geniecustomer.base.BaseActivity
import kotlinx.android.synthetic.main.activity_contactus.*

class ContactUsActivity : BaseActivity() {

    private var lockBtnClicking:Boolean = false
    companion object {
        var callIntent: Intent? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactus)

        tvTitle.setOnClickListener {
            finish()
        }

        tvPhoneNo?.setOnClickListener {
            if (!lockBtnClicking) {
                lockBtnClicking = true
                callIntent = Intent(Intent.ACTION_CALL)
                callIntent?.data = Uri.parse("tel:+35699805029")
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CALL_PHONE), 106)
                }else {
                    startActivity(callIntent)
                }
            }
        }

        tvEmailId.setOnClickListener {
            if (!lockBtnClicking) {
                lockBtnClicking = true
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "admin@genie.mt", null
                    )
                )
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
                startActivity(Intent.createChooser(emailIntent, "Send email..."))
            }
        }


    }

    override fun onResume() {
        super.onResume()
        lockBtnClicking = false
    }

}
