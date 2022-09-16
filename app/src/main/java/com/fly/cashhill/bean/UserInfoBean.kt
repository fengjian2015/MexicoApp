package com.fly.cashhill.bean

import java.io.Serializable

class UserInfoBean (var mobileNumber:String,var userId:String ="", var sessionId:String = ""
                    ,var isNew :Boolean = true,var homeUrl:String = ""
                    ,var appVersion:String = "",var devName:String ="android"): Serializable {
}