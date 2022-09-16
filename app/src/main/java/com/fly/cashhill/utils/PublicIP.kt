package com.fly.cashhill.utils

import com.fly.cashhill.utils.Cons.KEY_PUBLIC_IP


object PublicIP {
    fun getIp():String?{
        return SPUtils.getString(KEY_PUBLIC_IP)
    }
}