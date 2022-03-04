package com.vkontakte.miracle.network.methods.apis;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IGroups {

    @FormUrlEncoded
    @POST("groups.getById")
    Call<JSONObject> get(@Field("group_ids") String group_ids,
                         @Field("fields") String fields,
                         @Field("access_token") String access_token,
                         @Field("v") String v);

    @FormUrlEncoded
    @POST("groups.get")
    Call<JSONObject> get(@Field("count") int count,
                         @Field("offset") int offset,
                         @Field("user_id") String user_id,
                         @Field("extended") int extended,
                         @Field("fields") String fields,
                         @Field("access_token") String access_token,
                         @Field("v") String v);

    @FormUrlEncoded
    @POST("groups.removeRecents")
    Call<JSONObject> removeRecents(@Field("access_token") String access_token,
                                   @Field("v") String v);
}
