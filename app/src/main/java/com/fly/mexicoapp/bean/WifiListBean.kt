package com.fly.mexicoapp.bean

import java.io.Serializable

class WifiListBean : Serializable {
    var capabilities: String = "" //类型
    var level: Int = 0        //信号强度
    var bssid: String = ""   //wifi mac
    var ssid: String = ""     //ssid
    var frequency: Int = 0   //频率
    var timestamp: String = "" //时间  YYYY-MM-DD HH:MM:SS
//    override fun toString(): String {
//        return "WifiListBean(capabilities='$capabilities', level=$level, bssid='$bssid', ssid='$ssid', frequency=$frequency, timestamp='$timestamp')"
//    }

}