package com.fly.cashhill.utils

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fly.cashhill.MyApplication
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

object DeviceUtils {
    private fun DeviceUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * Return whether device is rooted.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDeviceRooted(): Boolean {
        val su = "su"
        val locations = arrayOf(
            "/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
            "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
            "/system/sbin/", "/usr/bin/", "/vendor/bin/"
        )
        for (location in locations) {
            if (File(location + su).exists()) {
                return true
            }
        }
        return false
    }

    /**
     * Return whether ADB is enabled.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun isAdbEnabled(): Boolean {
        return Settings.Secure.getInt(
            MyApplication.application.contentResolver,
            Settings.Global.ADB_ENABLED, 0
        ) > 0
    }

    /**
     * Return the version name of device's system.
     *
     * @return the version name of device's system
     */
    fun getSDKVersionName(): String? {
        return Build.VERSION.RELEASE
    }

    /**
     * Return version code of device's system.
     *
     * @return version code of device's system
     */
    fun getSDKVersionCode(): Int {
        return Build.VERSION.SDK_INT
    }

    /**
     * Return the android id of device.
     *
     * @return the android id of device
     */
    @SuppressLint("HardwareIds")
    fun getAndroidID(): String {
        val id = Settings.Secure.getString(
            MyApplication.application.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return if ("9774d56d682e549c" == id) "" else id ?: ""
    }

    /**
     * Return the MAC address.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`,
     * `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE])
    fun getMacAddress(): String {
        val macAddress = getMacAddress(null)
        if (!TextUtils.isEmpty(macAddress) || getWifiEnabled()) return macAddress
        setWifiEnabled(true)
        setWifiEnabled(false)
        return getMacAddress(null)
    }

    private fun getWifiEnabled(): Boolean {
        @SuppressLint("WifiManagerLeak") val manager =
            MyApplication.application.getSystemService(Context.WIFI_SERVICE) as WifiManager
                ?: return false
        return manager.isWifiEnabled
    }

    /**
     * Enable or disable wifi.
     *
     * Must hold `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @param enabled True to enabled, false otherwise.
     */
    @RequiresPermission(permission.CHANGE_WIFI_STATE)
    private fun setWifiEnabled(enabled: Boolean) {
        @SuppressLint("WifiManagerLeak") val manager =
            MyApplication.application.getSystemService(Context.WIFI_SERVICE) as WifiManager
                ?: return
        if (enabled == manager.isWifiEnabled) return
        manager.isWifiEnabled = enabled
    }

    /**
     * Return the MAC address.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE])
    fun getMacAddress(vararg excepts: String?): String {
        var macAddress = getMacAddressByNetworkInterface()
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = getMacAddressByInetAddress()
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = getMacAddressByWifiInfo()
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = getMacAddressByFile()
        return if (isAddressNotInExcepts(macAddress, *excepts)) {
            macAddress
        } else ""
    }

    private fun isAddressNotInExcepts(address: String, vararg excepts: String?): Boolean {
        if (TextUtils.isEmpty(address)) {
            return false
        }
        if ("02:00:00:00:00:00" == address) {
            return false
        }
        if (excepts == null || excepts.isEmpty()) {
            return true
        }
        for (filter in excepts) {
            if (filter != null && filter == address) {
                return false
            }
        }
        return true
    }

    @RequiresPermission(permission.ACCESS_WIFI_STATE)
    private fun getMacAddressByWifiInfo(): String {
        try {
            val wifi = MyApplication.application
                .applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifi != null) {
                val info = wifi.connectionInfo
                if (info != null) {
                    @SuppressLint("HardwareIds", "MissingPermission") val macAddress =
                        info.macAddress
                    if (!TextUtils.isEmpty(macAddress)) {
                        return macAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "02:00:00:00:00:00"
    }

    private fun getMacAddressByNetworkInterface(): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                if (ni == null || !ni.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = ni.hardwareAddress
                if (macBytes != null && macBytes.isNotEmpty()) {
                    val sb = StringBuilder()
                    for (b in macBytes) {
                        sb.append(String.format("%02x:", b))
                    }
                    return sb.substring(0, sb.length - 1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "02:00:00:00:00:00"
    }

    private fun getMacAddressByInetAddress(): String {
        try {
            val inetAddress = getInetAddress()
            if (inetAddress != null) {
                val ni = NetworkInterface.getByInetAddress(inetAddress)
                if (ni != null) {
                    val macBytes = ni.hardwareAddress
                    if (macBytes != null && macBytes.isNotEmpty()) {
                        val sb = StringBuilder()
                        for (b in macBytes) {
                            sb.append(String.format("%02x:", b))
                        }
                        return sb.substring(0, sb.length - 1)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "02:00:00:00:00:00"
    }

    private fun getInetAddress(): InetAddress? {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp) continue
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    val inetAddress = addresses.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        val hostAddress = inetAddress.hostAddress
                        if (hostAddress.indexOf(':') < 0) return inetAddress
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

    private fun execCmd(command: String, isRooted: Boolean): ShellUtils.CommandResult? {
        return ShellUtils.execCmd(command, isRooted)
    }

    private fun getMacAddressByFile(): String {
        var result = execCmd("getprop wifi.interface", false)
        if (result!!.result === 0) {
            val name = result!!.successMsg
            if (name != null) {
                result = execCmd("cat /sys/class/net/$name/address", false)
                if (result!!.result === 0) {
                    val address = result!!.successMsg
                    if (address != null && address.isNotEmpty()) {
                        return address
                    }
                }
            }
        }
        return "02:00:00:00:00:00"
    }

    /**
     * Return the manufacturer of the product/hardware.
     *
     * e.g. Xiaomi
     *
     * @return the manufacturer of the product/hardware
     */
    fun getManufacturer(): String? {
        return Build.MANUFACTURER
    }

    /**
     * Return the model of device.
     *
     * e.g. MI2SC
     *
     * @return the model of device
     */
    fun getModel(): String? {
        var model = Build.MODEL
        model = model?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
        return model
    }

    /**
     * Return an ordered list of ABIs supported by this device. The most preferred ABI is the first
     * element in the list.
     *
     * @return an ordered list of ABIs supported by this device
     */
    fun getABIs(): Array<String?>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                arrayOf(Build.CPU_ABI, Build.CPU_ABI2)
            } else arrayOf(Build.CPU_ABI)
        }
    }

    /**
     * Return whether device is tablet.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isTablet(): Boolean {
        return ((Resources.getSystem().configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    /**
     * Return whether device is emulator.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isEmulator(): Boolean {
        val checkProperty = (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.lowercase(Locale.getDefault()).contains("vbox")
                || Build.FINGERPRINT.lowercase(Locale.getDefault()).contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)
        if (checkProperty) return true
        var operatorName = ""
        val tm = MyApplication.application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (tm != null) {
            val name = tm.networkOperatorName
            if (name != null) {
                operatorName = name
            }
        }
        val checkOperatorName = operatorName.lowercase(Locale.getDefault()) == "android"
        if (checkOperatorName) return true
        val url = "tel:" + "123456"
        val intent = Intent()
        intent.data = Uri.parse(url)
        intent.action = Intent.ACTION_DIAL
        val checkDial = intent.resolveActivity(MyApplication.application.packageManager) == null
        return if (checkDial) true else false

//        boolean checkDebuggerConnected = Debug.isDebuggerConnected();
//        if (checkDebuggerConnected) return true;
    }

    /**
     * Whether user has enabled development settings.
     *
     * @return whether user has enabled development settings.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun isDevelopmentSettingsEnabled(): Boolean {
        return Settings.Global.getInt(
            MyApplication.application.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) > 0
    }


    private const val KEY_UDID = "KEY_UDID"

    @Volatile
    private var udid: String? = null

    /**
     * Return the unique device id.
     * <pre>{1}{UUID(macAddress)}</pre>
     * <pre>{2}{UUID(androidId )}</pre>
     * <pre>{9}{UUID(random    )}</pre>
     *
     * @return the unique device id
     */
    fun getUniqueDeviceId(): String? {
        return getUniqueDeviceId("", true)
    }

    /**
     * Return the unique device id.
     * <pre>android 10 deprecated {prefix}{1}{UUID(macAddress)}</pre>
     * <pre>{prefix}{2}{UUID(androidId )}</pre>
     * <pre>{prefix}{9}{UUID(random    )}</pre>
     *
     * @param prefix The prefix of the unique device id.
     * @return the unique device id
     */
    fun getUniqueDeviceId(prefix: String): String? {
        return getUniqueDeviceId(prefix, true)
    }

    /**
     * Return the unique device id.
     * <pre>{1}{UUID(macAddress)}</pre>
     * <pre>{2}{UUID(androidId )}</pre>
     * <pre>{9}{UUID(random    )}</pre>
     *
     * @param useCache True to use cache, false otherwise.
     * @return the unique device id
     */
    fun getUniqueDeviceId(useCache: Boolean): String? {
        return getUniqueDeviceId("", useCache)
    }

    /**
     * Return the unique device id.
     * <pre>android 10 deprecated {prefix}{1}{UUID(macAddress)}</pre>
     * <pre>{prefix}{2}{UUID(androidId )}</pre>
     * <pre>{prefix}{9}{UUID(random    )}</pre>
     *
     * @param prefix   The prefix of the unique device id.
     * @param useCache True to use cache, false otherwise.
     * @return the unique device id
     */
    fun getUniqueDeviceId(prefix: String, useCache: Boolean): String? {
        if (!useCache) {
            return getUniqueDeviceIdReal(prefix)
        }
        if (udid == null) {
            synchronized(DeviceUtils::class.java) {
                if (udid == null) {
                    val id: String = SPUtils.getString(KEY_UDID, null)
                    if (id != null) {
                        udid = id
                        return udid
                    }
                    return getUniqueDeviceIdReal(prefix)
                }
            }
        }
        return udid
    }

    private fun getUniqueDeviceIdReal(prefix: String): String? {
        try {
            val androidId = getAndroidID()
            if (!TextUtils.isEmpty(androidId)) {
                return saveUdid(prefix + 2, androidId)
            }
        } catch (ignore: Exception) { /**/
        }
        return saveUdid(prefix + 9, "")
    }

    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.INTERNET, permission.CHANGE_WIFI_STATE])
    fun isSameDevice(uniqueDeviceId: String): Boolean {
        // {prefix}{type}{32id}
        if (TextUtils.isEmpty(uniqueDeviceId) && uniqueDeviceId.length < 33) return false
        if (uniqueDeviceId == udid) return true
        val cachedId: String = SPUtils.getString(KEY_UDID, null)
        if (uniqueDeviceId == cachedId) return true
        val st = uniqueDeviceId.length - 33
        val type = uniqueDeviceId.substring(st, st + 1)
        if (type.startsWith("1")) {
            val macAddress = getMacAddress()
            return if (macAddress == "") {
                false
            } else uniqueDeviceId.substring(st + 1) == getUdid("", macAddress)
        } else if (type.startsWith("2")) {
            val androidId = getAndroidID()
            return if (TextUtils.isEmpty(androidId)) {
                false
            } else uniqueDeviceId.substring(st + 1) == getUdid("", androidId)
        }
        return false
    }

    private fun saveUdid(prefix: String, id: String): String? {
        udid = getUdid(prefix, id)
        SPUtils.putString(KEY_UDID, udid)
        return udid
    }

    private fun getUdid(prefix: String, id: String): String {
        return if (id == "") {
            prefix + UUID.randomUUID().toString().replace("-", "")
        } else prefix + UUID.nameUUIDFromBytes(id.toByteArray()).toString().replace("-", "")
    }
}