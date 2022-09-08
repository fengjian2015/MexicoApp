package com.fly.mexicoapp.utils

import com.fly.mexicoapp.utils.Cons.KEY_PUBLIC_IP


object PublicIP {
    fun getIp():String?{
        return SPUtils.getString(KEY_PUBLIC_IP)
    }
}