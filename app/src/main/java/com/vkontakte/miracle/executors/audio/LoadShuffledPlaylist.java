package com.vkontakte.miracle.executors.audio;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.AudioWrapContainer;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Audio;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Response;

public class LoadShuffledPlaylist extends AsyncExecutor<PlaylistItem> {


    private final PlaylistItem playlistItem;
    private final ProfileItem profileItem;

    public LoadShuffledPlaylist(PlaylistItem playlistItem) {
        this.playlistItem = playlistItem;
        profileItem = StorageUtil.get().currentUser();
    }

    @Override
    public PlaylistItem inBackground() {
        try {
            if(profileItem!=null) {

                int seed = 1;
                int rand = new Random().nextInt();
                if(rand!=0){
                    seed = rand;
                }

                String playlistId;
                String ownerId;
                String accessKey;

                if(playlistItem.getOriginal()!=null) {
                    playlistId = playlistItem.getOriginal().getId();
                    accessKey = playlistItem.getOriginal().getAccessKey();
                    ownerId = playlistItem.getOriginal().getOwnerId();
                } else {
                    playlistId = playlistItem.getId();
                    accessKey = playlistItem.getAccessKey();
                    ownerId = playlistItem.getOwnerId();
                }

                Response<JSONObject> response = Audio.get(ownerId, playlistId, accessKey, 0,
                        50, 0, 1, seed, profileItem.getAccessToken()).execute();

                JSONObject jo_response = validateBody(response).getJSONObject("response");

                JSONArray items = jo_response.getJSONArray("items");

                PlaylistItem playlistItem = new PlaylistItem(this.playlistItem);

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

                playlistItem.getAudioItems().addAll(audioItems);

                return playlistItem;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
