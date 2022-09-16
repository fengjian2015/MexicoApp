package com.fly.cashhill.bean

import java.io.Serializable

class ContactSelectInfoBean : Serializable {

    var name //名字	Tony
            : String? = null
    var mobile //电话	8602583474
            : String? = null

    override fun toString(): String {
        return "ContactSelectInfoBean(name=$name, mobile=$mobile)"
    }


}