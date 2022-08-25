package com.fly.mexicoapp.network

import com.fly.mexicoapp.bean.response.ImageResponse
import com.fly.mexicoapp.bean.response.ProtocolUrlBeanResponse
import com.fly.mexicoapp.bean.response.UpdateBeanResponse
import com.fly.mexicoapp.bean.response.UserInfoBeanResponse
import com.fly.mexicoapp.network.bean.BaseResponseBean
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface HttpService {
    @POST("/auth/uploadCocoLoanWardAuth")
    fun applyInfo(@Body applyInfoBean: Map<String, String>): Observable<BaseResponseBean>

    @POST("/system/getNewVersion")
    fun getNewVersion(): Observable<UpdateBeanResponse>

    @POST("/account/sendVerifyCode")
    fun sendVerifyCode(@Body applyInfoBean: Map<String, String>): Observable<BaseResponseBean>

    @POST("/account/loginByPhoneVerifyCode")
    fun loginByPhoneVerifyCode(@Body applyInfoBean: Map<String, String>): Observable<UserInfoBeanResponse>

    @POST("/system/getProtocolUrl")
    fun getProtocolUrl(): Observable<ProtocolUrlBeanResponse>

    @POST("/account/logout")
    fun logout(): Observable<BaseResponseBean>

    @Streaming
    @GET
    fun downloadApk(@Url url: String): Observable<ResponseBody>

    @Multipart
    @POST("/system/uploadimg")
    fun uploadImg(@Part suffix: MultipartBody.Part,@Part type: MultipartBody.Part,@Part oldPath: MultipartBody.Part, @Part file: MultipartBody.Part): Call<ImageResponse>
}