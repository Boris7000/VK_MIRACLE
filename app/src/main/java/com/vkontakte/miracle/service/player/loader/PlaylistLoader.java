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
import com.vkontakte.miracle.network.api.Execute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class PlaylistLoader implements AudioItemWCLoader{

    private PlaylistItem playlistItem;
    private final String playlistId;
    private final String ownerId;
    private final String accessKey;

    private int count;
    private int offset;

    public PlaylistLoader(String playlistId, String ownerId, String accessKey){
        this.playlistId = playlistId;
        this.ownerId = ownerId;
        this.accessKey = accessKey;
    }

    public PlaylistLoader(PlaylistItem playlistItem){
        this.playlistItem = playlistItem;
        playlistId = playlistItem.getId();
        ownerId = playlistItem.getOwnerId();
        accessKey = playlistItem.getAccessKey();
        count = playlistItem.getCount();
        offset = playlistItem.getAudioItems().size();
    }

    @Override
    public ArrayList<ItemDataHolder> load() throws Exception {

        User user = StorageUtil.get().currentUser();

        Response<JSONObject> response = Execute.getPlaylist(
                ownerId,
                playlistId,
                playlistItem==null,
                offset,
                25,
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

        JSONArray items = jo_response.getJSONArray("audios");

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
