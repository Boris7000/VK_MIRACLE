package com.vkontakte.miracle.network;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.engine.util.DeviceUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest = request.newBuilder()
                .addHeader("X-VK-Android-Client", "new")
                .addHeader("User-Agent", DeviceUtil.getUserAgent())
                .build();
        return chain.proceed(newRequest);
    }
}
