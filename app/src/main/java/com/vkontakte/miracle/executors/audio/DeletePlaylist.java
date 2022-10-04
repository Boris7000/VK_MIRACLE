package com.vkontakte.miracle.executors.audio;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Audio;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class DeletePlaylist extends AsyncExecutor<Boolean> {

    private final PlaylistItem playlistItem;
    private final ProfileItem profileItem;

    public DeletePlaylist(PlaylistItem playlistItem){
        this.playlistItem = playlistItem;
        profileItem = StorageUtil.get().currentUser();
    }

    @Override
    public Boolean inBackground() {
        try {

            Call<JSONObject> call = Audio.deletePlaylist(
                    playlistItem.getFollowed().getPlaylistId(),
                    playlistItem.getFollowed().getOwnerId(),
                    playlistItem.getAccessKey(),
                    profileItem.getAccessToken());

            Response<JSONObject> response = call.execute();

            JSONObject jo_response = validateBody(response);

            return jo_response.getInt("response")==1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onExecute(Boolean object) {
        if(object){
            playlistItem.setFollowing(false);
            playlistItem.setFollowed(null);
        }
    }
}
