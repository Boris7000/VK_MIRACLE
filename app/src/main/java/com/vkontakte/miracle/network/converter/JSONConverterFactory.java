package com.vkontakte.miracle.network.converter;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class JSONConverterFactory extends Converter.Factory {
    public static JSONConverterFactory create() {
        return new JSONConverterFactory();
    }

    private JSONConverterFactory() {
    }

    @Override public Converter<?, RequestBody>
    requestBodyConverter(@NonNull Type type, @NonNull Annotation[] parameterAnnotations,
                         @NonNull Annotation[] methodAnnotations, @NonNull Retrofit retrofit) {
        if (type == JSONObject.class
                || type == JSONArray.class) {
            return JSONRequestBodyConverter.INSTANCE;
        }
        return null;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull Type type,
                                                            @NonNull Annotation[] annotations,
                                                            @NonNull Retrofit retrofit) {
        if (type == JSONObject.class) {
            return JSONResponseBodyConverters.JSONObjectResponseBodyConverter.INSTANCE;
        }
        if (type == JSONArray.class) {
            return JSONResponseBodyConverters.JSONArrayResponseBodyConverter.INSTANCE;
        }
        return null;
    }
}