package com.fly.mexicoapp.network.download

import java.io.File

interface DownloadCallback {
    fun onSuccess(file: File)
    fun onProgress(progress: Long, length: Long)
    fun onFail(errorInfo: String?)
}