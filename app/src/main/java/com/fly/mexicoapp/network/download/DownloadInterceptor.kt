package com.fly.mexicoapp.network.download

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import java.io.IOException

class DownloadInterceptor constructor(downloadListener: DownloadListener?): Interceptor{
    private var downloadListener: DownloadListener? = null
    init {
        this.downloadListener = downloadListener
    }

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response? {
        val response = chain.proceed(chain.request())
        return response.newBuilder().body(response.body()
            ?.let { downloadListener?.let { it1 -> ResponseBody(it, it1) } }).build()
    }
}