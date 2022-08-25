package com.fly.mexicoapp.utils

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
import com.fly.mexicoapp.MyApplication
import com.fly.mexicoapp.bean.AlbumInfoBean
import com.fly.mexicoapp.utils.DateTool.FMT_DATE_TIME2
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
    private var albumInfos: ArrayList<AlbumInfoBean> = ArrayList()
    lateinit var activity: FragmentActivity
    var time = System.currentTimeMillis() / 1000 - 365 * 24 * 60 * 60

    fun load(activity: FragmentActivity) {
        this.activity = activity
        val loaderManager: LoaderManager = LoaderManager.getInstance(activity)
        onImageLoadListener?.let {
            if (isLoadFinish) it.onImageLoad(albumInfos)
        }
        loaderManager.initLoader(0, null, this) //加载所有的图片
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
            if ( isLoadFinish ) return@launch
            isLoadFinish = true
            albumInfos.clear()
            while (data.moveToNext()) {
                //查询数据
                val imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]))
                val imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]))
                val imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]))
                val imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]))
                val imageAddTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]))

                var exifInterface: ExifInterface? = null
                val latLong = FloatArray(2)
                try {
                    //兼容分区存储问题
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val id: Long = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[7]))
                        //通过id构造Uri
                        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                        //构造输入流
                        val inputStream: InputStream =
                            MyApplication.application.contentResolver.openInputStream(uri)!!
                        exifInterface = ExifInterface(inputStream)
                    } else {
                        exifInterface = ExifInterface(imagePath)
                    }
                    exifInterface.getLatLong(latLong)
                } catch (e: Exception) {
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
                val date = DateTool.convert2Date(
                    exifInterface?.getAttribute(ExifInterface.TAG_DATETIME),
                    FMT_DATE_TIME2
                )
                albumInfo.addTime = if (data != null) {
                    DateTool.convert2String(date, DateTool.FMT_DATE_TIME).toString()
                } else {
                    exifInterface?.getAttribute(ExifInterface.TAG_DATETIME).toString()
                }
                albumInfo.updateTime = DateTool.getTimeFromLong(
                    DateTool.FMT_DATE_TIME,
                    if (imageAddTime / 1000000000 > 100) imageAddTime else (imageAddTime * 1000)
                ).toString()
                if (TextUtils.isEmpty(albumInfo.addTime)) albumInfo.addTime =
                    albumInfo.updateTime
                albumInfo.save_time = albumInfo.updateTime
                albumInfo.orientation =
                    exifInterface?.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1).toString()
                albumInfo.x_resolution =
                    exifInterface?.getAttribute(ExifInterface.TAG_IMAGE_WIDTH).toString()
                albumInfo.y_resolution =
                    exifInterface?.getAttribute(ExifInterface.TAG_IMAGE_LENGTH).toString()
                albumInfo.gps_altitude =
                    exifInterface?.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF).toString()
                albumInfo.gps_processing_method =
                    exifInterface?.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD)
                        .toString()
                albumInfo.lens_make =
                    exifInterface?.getAttribute(ExifInterface.TAG_MAKE).toString()
                albumInfo.lens_model =
                    exifInterface?.getAttribute(ExifInterface.TAG_MODEL).toString()
                albumInfo.focal_length =
                    exifInterface?.getAttribute(ExifInterface.TAG_FOCAL_LENGTH).toString()
                albumInfo.flash =
                    exifInterface?.getAttribute(ExifInterface.TAG_FLASH).toString()
                albumInfo.software =
                    exifInterface?.getAttribute(ExifInterface.TAG_SOFTWARE).toString()
                albumInfos.add(albumInfo)
            }
            onImageLoadListener?.let { on ->
                on.onImageLoad(albumInfos)
            }
        }
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