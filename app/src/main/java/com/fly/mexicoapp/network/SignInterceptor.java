package com.fly.mexicoapp.network;

import com.fly.mexicoapp.MyApplication;
import com.fly.mexicoapp.utils.CommonUtil;
import com.fly.mexicoapp.utils.UserInfoManger;

import java.io.EOFException;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 功能描述：拦截器，用于处理网关请求的签名及加密操作
 */
public class SignInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Headers headers = request.headers();
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        if (hasRequestBody) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            if (isPlaintext(buffer)) {
                request = request.newBuilder()
                        .headers(headers)
                        .header("appVersion", checkHeader(CommonUtil.INSTANCE.getVersionName(MyApplication.application)))
                        .header("devName", checkHeader("android"))
                        .header("session", checkHeader(UserInfoManger.INSTANCE.getUserSessionId()))
                        .header("userId", checkHeader(UserInfoManger.INSTANCE.getUserUserId()))
                        .build();
            }
        }

        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            throw e;
        }
        return response;
    }

    /**
     * 兼容偶现崩溃
     * @param value
     * @return
     */
    public String checkHeader(String value){
        if (value==null)return "";
        int i = 0;
        for(int length = value.length(); i < length; ++i) {
            char c = value.charAt(i);
            if (c <= 31 && c != '\t' || c >= 127) {
                return "";
            }
        }
        return value;
    }

    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount;
            if (buffer.size() < 64) {
                byteCount = buffer.size();
            } else {
                byteCount = 64;
            }
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 15; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
