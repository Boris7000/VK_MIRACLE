package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.photos;

public class Photos {
    public static Call<JSONObject> getAlbums(String owner_id, int count, int offset, String access_token){
        return photos().getAlbums(owner_id, count,offset,1,1,1,access_token,latest_api_v);
    }
    public static Call<JSONObject> getAll(String owner_id, int count, int offset, String access_token){
        return photos().getAll(owner_id,1,1,offset,count,0,1,0,access_token,latest_api_v);
    }

    public static Call<JSONObject> get(String owner_id, String album_id, int count, int offset, String access_token){
        return photos().get(owner_id, album_id, 1,1,offset,count,access_token,latest_api_v);
    }
}
