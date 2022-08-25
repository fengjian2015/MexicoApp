package com.fly.mexicoapp.bean

import java.io.Serializable

class ContactInfoBean : Serializable {
    var name: String = "" //姓名
    var mobile: String = "" //手机号
    var lastUpdateTime: String = "" //记录的创建时间（YYYY-MM-DD HH:MM:SS）
    var create_time: String = "" //创建时间YYYY-MM-DD HH:MM:SS）
    override fun toString(): String {
        return "ContactInfoBean(name='$name', mobile='$mobile', lastUpdateTime='$lastUpdateTime', create_time='$create_time')"
    }
}