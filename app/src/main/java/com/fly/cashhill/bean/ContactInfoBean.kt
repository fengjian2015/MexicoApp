package com.fly.cashhill.bean

import java.io.Serializable

class ContactInfoBean : Serializable {
    var group //手机号分组	1
            : String? = null
    var name //名字	Tony
            : String? = null
    var source //通讯录来源	device
            : String? = null
    var last_used_times //最后一次使用次数	0
            : String? = "0"
    var phone //电话	8602583474
            : String? = null
    var last_update_times //上次更新时间	1603176737569
            : Long = 0
    var contact_times //联系次数
            = 0
    var last_contact_time //上次联系时间	1603176737569
            : Long = 0
    var contact_id //临时id
            : String? = null

    override fun toString(): String {
        return "ContactInfoBean(group=$group, name=$name, source=$source, last_used_times=$last_used_times, phone=$phone, last_update_times=$last_update_times, contact_times=$contact_times, last_contact_time=$last_contact_time, contact_id=$contact_id)"
    }

}