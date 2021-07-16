package com.geniecustomer.interfaces

import android.content.Intent


interface RequestCode {
    fun getRequestCode(requestcode:Int,data: Intent?)
    fun onGetPermissionCode(requestCode:Int)
    fun onLocationFetched()
    fun isPermanentlyDenied()
}