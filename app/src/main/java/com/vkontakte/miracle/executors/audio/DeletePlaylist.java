package com.vkontakte.miracle.executors.audio;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Audio;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class DeletePlaylist extends AsyncExecutor<Boolean> {

    private final PlaylistItem playlistItem;
    private final User user;

    public DeletePlaylist(PlaylistItem playlistItem){
        this.playlistItem = playlistItem;
        user = StorageUtil.get().currentUser();
    }

    @Override
    public Boolean inBackground() {
        try {

            Call<JSONObject> call = Audio.deletePlaylist(
                    playlistItem.getFollowed().getPlaylistId(),
                    playlistItem.getFollowed().getOwnerId(),
                    playlistItem.getAccessKey(),
                    user.getAccessToken());

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
