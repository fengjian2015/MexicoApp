package com.fly.cashhill.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.fly.cashhill.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {

    private const val IMG_WIDTH = 380 //超過此寬、高則會 resize圖片

    private const val IMG_HIGHT = 600
    private const val COMPRESS_QUALITY = 70 //壓縮 JPEG使用的品質(70代表壓縮率為 30%)


    fun compressImage(srcImgPath: String): File? {
        //先取得原始照片的旋轉角度
        var rotate = 0
        try {
            val exif = ExifInterface(srcImgPath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //計算取 Bitmap時的參數"inSampleSize"
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(srcImgPath, options)
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > IMG_HIGHT || width > IMG_WIDTH) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= IMG_HIGHT
                && halfWidth / inSampleSize >= IMG_WIDTH
            ) {
                inSampleSize *= 2
            }
        }
        options.inJustDecodeBounds = false
        options.inSampleSize = if (inSampleSize == 1){ 2 } else{ inSampleSize }

        //取出原檔的 Bitmap(若寬高超過會 resize)並設定原始的旋轉角度
        val srcBitmap = BitmapFactory.decodeFile(srcImgPath, options)
        if (srcBitmap == null) {
            Log.e("ImageUtils", "decode file error $srcImgPath")
            return null
        }
        val matrix = Matrix()
        matrix.postRotate(rotate.toFloat())
        val outBitmap =
            Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.width, srcBitmap.height, matrix, false)

        //壓縮並存檔至 cache路徑下的 File
        val tempImgDir = File(CommonUtil.getImageDir())
        if (!tempImgDir.exists()) {
            tempImgDir.mkdirs()
        }
        val compressedImgName =
            System.currentTimeMillis().toString() + getFileNameFromPath(srcImgPath)
        val compressedImgFile = File(tempImgDir, compressedImgName)
        var fos: FileOutputStream? = null
        try {
            compressedImgFile.createNewFile()
            fos = FileOutputStream(compressedImgFile)
            outBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, fos)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                srcBitmap.recycle()
                outBitmap.recycle()
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return compressedImgFile
    }

    /**
     * 获取文件名
     *
     * @param filepath dir+filename
     */
    fun getFileNameFromPath(filepath: String?): String? {
        if (filepath != null && filepath.length > 0) {
            val sep = filepath.lastIndexOf('/')
            if (sep > -1 && sep < filepath.length - 1) {
                return filepath.substring(sep + 1)
            }
        }
        return filepath
    }
}