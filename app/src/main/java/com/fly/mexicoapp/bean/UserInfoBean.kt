package com.fly.mexicoapp.bean

import java.io.Serializable

class UserInfoBean (var phone:String,var userId:String ="", var sessionId:String = ""
                    ,var isNew :Boolean = true,var homeUrl:String = ""
                    ,var appVersion:String = "",var devName:String ="android"): Serializable {
}