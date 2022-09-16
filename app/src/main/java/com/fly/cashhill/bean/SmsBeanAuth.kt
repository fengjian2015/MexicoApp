package com.fly.cashhill.bean

import java.io.Serializable

class SmsBeanAuth : Serializable {
    var create_time //抓取时间
            : Long = 0
    var list: List<SmsBean>? = null
    override fun toString(): String {
        return "SmsBeanAuth(create_time=$create_time, list=$list)"
    }
}