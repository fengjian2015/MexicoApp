package com.fly.mexicoapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Telephony
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.fly.mexicoapp.MyApplication
import com.fly.mexicoapp.bean.SimInfoBean
import com.fly.mexicoapp.bean.SmsBean
import java.util.*

object SmsUtil {
    private var simMultiInfo: List<SimInfoBean>? = null

    fun getSmsList(): ArrayList<SmsBean> {
        var smsBeans = ArrayList<SmsBean>()
        try {
            val time = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000
            val cur = MyApplication.application.contentResolver.query(
                Uri.parse("content://sms"), arrayOf(
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.TYPE,
                    Telephony.Sms.DATE,
                    Telephony.Sms.BODY,
                    Telephony.Sms.SUBSCRIPTION_ID
                ), Telephony.Sms.DATE + " > " + time, null, "date desc"
            )
            if (cur != null) {
                while (cur.moveToNext()) {
                    var address =cur.getString(0)
                    var type = cur.getInt(1)
                    var date = cur.getLong(2)
                    var body = cur.getString(3)
                    var subscriptionId = cur.getInt(4)
                    var smsBean = SmsBean()
                    smsBean.send_mobile = if (type ==1){
                        address
                    }else{
                        UserInfoManger.getUserPhone().toString()
                    }
                    smsBean.receive_mobile = if (type == 1){
                        UserInfoManger.getUserPhone().toString()
                    }else{
                        address
                    }
                    smsBean.sms_content = body
                    smsBean.sms_type = if (type == 1){
                        "20"
                    }else{
                        "10"
                    }
                    smsBean.send_time = DateTool.getTimeFromLong(DateTool.FMT_DATE_TIME,date).toString()
                    smsBean.contactor_name = getContactName(address).toString()
                    smsBeans.add(smsBean)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return smsBeans
    }

    private fun getContactName(phoneNumber: String): String? {
        try {
            val cursor: Cursor? = MyApplication.application.contentResolver
                .query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
                    , arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    , null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val nameIdx = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    val name = cursor.getString(nameIdx)
                    cursor.close()
                    return name
                }
            }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
        return phoneNumber
    }

    @SuppressLint("MissingPermission")
    private fun getNumber(subId: Int): String? {
        if (!isSimCardReady()) {
            return "无sim卡"
        }
        val tm = MyApplication.application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var phone = tm.line1Number
        if (subId != -1) {
            if (simMultiInfo == null) {
                simMultiInfo =getSimMultiInfo()
            }
            if (simMultiInfo != null) {
                for (simInfo in simMultiInfo!!) {
                    if (subId == simInfo.mSimSlotIndex && !TextUtils.isEmpty(simInfo.mNumber)) {
                        phone = simInfo.mNumber as String?
                    }
                }
            }
        }
        return phone
    }

    /**
     * 判断sim卡是否准备好
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isSimCardReady(): Boolean {
        val tm = MyApplication.application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm != null && tm.simState == TelephonyManager.SIM_STATE_READY
    }

    /**
     * 获取多卡信息
     *
     * @return 多Sim卡的具体信息
     */
    @SuppressLint("MissingPermission")
    fun getSimMultiInfo(): List<SimInfoBean>? {
        val infos: MutableList<SimInfoBean> =
            ArrayList<SimInfoBean>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //1.版本超过5.1，调用系统方法
            val mSubscriptionManager = MyApplication.application
                .getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            var activeSubscriptionInfoList: List<SubscriptionInfo>? = null
            if (mSubscriptionManager != null) {
                try {
                    activeSubscriptionInfoList = mSubscriptionManager.activeSubscriptionInfoList
                } catch (ignored: Exception) {
                }
            }
            if (activeSubscriptionInfoList != null && activeSubscriptionInfoList.isNotEmpty()) {
                //1.1.1 有使用的卡，就遍历所有卡
                for (subscriptionInfo in activeSubscriptionInfoList) {
                    val simInfo= SimInfoBean()
                    simInfo.mCarrierName = subscriptionInfo.carrierName
                    simInfo.mIccId = subscriptionInfo.iccId
                    simInfo.mSimSlotIndex = subscriptionInfo.simSlotIndex
                    simInfo.mNumber = subscriptionInfo.number
                    simInfo.mCountryIso = subscriptionInfo.countryIso
                    simInfo.mSubscriptionId = subscriptionInfo.subscriptionId
                    infos.add(simInfo)
                }
            }
        }
        //3.通过反射读取卡槽信息，最后通过IMEI去重
        for (i in 0 until getSimCount()) {
            getReflexSimInfo(i)?.let { infos.add(it) }
        }
        return infos
    }

    /**
     * 获取IMEI码
     *
     * 需添加权限 `<uses-permission android:name="android.permission.READ_PHONE_STATE"/>`
     *
     * @return IMEI码
     */
    @SuppressLint("HardwareIds")
    fun getIMEI(): String? {
        val tm = MyApplication.application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            return tm?.deviceId
        } catch (ignored: Exception) {
        }
        return getUniquePsuedoID()
    }

