package com.vkontakte.miracle.network.methods.apis;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ICatalog {

    @FormUrlEncoded
    @POST("catalog.getFriends")
    Call<JSONObject> getFriends(
            @Field("owner_id") String owner_id,
            @Field("need_blocks") int need_blocks,
            @Field("access_token") String access_token,
            @Field("v") String v);

    @FormUrlEncoded
    @POST("catalog.getGroups")
    Call<JSONObject> getGroups(
            @Field("owner_id") String owner_id,
            @Field("need_blocks") int need_blocks,
            @Field("access_token") String access_token,
            @Field("v") String v);

    @FormUrlEncoded
    @POST("catalog.getAudioArtist")
    Call<JSONObject> getAudioArtist(
            @Field("artist_id") String artist_id,
            @Field("need_blocks") int need_blocks,
            @Field("access_token") String access_token,
            @Field("v") String v);

    @FormUrlEncoded
    @POST("catalog.getAudioArtist")
    Call<JSONObject> getAudioArtistFromUrl(
            @Field("url") String url,
            @Field("need_blocks") int need_blocks,
            @Field("access_token") String access_token,
            @Field("v") String v);

    @FormUrlEncoded
    @POST("catalog.getAudio")
    Call<JSONObject> getAudio(
            @Field("owner_id") String owner_id,
            @Field("need_blocks") int need_blocks,
            @Field("access_token") String access_token,
            @Field("v") String v);

    @FormUrlEncoded
    @POST("catalog.getAudio")
    Call<JSONObject> getAudio(
            @Field("need_blocks") int need_blocks,
            @Field("url") String url,
            @Field("access_token") String access_token,
            @Field("v") String v);

    @FormUrlEncoded
    @POST("catalog.getAudioSearch")
    Call<JSONObject> getAudioSearch(
            @Field("query") String query,
            @Field("need_blocks") int need_blocks,
            @Field("access_token") String access_token,
            @Field("v") String v);

    @FormUrlEncoded
    @POST("catalog.getAudioSearch")
    Call<JSONObject> getAudioContextSearch(
            @Field("context") String query,
            @Field("need_blocks") int need_blocks,
            @Field("access_token") String access_token,
            @Field("v") String v);

    @FormUrlEncoded
    @POST("catalog.getStickers")
    Call<JSONObject> getStickers(
            @Field("owner_id") String owner_id,
            @Field("need_blocks") int need_blocks,
            @Field("access_token") String access_token,
            @Field("v") String v);

     /*if (num != null) {
        Y("purchase_for", num.intValue());
    }
        if (str != null) {
        b0("ref", str);
    } */



    @FormUrlEncoded
    @POST("catalog.getBlockItems")
    Call<JSONObject> getBlockItems(@Field("block_id") String block_id,
                                   @Field("start_from") String start_from,
                                   @Field("access_token") String access_token,
                                   @Field("v") String v);
    @FormUrlEncoded
    @POST("catalog.getSection")
    Call<JSONObject> getSection(@Field("section_id") String block_id,
                                @Field("access_token") String access_token,
                                @Field("v") String v);

    @FormUrlEncoded
    @POST("catalog.getSection")
    Call<JSONObject> getSection(@Field("section_id") String block_id,
                                @Field("start_from") String start_from,
                                @Field("access_token") String access_token,
                                @Field("v") String v);
}
