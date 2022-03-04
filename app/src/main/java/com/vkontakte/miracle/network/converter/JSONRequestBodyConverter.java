package com.vkontakte.miracle.network.converter;

import androidx.annotation.NonNull;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

public class JSONRequestBodyConverter<T> implements Converter<T, RequestBody> {
    static final JSONRequestBodyConverter<Object> INSTANCE = new JSONRequestBodyConverter<>();
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain; charset=UTF-8");

    private JSONRequestBodyConverter() {
    }

    @Override public RequestBody convert(@NonNull T value) {
        return RequestBody.create(MEDIA_TYPE, String.valueOf(value));
    }
}