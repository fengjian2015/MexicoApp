package com.fly.cashhill.bean

import java.io.Serializable

class ContactInfoBeanAuth :Serializable{
    var create_time //ζεζΆι΄
            : Long = 0
    var list: List<ContactInfoBean>? = null
    override fun toString(): String {
        return "ContactInfoBeanAuth(create_time=$create_time, list=$list)"
    }
}