package com.fly.cashhill.utils

import android.annotation.SuppressLint
import android.database.Cursor
import android.provider.CallLog
import android.provider.ContactsContract
import com.fly.cashhill.MyApplication
import com.fly.cashhill.bean.ContactInfoBean
import com.fly.cashhill.bean.GroupEntity

object ContactUtil {
    @SuppressLint("Range")
    fun getContactInfoListName():ArrayList<ContactInfoBean>{
        val contacts: ArrayList<ContactInfoBean> = ArrayList()
        try {
            val cursor: Cursor = MyApplication.application.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)!!
            while (cursor.moveToNext()) {
                val temp = ContactInfoBean()
                temp.name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                temp.phone=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contacts.add(temp)
            }
            cursor.close()
            return contacts
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return contacts
    }

    @SuppressLint("Range")
    fun getContactInfoList():ArrayList<ContactInfoBean>{
        val contacts: ArrayList<ContactInfoBean> = ArrayList()
        try {
            val cursor: Cursor = MyApplication.application.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)!!
            while (cursor.moveToNext()) {
                val temp = ContactInfoBean()
                temp.name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                temp.contact_id= cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                temp.phone=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                temp.last_update_times = CommonUtil.stringToLong(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP)))
                temp.last_contact_time = CommonUtil.stringToLong(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED)))
                temp.source = getContactAccount(temp.contact_id)
                contacts.add(temp)
            }
            cursor.close()
            getAllGroupInfo(contacts)
//            getContentCallLog(contacts)
            return contacts
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return contacts
    }

    fun getContactGroup():ArrayList<String>{
        var contactGroup  = ArrayList<String>()
        try {
            val groupCursor: Cursor = MyApplication.application.contentResolver.query(
                ContactsContract.Groups.CONTENT_URI,
                null, null, null, null
            )!!
            while (groupCursor.moveToNext()) {
                contactGroup.add(groupCursor.getString(groupCursor.getColumnIndexOrThrow(ContactsContract.Groups.TITLE)))
            }
            groupCursor.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return contactGroup
    }

    private fun getContactAccount(id: String?): String? {
        var cursor: Cursor? = null
        var accountName: String? = null
        try {
            cursor = MyApplication.application.contentResolver.query(
                ContactsContract.RawContacts.CONTENT_URI,
                arrayOf(
                    ContactsContract.RawContacts.ACCOUNT_NAME,
                    ContactsContract.RawContacts.ACCOUNT_TYPE
                ),
                ContactsContract.RawContacts.CONTACT_ID + "=?",
                arrayOf(id),
                null
            )
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                accountName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.ACCOUNT_NAME))
                cursor.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            cursor!!.close()
        }
        return accountName
    }

    /**
     * ??????????????? ?????????????????????
     *
     * @return
     */
    fun getAllGroupInfo(contacts: ArrayList<ContactInfoBean>): List<GroupEntity>? {
        val groupList: MutableList<GroupEntity> = java.util.ArrayList<GroupEntity>()
        var cursor: Cursor? = null
        try {
            cursor =  MyApplication.application.contentResolver.query(
                ContactsContract.Groups.CONTENT_URI,
                null, null, null, null
            )
            if (cursor == null) return groupList
            while (cursor.moveToNext()) {
                val ge = GroupEntity()
                val groupId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Groups._ID)) // ???id
                val groupName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Groups.TITLE)) // ??????
                ge.groupId = (groupId)
                ge.groupName = (groupName)
                getAllGroupMembers(groupId, groupName,contacts)
                LogUtils.d("group id:$groupId>>groupName:$groupName")
                groupList.add(ge)
            }
        } finally {
            cursor?.close()
        }
        return groupList
    }

    private fun getAllGroupMembers(
        raw_group_id: Int,
        groupName: String,
        contacts: ArrayList<ContactInfoBean>
    ) {
        try {
            val groupContactCursor: Cursor =
                MyApplication.application.contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.Data.RAW_CONTACT_ID),
                    ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + " = " + raw_group_id,
                    null,
                    null
                ) ?: return
            while (groupContactCursor.moveToNext()) {
                val contactCursor: Cursor = MyApplication.application.contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(
                        ContactsContract.Data.RAW_CONTACT_ID,
                        ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID,
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
                    ),
                    ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.Data.RAW_CONTACT_ID + "=" + groupContactCursor.getInt(
                        0
                    ),
                    null,
                    null
                )!!
                contactCursor.moveToNext()
                val contactId = contactCursor.getString(0)
                for (contactInfoModel in contacts) {
                    if (contactId != null && contactId == contactInfoModel.contact_id) {
                        contactInfoModel.group=groupName
                        continue
                    }
                }
                LogUtils.d(
                    "Member name is: " + contactCursor.getString(0) + "  " + contactCursor.getString(
                        1
                    ) + " " + contactCursor.getString(2)
                )
                contactCursor.close()
            }
            groupContactCursor.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return
    }

    //??????????????????
    private fun getContentCallLog(contacts: ArrayList<ContactInfoBean>) {
        try {
            val cursor: Cursor = MyApplication.application.contentResolver.query(
                CallLog.Calls.CONTENT_URI,  // ?????????????????????URI
                null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER // ???????????????????????????????????????????????????
            ) ?: return
            while (cursor.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)) //??????
                val number =
                    cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)) //??????
                val dateLong =
                    cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)) //??????????????????
                val duration =
                    cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)) //????????????????????????????????????
                for (contactInfoModel: ContactInfoBean in contacts) {
                    if (contactInfoModel.phone.equals(number)) {
                        contactInfoModel.contact_times = (contactInfoModel.contact_times + 1)
                        contactInfoModel.last_used_times = (duration.toString() + "")
                        if (contactInfoModel.last_contact_time == 0L) contactInfoModel.last_contact_time = dateLong
                    }
                }
                LogUtils.d(
                    "Call log: " + "\n"
                            + "name: " + name + "\n"
                            + "phone number: " + number + "\n"
                            + "phone dateLong: " + dateLong + "\n"
                )
            }
            cursor.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}