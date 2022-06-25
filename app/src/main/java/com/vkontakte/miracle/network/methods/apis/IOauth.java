package com.vkontakte.miracle.network.methods.apis;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface IOauth {
    @FormUrlEncoded
    @POST("token")
    Call<JSONObject> token(@Field("username") String username,
                           @Field("password") String password,
                           @Field("device_id") String device_id,
                           @FieldMap Map<String,Object> fields);


    @FormUrlEncoded
    @POST("token")
    Call<JSONObject> token(@Field("grant_type") String grant_type,
                           @Field("client_id") int client_id,
                           @Field("client_secret") String client_secret,
                           @Field("username") String username,
                           @Field("password") String password,
                           @Field("scope") String scope,
                           @Field("2fa_supported") int twoFa_supported,
                           @Field("libverify_support") Integer libverify_support,
                           @HeaderMap Map<String,String> headers);

    @FormUrlEncoded
    @POST("auth.validatePhone")
    Call<JSONObject> validatePhone(@Field("sid") String sid,
                                   @FieldMap Map<String,Object> fields);

}
