package com.fly.cashhill.bean

import java.io.Serializable

class LocationGpsBean :Serializable{
    var latitude //gps维度	23.1218131
            : String? = null
    var longitude //string	gps经度	113.3854558
            : String? = null

    override fun toString(): String {
        return "LocationGpsBean(latitude=$latitude, longitude=$longitude)"
    }
}