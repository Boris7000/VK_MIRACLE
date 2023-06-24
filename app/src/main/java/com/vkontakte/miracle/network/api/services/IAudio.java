package com.vkontakte.miracle.network.api.services;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IAudio {

    @FormUrlEncoded
    @POST("audio.getCatalog")
    Call<JSONObject> getArtistCatalog(@Field("artist_id") String artist_id,
                                      @Field("extended") int extended,
                                      @Field("access_token") String access_token,
                                      @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.getCatalog")
    Call<JSONObject> getCatalog(@Field("fields") String fields,
                                @Field("extended") int extended,
                                @Field("access_token") String access_token,
                                @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.getCatalog")
    Call<JSONObject> getCatalog(@Field("query") String query,
                                @Field("fields") String fields,
                                @Field("extended") int extended,
                                @Field("access_token") String access_token,
                                @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.getCatalogBlockById")
    Call<JSONObject> getCatalogBlockById(@Field("block_id") String block_id,
                                         @Field("count") int count,
                                         @Field("start_from") String start_from,
                                         @Field("extended") int extended,
                                         @Field("access_token") String access_token,
                                         @Field("v") String v);
    @FormUrlEncoded
    @POST("audio.getCatalogBlockById")
    Call<JSONObject> getCatalogBlockById(@Field("block_id") String block_id,
                                         @Field("count") int count,
                                         @Field("extended") int extended,
                                         @Field("access_token") String access_token,
                                         @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.add")
    Call<JSONObject> add(@Field("owner_id") String owner_id,
                         @Field("audio_id") String audio_id,
                         @Field("access_token") String access_token,
                         @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.delete")
    Call<JSONObject> delete(@Field("owner_id") String owner_id,
                            @Field("audio_id") String audio_id,
                            @Field("access_token") String access_token,
                            @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.restore")
    Call<JSONObject> restore(@Field("owner_id") String owner_id,
                             @Field("audio_id") String audio_id,
                             @Field("access_token") String access_token,
                             @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.followPlaylist")
    Call<JSONObject> followPlaylist(@Field("playlist_id") String owner_id,
                                 @Field("owner_id") String playlist_id,
                                 @Field("access_key") String access_key,
                                 @Field("access_token") String access_token,
                                 @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.deletePlaylist")
    Call<JSONObject> deletePlaylist(@Field("playlist_id") String owner_id,
                                    @Field("owner_id") String playlist_id,
                                    @Field("access_key") String access_key,
                                    @Field("access_token") String access_token,
                                    @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.getById")
    Call<JSONObject> getByID(@Field("audios") String audios,
                             @Field("access_token") String access_token,
                             @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.get")
    Call<JSONObject> get(@Field("owner_id") String owner_id,
                             @Field("playlist_id") String playlist_id,
                             @Field("count") int count,
                             @Field("offset") int offset,
                             @Field("access_key") String access_key,
                             @Field("extended") int extended,
                             @Field("access_token") String access_token,
                             @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.get")
    Call<JSONObject> get(@Field("owner_id") String owner_id,
                         @Field("playlist_id") String playlist_id,
                         @Field("count") int count,
                         @Field("offset") int offset,
                         @Field("access_key") String access_key,
                         @Field("extended") int extended,
                         @Field("shuffle") int shuffle,
                         @Field("shuffle_seed") int shuffle_seed,
                         @Field("access_token") String access_token,
                         @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.getPlaylists")
    Call<JSONObject> getPlaylists(@Field("owner_id") String owner_id,
                                  @Field("count") int count,
                                  @Field("offset") int offset,
                                  @Field("extended") int extended,
                                  @Field("access_token") String access_token,
                                  @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.getPlaylistById")
    Call<JSONObject> getPlaylistById(@Field("playlist_id") String playlist_id,
                                     @Field("owner_id") String ownerId,
                                     @Field("access_key") String accessKey,
                                     @Field("extended") int extended,
                                     @Field("access_token") String access_token,
                                     @Field("v") String v);

    @FormUrlEncoded
    @POST("audio.search")
    Call<JSONObject> Search(@Field("q") String q,
                            @Field("user_id") String user_id,
                            @Field("count") int count,
                            @Field("offset") int offset,
                            @Field("search_own") int search_own,
                            @Field("performer_only") int performer_only,
                            @Field("access_token") String access_token,
                            @Field("v") String v);

}
