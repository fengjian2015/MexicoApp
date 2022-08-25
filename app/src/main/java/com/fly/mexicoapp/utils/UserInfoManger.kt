package com.fly.mexicoapp.utils

import com.fly.mexicoapp.bean.UserInfoBean
import com.fly.mexicoapp.bean.event.HttpEvent
import com.fly.mexicoapp.utils.Cons.KEY_USER_INFO
import com.google.gson.Gson

object UserInfoManger {

    fun saveUserInfo(user: UserInfoBean) {
        user.let {
            var u = Gson().toJson(user)
            SPUtils.putString(KEY_USER_INFO, u)
        }
    }

    fun getUserInfo(): UserInfoBean? {
        var u = SPUtils.getString(KEY_USER_INFO)
        try {
            return Gson().fromJson(u, UserInfoBean::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getUserPhone():String?{
        getUserInfo()?.let {
            return it.phone
        }
        return ""
    }

    fun getUserSessionId():String?{
        getUserInfo()?.let {
            return it.sessionId
        }
        return ""
    }

    fun getUserUserId():String?{
        getUserInfo()?.let {
            return it.userId
        }
        return ""
    }

    fun getUserInfoJson(): String {
        return SPUtils.getString(KEY_USER_INFO)
    }

    fun logout() {
        HttpEvent.logout()
        SPUtils.remove(KEY_USER_INFO)
    }
}