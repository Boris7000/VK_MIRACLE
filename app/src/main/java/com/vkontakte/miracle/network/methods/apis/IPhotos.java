package com.vkontakte.miracle.network.methods.apis;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IPhotos {
    @FormUrlEncoded
    @POST("photos.getAlbums")
    Call<JSONObject> getAlbums(@Field("owner_id") String user_ids,
                         @Field("count") Integer count,
                         @Field("offset") Integer offset,
                         @Field("need_system") Integer need_system,
                         @Field("need_covers") Integer needCovers,
                         @Field("photo_sizes") Integer photoSizes,
                         @Field("access_token") String access_token,
                         @Field("v") String v);

    @FormUrlEncoded
    @POST("photos.getAlbums")
    Call<JSONObject> getAlbums(@Field("owner_id") String user_ids,
                               @Field("album_ids") String album_ids,
                               @Field("need_system") Integer need_system,
                               @Field("need_covers") Integer needCovers,
                               @Field("photo_sizes") Integer photoSizes,
                               @Field("access_token") String access_token,
                               @Field("v") String v);

    @FormUrlEncoded
    @POST("photos.getAll")
    Call<JSONObject> getAll(@Field("owner_id") String ownerId,
                            @Field("extended") Integer extended,
                            @Field("photo_sizes") Integer photo_sizes,
                            @Field("offset") Integer offset,
                            @Field("count") Integer count,
                            @Field("no_service_albums") Integer no_service_albums,
                            @Field("need_hidden") Integer need_hidden,
                            @Field("skip_hidden") Integer skip_hidden,
                            @Field("access_token") String access_token,
                            @Field("v") String v);

    @FormUrlEncoded
    @POST("photos.get")
    Call<JSONObject> get(@Field("owner_id") String ownerId,
                         @Field("album_id") String album_id,
                         @Field("extended") Integer extended,
                         @Field("photo_sizes") Integer photo_sizes,
                         @Field("offset") Integer offset,
                         @Field("count") Integer count,
                         @Field("access_token") String access_token,
                         @Field("v") String v);
}
