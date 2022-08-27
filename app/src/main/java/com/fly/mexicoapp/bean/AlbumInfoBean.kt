package com.fly.mexicoapp.bean

import java.io.Serializable

class AlbumInfoBean:Serializable {
    var name :String ="" //照片名称
    var author :String ="" //拍摄者（无拍摄者则获取手机品牌）
    var height :String ="" //照片高度，单位：像素
    var width :String ="" //照片宽度，单位：像素
    var longitude :String ="" //经度
    var latitude :String ="" //纬度
    var model :String ="" //拍摄照片的手机机型
    var addTime :String ="" //拍摄时间(格式：yyyy-mm-dd HH:mm:ss)
    var updateTime :String ="" //修改时间(格式：yyyy-mm-dd HH:mm:ss)
    var save_time :String ="" //保存时间
    var orientation :String ="" //照片方向
    var x_resolution :String ="" //水平方向分辨率
    var y_resolution :String ="" //垂直方向分辨率
    var gps_altitude :String ="" //海拔
    var gps_processing_method :String ="" //gps定位方式
    var lens_make :String ="" //镜头生产商
    var lens_model :String ="" //镜头模型
    var focal_length :String ="" //镜头焦距
    var flash :String ="" //闪光灯状态
    var software :String ="" //软件
    var id:Long=0;//图片存储id
    override fun toString(): String {
        return "AlbumInfoBean(name='$name', author='$author', height='$height', width='$width', longitude='$longitude', latitude='$latitude', model='$model', addTime='$addTime', updateTime='$updateTime', save_time='$save_time', orientation='$orientation', x_resolution='$x_resolution', y_resolution='$y_resolution', gps_altitude='$gps_altitude', gps_processing_method='$gps_processing_method', lens_make='$lens_make', lens_model='$lens_model', focal_length='$focal_length', flash='$flash', software='$software')"
    }

}