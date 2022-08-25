package com.fly.mexicoapp.network.download

import com.fly.mexicoapp.network.HttpService
import com.fly.mexicoapp.utils.Cons
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.io.*
import java.util.concurrent.TimeUnit

class DownloadClient constructor(baseUrl: String?, listener: DownloadListener?){
    private val DEFAULT_TIMEOUT = 60
    private var retrofit: Retrofit? = null
    private var listener: DownloadListener? = null
    private var baseUrl: String? = null

    init {
        this.baseUrl = baseUrl
        this.listener = listener
        val mInterceptor = DownloadInterceptor(listener)
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(mInterceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
        retrofit = Retrofit.Builder()
            .baseUrl(Cons.baseUrl)
            .client(httpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    /**
     * 开始下载
     * @param url
     * @param file
     * @param subscriber
     */
    fun download(url: String, file: File, subscriber: Observer<InputStream>) {
        retrofit?.create(HttpService::class.java)
            ?.downloadApk(url)
            ?.subscribeOn(Schedulers.io())
            ?.unsubscribeOn(Schedulers.io())
            ?.map { responseBody -> responseBody.byteStream() }
            ?.observeOn(Schedulers.computation()) // 用于计算任务
            ?.doOnNext { inputStream -> writeFile(inputStream, file) }
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(subscriber)
    }

    /**
     * 将输入流写入文件
     * @param inputString
     * @param file
     */
    private fun writeFile(inputString: InputStream, file: File) {
        if (file.exists()) {
            file.delete()
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            val b = ByteArray(1024)
            var len: Int
            while (inputString.read(b).also { len = it } != -1) {
                fos.write(b, 0, len)
            }
            inputString.close()
            fos.close()
        } catch (e: FileNotFoundException) {
            listener?.onFail("FileNotFoundException")
        } catch (e: IOException) {
            listener?.onFail("IOException")
        }
    }
}