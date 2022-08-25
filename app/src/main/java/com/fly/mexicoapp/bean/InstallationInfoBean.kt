package com.fly.mexicoapp.bean

import java.io.Serializable

class InstallationInfoBean : Serializable {
    var appName: String = ""//名字
    var version: String = ""//App版本号
    var packageName: String = ""//Package名称
    var installationTime: String = ""//App安装时间, yyyy-mm-dd
    var is_system: String = ""//是否系统app  1 表示是， 0 表示否
    var lastUpdateTime: String = ""//App最后使用/更新时间  yyyy-mm-dd HH:mm:ss
    override fun toString(): String {
        return "InstallationInfoBean(appName='$appName', version='$version', packageName='$packageName', installationTime='$installationTime', is_system='$is_system', lastUpdateTime='$lastUpdateTime')"
    }

}