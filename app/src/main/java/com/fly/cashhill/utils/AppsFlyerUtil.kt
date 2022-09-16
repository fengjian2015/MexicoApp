package com.fly.cashhill.utils

import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.fly.cashhill.MyApplication
import com.fly.cashhill.utils.Cons.APPS_FLYER_KEY

object AppsFlyerUtil {

    fun postAF(evenName: String?) {
        var evenName = evenName
        val map = HashMap<String, Any?>()
        val loadId: String
        if (evenName != null && evenName.contains("|")) {
            val split = evenName.split("\\|".toRegex()).toTypedArray()
            loadId = split[1]
            evenName = split[0]
            map["loan_id"] = loadId
        }
        map["mobile"] = UserInfoManger.getUserPhone()
        map["event_code"] = evenName
        AppsFlyerLib.getInstance().logEvent(MyApplication.application, evenName, map)
    }

    fun initAppsFlyer() {
        AppsFlyerLib.getInstance().init(APPS_FLYER_KEY, object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(map: Map<String, Any>) {
                LogUtils.d("AppsFlyerLib Success: $map")
                for (attrName in map.keys) {
                    if ("af_status" == attrName) {
                        val data = map[attrName] as String?
                        if ("Organic" == data) {
                            SPUtils.putString(Cons.KEY_AF_CHANNEL, data)
                        } else if ("Non-organic" == data) {
                            SPUtils.putString(Cons.KEY_AF_CHANNEL, map["media_source"].toString())
                        }
                    }
                }
            }

            override fun onConversionDataFail(s: String) {
                LogUtils.d("AppsFlyerLib DataFail: $s")
            }

            override fun onAppOpenAttribution(map: Map<String, String>) {
                LogUtils.d("AppsFlyerLib Attribution: $map")
            }

            override fun onAttributionFailure(s: String) {
                LogUtils.d("AppsFlyerLib AttributionFailure: $s")
            }
        }, MyApplication.application)
        AppsFlyerLib.getInstance().start(MyApplication.application, APPS_FLYER_KEY, object : AppsFlyerRequestListener {
            override fun onSuccess() {
                LogUtils.d("AppsFlyerLib start Success ")
            }

            override fun onError(i: Int, s: String) {
                LogUtils.d("AppsFlyerLib start Error $s")
            }
        })
        AppsFlyerLib.getInstance().setDebugLog(com.fly.cashhill.BuildConfig.DEBUG)
    }
}