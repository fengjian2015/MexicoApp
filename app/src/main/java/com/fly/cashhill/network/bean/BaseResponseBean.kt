package com.fly.cashhill.network.bean

import java.io.Serializable

class BaseResponseBean : Serializable{
    var code: Int = 0
    var message: String = ""
    var data :String = ""
    override fun toString(): String {
        return "BaseResponseBean(code=$code, message='$message', data='$data')"
    }

}