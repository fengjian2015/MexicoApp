package com.fly.mexicoapp.utils

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.fly.mexicoapp.BuildConfig
import com.fly.mexicoapp.MyApplication
import com.fly.mexicoapp.bean.event.HttpEvent
import com.fly.mexicoapp.network.download.DownloadCallback
import java.io.File

object UploadUtil {

    fun startDownload(downloadUrl:String,code:String,downloadCallBack: DownloadCallback){
        val file = File( CommonUtil.getDownloadDir(), getUpdateApkName(code))
        if (file != null && file.exists()) {
            val f: Boolean = installApk(MyApplication.application, file)
            if (!f) {
                LogUtils.e("DownloadService----downloadUrl:::$downloadUrl    apkPath=${file.absolutePath}")
                HttpEvent.update(downloadUrl,file,downloadCallBack)
            }else{
                downloadCallBack.onSuccess(file)
            }
            return
        }
        LogUtils.e("DownloadService----downloadUrl:::$downloadUrl    apkPath=${file.absolutePath}")
        HttpEvent.update(downloadUrl,file,downloadCallBack)
    }

    fun getUpdateApkName(versionCode: String): String {
        return "mexico-$versionCode.apk"
    }

    fun installApk(context: Context, apkFile: File): Boolean {
        if (!checkApkFile(context, apkFile.absolutePath)) return false
        val intent: Intent = getInstallIntent(context, apkFile)
        context.startActivity(intent)
        return true
    }

    private fun getInstallIntent(context: Context?, apkFile: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val auth: String = BuildConfig.APPLICATION_ID +".provider"
            uri = FileProvider.getUriForFile(context!!, auth, apkFile!!)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(apkFile)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        return intent
    }

    /**
     * 检查apk文件是否有效(是正确下载,没有损坏的)
     *
     * @param apkFilePath
     * @return
     */
    private fun checkApkFile(context: Context, apkFilePath: String): Boolean {
        var result: Boolean
        try {
            val pManager = context.packageManager
            val pInfo = pManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES)
            result = if (pInfo == null) {
                delFile(apkFilePath)
                false
            } else {
                true
            }
        } catch (e: Exception) {
            delFile(apkFilePath)
            result = false
            e.printStackTrace()
        }
        return result
    }

    private fun delFile(path: String) {
        val f = File(path)
        if (f.exists()) f.delete()
    }
}