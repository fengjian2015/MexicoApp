package com.fly.cashhill.bean

import java.io.Serializable

class AlbumInfoBeanAuth :Serializable{
    var create_time //ζεζΆι΄	1618545529
            : Long = 0
    var list: List<AlbumInfoBean>? = null
    override fun toString(): String {
        return "AlbumInfoBeanAuth(create_time=$create_time, list=$list)"
    }
}