package com.vkontakte.miracle.network.api.services;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ILikes {

    @FormUrlEncoded
    @POST("likes.add")
    Call<JSONObject> add(@Field("type") String type,
                         @Field("item_id") String item_id,
                         @Field("owner_id") String owner_id,
                         @Field("access_token") String access_token,
                         @Field("v") String v);

    @FormUrlEncoded
    @POST("likes.delete")
    Call<JSONObject> delete(@Field("type") String type,
                                 @Field("item_id") String item_id,
                                 @Field("owner_id") String owner_id,
                                 @Field("access_token") String access_token,
                                 @Field("v") String v);

}
