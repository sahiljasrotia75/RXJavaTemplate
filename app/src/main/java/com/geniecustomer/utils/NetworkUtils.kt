package com.geniecustomer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager


/**
 * Created by Sharanjeet Kaur on 13/02/2020.
 */
object NetworkUtils {
    @SuppressLint("MissingPermission")
    fun isNetworkConnected(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
        return false
    }
}