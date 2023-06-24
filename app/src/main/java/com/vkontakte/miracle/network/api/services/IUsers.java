package com.vkontakte.miracle.network.api.services;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IUsers {


    @FormUrlEncoded
    @POST("users.get")
    Call<JSONObject> get(@Field("user_ids") String user_ids,
                               @Field("fields") String fields,
                               @Field("access_token") String access_token,
                               @Field("v") String v);

}
