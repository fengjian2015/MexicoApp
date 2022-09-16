package com.fly.cashhill.bean

import java.io.Serializable

class GeneralDataBean :Serializable{
    var phone_type //指示设备电话类型的常量 NONE：0，GS：1，CDMA：2，SIP=3"language
            : String? = null
    var language //系统语言	en
            : String? = null
    var locale_display_language //English
            : String? = null
    var network_operator_name //网络运营商名称	TELCEL
            : String? = null
    var locale_iso_3_country //此地区的国家地区缩写	USA
            : String? = null
    var locale_iso_3_language //语言环境的字母缩写	eng
            : String? = null

    override fun toString(): String {
        return "GeneralDataBean(phone_type=$phone_type, language=$language, locale_display_language=$locale_display_language, network_operator_name=$network_operator_name, locale_iso_3_country=$locale_iso_3_country, locale_iso_3_language=$locale_iso_3_language)"
    }
}