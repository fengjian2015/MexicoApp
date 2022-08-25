package com.fly.mexicoapp.utils

import android.database.Cursor
import android.net.Uri
import com.fly.mexicoapp.MyApplication
import com.fly.mexicoapp.bean.CalendersInfoBean


object CalendersUtil {
    fun getCalendersList():ArrayList<CalendersInfoBean>{
        var calendersInfoBeans = ArrayList<CalendersInfoBean>()
        val eventCursor: Cursor? = MyApplication.application.contentResolver.query(
            Uri.parse("content://com.android.calendar/events"), null,
            null, null, null
        )
        try {
            eventCursor?.let {
                while (it.moveToNext()) {
                    var id = it.getString(it.getColumnIndexOrThrow("calendar_id"))
                    var eventTitle = it.getString(it.getColumnIndexOrThrow("title"))
                    var description = it.getString(it.getColumnIndexOrThrow("description"))
                    var startTime = DateTool.getTimeFromLong(DateTool.FMT_DATE_TIME,eventCursor.getString(it.getColumnIndexOrThrow("dtstart")).toLong())
                    var endTime = DateTool.getTimeFromLong(DateTool.FMT_DATE_TIME,it.getString(it.getColumnIndexOrThrow("dtend")).toLong())

                    var calendersInfoBean = CalendersInfoBean()
                    calendersInfoBean.id = id
                    calendersInfoBean.title = eventTitle
                    calendersInfoBean.content = description
                    calendersInfoBean.start_time = startTime.toString()
                    calendersInfoBean.end_time = endTime.toString()
                    calendersInfoBeans.add(calendersInfoBean)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        } finally {
            eventCursor?.close()
        }
        return calendersInfoBeans
    }
}