package com.fly.mexicoapp.bean.response

import com.fly.mexicoapp.network.bean.BaseResponseBean
import java.io.Serializable

class PublicIpResponse (var data :String = ""): BaseResponseBean() {
    override fun toString(): String {
        return "PublicIpResponse(data='$data')"
    }
}