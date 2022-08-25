package com.fly.mexicoapp.bean

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
    override fun toString(): String {
        return "BatteryBean(battery_max='$battery_max', battery_now='$battery_now', battery_level='$battery_level', health=$health, status=$status, temperature='$temperature', technology='$technology')"
    }

}