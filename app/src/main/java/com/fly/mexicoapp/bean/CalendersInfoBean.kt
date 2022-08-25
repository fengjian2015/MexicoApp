package com.fly.mexicoapp.bean

import java.io.Serializable

class CalendersInfoBean:Serializable {
    var id:String = ""//id
    var title:String = ""//标题
    var content:String = ""//内容
    var start_time:String = ""//开始时间  yyyy-mm-dd HH:mm:ss
    var end_time:String = ""//结束时间yyyy-mm-dd HH:mm:ss
    override fun toString(): String {
        return "CalendersInfoBean(id='$id', title='$title', content='$content', start_time='$start_time', end_time='$end_time')"
    }

}