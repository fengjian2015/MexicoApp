package com.fly.mexicoapp.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.fly.mexicoapp.MyApplication
import com.fly.mexicoapp.utils.Cons.TACK_PHOTO
import java.io.File

object CommonUtil {


    fun getVersionName(context: Context): String? {
        val pm = context.packageManager
        return try {
            val info = pm.getPackageInfo(context.packageName, 0)
            var versionName = info.versionName
            if (!TextUtils.isEmpty(versionName) && versionName.contains("-")) {
                val index = versionName.indexOf("-")
                if (index > 0) {
                    versionName = versionName.substring(0, index)
                    return versionName
                }
            }
            info.versionName
        } catch (var5: NameNotFoundException) {
            var5.printStackTrace()
            "1"
        }
    }


    /**
     * get App versionCode
     * @param context
     * @return
     */
    fun getVersionCode(context: Context): String? {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionCode = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionCode = packageInfo.versionCode.toString() + ""
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }

    fun stringToInt(s: String): Int {
        if ("null" == s)
            return 0
        try {
            return s.toInt()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获取图片文件夹
     *
     * @param context
     * @return
     */
    fun getImageDir(): String? {
        return getCacheDir(Environment.DIRECTORY_PICTURES)
    }

    /**
     * 获取程序下载文件夹
     *
     * @param context
     * @return
     */
    fun getDownloadDir(): String? {
        return getCacheDir(Environment.DIRECTORY_DOWNLOADS)
    }

    /**
     * 获取程序下载文件夹
     *
     * @return
     */
    private fun getCacheDir(environment: String): String? {
        var path =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                val f = MyApplication.application.getExternalFilesDir(environment)
                if (f == null) {
                    MyApplication.application.filesDir.absolutePath
                } else {
                    f.absolutePath
                }
            } else {
                MyApplication.application.filesDir.absolutePath
            }
        try {
            val f = File(path)
            if (!f.exists()) {
                f.mkdirs()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return path
    }

    fun tackPhoto():File?{
        var photoFile = File(getImageDir(),  System.currentTimeMillis().toString() + ".jpg")
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var mImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //兼容7.0以上
            FileProvider.getUriForFile(MyApplication.application, getProviderString(), photoFile)
        } else {
            Uri.fromFile(photoFile)
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        //  下面三个 调用前置摄像头
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
        takePictureIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        ActivityManager.getCurrentActivity()?.startActivityForResult(takePictureIntent, TACK_PHOTO)
        return photoFile
    }

    fun getProviderString() : String{
        return MyApplication.application.packageName.toString() + ".provider"
    }
}