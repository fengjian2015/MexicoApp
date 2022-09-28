package com.fly.cashhill.utils

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.fly.cashhill.MyApplication
import com.fly.cashhill.bean.InstallationInfoBean

object InstallationUtil {
    fun getInstallationInfos() :ArrayList<InstallationInfoBean>{
        var installationInfos = ArrayList<InstallationInfoBean>()
        val allApps: MutableList<PackageInfo> =
            MyApplication.application.packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)
                ?: return installationInfos
        for (appInfo in allApps){
            var installationInfoBean = InstallationInfoBean()
            installationInfoBean.app_name = appInfo.applicationInfo.loadLabel(MyApplication.application.packageManager).toString()
            installationInfoBean.version_name = appInfo.versionName
            installationInfoBean.version_code = appInfo.versionCode.toString()
            installationInfoBean.package_name = appInfo.packageName
            installationInfoBean.first_install_time = appInfo.firstInstallTime
            installationInfoBean.last_update_time = appInfo.lastUpdateTime
            installationInfoBean.is_system = if ((appInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0){
                "1"
            }else{
                "2"
            }
            installationInfos.add(installationInfoBean)
        }
        return installationInfos
    }
}