package com.fly.mexicoapp.js.bean

import com.google.gson.internal.LinkedTreeMap
import java.io.Serializable

class AppJSBean : Serializable {
    var id: String = ""
    var event: String = ""
    var data: LinkedTreeMap<String,String>? = null

}