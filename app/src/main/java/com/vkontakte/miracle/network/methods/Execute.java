package com.vkontakte.miracle.network.methods;

import org.json.JSONObject;

import retrofit2.Call;

import static com.vkontakte.miracle.network.Constants.current_api_v;
import static com.vkontakte.miracle.network.Constants.defaultWallFields;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.execute;

public class Execute {

    public static Call<JSONObject> getPlaylist(String owner_id, String id, boolean need_playlist,int audio_offset, int audio_count, String access_key, String access_token){
        return execute().getPlaylist(owner_id, id, need_playlist?1:0, need_playlist?1:0, audio_offset, audio_count, access_key, access_token,  current_api_v);
    }

    public static Call<JSONObject> executePlaylist(String owner_id, String id,int audio_offset, int audio_count, String access_key, String access_token){
        return execute().executePlaylist("var owner_id = Args.owner_id;\n" +
                        "var playlist_id = Args.playlist_id;\n" +
                        "var access_key = Args.access_key;\n" +
                        "var count = Args.count;\n" +
                        "var offset = Args.offset;\n" +
                        "var fields = Args.fields;\n\n" +
                        "var res = {};\n\n" +
                        "if (offset == 0) {\n" +
                        "    res.playlist = API.audio.getPlaylistById({\n" +
                        "        owner_id: owner_id,\n        " +
                        "playlist_id: playlist_id,\n        " +
                        "access_key: access_key,\n        " +
                        "ref: Args.ref\n" +
                        "    });\n    \n" +
                        "    if (res.playlist.type == 0) {\n" +
                        "        if (owner_id < 0) {\n" +
                        "            res.owner = API.groups.getById({ group_id: -owner_id, fields: fields }).groups[0];\n" +
                        "        } else {\n" +
                        "            res.owner = API.users.get({ user_ids: owner_id, fields: fields })[0];\n" +
                        "        }\n    }\n" +
                        "    if (res.playlist.original != null) {\n" +
                        "        if (res.playlist.original.owner_id < 0) {\n" +
                        "            res.owner = API.groups.getById({ group_id: -res.playlist.original.owner_id, fields: fields }).groups[0];\n" +
                        "        } else {\n" +
                        "            res.owner = API.users.get({ user_ids: res.playlist.original.owner_id, fields: fields })[0];\n" +
                        "        }\n    }\n}" +
                        "\n\nres.audios = API.audio.get({ owner_id: owner_id, playlist_id: playlist_id, access_key: access_key, count: count, offset: offset, ref: Args.ref }).items;" +
                        "\nreturn res;\n",
                owner_id, id, audio_offset, audio_count, access_key, access_token, latest_api_v);
    }

    public static Call<JSONObject> getMusicPage(int count, int offset,boolean need_playlists, String owner_id, String access_token){
        if(need_playlists) return  execute().getMusicPage(owner_id, count, offset, 1, 12, access_token, 3, latest_api_v);
        else return  execute().getMusicPage(owner_id, count, offset, 0, access_token, 3, latest_api_v);
    }

    public static Call<JSONObject> getNewsfeedSmart(boolean need_stories, String start_from, int count, String access_token){
        if (need_stories) return execute().getNewsfeedSmart(start_from,count,defaultWallFields(),access_token,latest_api_v);
        else return execute().newsFeed_get(start_from,count,defaultWallFields(),access_token,latest_api_v);
    }

    public static Call<JSONObject> getFeedLikes(int start_from, String access_token){
        return execute().getFeedLikes(start_from,defaultWallFields(),access_token,latest_api_v);
    }
}
