package com.vkontakte.miracle.network.api.services;

import org.json.JSONObject;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ILongPoll {
    @GET
    Call<JSONObject> request(@Url String server,
                             @Query("act") String act,
                             @Query("key") String key,
                             @Query("ts") String ts,
                             @Query("wait") int wait,
                             @Query("mode") int mode,
                             @Query("version") int version);
}
