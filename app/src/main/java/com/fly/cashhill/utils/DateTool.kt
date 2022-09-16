package com.fly.cashhill.utils

import android.os.SystemClock
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

object DateTool {
    const val FMT_DATE_TIME = "yyyy-MM-dd HH:mm:ss"
    const val FMT_DATE_TIME1 = "yyyy-MM-dd"
    const val FMT_DATE_TIME2 = "yyyy:MM:dd HH:mm:ss"

    /**
     * 获取服务器时间戳
     */
    fun getServerTimestamp(): Long {
        val aLong: Long = SPUtils.getLong(Cons.KEY_SERVICE_TIME,0)
        val elapsedRealtime: Long = SPUtils.getLong(Cons.KEY_DIFFERENCE_TIME,0)
        return if (aLong < 10) {
            System.currentTimeMillis()
        } else {
            SystemClock.elapsedRealtime() - elapsedRealtime + aLong
        }
    }

    fun initTime(s: String) {
        if (TextUtils.isEmpty(s)) return
        var serviceTimestamp: Long = 0
        try {
            serviceTimestamp = s.toLong()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (serviceTimestamp == 0L) {
            return
        }
        val elapsedRealtime = SystemClock.elapsedRealtime()
        SPUtils.putLong(Cons.KEY_SERVICE_TIME, serviceTimestamp)
        SPUtils.putLong(Cons.KEY_DIFFERENCE_TIME, elapsedRealtime)
    }

    fun getTimeFromLong(format: String?, time: Long): String? {
        try {
            return SimpleDateFormat(format).format(time)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 把日期字符串格式化成日期类型
     *
     * @param dateStr
     * @param format
     * @return
     */
    fun convert2Date(dateStr: String?, format: String?): Date? {
        val simple = SimpleDateFormat(format)
        return try {
            simple.isLenient = false
            simple.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 把日期类型格式化成字符串
     *
     * @param date
     * @param format
     * @return
     */
    fun convert2String(date: Date?, format: String?): String? {
        val formater = SimpleDateFormat(format)
        return try {
            formater.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}