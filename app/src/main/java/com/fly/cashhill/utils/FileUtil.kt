package com.fly.cashhill.utils

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.fly.cashhill.MyApplication
import java.io.File


object FileUtil {
    fun getDownloadFile():ArrayList<File>{
        var fileList = ArrayList<File>()
        val allFiles = getFiles(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),MediaStore.Files.FileColumns.DATA)
        allFiles?.let {
            fileList.addAll(it)
        }
        return fileList
    }

    fun getAudioExternal():ArrayList<File>{
        var fileList = ArrayList<File>()
        val allFiles = getFiles(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,MediaStore.Audio.Media.DATA)
        allFiles?.let {
            fileList.addAll(it)
        }
        return fileList
    }

    fun getAudioInternal():ArrayList<File>{
        var fileList = ArrayList<File>()
        val allFiles = getFiles(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,MediaStore.Audio.Media.DATA)
        allFiles?.let {
            fileList.addAll(it)
        }
        return fileList
    }

    fun getVideoInternal():ArrayList<File>{
        var fileList = ArrayList<File>()
        val allFiles = getFiles(MediaStore.Video.Media.INTERNAL_CONTENT_URI,MediaStore.Video.Media.DATA)
        allFiles?.let {
            fileList.addAll(it)
        }
        return fileList
    }

    fun getImagesInternal():ArrayList<File>{
        var fileList = ArrayList<File>()
        val allFiles = getFiles(MediaStore.Images.Media.INTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA)
        allFiles?.let {
            fileList.addAll(it)
        }
        return fileList
    }

    fun getImagesExternal():ArrayList<File>{
        var fileList = ArrayList<File>()
        val allFiles = getFiles(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA)
        allFiles?.let {
            fileList.addAll(it)
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