package com.fly.mexicoapp.utils

import android.annotation.SuppressLint
import android.database.Cursor
import android.provider.ContactsContract
import com.fly.mexicoapp.MyApplication
import com.fly.mexicoapp.bean.ContactInfoBean

object ContactUtil {
    @SuppressLint("Range")
    fun getContactInfoList():ArrayList<ContactInfoBean>{
        val contacts: ArrayList<ContactInfoBean> = ArrayList()
        try {
            val cursor: Cursor = MyApplication.application.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)!!
            while (cursor.moveToNext()) {
                val temp = ContactInfoBean()
                val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                temp.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val time = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP))
                val toString = DateTool.getTimeFromLong(DateTool.FMT_DATE_TIME, time.toLong()).toString()
                temp.lastUpdateTime = toString
                temp.create_time = toString
                val phoneCursor: Cursor? = MyApplication.application.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null)
                phoneCursor?.let {
                    while (phoneCursor.moveToNext()) {
                        var phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        temp.mobile = phone
                    }
                }
                contacts.add(temp)
                phoneCursor?.close()
            }
            cursor.close()
            return contacts
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return contacts
    }

    fun getContactGroupList():ArrayList<String>{
        var contactGroupList = ArrayList<String>()
        try {
            //通过使用此dataCursor,您将获得联系人数据库中所有联系人的contact_id和group_id.
            val groupCursor: Cursor = MyApplication.application.contentResolver.query(
                ContactsContract.Groups.CONTENT_URI, arrayOf(
                    ContactsContract.Groups._ID,
                    ContactsContract.Groups.TITLE
                ), null, null, null
            )!!
            while (groupCursor.moveToNext()) {
                contactGroupList.add(groupCursor.getString(1))
            }
            groupCursor.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return contactGroupList
    }
}