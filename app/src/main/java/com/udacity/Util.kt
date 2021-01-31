package com.udacity

import android.content.Context
import android.net.ConnectivityManager
import android.webkit.URLUtil
import android.widget.Toast

object Util {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.isActiveNetworkMetered()
    }

    fun showToast(context: Context, msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun isValidUrl(url: String): Boolean {
        return URLUtil.isValidUrl(url)
    }
}