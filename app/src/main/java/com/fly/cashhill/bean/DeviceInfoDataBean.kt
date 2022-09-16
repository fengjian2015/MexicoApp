package com.fly.cashhill.bean

import java.io.Serializable

class DeviceInfoDataBean :Serializable{
    var isRooted //是否root	false
            : String? = null

    override fun toString(): String {
        return "DeviceInfoDataBean(isRooted=$isRooted)"
    }
}