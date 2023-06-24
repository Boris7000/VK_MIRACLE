package com.vkontakte.miracle.network.api.services;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IUtils {

    @FormUrlEncoded
    @POST("utils.resolveScreenName")
    Call<JSONObject> resolveScreenName(@Field("screen_name") String screen_name,
                         @Field("access_token") String access_token,
                         @Field("v") String v);

}
