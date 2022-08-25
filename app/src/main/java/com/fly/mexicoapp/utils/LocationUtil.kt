package com.fly.mexicoapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import com.blankj.utilcode.util.Utils
import com.fly.mexicoapp.MyApplication
import java.io.IOException
import java.util.*

object LocationUtil {

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
        val geocoder = Geocoder(Utils.getApp(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.size > 0) return addresses[0]
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


}