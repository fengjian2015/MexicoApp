package com.fly.cashhill.bean

import java.io.Serializable

class AlbumInfoBeanAuth :Serializable{
    var create_time //抓取时间	1618545529
            : Long = 0
    var list: List<AlbumInfoBean>? = null
}