package com.fly.cashhill.js.bean

import java.io.Serializable

class CallBackJSBean(var id: String,var event: String,var data: String?=null,var code: String = "0",var errorMsg: String? = null) :
    Serializable {

}