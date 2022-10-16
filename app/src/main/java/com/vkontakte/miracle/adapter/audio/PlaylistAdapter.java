package com.vkontakte.miracle.adapter.audio;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_BUTTON_PLAY_SHUFFLED;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST_DESCRIPTION;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.PlaylistDescriptionViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.PlaylistLargeViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.PlaylistShuffleViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorDataHolder;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWF;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.PlaylistShuffleItem;
import com.vkontakte.miracle.model.audio.fields.Description;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Execute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class PlaylistAdapter extends MiracleAsyncLoadAdapter {

    private final String playlistId;
    private final String ownerId;
    private final String accessKey;
    private PlaylistItem playlistItem;

    public PlaylistAdapter(String playlistId, String ownerId, String accessKey) {
        this.playlistId = playlistId;
        this.ownerId = ownerId;
        this.accessKey = accessKey;
    }

    @Override
    public void onLoading() throws Exception {

        ProfileItem profileItem = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        int previous = holders.size();

        JSONObject jo_response = null;
        Response<JSONObject> response = null;

        if(!loaded()) {
            Call<JSONObject> call = Execute.getPlaylist(
                    ownerId, playlistId, true, 0,
                    getStep(), accessKey, profileItem.getAccessToken());
            response = call.execute();
            jo_response = validateBody(response);

            jo_response = jo_response.getJSONObject("response");

            if(jo_response.get("playlist") instanceof Boolean){
                holders.add(new ErrorDataHolder(R.string.playlist_missing));
                setAddedCount(1);
                setFinallyLoaded(true);
                return;
            }

            JSONObject jo_playlist = jo_response.getJSONObject("playlist");

            if (playlistItem == null) {
                ArrayMap<String, GroupItem> groupsMap = null;
                ArrayMap<String, ProfileItem> profilesMap = null;
                if (jo_response.has("owner")){
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
            }

            holders.add(playlistItem);
            if(playlistItem.getDescription()!=null&&!playlistItem.getDescription().isEmpty()){
                holders.add(new Description(playlistItem.getDescription()));
            }
            holders.add(new PlaylistShuffleItem(playlistItem));
            setTotalCount(playlistItem.getCount());
        }

        if(response==null){
            response = Execute.getPlaylist(
                    ownerId, playlistId, false, getLoadedCount(),
                    getStep(), accessKey, profileItem.getAccessToken()).execute();
            jo_response = validateBody(response).getJSONObject("response");
        }

        JSONArray items = jo_response.getJSONArray("audios");

        ArrayList<ItemDataHolder> audioItems = new ArrayList<>();
        AudioItemWF audioItemWF = new AudioItemWF();
        for (int i = 0; i < items.length(); i++) {
            JSONObject jo_item = items.getJSONObject(i);
            AudioItem audioItem = new AudioItem(jo_item);
            audioItems.add(audioItemWF.create(audioItem, playlistItem));
        }

        holders.addAll(audioItems);
        playlistItem.getAudioItems().addAll(audioItems);
        setLoadedCount(getLoadedCount() + audioItems.size());

        setAddedCount(holders.size() - previous);

        if (getLoadedCount() >= getTotalCount()) {
            setFinallyLoaded(true);
        }

    }

    @Override
    public void ini() {
        super.ini();
        setStep(50);
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = super.getViewHolderFabricMap();
        arrayMap.put(TYPE_WRAPPED_AUDIO, new WrappedAudioViewHolder.Fabric());
        arrayMap.put(TYPE_PLAYLIST, new PlaylistLargeViewHolder.Fabric());
        arrayMap.put(TYPE_PLAYLIST_DESCRIPTION, new PlaylistDescriptionViewHolder.Fabric());
        arrayMap.put(TYPE_BUTTON_PLAY_SHUFFLED, new PlaylistShuffleViewHolder.Fabric());
        return arrayMap;
    }
}
