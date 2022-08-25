package com.fly.mexicoapp.utils

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.fly.mexicoapp.MyApplication
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream


object FileUtil {
    fun getDownloadFile():ArrayList<File>{
        var fileList = ArrayList<File>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val allFiles = getFiles(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),MediaStore.Files.FileColumns.DATA)
            allFiles?.let {
                fileList.addAll(it)
            }
        }else{
            val externalStoragePublicDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            externalStoragePublicDirectory?.let {
                val tempList: Array<out File>? = externalStoragePublicDirectory.listFiles()
                tempList?.let {
                    getAllFiles(it,fileList)
                }
            }
        }
        return fileList
    }

    fun getAudioFile():ArrayList<File>{
        var fileList = ArrayList<File>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val allFiles = getFiles(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,MediaStore.Audio.Media.DATA)
            allFiles?.let {
                fileList.addAll(it)
            }
        }else{
            val externalStoragePublicDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            externalStoragePublicDirectory?.let {
                val tempList: Array<out File>? = externalStoragePublicDirectory.listFiles()
                tempList?.let {
                    getAllFiles(it,fileList)
                }
            }
        }
        return fileList
    }

    fun getImagesFile():ArrayList<File>{
        var fileList = ArrayList<File>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val allFiles = getFiles(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA)
            allFiles?.let {
                fileList.addAll(it)
            }
        }else{
            val externalStoragePublicDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            externalStoragePublicDirectory?.let {
                val tempList: Array<out File>? = externalStoragePublicDirectory.listFiles()
                tempList?.let {
                    getAllFiles(it,fileList)
                }
            }
        }
        return fileList
    }



    /**
     * 获取指定目录内所有文件路径
     */
    fun getAllFiles(files : Array<out File> ,myFileList: ArrayList<File>){
        for (_file in files) { //遍历目录
            if (_file.isFile) {
                myFileList.add(_file)
            } else if (_file.isDirectory&& !_file.name.contains("thumbnails")) { //查询子目录
                getAllFiles(_file.absolutePath,myFileList)
            } else {
            }
        }
    }

    /**
     * 获取指定目录内所有文件路径
     * @param dirPath 需要查询的文件目录
     */
    fun getAllFiles(dirPath: String?,myFileList: ArrayList<File>):ArrayList<File>? {
        val f = File(dirPath)
        if (!f.exists()) { //判断路径是否存在
            return null
        }
        val files = f.listFiles()
            ?: //判断权限
            return null

        for (_file in files) { //遍历目录
            if (_file.isFile) {
                myFileList.add(_file)
            } else if (_file.isDirectory && !_file.name.contains("thumbnails")) { //查询子目录
                getAllFiles(_file.absolutePath,myFileList)
            } else {
            }
        }
        return myFileList
    }

    /**
     * 获取所有文件
     */
    private fun getFiles(volumeName: Uri, columnName: String  ): ArrayList<File>? {
        val files: ArrayList<File> = ArrayList<File>()
        // 扫描files文件库
        var c: Cursor? = null
        try {
            var mContentResolver = MyApplication.application.contentResolver
            c = mContentResolver.query(
                volumeName,
                null,
                null,
                null,
                null
            )
            if (c != null) {
                val columnIndexOrThrow_DATA = c.getColumnIndexOrThrow(columnName)
                while (c.moveToNext()) {
                    val path = c.getString(columnIndexOrThrow_DATA)
                    val position_do = path.lastIndexOf(".")
                    if (position_do == -1) {
                        continue
                    }
                    val position_x = path.lastIndexOf(File.separator)
                    if (position_x == -1) {
                        continue
                    }
                    val file = File(path)
                    files.add(file)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            c?.close()
        }
        return files
    }
}