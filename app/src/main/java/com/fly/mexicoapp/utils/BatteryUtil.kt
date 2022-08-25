package com.fly.mexicoapp.utils

import android.content.Intent
import android.content.IntentFilter
import com.fly.mexicoapp.BatteryChangeReceiver
import com.fly.mexicoapp.MyApplication
import com.fly.mexicoapp.bean.BatteryBean
import com.fly.mexicoapp.utils.Cons.KEY_BATTERY_INFO
import com.google.gson.Gson


object BatteryUtil {

    fun registerReceiver(){
        var intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        MyApplication.application.registerReceiver(BatteryChangeReceiver(), intentFilter)
    }

    fun getBatteryBean():BatteryBean{
        var batteryBean = BatteryBean()
        val string = SPUtils.getString(KEY_BATTERY_INFO)
        try {
            string?.let {
                batteryBean = Gson().fromJson(string,BatteryBean::class.java)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return batteryBean
    }
}