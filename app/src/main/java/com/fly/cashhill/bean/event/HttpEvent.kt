package com.fly.cashhill.bean.event

import android.os.Build
import android.os.Handler
import android.util.Base64
import android.webkit.WebView
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.appsflyer.AppsFlyerLib
import com.fly.cashhill.MyApplication
import com.fly.cashhill.activity.BaseWebActivity
import com.fly.cashhill.activity.SendCodeActivity
import com.fly.cashhill.bean.ApplyInfoBean
import com.fly.cashhill.bean.response.*
import com.fly.cashhill.js.CallBackJS
import com.fly.cashhill.js.bean.CommentParseDataBean
import com.fly.cashhill.network.HttpClient
import com.fly.cashhill.network.NetworkScheduler
import com.fly.cashhill.network.bean.BaseResponseBean
import com.fly.cashhill.network.bean.HttpErrorBean
import com.fly.cashhill.network.bean.HttpResponse
import com.fly.cashhill.utils.*
import com.fly.cashhill.utils.Cons.KEY_AF_CHANNEL
import com.fly.cashhill.utils.Cons.KEY_PROTOCAL_1
import com.fly.cashhill.utils.Cons.KEY_PROTOCAL_2
import com.fly.cashhill.utils.Cons.KEY_PROTOCAL_3
import com.fly.cashhill.utils.Cons.KEY_PROTOCAL_4
import com.fly.cashhill.utils.Cons.KEY_PROTOCAL_5
import com.fly.cashhill.utils.Cons.KEY_PROTOCAL_6
import com.fly.cashhill.utils.Cons.KEY_PROTOCAL_7
import com.fly.cashhill.weight.UpdateDialog
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observer
import kotlinx.coroutines.GlobalScope
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.collections.HashMap

object HttpEvent {

    /**
     * 发送验证码
     */
    fun sendVerifyCode(phone: String, param: HttpResponse<BaseResponseBean>){
        val map: MutableMap<String, String> = HashMap()
        map["phone"] = phone
        HttpClient.instance.httpService
            .sendVerifyCode(map)
            .compose(NetworkScheduler.compose())
            .doOnSubscribe { LoadingUtil.showLoading() }
            .doFinally { LoadingUtil.dismissProgress() }
            .subscribe(object : HttpResponse<BaseResponseBean>() {
                override fun businessSuccess(data: BaseResponseBean) {
                    param.businessSuccess(data)
                }

                override fun businessFail(statusCode: Int, httpErrorBean: HttpErrorBean) {
                    param.businessFail(statusCode,httpErrorBean)
                }
            })
    }

    /**
     * 验证码登录接口
     */
    fun loginByPhoneVerifyCode(
        phone: String,
        code: String,
        activity: SendCodeActivity,
        etCode: EditText
    ){
        val map: MutableMap<String, String> = HashMap()
        map["Phone"] = phone
        map["Code"] = code
        map["mobilePhoneBrands"] = Build.BRAND
        map["appMarketCode"] = "Google"
        map["appsFlyerId"] = AppsFlyerLib.getInstance().getAppsFlyerUID(MyApplication.application)
        map["deviceModel"] = Build.MODEL
        map["channelCode"] = SPUtils.getString(KEY_AF_CHANNEL)
        HttpClient.instance.httpService
            .loginByPhoneVerifyCode(map)
            .compose(NetworkScheduler.compose())
            .doOnSubscribe { LoadingUtil.showLoading() }
            .doFinally { LoadingUtil.dismissProgress() }
            .subscribe(object : HttpResponse<UserInfoBeanResponse>() {
                override fun businessSuccess(data: UserInfoBeanResponse) {
                    if (data.code == 200){
                        data.data?.let {

                            if (it.isNew){
                                AppsFlyerUtil.postAF("hilogin")
                            }
                            UserInfoManger.saveUserInfo(it)
                            BaseWebActivity.openWebView(activity,it.homeUrl,true)
                            Handler().postDelayed({
                                ActivityManager.finishCloseActivity()
                                activity.finish()
                            },500)
                        }

                    }else{
                        etCode.setText("")
                        ToastUtils.showShort(data.message)
                    }
                }

                override fun businessFail(statusCode: Int, httpErrorBean: HttpErrorBean) {
                    etCode.setText("")
                    ToastUtils.showShort(httpErrorBean.message)
                }
            })
    }

    /**
     * 验证码登录接口
     */
    fun logout(){
        HttpClient.instance.httpService
            .logout()
            .compose(NetworkScheduler.compose())
            .subscribe(object : HttpResponse<BaseResponseBean>() {
                override fun businessSuccess(data: BaseResponseBean) {

                }

                override fun businessFail(statusCode: Int, httpErrorBean: HttpErrorBean) {

                }
            })
    }

