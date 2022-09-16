package com.fly.cashhill.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

object SoftKeyboardUtils {
    @JvmStatic
    fun hideSoftKeyboard(context: Context) {
        val focus_view = (context as Activity).currentFocus
        if (focus_view != null) {
            val inputManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(focus_view.windowToken, 0)
        }
    }
}