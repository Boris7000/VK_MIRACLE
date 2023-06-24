package com.vkontakte.miracle.network.api.services;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IStats {
    @FormUrlEncoded
    @POST("stats.trackEvents")
    Call<JSONObject> trackEvents(@Field("events") String events,
                                 @Field("access_token") String access_token,
                                 @Field("v") String v);
}
