package com.fly.mexicoapp.bean

import java.io.Serializable

class LocationBean : Serializable {
    var geo_time:String =""//获取时间  yyyy-mm-dd HH:mm:ss
    var latitude:String =""//当前纬度
    var longtitude:String =""//当前经度
    var location:String =""//当前位置名称
    var gps_address_province:String =""//gps解析出来的省 sublocality
    var gps_address_city:String =""//gps解析出来的城市 locailty
    var gps_address_street:String =""//gps解析的地址 maddresslines
    override fun toString(): String {
        return "LocationBean(geo_time='$geo_time', latitude='$latitude', longtitude='$longtitude', location='$location', gps_address_province='$gps_address_province', gps_address_city='$gps_address_city', gps_address_street='$gps_address_street')"
    }

}