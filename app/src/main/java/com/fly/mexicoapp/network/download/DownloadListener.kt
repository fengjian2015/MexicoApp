package com.fly.mexicoapp.network.download

interface DownloadListener {
    fun onStartDownload(length: Long)
    fun onProgress(progress: Long, length: Long)
    fun onFail(errorInfo: String?)
}