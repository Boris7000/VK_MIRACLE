package com.vkontakte.miracle.network.methods.apis;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface IAccount {
    @FormUrlEncoded
    @POST("account.registerDevice")
    Call<JSONObject> registerDevice(@Field("token") String token,
                                    @Field("pushes_granted") Integer pushes_granted,
                                    @Field("device_id") String deviceId,
                                    @Field("settings") String settings,
                                    @Field("access_token") String access_token,
                                    @FieldMap Map<String, Object> fields);

    @FormUrlEncoded
    @POST("account.unregisterDevice")
    Call<JSONObject> unregisterDevice(@Field("device_id") String deviceId,
                                      @Field("access_token") String access_token,
                                      @Field("v") String v);


    @FormUrlEncoded
    @POST("auth.refreshToken")
    Call<JSONObject> authRefreshToken(@Field("access_token") String access_token,
                                      @Field("receipt") String receipt,
                                      @Field("receipt2") String receipt2,
                                      @Field("nonce") String nonce,
                                      @Field("timestamp") long timestamp,
                                      @FieldMap Map<String,Object> fields);

    @FormUrlEncoded
    @POST("auth.refreshToken")
    Call<JSONObject> authRefreshToken(@Field("access_token") String access_token,
                                      @Field("receipt") String receipt,
                                      @FieldMap Map<String,Object> fields);

    @FormUrlEncoded
    @POST("auth.refreshToken")
    Call<JSONObject> authRefreshToken(@Field("access_token") String access_token,
                                      @Field("client_id") int client_id,
                                      @Field("client_secret") String client_secret,
                                      @Field("https") int https,
                                      @Field("lang") String lang,
                                      @Field("receipt") String receipt,
                                      @Field("sig") String sig,
                                      @Field("v") String v,
                                      @HeaderMap Map<String,String> headers);

}
