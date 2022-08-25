package com.fly.mexicoapp.utils

import com.fly.mexicoapp.utils.Cons.KEY_PUBLIC_IP
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*


object PublicIP {

    fun requestIp(){
       val ip =  get("https://pv.sohu.com/cityjson?ie=utf-8")
        SPUtils.putString(KEY_PUBLIC_IP,ip)
    }

    fun getIp():String?{
        return SPUtils.getString(KEY_PUBLIC_IP)
    }

    fun get(url: String) :String{
        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var reader: BufferedReader? = null
        try {
            urlConnection = URL(url).openConnection() as HttpURLConnection?
            if (urlConnection is HttpsURLConnection) {
                val sc = SSLContext.getInstance("SSL")
                sc.init(null, arrayOf<TrustManager>(UnSafeTrustManager), SecureRandom())
                (urlConnection as HttpsURLConnection).sslSocketFactory = sc.socketFactory
                (urlConnection as HttpsURLConnection).hostnameVerifier = UnSafeHostnameVerifier
            }
            urlConnection!!.instanceFollowRedirects = false
            urlConnection!!.doInput = true
            urlConnection!!.connectTimeout = 20000
            urlConnection!!.readTimeout = 20000
            urlConnection!!.setRequestProperty("Cache-Control", "no_cache")
            urlConnection!!.defaultUseCaches = false
            urlConnection!!.useCaches = false
            urlConnection!!.connect()
            val statusCode = urlConnection!!.responseCode
            if (statusCode == 200) {
                inputStream = urlConnection!!.inputStream
                reader = BufferedReader(InputStreamReader(inputStream, "utf-8"))
                var line: String? = null
                val strber = StringBuilder()
                while (reader.readLine().also { line = it } != null) strber.append(
                    """${line.toString()}""".trimIndent())
                // 从反馈的结果中提取出IP地址
                // 从反馈的结果中提取出IP地址
                val start = strber.indexOf("{")
                val end = strber.indexOf("}")
                val json = strber.substring(start, end + 1)
                if (json != null) {
                    try {
                        val jsonObject = JSONObject(json)
                        line = jsonObject.optString("cip")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                return line!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                urlConnection?.disconnect()
                inputStream?.close()
            } catch (e: Exception) {
                //ignore
            }
        }
        return ""
    }

    /**
     * 为了解决客户端不信任服务器数字证书的问题，网络上大部分的解决方案都是让客户端不对证书做任何检查，
     * 这是一种有很大安全漏洞的办法
     */
    var UnSafeTrustManager: X509TrustManager = object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }
    var UnSafeHostnameVerifier =
        HostnameVerifier { hostname, session -> true }

}