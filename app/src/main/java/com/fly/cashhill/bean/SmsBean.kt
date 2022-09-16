package com.fly.cashhill.bean

import java.io.Serializable

class SmsBean : Serializable {
    var phone //对方名称
            : String? = null
    var content //短信内容
            : String? = null
    var time //发送时间
            : Long = 0
    var type //1:'Receive',2:'Send',3:'ReceiveDraft',5:'SendDraft'
            = 0
    var address //临时记录查询到的电话号码
            : String? = null
    var contactor_name //对方名称  senderid或者备注或者接收号码
            : String? = null


    override fun toString(): String {
        return "SmsBean(phone=$phone, content=$content, time=$time, type=$type, address=$address, contactor_name=$contactor_name)"
    }
}