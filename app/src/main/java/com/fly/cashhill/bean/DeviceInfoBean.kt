package com.fly.cashhill.bean

import java.io.Serializable

class DeviceInfoBean : Serializable {
    var create_time :Long =0
    var VideoExternal //视频外部文件个数
            = 0
    var phone_brand //手机品牌	samsung
            : String? = null
    var cur_wifi_mac //最近WiFi的mac地址	e4:26:86:72:fc:b0
            : String? = null
    var imei2 //设备号2	358208088672438
            : Long = 0
    var imei1 //设备号1	358208088672438
            : Long = 0
    var build_fingerprint //指纹信息
            : String? = null
    var cur_wifi_ssid //最近WiFi的WLAN网络名称
            : String? = null
    var DownloadFiles //下载文件个数
            = 0
    var time_zoneId //时区id	Africa/Lagos
            : String? = null
    var kernel_version //string	内核版本	3.18.19-11839938
            : String? = null
    var currentSystemTime //1618985088
            : String? = null
    var AudioInternal //音频内部文件个数
            : String? = null
    var nettype //网络类型 unknown：0，GPRS：1，EDGE： 2，UMTS：3，CDMA: Either IS95A or IS95B：4，EVDO revision 0：5
            : String? = null
    var serial //序列号	4200bb14ea4734db
            : String? = null
    var android_id //安卓id
            : String? = null
    var kernel_architecture //string	内核架构	armeabi-v7a
            : String? = null
    var build_id //版本id	MMB29T
            : String? = null
    var ImagesInternal //图片内部文件个数
            : String? = null
    var build_number //string	版本号	MMB29T.G532MUBU1AQG4
            : String? = null
    var mac // mac地址	F0:EE:10:0F:3C:98
            : String? = null
    var board //主板	MT6737T
            : String? = null
    var VideoInternal //视频内部文件个数	0
            = 0
    var AudioExternal //外部文件个数	2
            = 0
    var build_time //int	版本日期
            : Long = 0
    var wifilist //WiFi名称
            : List<String>? = null
    var sensorcount //传感器数量
            : String? = null
    var time_zone //时区	CST
            : String? = null
    var release_date //int	更新日期	1499779791000
            : Long = 0
    var device_name //设备名称	grandpplte
            : String? = null
    var ImagesExternal //图片外部文件个数	5
            : String? = null
    var security_patch_level //安全补丁时间	2017-07-01
            : String? = null
    var storage: StorageDataBean? = null
    var general_data: GeneralDataBean? = null
    var hardware: HardwareDataBean? = null
    var public_ip: PublicIpDataBean? = null
    var battery_status: BatteryStatusDataBean? = null
    var device_info: DeviceInfoDataBean? = null
    override fun toString(): String {
        return "DeviceInfoBean(create_time=$create_time, VideoExternal=$VideoExternal, phone_brand=$phone_brand, cur_wifi_mac=$cur_wifi_mac, imei2=$imei2, imei1=$imei1, build_fingerprint=$build_fingerprint, cur_wifi_ssid=$cur_wifi_ssid, DownloadFiles=$DownloadFiles, time_zoneId=$time_zoneId, kernel_version=$kernel_version, currentSystemTime=$currentSystemTime, AudioInternal=$AudioInternal, nettype=$nettype, serial=$serial, android_id=$android_id, kernel_architecture=$kernel_architecture, build_id=$build_id, ImagesInternal=$ImagesInternal, build_number=$build_number, mac=$mac, board=$board, VideoInternal=$VideoInternal, AudioExternal=$AudioExternal, build_time=$build_time, wifilist=$wifilist, sensorcount=$sensorcount, time_zone=$time_zone, release_date=$release_date, device_name=$device_name, ImagesExternal=$ImagesExternal, security_patch_level=$security_patch_level, storage=$storage, general_data=$general_data, hardware=$hardware, public_ip=$public_ip, battery_status=$battery_status, device_info=$device_info)"
    }


}