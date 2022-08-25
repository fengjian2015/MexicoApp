package com.fly.mexicoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.fly.mexicoapp.bean.BatteryBean
import com.fly.mexicoapp.utils.Cons.KEY_BATTERY_INFO
import com.fly.mexicoapp.utils.Cons.KEY_LIMIT_TIME
import com.fly.mexicoapp.utils.LogUtils
import com.fly.mexicoapp.utils.SPUtils
import com.google.gson.Gson
import java.util.*


class BatteryChangeReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        intent?.let {
            var limit = SPUtils.getLong(KEY_LIMIT_TIME, 0)
            LogUtils.d("限制1："+limit +"  当前"+System.currentTimeMillis())
            if (limit + 15000 > System.currentTimeMillis()) {
                //限制15秒记录一次，避免频繁记录
                return
            }
            SPUtils.putLong(KEY_LIMIT_TIME, System.currentTimeMillis())
            var batteryBean = BatteryBean()
            val batteryTotal = it.extras?.getInt("scale")?.toFloat()
            val level: Float? = it.extras?.getInt("level")?.toFloat()
            batteryBean.battery_max = batteryTotal.toString()
            batteryBean.battery_now = level.toString()
            if (level != null && batteryTotal != null) {
                batteryBean.battery_level =
                    String.format(Locale.getDefault(), " %.2f", level / batteryTotal)
            }
            batteryBean.plugged = it.getIntExtra(BatteryManager.EXTRA_PLUGGED,0)
            batteryBean.health = it.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            batteryBean.status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            batteryBean.temperature =
                (it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)/10).toString()
            LogUtils.d("电池温度："+batteryBean.temperature)
            batteryBean.technology = it.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY).toString();
            SPUtils.putString(KEY_BATTERY_INFO, Gson().toJson(batteryBean))
        }
    }
}