    /**
     * 通过读取设备的ROM版本号、厂商名、CPU型号和其他硬件信息来组合出一串15位的号码
     * 其中“Build.SERIAL”这个属性来保证ID的独一无二，当API < 9 无法读取时，使用AndroidId
     *
     * @return 伪唯一ID
     */
    fun getUniquePsuedoID(): String? {
        val m_szDevIDShort =
            "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10
        var serial: String? = null
        try {
            serial = Build::class.java.getField("SERIAL")[null].toString()
            return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        } catch (e: Exception) {
            //获取失败，使用AndroidId
            if (TextUtils.isEmpty(serial)) {
                serial = "serial"
            }
        }
        return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }

    /**
     * 通过反射获得SimInfo的信息
     * 当index为0时，读取默认信息
     *
     * @param index 位置,用来当subId和phoneId
     * @return [SimInfoBean] sim信息
     */
    private fun getReflexSimInfo(index: Int): SimInfoBean? {
        val simInfo = SimInfoBean()
        simInfo.mSimSlotIndex = index
        try {
            simInfo.mImei =
                getReflexMethodWithId("getDeviceId", simInfo.mSimSlotIndex.toString())
            //slotId,比较准确
            simInfo.mImsi = getReflexMethodWithId(
                "getSubscriberId",
                simInfo.mSimSlotIndex.toString()
            )
            //subId,很不准确
            simInfo.mCarrierName = getReflexMethodWithId(
                "getSimOperatorNameForPhone",
                simInfo.mSimSlotIndex.toString()
            )
            //PhoneId，基本准确
            simInfo.mCountryIso = getReflexMethodWithId(
                "getSimCountryIso",
                simInfo.mSimSlotIndex.toString()
            )
            //subId，很不准确
            simInfo.mIccId = getReflexMethodWithId(
                "getSimSerialNumber",
                simInfo.mSimSlotIndex.toString()
            )
            //subId，很不准确
            simInfo.mNumber =
                getReflexMethodWithId("getLine1Number", simInfo.mSimSlotIndex.toString())
            //subId，很不准确
        } catch (ignored: MethodNotFoundException) {
        }
        return simInfo
    }

    /**
     * 通过反射调取@hide的方法
     *
     * @param predictedMethodName 方法名
     * @param id 参数
     * @return 返回方法调用的结果
     * @throws MethodNotFoundException 方法没有找到
     */
    @Throws(MethodNotFoundException::class)
    private fun getReflexMethodWithId(predictedMethodName: String, id: String): String? {
        var result: String? = null
        val telephony = MyApplication.application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val telephonyClass = Class.forName(telephony.javaClass.name)
            val parameter = arrayOfNulls<Class<*>?>(1)
            parameter[0] = Int::class.javaPrimitiveType
            val getSimID = telephonyClass.getMethod(predictedMethodName, *parameter)
            val parameterTypes = getSimID.parameterTypes
            val obParameter = arrayOfNulls<Any>(parameterTypes.size)
            if (parameterTypes[0].simpleName == "int") {
                obParameter[0] = Integer.valueOf(id)
            } else if (parameterTypes[0].simpleName == "long") {
                obParameter[0] = java.lang.Long.valueOf(id)
            } else {
                obParameter[0] = id
            }
            val ob_phone = getSimID.invoke(telephony, *obParameter)
            if (ob_phone != null) {
                result = ob_phone.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw MethodNotFoundException(predictedMethodName)
        }
        return result
    }

    /**
     * 获得卡槽数，默认为1
     *
     * @return 返回卡槽数
     */
    fun getSimCount(): Int {
        var count = 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                val mSubscriptionManager = MyApplication.application
                    .getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                if (mSubscriptionManager != null) {
                    count = mSubscriptionManager.activeSubscriptionInfoCountMax
                    return count
                }
            } catch (ignored: Exception) {
            }
        }
        try {
            count = getReflexMethod("getPhoneCount")?.toInt()!!
        } catch (ignored: MethodNotFoundException) {
        }
        return count
    }

    /**
     * 通过反射调取@hide的方法
     *
     * @param predictedMethodName 方法名
     * @return 返回方法调用的结果
     * @throws MethodNotFoundException 方法没有找到
     */
    @Throws(MethodNotFoundException::class)
    private fun getReflexMethod(predictedMethodName: String): String? {
        var result: String? = null
        val telephony = MyApplication.application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val telephonyClass = Class.forName(telephony.javaClass.name)
            val getSimID = telephonyClass.getMethod(predictedMethodName)
            val ob_phone = getSimID.invoke(telephony)
            if (ob_phone != null) {
                result = ob_phone.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw MethodNotFoundException(predictedMethodName)
        }
        return result
    }

    /**
     * 反射未找到方法
     */
    private class MethodNotFoundException internal constructor(info: String?) : Exception(info) {
        companion object {
            const val serialVersionUID = -3241033488141442594L
        }
    }
}