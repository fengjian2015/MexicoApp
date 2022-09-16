package com.fly.cashhill.bean.response

import com.fly.cashhill.bean.ProtocolUrlBean
import com.fly.cashhill.network.bean.BaseResponseBean
import java.io.Serializable

class ProtocolUrlBeanResponse :Serializable {
    var code: Int = 0
    var message: String = ""
    var data :ArrayList<ProtocolUrlBean>? =null
    override fun toString(): String {
        return "ProtocolUrlBeanResponse(code=$code, message='$message', data=$data)"
    }
}