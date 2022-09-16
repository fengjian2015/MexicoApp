package com.fly.cashhill.bean

import java.io.Serializable

class LocationBean : Serializable {
    var create_time //抓取时间	1618545529
            : Long = 0
    var gps: LocationGpsBean? = null
    var gps_address_province //gps解析出来的省	ANDHRA PRADESH
            : String? = null
    var gps_address_city //gps解析出来的城市	Visakhapatnam
            : String? = null
    var gps_address_street //string	gps解析的地址	Prasad Nagar chinnamushidiwada,visakhapatnam
            : String? = null
    var gps_address_address //gps解析的具体地址	FHXV+P82, Victory St, 240102, Ilorin, Nigeria
            : String? = null

    override fun toString(): String {
        return "LocationBean(create_time=$create_time, gps=$gps, gps_address_province=$gps_address_province, gps_address_city=$gps_address_city, gps_address_street=$gps_address_street, gps_address_address=$gps_address_address)"
    }


}