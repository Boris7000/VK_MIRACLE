package com.vkontakte.miracle.network.api.services;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IExecute {

    @FormUrlEncoded
    @POST("execute")
    Call<JSONObject> execute(@Field("code") String code,
                             @Field("access_token") String access_token,
                             @Field("v") String v);

                  @FormUrlEncoded
    @POST("execute.getFeedLikes")
    Call<JSONObject> getFeedLikes(@Field("start_from") int start_from,
                                      @FieldMap() Map<String,Object> walls_fields,
                                      @Field("access_token") String access_token,
                                      @Field("v") String v);

    @FormUrlEncoded
    @POST("execute.getNewsfeedSmart")
    Call<JSONObject> getNewsfeedSmart(@Field("start_from") String start_from,
                                      @Field("count") int count,
                                      @FieldMap() Map<String,Object> walls_fields,
                                      @Field("access_token") String access_token,
                                      @Field("v") String v);

    @FormUrlEncoded
    @POST("newsfeed.get")
    Call<JSONObject> newsFeed_get(@Field("start_from") String start_from,
                                  @Field("count") int count,
                                  @FieldMap() Map<String,Object> walls_fields,
                                  @Field("access_token") String access_token,
                                  @Field("v") String v);

    @FormUrlEncoded
    @POST("execute.getPlaylist")
    Call<JSONObject> getPlaylist(@Field("owner_id") String owner_id,
                                 @Field("id") String id,
                                 @Field("need_playlist") int need_playlist,
                                 @Field("need_owner") int need_owner,
                                 @Field("audio_offset") int audio_offset,
                                 @Field("audio_count") int audio_count,
                                 @Field("access_key") String access_key,
                                 @Field("extended") int extended,
                                 @Field("access_token") String access_token,
                                 @Field("v") String v);

    @FormUrlEncoded
    @POST("execute.getMutualFriendsExtended")
    Call<JSONObject> getMutualFriendsExtended(
                                              @Field("target_uid") String target_uid,
                                              @FieldMap() Map<String,Object> walls_fields,
                                        @Field("access_token") String access_token,
                                        @Field("v") String v);

    @FormUrlEncoded
    @POST("execute.getFullGroupNewNew")
    Call<JSONObject> getFullGroupNewNew(@Field("group_id") String group_id,
                                 @Field("func_v") int func_v,
                                 @Field("photo_sizes") int photo_sizes,
                                 @Field("skip_hidden") int skip_hidden,
                                 @Field("good_count") int good_count,
                                 @Field("need_market_albums") int need_market_albums,
                                 @Field("need_onboarding") int need_onboarding,
                                 @Field("access_token") String access_token,
                                 @Field("v") String v);


}
