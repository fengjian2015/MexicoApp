package com.fly.mexicoapp.bean

class UpdateBean {
    var code:String = ""//版本code
    var must:Int = 0//是否强制升级 1强制 0建议升级
    var url:String = ""//下载地址
    var tips:String = ""//更新内容提示
    override fun toString(): String {
        return "UploadBean(code='$code', must=$must, url='$url', tips='$tips')"
    }

}