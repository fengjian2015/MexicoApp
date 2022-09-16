package com.fly.cashhill.bean

import java.io.Serializable

class BatteryBean :Serializable{
    var battery_max:String=""//最大电量
    var battery_now:String =""//当前电量
    var battery_level:String =""//电量百分比
    var health:Int =0//电池健康度
    var status:Int=0//电池状态
    var temperature:String =""//电池温度
    var technology:String =""//电池技术
    var plugged:Int = 0 //充电类型
    var is_usb_charge //是否 usb 充电，0：否，1：是
            = 0
    var is_ac_charge //是否交流充电，0：否，1：是
            = 0
    var batteryPercentage //电池百分比	0.17
            : String? = null
    var battery_temper //电池状态	32
            : String? = null
    var battery_health //电池寿命	2
            : String? = null
    var batteryStatus //是否正在充电，UNKNOWN： 1，CHARGING：2，DISCHARGING：3
            = 0

    override fun toString(): String {
        return "BatteryBean(battery_max='$battery_max', battery_now='$battery_now', battery_level='$battery_level', health=$health, status=$status, temperature='$temperature', technology='$technology', plugged=$plugged, is_usb_charge=$is_usb_charge, is_ac_charge=$is_ac_charge, batteryPercentage=$batteryPercentage, battery_temper=$battery_temper, battery_health=$battery_health, batteryStatus=$batteryStatus)"
    }


}