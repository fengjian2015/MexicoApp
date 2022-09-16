package com.fly.cashhill.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import com.fly.cashhill.MyApplication
import java.io.IOException
import java.util.*

object LocationUtil {

    private var locationManager: LocationManager? = null


    fun getLocation(): Location? {
        var location: Location? = null
        val locationManager =
            MyApplication.application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.let {
            val providers = locationManager.getProviders(true)
            for (provider in providers) {
                @SuppressLint("MissingPermission")
                val l = locationManager.getLastKnownLocation(provider!!) ?: continue
                if (location == null || l.accuracy < location!!.accuracy) {
                    location = l
                }
            }
        }
        return location
    }

    /**
     * 根据经纬度获取所在地
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 所在地
     */
    fun getLocality(latitude: Double, longitude: Double): String? {
        val address: Address? = getAddress(latitude, longitude)
        return if (address == null) "unknown" else address.locality
    }

    /**
     * 根据经纬度获取地理位置
     * @param latitude  纬度
     * @param longitude 经度
     * @return [Address]
     */
    fun getAddress(latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(MyApplication.application, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.size > 0) return addresses[0]
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    private val mLocationListener: LocationListener = object : LocationListener {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            LogUtils.d("onStatusChanged")
        }

        // Provider被enable时触发此函数，比如GPS被打开
        override fun onProviderEnabled(provider: String) {
            LogUtils.d("onProviderEnabled")
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        override fun onProviderDisabled(provider: String) {
            LogUtils.d("onProviderDisabled")
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        override fun onLocationChanged(location: Location) {
            LogUtils.d(
                String.format(
                    "location: longitude: %f, latitude: %f", location.longitude,
                    location.latitude
                )
            )
            //更新位置信息
            locationManager?.removeUpdates(this)
        }
    }


    /**
     * 监听位置变化
     */
    @SuppressLint("MissingPermission")
    fun initLocationListener() {
        locationManager = MyApplication.application.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (locationManager == null) {
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            1000,
            10f,
            mLocationListener
        )
    }

}