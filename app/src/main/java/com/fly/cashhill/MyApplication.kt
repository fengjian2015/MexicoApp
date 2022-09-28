package com.fly.cashhill

import android.app.Application
import com.flurry.android.FlurryAgent
import com.fly.cashhill.network.HttpClient
import com.fly.cashhill.utils.ActivityManager
import com.fly.cashhill.utils.AppsFlyerUtil
import com.fly.cashhill.utils.Cons
import com.fly.cashhill.utils.LogUtils


class MyApplication : Application() {
    companion object {
        @JvmStatic
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        FlurryAgent.Builder()
            .withLogEnabled(true)
            .build(this, "ZGCMWYX2PTYHD78QXF77")
        ActivityManager.registerActivityLifecycleCallbacks()
        LogUtils.setDebug(com.fly.cashhill.BuildConfig.DEBUG)
        HttpClient.instance.init(Cons.baseUrl)
        AppsFlyerUtil.initAppsFlyer()
    }
}

