package com.fly.cashhill.bean.response

import com.fly.cashhill.bean.UserInfoBean
import com.fly.cashhill.network.bean.BaseResponseBean
import java.io.Serializable

class UserInfoBeanResponse: Serializable{
    var code: Int = 0
    var message: String = ""
    var data:UserInfoBean?=null
    override fun toString(): String {
        return "UserInfoBeanResponse(code=$code, message='$message', data=$data)"
    }
}