    /**
     * 验证码登录接口
     */
    fun getProtocolUrl(){
        HttpClient.instance.httpService
            .getProtocolUrl()
            .compose(NetworkScheduler.compose())
            .subscribe(object : HttpResponse<ProtocolUrlBeanResponse>() {
                override fun businessSuccess(data: ProtocolUrlBeanResponse) {
                    if (data.code == 200){
                        data.data?.let {
                            for (protocolUrlBean in it){
                                if (protocolUrlBean.protocalType == 1){
                                    SPUtils.putString(KEY_PROTOCAL_1,protocolUrlBean.url)
                                } else if (protocolUrlBean.protocalType == 2){
                                    SPUtils.putString(KEY_PROTOCAL_2,protocolUrlBean.url)
                                }else if (protocolUrlBean.protocalType == 3){
                                    SPUtils.putString(KEY_PROTOCAL_3,protocolUrlBean.url)
                                }else if (protocolUrlBean.protocalType == 4){
                                    SPUtils.putString(KEY_PROTOCAL_4,protocolUrlBean.url)
                                }else if (protocolUrlBean.protocalType == 5){
                                    SPUtils.putString(KEY_PROTOCAL_5,protocolUrlBean.url)
                                }else if (protocolUrlBean.protocalType == 6){
                                    SPUtils.putString(KEY_PROTOCAL_6,protocolUrlBean.url)
                                }else if (protocolUrlBean.protocalType == 7){
                                    SPUtils.putString(KEY_PROTOCAL_7,protocolUrlBean.url)
                                }
                            }
                        }
                    }
                }

                override fun businessFail(statusCode: Int, httpErrorBean: HttpErrorBean) {

                }
            })
    }

    /**
     * 验证码登录接口
     */
    fun getPublicIp(){
        HttpClient.instance.httpService
            .getPublicIp()
            .compose(NetworkScheduler.compose())
            .subscribe(object : HttpResponse<PublicIpResponse>() {
                override fun businessSuccess(data: PublicIpResponse) {
                    if (data.code == 200){
                        data.data?.let {
                            SPUtils.putString(Cons.KEY_PUBLIC_IP,it)
                        }
                    }
                }

                override fun businessFail(statusCode: Int, httpErrorBean: HttpErrorBean) {

                }
            })
    }

    /**
     * 检测更新
     */
    fun getNewVersion(){
        HttpClient.instance.httpService
            .getNewVersion()
            .compose(NetworkScheduler.compose())
            .subscribe(object : HttpResponse<UpdateBeanResponse>() {
                override fun businessSuccess(data: UpdateBeanResponse) {
                    if (data.code == 200){
                        var updateBean = data.data
                        updateBean?.let {
                            if (CommonUtil.stringToInt(it.code) >
                                CommonUtil.stringToInt(CommonUtil.getVersionCode(MyApplication.application).toString())){
                                try {
                                    val dialog = UpdateDialog(it)
                                    val activity =  ActivityManager.getCurrentActivity() as FragmentActivity
                                    dialog.show(activity.supportFragmentManager, "update")
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                }

                override fun businessFail(statusCode: Int, httpErrorBean: HttpErrorBean) {

                }
            })
    }

    /**
     * 上传风控数据
     */
    fun uploadApplyInfo(applyInfoBean:ApplyInfoBean,mWebView: WebView,id:String ,event:String){
        val map: MutableMap<String, String> = HashMap()
        val content = Gson().toJson(applyInfoBean)
        map["authInfo"] = Base64.encodeToString(content.toByteArray(), Base64.DEFAULT)
        HttpClient.instance.authService
            .applyInfo(map)
            .compose(NetworkScheduler.compose())
            .subscribe(object : HttpResponse<BaseResponseBean>() {
                override fun businessFail(statusCode: Int, httpErrorBean: HttpErrorBean) {
                    CallBackJS.callbackJsErrorOther(mWebView,id,event,httpErrorBean.message)
                }

                override fun businessSuccess(data: BaseResponseBean) {
                    if (data.code == 200){
                        CallBackJS.callBackJsSuccess(mWebView,id,event)
                    }else{
                        CallBackJS.callbackJsErrorOther(mWebView,id,event,data.message)
                    }
                }
            })
    }

    /**
     * 上传图片
     * 文件类型1-用户自拍头像面部照片 2-curp身份证正面照 3-curp身份证反面照片 4-活体认证照片
     */
    fun uploadImage(file: File,type:String,mWebView: WebView,id:String ,event:String){
        LoadingUtil.showLoading()
        val imageBody: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val imageBodyPart = MultipartBody.Part.createFormData("file", file.name, imageBody)
        val txtBodyPart = MultipartBody.Part.createFormData("suffix", "jpg")
        val type = MultipartBody.Part.createFormData("type", type)
        val oldPath = MultipartBody.Part.createFormData("oldPath", "")
        val repos: Call<ImageResponse> =
            HttpClient
                .instance
                .httpService
                .uploadImg(txtBodyPart,type,oldPath, imageBodyPart)
        repos.enqueue(object : Callback<ImageResponse> {
            override fun onResponse(
                call: Call<ImageResponse>,
                response: Response<ImageResponse>) {
                LoadingUtil.dismissProgress()
                try {
                    if (response.body()!!.code === 200) { //请求成功
                        //返回数据处理
                        LogUtils.d("----上传图片成功：$response")
                        var commontParseDataBean = CommentParseDataBean()
                        commontParseDataBean.value = response.body()!!.data
                        CallBackJS.callBackJsSuccess(mWebView,id,event,Gson().toJson(commontParseDataBean))
                    } else {
                        //图片上传失败
                        LogUtils.d("----上传图片失败：$response")
                        CallBackJS.callbackJsErrorOther(mWebView,id,event,response.message())
                    }
                } catch (e: Exception) {
                    //返回数据异常
                    CallBackJS.callbackJsErrorOther(mWebView,id,event,e.toString())
                    LogUtils.d("----上传图片异常：$e")
                }
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                LoadingUtil.dismissProgress()
                //请求异常
                LogUtils.d("----上传图片异常：$t")
                CallBackJS.callbackJsErrorOther(mWebView,id,event,t.toString())
            }
        })
    }

}