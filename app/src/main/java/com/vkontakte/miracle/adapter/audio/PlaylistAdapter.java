package com.vkontakte.miracle.adapter.audio;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_AUDIO;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ERROR;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_LOADING;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST_DESCRIPTION;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;

import com.vkontakte.miracle.adapter.audio.holders.PlaylistDescriptionViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.PlaylistLargeViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.AudioViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.loading.LoadingViewHolder;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Description;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Execute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
        playlistId = playlistItem.getId();
        accessKey = playlistItem.getAccessKey();
        ownerId = playlistItem.getOwnerId();
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
            if (playlistItem == null) {
                response = Execute.getPlaylist(
                        ownerId, playlistId, true, 0,
                        getStep(), accessKey, profileItem.getAccessToken()).execute();
                jo_response = validateBody(response).getJSONObject("response");
                playlistItem = new PlaylistItem(jo_response.getJSONObject("playlist"),null,null);
            } else {
                response = Execute.getPlaylist(
                        ownerId, playlistId, true, 0,
                        getStep(), accessKey, profileItem.getAccessToken()).execute();
                jo_response = validateBody(response).getJSONObject("response");

                playlistItem.update(jo_response.getJSONObject("playlist"));
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

        ArrayList<AudioItem> audioItems = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject jo_item = items.getJSONObject(i);
            AudioItem audioItem = new AudioItem(jo_item);
            audioItem.setPlaylistItem(playlistItem);
            audioItems.add(audioItem);
        }

        holders.addAll(audioItems);
        playlistItem.getItems().addAll(audioItems);

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
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.Fabric());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        arrayMap.put(TYPE_AUDIO, new AudioViewHolder.Fabric());
        arrayMap.put(TYPE_PLAYLIST, new PlaylistLargeViewHolder.Fabric());
        arrayMap.put(TYPE_PLAYLIST_DESCRIPTION, new PlaylistDescriptionViewHolder.Fabric());
        return arrayMap;
    }
}
