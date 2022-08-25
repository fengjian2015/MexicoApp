package com.fly.mexicoapp

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.fly.mexicoapp.network.HttpClient
import com.fly.mexicoapp.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyApplication : Application() {
    companion object {
        @JvmStatic
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        Utils.init(this)
        ActivityManager.registerActivityLifecycleCallbacks()
        LogUtils.setDebug(BuildConfig.DEBUG)
        HttpClient.instance.init(Cons.baseUrl)
        //电池信息需要广播
        BatteryUtil.registerReceiver()
        //公网ip
        GlobalScope.launch(Dispatchers.IO) {
            PublicIP.requestIp()
        }
        AppsFlyerUtil.initAppsFlyer()
    }
}

