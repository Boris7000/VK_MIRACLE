package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.defaultMessagesFields;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.message;

public class Message {
    public static Call<JSONObject> getHistory(String peer_id,String start_message_id ,int offset, int count, String access_token){
        return message().getHistory(peer_id, start_message_id, offset, count,
                defaultMessagesFields(), access_token, latest_api_v);
    }

    public static Call<JSONObject> getConversations(int offset, int count, String filter, String access_token){
        return message().getConversations(offset, count, filter, defaultMessagesFields(), access_token, latest_api_v);
    }

    public static Call<JSONObject> getConversationById(String peer_ids, String access_token){
        return message().getConversationsById(peer_ids, defaultMessagesFields(), access_token, latest_api_v);
    }

    public static Call<JSONObject> getConversationMembers(String peer_id, int offset, int count, String access_token){
        return message().getConversationMembers(peer_id, offset, count, defaultMessagesFields(), access_token, latest_api_v);
    }

    public static Call<JSONObject> getById(String messageIds, String access_token){
        return message().getById(messageIds, defaultMessagesFields(), access_token, latest_api_v);
    }
}
