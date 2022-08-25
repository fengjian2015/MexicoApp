package com.fly.mexicoapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.fly.mexicoapp.MyApplication

object SPUtils {

    private var mSharedPreferences : SharedPreferences? = null
    private const val FILLNAME = "mexico" // 文件名称

    @JvmStatic
    fun getInstance() : SharedPreferences {
        mSharedPreferences = mSharedPreferences ?: MyApplication.application.getSharedPreferences(FILLNAME,Context.MODE_PRIVATE)
        return mSharedPreferences!!
    }

    /**
     * SharedPreferences常用的10个操作方法
     */
    @SuppressLint("CommitPrefEdits")
    @JvmStatic
    fun putBoolean(key: String, value: Boolean) {
        getInstance().edit().putBoolean(key, value).apply()
    }

    @JvmStatic
    fun getBoolean(key: String?, defValue: Boolean) = getInstance().getBoolean(key, defValue)

    @SuppressLint("CommitPrefEdits")
    @JvmStatic
    fun putString(key: String, value: String?) {
        getInstance().edit().putString(key, value).apply()
    }

    @JvmStatic
    fun getString(key: String, defValue: String) = getInstance().getString(key, defValue) ?: ""

    @JvmStatic
    fun getString(key: String) = getInstance().getString(key, "") ?: ""

    @SuppressLint("CommitPrefEdits")
    @JvmStatic
    fun putInt(key: String, value: Int) {
        getInstance().edit().putInt(key, value).apply()
    }

    fun getLong(key: String, defValue: Long) = getInstance().getLong(key, defValue)

    @SuppressLint("CommitPrefEdits")
    @JvmStatic
    fun putLong(key: String?, value: Long) {
        getInstance().edit().putLong(key, value).apply()
    }

    @JvmStatic
    fun getInt(key: String?, defValue: Int) = getInstance().getInt(key, defValue)

    /**
     * 移除某个key值已经对应的值
     */
    @SuppressLint("CommitPrefEdits")
    @JvmStatic
    fun remove(key: String) {
        getInstance().edit().remove(key).apply()
    }

    /**
     * 清除所有内容
     */
    @SuppressLint("CommitPrefEdits")
    @JvmStatic
    fun clear() {
        getInstance().edit().clear().apply()
    }

}