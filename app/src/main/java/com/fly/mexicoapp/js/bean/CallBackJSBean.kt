package com.fly.mexicoapp.js.bean

import java.io.Serializable

class CallBackJSBean(var id: String,var event: String,var data: String?=null,var code: String = "200",var errorMsg: String? = null) :
    Serializable {

}