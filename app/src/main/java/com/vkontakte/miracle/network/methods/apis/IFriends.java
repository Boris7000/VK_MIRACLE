package com.vkontakte.miracle.network.methods.apis;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IFriends {

    @FormUrlEncoded
    @POST("friends.get")
    Call<JSONObject> get(@Field("count") int count,
                                 @Field("offset") int offset,
                                 @Field("order") String order,
                                 @Field("user_id") String user_id,
                                 @Field("fields") String fields,
                                 @Field("access_token") String access_token,
                                 @Field("v") String v);

    @FormUrlEncoded
    @POST("friends.getOnline")
    Call<JSONObject> getOnline(@Field("count") int count,
                         @Field("offset") int offset,
                         @Field("order") String order,
                         @Field("user_id") String user_id,
                         @Field("fields") String fields,
                         @Field("access_token") String access_token,
                         @Field("v") String v);

}
