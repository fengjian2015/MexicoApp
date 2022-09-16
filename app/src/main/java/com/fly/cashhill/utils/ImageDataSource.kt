package com.fly.cashhill.utils

import android.content.ContentUris
import android.database.Cursor
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.fly.cashhill.MyApplication
import com.fly.cashhill.bean.AlbumInfoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream


class ImageDataSource : LoaderManager.LoaderCallbacks<Cursor> {
    private val IMAGE_PROJECTION = arrayOf(
        //查询图片需要的数据列
        MediaStore.Images.Media.DISPLAY_NAME,  //图片的显示名称  aaa.jpg
        MediaStore.Images.Media.DATA,  //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
        MediaStore.Images.Media.SIZE,  //图片的大小，long型  132492
        MediaStore.Images.Media.WIDTH,  //图片的宽度，int型  1920
        MediaStore.Images.Media.HEIGHT,  //图片的高度，int型  1080
        MediaStore.Images.Media.MIME_TYPE,  //图片的类型     image/jpeg
        MediaStore.Images.Media.DATE_ADDED,//图片被添加的时间，long型  1450518608
        MediaStore.MediaColumns._ID,//ID信息
    )

    private var onImageLoadListener: OnImageLoadListener? = null
    private var isLoadFinish = false
    private var isLoading = false
    private var albumInfos: ArrayList<AlbumInfoBean> = ArrayList()
    lateinit var activity: FragmentActivity
    var time = DateTool.getServerTimestamp() / 1000 - 365 * 24 * 60 * 60

    fun load(activity: FragmentActivity) {
        this.activity = activity
        val loaderManager: LoaderManager = LoaderManager.getInstance(activity)
        onImageLoadListener?.let {
            if (!isLoading && isLoadFinish) it.onImageLoad(albumInfos)
        }
        if (!isLoading) {
            loaderManager.initLoader(0, null, this) //加载所有的图片
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(
            activity,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            IMAGE_PROJECTION[6] + " > " + time,
            null,
            IMAGE_PROJECTION[6] + " DESC"
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data?.let { it ->
            if (!data.isAfterLast) {
                parseImage(data)
            }

        }
        if (data == null || data.isAfterLast) {
            //回调接口，通知图片数据准备完成
            onImageLoadListener?.let { on ->
                on.onImageLoad(albumInfos)
            }
        }
    }

    fun parseImage(data: Cursor) {
        GlobalScope.launch(Dispatchers.IO){
            if ( isLoadFinish  || isLoading) return@launch
            isLoading = true
            albumInfos.clear()
            while (!data.isClosed && data.moveToNext()) {
                //查询数据
                val imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]))
                val imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]))
                val imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]))
                val imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]))
                val mimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]))
                val imageAddTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]))

                var exifInterface: ExifInterface? = null
                val latLong = FloatArray(2)
                if (mimeType!=null && mimeType.toLowerCase().contains("jpeg")  || mimeType.toLowerCase().contains("jpg"))
                {
                    try {
                        //兼容分区存储问题
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val id: Long = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[7]))
                            //通过id构造Uri
                            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
//                            //构造输入流
                            val inputStream: InputStream =
                                MyApplication.application.contentResolver.openInputStream(uri)!!
                            exifInterface = ExifInterface(inputStream)
                            inputStream.close()
                        } else {
                            exifInterface = ExifInterface(imagePath)
                        }
                        exifInterface?.getLatLong(latLong)
                    } catch (e: Exception) {
                    }
                }
                var albumInfo = AlbumInfoBean()
                albumInfo.name = imageName
                albumInfo.author =
                    if (TextUtils.isEmpty(exifInterface?.getAttribute(ExifInterface.TAG_MAKE))) {
                        Build.BRAND
                    } else {
                        exifInterface?.getAttribute(ExifInterface.TAG_MAKE).toString()
                    }

                albumInfo.height = imageHeight.toString()
                albumInfo.width = imageWidth.toString()
                albumInfo.longitude = latLong[1].toString()
                albumInfo.latitude = latLong[0].toString()
                albumInfo.model =
                    exifInterface?.getAttribute(ExifInterface.TAG_MODEL).toString();
                albumInfo.take_time = if (imageAddTime / 1000000000 > 100) imageAddTime else (imageAddTime * 1000)
                albumInfo.updateTime = DateTool.getTimeFromLong(
                    DateTool.FMT_DATE_TIME,
                    if (imageAddTime / 1000000000 > 100) imageAddTime else (imageAddTime * 1000)
                ).toString()
                albumInfos.add(albumInfo)
                isLoading = false
                isLoadFinish = true
            }
            onImageLoadListener?.let { on ->
                on.onImageLoad(albumInfos)
            }
        }
    }

    private fun addExifInterface29(){
//        for (albumInfo in albumInfos){
//            var exifInterface: ExifInterface? = null
//            val latLong = FloatArray(2)
//            try {
//                //兼容分区存储问题
//                var albums: ArrayList<AlbumInfoBean> = ArrayList()
//                var mContentResolver = MyApplication.application.contentResolver
//                var c = mContentResolver.query(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
//                if (c!=null){
//                    while (c.moveToNext()) {
//
//                    }
//                }
//                //构造输入流
//                val inputStream: InputStream =
//                    MyApplication.application.contentResolver.openInputStream(uri)!!
//                exifInterface = ExifInterface(inputStream)
//                inputStream.close()
//                exifInterface.getLatLong(latLong)
//            } catch (e: Exception) {
//            }
//        }
    }


    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    fun setOnImageLoadListener(listener: OnImageLoadListener) {
        onImageLoadListener = listener
    }

    fun unOnImageLoadListener() {
        onImageLoadListener = null
    }

    /**
     * 所有图片加载完成的回调接口
     */
    interface OnImageLoadListener {
        fun onImageLoad(imageFolders: ArrayList<AlbumInfoBean>)
    }
}