package com.fly.cashhill.utils

import android.content.pm.ApplicationInfo
import com.fly.cashhill.MyApplication
import com.fly.cashhill.bean.InstallationInfoBean

object InstallationUtil {
    fun getInstallationInfos() :ArrayList<InstallationInfoBean>{
        var installationInfos = ArrayList<InstallationInfoBean>()
        val allApps: List<ApplicationInfo> =
            MyApplication.application.packageManager.getInstalledApplications(0)
        for (app in allApps){
            var installationInfoBean = InstallationInfoBean()
            var appInfo = MyApplication.application.packageManager.getPackageInfo(app.packageName,0)

            installationInfoBean.app_name = appInfo.applicationInfo.loadLabel(MyApplication.application.packageManager).toString()
            installationInfoBean.version_name = appInfo.versionName
            installationInfoBean.version_code = appInfo.versionCode.toString()
            installationInfoBean.package_name = appInfo.packageName
            installationInfoBean.first_install_time = appInfo.firstInstallTime
            installationInfoBean.last_update_time = appInfo.lastUpdateTime
            installationInfoBean.is_system = if ((appInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0){
                "0"
            }else{
                "1"
            }
            installationInfos.add(installationInfoBean)
        }
        return installationInfos
    }
}