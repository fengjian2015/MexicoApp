package com.fly.cashhill.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.fly.cashhill.bean.BatteryBean
import com.fly.cashhill.utils.Cons.KEY_BATTERY_INFO
import com.google.gson.Gson


object BatteryUtil {

    fun registerReceiver(context: Context,batteryChangeReceiver: com.fly.cashhill.BatteryChangeReceiver){
        var intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryChangeReceiver, intentFilter)
    }

    fun unRegisterReceiver(context: Context,batteryChangeReceiver: com.fly.cashhill.BatteryChangeReceiver){
        context.unregisterReceiver(batteryChangeReceiver)
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