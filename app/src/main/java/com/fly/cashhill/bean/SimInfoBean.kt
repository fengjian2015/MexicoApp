package com.fly.cashhill.bean

import android.text.TextUtils
import com.fly.cashhill.utils.SmsUtil
import java.io.Serializable

class SimInfoBean :Serializable {
    /** 运营商信息：中国移动 中国联通 中国电信  */
    var mCarrierName: CharSequence? = null

    /** 卡槽ID，SimSerialNumber  */
    var mIccId: CharSequence? = null

    /** 卡槽id， -1 - 没插入、 0 - 卡槽1 、1 - 卡槽2  */
    var mSimSlotIndex = 0

    /** 号码  */
    var mNumber: CharSequence? = null

    /** 城市  */
    var mCountryIso: CharSequence? = null

    /** 设备唯一识别码  */
    var mImei: String? = SmsUtil.getIMEI()

    /** SIM的编号  */
    var mImsi: CharSequence? = null

    /** sub id  */
    var mSubscriptionId = 0

    /**
     * 通过 IMEI 判断是否相等
     *
     * @param obj
     * @return
     */
    override fun equals(obj: Any?): Boolean {
        return obj != null && obj is SimInfoBean && (TextUtils.isEmpty((obj as SimInfoBean).mImei) || (obj as SimInfoBean).mImei == mImei)
    }

    override fun toString(): String {
        return "SimInfoBean(mCarrierName=$mCarrierName, mIccId=$mIccId, mSimSlotIndex=$mSimSlotIndex, mNumber=$mNumber, mCountryIso=$mCountryIso, mImei=$mImei, mImsi=$mImsi, mSubscriptionId=$mSubscriptionId)"
    }
}