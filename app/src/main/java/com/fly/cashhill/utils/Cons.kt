package com.fly.cashhill.utils

object Cons {
    @JvmStatic
//    val baseUrl: String = "http://112.124.4.191:7004"
    //正式环境
    val baseUrl: String = "https://api.cashhillss.com"
    val APPS_FLYER_KEY = "UdCiVWEuDa77NDd64FRAnW"
    var sendPhone:String = ""

    const val TACK_PHOTO = 1010
    const val SELECT_CONTACTS_CONTRACT = 1011

    //sp存储key
    val KEY_USER_INFO="myUserInfo"
    val KEY_BATTERY_INFO="BatteryInfo"
    val KEY_PUBLIC_IP="PublicIP"
    val KEY_LIMIT_TIME="LimitTime"
    val KEY_AF_CHANNEL ="KEY_AF_CHANNEL"
    val KEY_SERVICE_TIME ="keyServicesTimes"
    val KEY_DIFFERENCE_TIME ="keyDifTimes"

    val KEY_PROTOCAL_1 ="PROTOCAL_1"
    val KEY_PROTOCAL_2 ="PROTOCAL_2"
    val KEY_PROTOCAL_3 ="PROTOCAL_3"
    val KEY_PROTOCAL_4 ="PROTOCAL_4"
    val KEY_PROTOCAL_5 ="PROTOCAL_5"
    val KEY_PROTOCAL_6 ="PROTOCAL_6"
    val KEY_PROTOCAL_7 ="PROTOCAL_7"

    //js交互的event
    val JS_KEY_COPY="eventCopy"
    val JS_KEY_USER_INFO="eventUserInfo"
    val JS_KEY_LOGOUT="eventSignOut"
    val JS_KEY_CONTACT_INFO="eventContactInfo"
    val JS_KEY_DEVICE_INFO="eventDeviceInfo"
    val JS_KEY_LOCATION_INFO="eventLocationInfo"
    val JS_KEY_INSTALLATION_INFO="eventInstallationInfo"
    val JS_KEY_SMS_INFO="eventSmsInfo"
    val JS_KEY_TACK_PHOTO="eventTackPhoto"
    val JS_KEY_ALBUM_PHOTO="eventAlbumInfo"
    val JS_KEY_CALENDERS_PHOTO="eventCalendersInfo"
    val JS_KEY_SELECT_CONTACT="eventSelectContact"
    val JS_KEY_CALL_PHONE="eventCallPhone"
    val JS_KEY_APPS_FLYER="eventAppsFlyer"
    val JS_KEY_NEW_VIEW="eventNewView"
    val JS_KEY_SERVICE_TIME="eventServiceTime"
}