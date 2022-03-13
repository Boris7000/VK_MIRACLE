package com.vkontakte.miracle.network.methods.apis;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IMessage {

    @FormUrlEncoded
    @POST("messages.getHistory")
    Call<JSONObject> getHistory(@Field("peer_id") String peer_id,
                                         @Field("start_message_id") String start_message_id,
                                         @Field("offset") int offset,
                                         @Field("count") int count,
                                         @FieldMap() Map<String,Object> messages_fields,
                                         @Field("access_token") String access_token,
                                         @Field("v") String v);

    @FormUrlEncoded
    @POST("messages.getConversations")
    Call<JSONObject> getConversations(@Field("offset") int offset,
                                               @Field("count") int count,
                                               @Field("filter") String filter,
                                               @FieldMap() Map<String,Object> messages_fields,
                                               @Field("access_token") String access_token,
                                               @Field("v") String v);

    @FormUrlEncoded
    @POST("messages.getConversationsById")
    Call<JSONObject> getConversationsById(@Field("peer_ids") String peer_ids,
                                      @FieldMap() Map<String,Object> messages_fields,
                                      @Field("access_token") String access_token,
                                      @Field("v") String v);

    @FormUrlEncoded
    @POST("messages.getConversationMembers")
    Call<JSONObject> getConversationMembers(@Field("peer_id") String peer_id,
                                             @Field("offset") int offset,
                                             @Field("count") int count,
                                             @FieldMap() Map<String,Object> messages_fields,
                                             @Field("access_token") String access_token,
                                             @Field("v") String v);

    @FormUrlEncoded
    @POST("messages.send")
    Call<JSONObject> send(@Field("peer_id") String peer_id,
                                   @Field("message") String message,
                                   @Field("attachment") String attachment,
                                   @Field("stickerId") String stickerId,
                                   @Field("random_id") Long random_id,
                                   @Field("access_token") String access_token,
                                   @Field("v") String v);


    @FormUrlEncoded
    @POST("messages.getById")
    Call<JSONObject> getById(@Field("message_ids") String message_ids,
                                      @FieldMap() Map<String,Object> messages_fields,
                                      @Field("access_token") String access_token,
                                      @Field("v") String v);

    @FormUrlEncoded
    @POST("messages.getLongPollServer")
    Call<JSONObject> getLongPollServer(@Field("need_pts") int needPts,
                                       @Field("lp_version") int lpVersion,
                                       @Field("access_token") String access_token,
                                       @Field("v") String v);

}
