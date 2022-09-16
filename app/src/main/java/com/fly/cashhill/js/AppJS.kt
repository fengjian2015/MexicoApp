package com.fly.cashhill.js

import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.annotation.Keep
import androidx.lifecycle.ViewModelStoreOwner
import com.fly.cashhill.js.bean.AppJSBean
import com.fly.cashhill.utils.LogUtils
import com.google.gson.Gson

/**
 * 桥接
 */

@Keep
class AppJS constructor(webView: WebView, viewModelStoreOwner: ViewModelStoreOwner) {
    val APP_CLIENT = "appAndroid"
    var appJsParseData: AppJsParseData

    init {
        appJsParseData = AppJsParseData(webView, viewModelStoreOwner)
    }

    @JavascriptInterface
    fun appJS(json: String) {
        LogUtils.d("js调用app:$json")
        val model: AppJSBean = Gson().fromJson(json, AppJSBean::class.java)
        appJsParseData.parseData(model.data, model.event, model.id)
    }
}