package com.fly.cashhill.bean.response

import androidx.annotation.Keep
import com.fly.cashhill.bean.UpdateBean
import com.fly.cashhill.network.bean.BaseResponseBean
import java.io.Serializable

class UpdateBeanResponse : Serializable {
    var code: Int = 0
    var message: String = ""
    var data: UpdateBean? = null
    override fun toString(): String {
        return "UpdateBeanResponse(data=$data)"
    }
}