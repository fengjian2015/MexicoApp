package com.fly.mexicoapp.network

import android.annotation.SuppressLint
import android.util.Log
import com.fly.mexicoapp.BuildConfig
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.logging.Level
import javax.net.ssl.*

class HttpClient private constructor() {
    lateinit var httpService: HttpService
    private var mOkHttpClient: OkHttpClient? = null

    private object Holder {
        val INSTANCE = HttpClient()
    }

    companion object {
        @JvmStatic
        val instance by lazy { Holder.INSTANCE }
    }

    /**
     * 初始化
     */
    fun init(baseUrl: String) {
        // 初始化okHttp
        initOkHttpClient()
        if (mOkHttpClient == null) return
        httpService = createRetrofit(baseUrl).create(HttpService::class.java)
    }

    /**
     * 生成网络请求代理
     */
    fun createRetrofit(url: String): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(mOkHttpClient)
            .build()
        return retrofit
    }

    /**
     * 初始化okHttpClient
     */
    private fun initOkHttpClient() {
        val builder1 = OkHttpClient.Builder()
        builder1
            .retryOnConnectionFailure(false)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .sslSocketFactory(createSSLSocketFactory())
            .addNetworkInterceptor(rewriteCacheControlInterceptor)
            .addInterceptor(SignInterceptor())
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor("HttpsLog")
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
            loggingInterceptor.setColorLevel(Level.INFO)
            builder1.addInterceptor(loggingInterceptor)
        }
        mOkHttpClient = builder1.build()
    }

    /**
     * 拦截器 - 重写
     */
    private var rewriteCacheControlInterceptor: Interceptor = Interceptor { chain ->
        val request = chain.request()
        val originalResponse = chain.proceed(request)

        originalResponse.newBuilder()
            .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
            .removeHeader("Cache-Control")
            .header("Cache-Control", "public, max-age=180")
            .build()
    }


    @SuppressLint("TrulyRandom")
    fun createSSLSocketFactory(): SSLSocketFactory? {
        var sSLSocketFactory: SSLSocketFactory? = null
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(
                null, arrayOf<TrustManager>(TrustAllManager()),
                SecureRandom()
            )
            sSLSocketFactory = sc.socketFactory
        } catch (e: Exception) {
        }
        return sSLSocketFactory
    }

    private class TrustAllManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOfNulls(0)
    }
}