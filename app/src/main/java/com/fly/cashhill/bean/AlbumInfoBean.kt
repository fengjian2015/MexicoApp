package com.fly.cashhill.bean

import java.io.Serializable

class AlbumInfoBean:Serializable {
    var name //名字	IMG_20190521_113722.jpg
            : String? = null
    var take_time //拍摄时间戳	1577633400160
            : Long = 0
    var author //拍摄者，无则获取手机品牌
            : String? = null
    var height //图片高度，像素
            : String? = null
    var width //图片宽度 像素
            : String? = null
    var longitude //经度
            : String? = null
    var latitude //纬度
            : String? = null
    var model //拍摄手机机型
            : String? = null
    var updateTime //修改时间 yy-MM-dd HH:mm:ss
            : String? = null

    override fun toString(): String {
        return "AlbumInfoBean(name=$name, take_time=$take_time, author=$author, height=$height, width=$width, longitude=$longitude, latitude=$latitude, model=$model, updateTime=$updateTime)"
    }


}