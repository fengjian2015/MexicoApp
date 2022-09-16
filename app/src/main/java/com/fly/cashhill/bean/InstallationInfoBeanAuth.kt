package com.fly.cashhill.bean

import java.io.Serializable

class InstallationInfoBeanAuth:Serializable {
    var create_time // app最后使用时间/更新时间
            : Long = 0
    var list: List<InstallationInfoBean>? = null
    override fun toString(): String {
        return "InstallationInfoBeanAuth(create_time=$create_time, list=$list)"
    }
}