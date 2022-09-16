package com.fly.cashhill.bean

import java.io.Serializable

class PublicIpDataBean :Serializable{
    var first_ip //公网ip	189.243.251.162
            : String? = null

    override fun toString(): String {
        return "PublicIpDataBean(first_ip=$first_ip)"
    }
}