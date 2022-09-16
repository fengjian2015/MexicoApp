package com.fly.cashhill.bean

import java.io.Serializable

class StorageDataBean:Serializable {
     var availableDiskSize //手机内部存储可用大小	383799296
            : String? = null
    var availableMemory //手机内部存储可用大小 b
            : String? = null
    var elapsedRealtime //从开机到现在的毫秒数(包括睡眠时间)
            : Long = 0
    var isUSBDebug //是否开启 USB 调试	false
            : String? = null
    var isUsingProxyPort //是否使用代理 true false
            : String? = null
    var isUsingVPN //是否使用VPN	false
            : String? = null
    var memorySize //手机内部存储总大小
            : Long = 0
    var ram_total_size //总内存大小	1475395584
            : Long = 0
    var totalDiskSize //sd_card 使用了的内存大小
            : String? = null

    override fun toString(): String {
        return "StorageDataBean(availableDiskSize=$availableDiskSize, availableMemory=$availableMemory, elapsedRealtime=$elapsedRealtime, isUSBDebug=$isUSBDebug, isUsingProxyPort=$isUsingProxyPort, isUsingVPN=$isUsingVPN, memorySize=$memorySize, ram_total_size=$ram_total_size, totalDiskSize=$totalDiskSize)"
    }
}