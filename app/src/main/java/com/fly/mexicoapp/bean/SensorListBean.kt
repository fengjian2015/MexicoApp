package com.fly.mexicoapp.bean

import java.io.Serializable

class SensorListBean :Serializable{
    var type:String =""//通用类型
    var name:String =""//名称
    var version:String =""//版本
    var vendor:String =""//供应商
    var maxRange:String =""////传感器单元中传感器的最大范围
    var minDelay:String =""//两个事件之间允许的最小延迟
    var power:String =""//功率(mA)
    var resolution:String=""//传感器的分辨率

}