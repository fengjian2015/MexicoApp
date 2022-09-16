package com.fly.cashhill.web

import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.ConsoleMessage.MessageLevel
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.fly.cashhill.utils.LogUtils
import com.fly.cashhill.weight.IWebView

class IWebChromeClient constructor(iWebView: IWebView) : WebChromeClient() {
    private var webView: WebView? = null
    private var lineProgressbar: ProgressBar? = null
    private var fullFrameLayout: FrameLayout? = null
    private var callback: CustomViewCallback? = null

    init {
        webView = iWebView.getWebView()
        lineProgressbar = iWebView.getProgressbar()
        fullFrameLayout = iWebView.getFullFrameLayout()
    }

    override fun onProgressChanged(webView: WebView?, newProgress: Int) {
        super.onProgressChanged(webView, newProgress)
        lineProgressbar?.visibility = View.VISIBLE
        lineProgressbar?.progress = newProgress
        if (newProgress >= 80) {
            lineProgressbar?.visibility = View.GONE
        }
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        val sb = StringBuffer()
        sb.append("\n|")
        sb.append("|------------------------------------------------------------------------------------------------------------------|")
        sb.append("\n|")
        sb.append("|\tmessage->" + consoleMessage.message())
        sb.append("\n|")
        sb.append("|\tsourceId->" + consoleMessage.sourceId())
        sb.append("\n|")
        sb.append("|\tlineNumber->" + consoleMessage.lineNumber())
        sb.append("\n|")
        sb.append("|\tmessageLevel->" + consoleMessage.messageLevel())
        sb.append("\n|")
        sb.append("|----------------------------------------------------------------------------------------------------------------|")
        when (consoleMessage.messageLevel()) {
            MessageLevel.ERROR -> LogUtils.e("consoleMessage:$sb")
            MessageLevel.WARNING -> LogUtils.w("consoleMessage:$sb")
            else -> LogUtils.d("consoleMessage:$sb")
        }
        return super.onConsoleMessage(consoleMessage)
    }

    override fun onHideCustomView() {
        LogUtils.d("onHideCustomView:")
        if (callback != null) {
            callback?.onCustomViewHidden()
        }
        webView?.visibility = View.VISIBLE
        fullFrameLayout?.removeAllViews()
        fullFrameLayout?.visibility = View.GONE
        super.onHideCustomView()
    }


    override fun onShowCustomView(view: View?, customViewCallback: CustomViewCallback) {
        LogUtils.d("onShowCustomView:")
        webView?.visibility = View.GONE
        fullFrameLayout?.visibility = View.VISIBLE
        fullFrameLayout?.removeAllViews()
        fullFrameLayout?.addView(view)
        callback = customViewCallback
        super.onShowCustomView(view, customViewCallback)
    }

    override fun onShowCustomView(view: View?, i: Int, customViewCallback: CustomViewCallback) {
        LogUtils.d("onShowCustomView:")
        webView?.visibility = View.GONE
        fullFrameLayout?.visibility = View.VISIBLE
        fullFrameLayout?.removeAllViews()
        fullFrameLayout?.addView(view)
        callback = customViewCallback
        super.onShowCustomView(view, i, customViewCallback)
    }
}