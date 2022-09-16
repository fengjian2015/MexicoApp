package com.fly.cashhill.bean

import java.io.Serializable

class ContactInfoBeanAuth :Serializable{
    var create_time //抓取时间
            : Long = 0
    var list: List<ContactInfoBean>? = null
    override fun toString(): String {
        return "ContactInfoBeanAuth(create_time=$create_time, list=$list)"
    }
}