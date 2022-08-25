package com.fly.mexicoapp.bean

import java.io.Serializable

class ApplyInfoBean:Serializable {
    var phonebook_info :ArrayList<ContactInfoBean>?=null //通讯录
    var device_info :ArrayList<DeviceInfoBean>?=null //设备信息
    var geo_info :ArrayList<LocationBean>?=null //地理位置信息
    var applist_info :ArrayList<InstallationInfoBean>?=null //app安装信息
    var sms_info :ArrayList<SmsBean>?=null //短信数据
    var album_info :ArrayList<AlbumInfoBean>?=null //相册信息
    var calenders_info :ArrayList<CalendersInfoBean>?=null //日历信息
    override fun toString(): String {
        return "ApplyInfoBean(phonebook_info=$phonebook_info, device_info=$device_info, geo_info=$geo_info, applist_info=$applist_info, sms_info=$sms_info, album_info=$album_info, calenders_info=$calenders_info)"
    }

}