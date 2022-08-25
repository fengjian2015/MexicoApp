package com.fly.mexicoapp.utils

import android.content.pm.ApplicationInfo
import com.fly.mexicoapp.MyApplication
import com.fly.mexicoapp.bean.InstallationInfoBean

object InstallationUtil {
    fun getInstallationInfos() :ArrayList<InstallationInfoBean>{
        var installationInfos = ArrayList<InstallationInfoBean>()
        val allApps: List<ApplicationInfo> =
            MyApplication.application.packageManager.getInstalledApplications(0)
        for (app in allApps){
            var installationInfoBean = InstallationInfoBean()
            var appInfo = MyApplication.application.packageManager.getPackageInfo(app.packageName,0)

            installationInfoBean.appName = appInfo.applicationInfo.loadLabel(MyApplication.application.packageManager).toString()
            installationInfoBean.version = appInfo.versionName
            installationInfoBean.packageName = appInfo.packageName
            installationInfoBean.installationTime =
                DateTool.getTimeFromLong(DateTool.FMT_DATE_TIME1,appInfo.firstInstallTime).toString()
            installationInfoBean.lastUpdateTime =
                DateTool.getTimeFromLong(DateTool.FMT_DATE_TIME1,appInfo.lastUpdateTime).toString()
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