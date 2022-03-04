package com.vkontakte.miracle.network.methods.apis;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface IExecute {

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
    @POST("execute.getMusicPage")
    Call<JSONObject> getMusicPage(@Field("owner_id") String owner_id,
                                  @Field("audio_count") int audio_count,
                                  @Field("audio_offset") int audio_offset,
                                  @Field("need_playlists") int need_playlists,
                                  @Field("playlists_count") int playlists_count,
                                  @Field("access_token") String access_token,
                                  @Field("func_v") int func_v,
                                  @Field("v") String v,
                                  @HeaderMap Map<String,String> headers);

    @FormUrlEncoded
    @POST("execute.getMusicPage")
    Call<JSONObject> getMusicPage(@Field("owner_id") String owner_id,
                                  @Field("audio_count") int audio_count,
                                  @Field("audio_offset") int audio_offset,
                                  @Field("need_playlists") int need_playlists,
                                  @Field("access_token") String access_token,
                                  @Field("func_v") int func_v,
                                  @Field("v") String v,
                                  @HeaderMap Map<String,String> headers);

    @FormUrlEncoded
    @POST("execute.getPlaylist")
    Call<JSONObject> getPlaylist(@Field("owner_id") String owner_id,
                                 @Field("id") String id,
                                 @Field("need_playlist") int need_playlist,
                                 @Field("need_owner") int need_owner,
                                 @Field("audio_offset") int audio_offset,
                                 @Field("audio_count") int audio_count,
                                 @Field("access_key") String access_key,
                                 @Field("access_token") String access_token,
                                 @Field("v") String v,
                                 @HeaderMap Map<String,String> headers);

}
