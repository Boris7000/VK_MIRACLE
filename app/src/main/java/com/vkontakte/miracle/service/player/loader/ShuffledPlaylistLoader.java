package com.vkontakte.miracle.service.player.loader;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWC;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWF;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Audio;
import com.vkontakte.miracle.network.api.Execute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Response;

public class ShuffledPlaylistLoader implements AudioItemWCLoader{

    private PlaylistItem playlistItem;
    private final String playlistId;
    private final String ownerId;
    private final String accessKey;

    private int count;
    private int offset;

    private final int seed;

    public ShuffledPlaylistLoader(String playlistId, String ownerId, String accessKey){
        this.playlistId = playlistId;
        this.ownerId = ownerId;
        this.accessKey = accessKey;

        int rand = new Random().nextInt();
        if(rand!=0){
            this.seed = rand;
        } else {
            this.seed = 1;
        }

        count = 0;
        offset = 0;

    }


    @Override
    public ArrayList<ItemDataHolder> load() throws Exception {

        User user = StorageUtil.get().currentUser();

        if(playlistItem==null){
            Response<JSONObject> response = Execute.getPlaylist(
                    ownerId,
                    playlistId,
                    true,
                    0,
                    0,
                    accessKey,
                    user.getAccessToken()).execute();

            JSONObject jo_response = validateBody(response).getJSONObject("response");

            if(playlistItem==null){

                if(jo_response.get("playlist") instanceof Boolean){
                    throw new Exception("Playlist not found");
                }

                JSONObject jo_playlist = jo_response.getJSONObject("playlist");

                ArrayMap<String, GroupItem> groupsMap = null;
                ArrayMap<String, ProfileItem> profilesMap = null;
                if (jo_response.has("owner")) {
                    JSONObject jo_owner = jo_response.getJSONObject("owner");
                    if (jo_owner.has("name")) {
                        GroupItem owner = new GroupItem(jo_owner);
                        groupsMap = new ArrayMap<>();
                        groupsMap.put(owner.getId(), owner);

                    } else {
                        if (jo_owner.has("first_name")) {
                            ProfileItem owner = new ProfileItem(jo_owner);
                            profilesMap = new ArrayMap<>();
                            profilesMap.put(owner.getId(), owner);
                        }
                    }
                }
                playlistItem = new PlaylistItem(jo_playlist, profilesMap, groupsMap);
                count = playlistItem.getCount();
            }
        }

        Response<JSONObject> response = Audio.get(
                playlistItem.getOwnerId(),
                playlistItem.getId(),
                playlistItem.getAccessKey(),
                0,
                25,
                offset,
                1,
                seed,
                user.getAccessToken()).execute();

        JSONObject jo_response = validateBody(response).getJSONObject("response");

        JSONArray items = jo_response.getJSONArray("items");

        ArrayList<ItemDataHolder> itemDataHolders = new ArrayList<>();
        AudioItemWF audioItemWF = new AudioItemWF();
        for (int i = 0; i < items.length(); i++) {
            JSONObject jo_item = items.getJSONObject(i);
            AudioItem audioItem = new AudioItem(jo_item);
            itemDataHolders.add(audioItemWF.create(audioItem, playlistItem));
        }

        offset+=itemDataHolders.size();

        return itemDataHolders;
    }

    @Override
    public AudioItemWC getContainer() {
        return playlistItem;
    }

    @Override
    public boolean canLoadMore() {
        return offset<count;
    }
}
