package com.fly.mexicoapp.network.download

import com.fly.mexicoapp.utils.LogUtils
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class ResponseBody constructor(responseBody: ResponseBody, downloadListener: DownloadListener) :
    ResponseBody() {
    private var responseBody: ResponseBody? = null
    private var downloadListener: DownloadListener? = null

    // BufferedSource 是okio库中的输入流，这里就当作inputStream来使用。
    private var bufferedSource: BufferedSource? = null

    init {
        this.responseBody = responseBody
        this.downloadListener = downloadListener
        downloadListener.onStartDownload(responseBody.contentLength())
    }

    override fun contentType(): MediaType? {
        return responseBody?.contentType()
    }

    override fun contentLength(): Long {
        var long: Long = 0
        responseBody?.let {
            long = it.contentLength()
        }
        return long
    }

    override fun source(): BufferedSource? {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(responseBody?.let { source(it.source()) })
        }
        return bufferedSource
    }

    private fun source(source: Source): Source? {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                responseBody?.let {
                    LogUtils.d("read: " + (totalBytesRead * 100 / it.contentLength()).toInt())
                }

                if (null != downloadListener) {
                    if (bytesRead != -1L) {
                        responseBody?.let {
                            downloadListener?.onProgress(
                                totalBytesRead,
                                it.contentLength()
                            )
                        }
                    }
                }
                return bytesRead
            }
        }
    }
}