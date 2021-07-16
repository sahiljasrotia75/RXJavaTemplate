package com.geniecustomer.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.geniecustomer.R

class CustomProgressDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress_dialog)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)

    }

}