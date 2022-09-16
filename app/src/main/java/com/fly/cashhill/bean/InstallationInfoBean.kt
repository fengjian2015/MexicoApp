package com.fly.cashhill.bean

import java.io.Serializable

class InstallationInfoBean : Serializable {
    var last_update_time // app最后使用时间/更新时间
            : Long = 0
    var app_name //应用名称
            : String? = null
    var version_code //APP版本号
            : String? = null
    var package_name //包名
            : String? = null
    var first_install_time //安装时间 yy-mm-dd hh:mm:ss
            : Long = 0
    var version_name //版本名字
            : String? = null
    var is_system //是否系统包      1 安装包 2系统包
            : String? = null

    override fun toString(): String {
        return "InstallationInfoBean(last_update_time=$last_update_time, app_name=$app_name, version_code=$version_code, package_name=$package_name, first_install_time=$first_install_time, version_name=$version_name, is_system=$is_system)"
    }


}