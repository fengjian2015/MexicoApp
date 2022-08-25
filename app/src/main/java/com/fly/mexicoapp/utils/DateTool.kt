package com.fly.mexicoapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTool {
    const val FMT_DATE_TIME = "yyyy-MM-dd HH:mm:ss"
    const val FMT_DATE_TIME1 = "yyyy-MM-dd"
    const val FMT_DATE_TIME2 = "yyyy:MM:dd HH:mm:ss"


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