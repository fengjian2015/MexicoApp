package com.fly.mexicoapp.bean

import java.io.Serializable

class DeviceInfoBean : Serializable {
    var regDevice: String = "Android" //注册设备类型1: Laptop 2: Web 3: iOS 4: Android

    var regWifiAddress: String = "" //注册wifi地址
    var wifiList: ArrayList<WifiListBean>? = null //注册时环境wifi列表
    var imei: String = ""    //IMEI，如无，则传androidID
    var imsi: String = ""      //IMSI
    var phoneModel: String = "" //手机型号
    var phoneVersion: String = ""//系统版本
    var macAddress: String = "" //mac地址
    var availableSpace: String = ""//可用空间  单位字节
    var sensorCount: String = "" //传感器数量
    var totalRam: String = ""//总空间 单位字节
    var deviceName: String = ""//设备名称
    var isRooted: String = ""//是否 root，仅对 android 有效， 0--否；1--是
    var basebandVer: String = "" //基带版本
    var screenResolution: String = ""//屏幕分辨率
    var ip: String = ""//ip
    var deviceCreateTime: String = ""//手机信息抓取时间  YYYY-MM-DD HH:MM:SS）
    var battery_temper: Int = 0//电池温度
    var cores: Int = 0//Cpu核心数量
    var app_max_memory: String = ""//App 最大内存量   单位字节
    var app_free_memory: String = ""//App最大可用内存  单位字节
    var update_mills: Long = 0//设备启动后的毫秒，不包括休眠时间
    var elapsed_realtime: Long = 0//设备启动后的毫秒，包括休眠时间
    var network_type: String = ""//设备网络类型，'4G'，'WIFI'，'3G'，'2G'，'5G'
    var is_simulator: Int = 0//1 表示是， 0 表示不是
    var android_id: String = ""//Android 系统生成id
    var time_zone_id: String = ""//设备默认时区
    var battery: BatteryBean? = null//电池信息
    var locale_ios3_country: String = ""//语言环境所在的国家字母缩写
    var locale_display_language: String = ""//区域语言名称，例如：en_US
    var gaid: String = ""//gaid
    var wifi_ssid: String = ""//当前接入的wifi 的ssid
    var wifi_mac: String = ""//当前接入的wifi 的mac 地址
    var longitude: String = ""//当前的经度
    var latitude: String = ""//当前的维度
    var sdk_public_ip: String = ""//公网IP
    var isUsingProxyPort: String = ""//是否使用代理  false or true
    var isUsingVPN: String = ""//是否使用vpn false or ture
    var isUSBDebug: String = ""//是否开启 USB 调试 false or true
    var bluetooth_saved: String = ""//存储蓝牙个数
    var sensorList: ArrayList<SensorListBean>? = null//传感器的详细信息
    var phone_type: String = ""//指示设备电话类型的常量。NONE：0，GS：1，CDMA：2，SIP=3
    var language: String = ""//系统语言 ，注意，不是locale_display_language
    var network_operator_name: String = ""//网络运营商名称
    var locale_iso_3_language: String = ""//语言环境的 3 字母缩写
    var build_fingerprint: String = ""//指纹信息
    var cur_wifi_ssid: String = ""//最近WiFi的WLAN网络名称
    var DownloadFiles: Int = 0//下载文件个数
    var battery_status: Int = 0//是否正在充电，UNKNOWN： 1，CHARGING：2，DISCHARGING：3
    var is_usb_charge: Int = 0;//是否 usb 充电，0：否，1：是
    var is_ac_charge: Int = 0;//是否交流充电，0：否，1：是
    var AudioInternal: Int = 0;//音频内部文件个数
    var nettype: String =
        "0"//网络类型 unknown：0，GPRS：1，EDGE： 2，UMTS：3，CDMA: Either IS95A or IS95B：4，EVDO revision 0：5
    var iccid1: String = ""//集成电路卡识别码
    var serial: String = ""//序列号
    var kernel_architecture: String = ""//内核架构
    var build_id: String = ""//版本id
    var ImagesInternal: Int = 0//图片内部文件个数
    var build_number: String = ""//版本号
    var ContactGroup: String = ""//联系小组个数
    var gsfid: String = ""//gsfid
    var board: String = ""//主板
    var VideoInternal: Int = 0;//视频内部文件个数
    var AudioExternal: Int = 0;//外部文件个数
    var build_time: String = ""//版本日期  YYYY-MM-DD
    var wifiCount: Int = 0//Wifi数量
    var time_zone: String = ""//时区  示例 CST
    var release_date: String = ""//更新日期YYYY-MM-DD
    var iccid2: String = ""//集成电路卡识别码2
    var meid: String = ""//移动设备识别码
    var ImagesExternal: Int = 0;//图片外部文件个数
    var security_patch_level: String = ""//YYYY-MM-DD
    var API_level: String = ""//API版本
    override fun toString(): String {
        return "DeviceInfoBean(regDevice='$regDevice', regWifiAddress='$regWifiAddress', wifiList=$wifiList, imei='$imei', imsi='$imsi', phoneModel='$phoneModel', phoneVersion='$phoneVersion', macAddress='$macAddress', availableSpace='$availableSpace', sensorCount='$sensorCount', totalRam='$totalRam', deviceName='$deviceName', isRooted='$isRooted', basebandVer='$basebandVer', screenResolution='$screenResolution', ip='$ip', deviceCreateTime='$deviceCreateTime', battery_temper=$battery_temper, cores=$cores, app_max_memory='$app_max_memory', app_free_memory='$app_free_memory', update_mills=$update_mills, elapsed_realtime=$elapsed_realtime, network_type='$network_type', is_simulator=$is_simulator, android_id='$android_id', time_zone_id='$time_zone_id', battery=$battery, locale_ios3_country='$locale_ios3_country', locale_display_language='$locale_display_language', gaid='$gaid', wifi_ssid='$wifi_ssid', wifi_mac='$wifi_mac', longitude='$longitude', latitude='$latitude', sdk_public_ip='$sdk_public_ip', isUsingProxyPort='$isUsingProxyPort', isUsingVPN='$isUsingVPN', isUSBDebug='$isUSBDebug', bluetooth_saved='$bluetooth_saved', sensorList=$sensorList, phone_type='$phone_type', language='$language', network_operator_name='$network_operator_name', locale_iso_3_language='$locale_iso_3_language', build_fingerprint='$build_fingerprint', cur_wifi_ssid='$cur_wifi_ssid', DownloadFiles=$DownloadFiles, battery_status=$battery_status, is_usb_charge=$is_usb_charge, is_ac_charge=$is_ac_charge, AudioInternal=$AudioInternal, nettype='$nettype', iccid1='$iccid1', serial='$serial', kernel_architecture='$kernel_architecture', build_id='$build_id', ImagesInternal=$ImagesInternal, build_number='$build_number', ContactGroup='$ContactGroup', gsfid='$gsfid', board='$board', VideoInternal=$VideoInternal, AudioExternal=$AudioExternal, build_time='$build_time', wifiCount=$wifiCount, time_zone='$time_zone', release_date='$release_date', iccid2='$iccid2', meid='$meid', ImagesExternal=$ImagesExternal, security_patch_level='$security_patch_level', API_level='$API_level')"
    }


}