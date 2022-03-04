package com.vkontakte.miracle.network.methods.apis;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IWall {

    @FormUrlEncoded
    @POST("wall.get")
    Call<JSONObject> get(@Field("owner_id") String owner_id,
                         @Field("start_from") String start_from,
                         @Field("count") int count,
                         @FieldMap() Map<String,Object> walls_fields,
                         @Field("access_token") String access_token,
                         @Field("v") String v);

    @FormUrlEncoded
    @POST("wall.getById")
    Call<JSONObject> getById(@Field("posts") String posts,
                                  @FieldMap() Map<String,Object> walls_fields,
                                  @Field("access_token") String access_token,
                                  @Field("v") String v);

    @FormUrlEncoded
    @POST("wall.getComments")
    Call<JSONObject> getComments(@Field("owner_id") String owner_id,
                                      @Field("post_id") String post_id,
                                      @Field("sort") String sort,
                                      @Field("start_comment_id") String start_comment_id,
                                      @Field("thread_items_count") int thread_items_count,
                                      @Field("need_likes") int need_likes,
                                      @Field("offset") int offset,
                                      @Field("count") int count,
                                      @FieldMap() Map<String,Object> comments_fields,
                                      @Field("access_token") String access_token,
                                      @Field("v") String v);

}
