package com.vkontakte.miracle.adapter.audio;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST_DESCRIPTION;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;
import android.util.Log;

import com.vkontakte.miracle.adapter.audio.holders.PlaylistDescriptionViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.PlaylistLargeViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.AudioWrapContainer;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Description;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Execute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class PlaylistAdapter extends MiracleLoadableAdapter {

    private final String playlistId;
    private final String ownerId;
    private final String accessKey;
    private PlaylistItem playlistItem;

    public PlaylistAdapter(Album album){
        playlistId = album.getId();
        accessKey = album.getAccessKey();
        ownerId = album.getOwnerId();
    }

    public PlaylistAdapter(PlaylistItem playlistItem){
        if(playlistItem.getOriginal()!=null) {
            playlistId = playlistItem.getOriginal().getId();
            accessKey = playlistItem.getOriginal().getAccessKey();
            ownerId = playlistItem.getOriginal().getOwnerId();
        } else {
            playlistId = playlistItem.getId();
            accessKey = playlistItem.getAccessKey();
            ownerId = playlistItem.getOwnerId();
        }
        this.playlistItem = new PlaylistItem(playlistItem);
    }

    @Override
    public void onLoading() throws Exception {

        ProfileItem profileItem = getUserItem();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        int previous = holders.size();

        JSONObject jo_response = null;
        Response<JSONObject> response = null;


        if(!hasData()) {
            Call<JSONObject> call = Execute.getPlaylist(
                    ownerId, playlistId, true, 0,
                    getStep(), accessKey, profileItem.getAccessToken());
            response = call.execute();
            jo_response = validateBody(response);
            jo_response = jo_response.getJSONObject("response");
            JSONObject jo_playlist = jo_response.getJSONObject("playlist");
            if (playlistItem == null) {
                playlistItem = new PlaylistItem(jo_playlist,null,null);
            } else {
                playlistItem.update(jo_playlist);
            }
            holders.add(playlistItem);
            if(playlistItem.getDescription()!=null&&!playlistItem.getDescription().isEmpty()){
                holders.add(new Description(playlistItem.getDescription()));
            }
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
        for (int i = 0; i < items.length(); i++) {
            JSONObject jo_item = items.getJSONObject(i);
            AudioItem audioItem = new AudioItem(jo_item);
            DataItemWrap<AudioItem, AudioWrapContainer> dataItemWrap =
                    new DataItemWrap<AudioItem, AudioWrapContainer>(audioItem, playlistItem) {
                        @Override
                        public int getViewHolderType() {
                            return TYPE_WRAPPED_AUDIO;
                        }
                    };
            audioItems.add(dataItemWrap);
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
        return arrayMap;
    }
}
