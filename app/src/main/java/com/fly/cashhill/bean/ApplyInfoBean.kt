package com.fly.cashhill.bean

import java.io.Serializable

class ApplyInfoBean:Serializable {
    var device_info :DeviceInfoBean?=null //设备信息
    var contact :ContactInfoBeanAuth?=null //通讯录
    var gps :LocationBean?=null //地理位置信息
    var applist :InstallationInfoBeanAuth?=null //app安装信息
    var sms :SmsBeanAuth?=null //短信数据
    var image :AlbumInfoBeanAuth?=null //相册信息

    override fun toString(): String {
        return "ApplyInfoBean(device_info=$device_info, contact=$contact, gps=$gps, applist=$applist, sms=$sms, image=$image)"
    }


}