package com.fly.cashhill.bean

import java.io.Serializable

class HardwareDataBean : Serializable{
    var device_model //型号	SM-G532M
            : String? = null
    var imei //设备号	000000000000000
            : String? = null
    var sys_version //系统版本	23
            : String? = null
    var screenResolution //物理尺寸	540 * 960
            : String? = null
    var manufacturerName //品牌	samsung
            : String? = null

    override fun toString(): String {
        return "HardwareDataBean(device_model=$device_model, imei=$imei, sys_version=$sys_version, screenResolution=$screenResolution, manufacturerName=$manufacturerName)"
    }
}