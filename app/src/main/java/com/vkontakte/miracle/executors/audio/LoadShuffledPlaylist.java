package com.vkontakte.miracle.executors.audio;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWF;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Audio;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Response;

public class LoadShuffledPlaylist extends AsyncExecutor<PlaylistItem> {


    private final PlaylistItem playlistItem;
    private final User user;

    public LoadShuffledPlaylist(PlaylistItem playlistItem) {
        this.playlistItem = playlistItem;
        user = StorageUtil.get().currentUser();
    }

    @Override
    public PlaylistItem inBackground() {
        try {
            if(user !=null) {

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
                        50, 0, 1, seed, user.getAccessToken()).execute();

                JSONObject jo_response = validateBody(response).getJSONObject("response");

                JSONArray items = jo_response.getJSONArray("items");

                PlaylistItem playlistItem = new PlaylistItem(this.playlistItem);

                ArrayList<ItemDataHolder> audioItems = new ArrayList<>();
                AudioItemWF audioItemWF = new AudioItemWF();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject jo_item = items.getJSONObject(i);
                    AudioItem audioItem = new AudioItem(jo_item);
                    audioItems.add(audioItemWF.create(audioItem, playlistItem));
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
