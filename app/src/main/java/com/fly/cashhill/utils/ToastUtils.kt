package com.fly.cashhill.utils

import android.widget.Toast
import com.fly.cashhill.MyApplication

object ToastUtils {

    fun showLong(message: String?) {
        try {
            Toast.makeText(MyApplication.application, message, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showShort(message: String?) {
        try {
            Toast.makeText(MyApplication.application, message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}