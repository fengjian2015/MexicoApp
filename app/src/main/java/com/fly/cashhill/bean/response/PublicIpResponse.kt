package com.fly.cashhill.bean.response

import com.fly.cashhill.network.bean.BaseResponseBean
import java.io.Serializable

class PublicIpResponse : Serializable {
    var code: Int = 0
    var message: String = ""
    var data :String = ""
    override fun toString(): String {
        return "ImageResponse(code=$code, message='$message', data='$data')"
    }
}