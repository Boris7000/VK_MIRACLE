package com.vkontakte.miracle.network.api.services;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ISearch {
    @FormUrlEncoded
    @POST("search.addRecents")
    Call<JSONObject> addRecent(
            @Field("owner_ids") String group_ids,
            @Field("access_token") String access_token,
            @Field("v") String v);
}
