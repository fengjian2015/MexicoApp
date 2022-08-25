package com.fly.mexicoapp.bean.response

import com.fly.mexicoapp.network.bean.BaseResponseBean
import java.io.Serializable

class ImageResponse (var data :String = ""): BaseResponseBean() {
    override fun toString(): String {
        return "ImageResponse(data='$data')"
    }
}