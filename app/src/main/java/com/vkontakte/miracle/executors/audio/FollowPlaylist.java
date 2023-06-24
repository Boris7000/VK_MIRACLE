package com.vkontakte.miracle.executors.audio;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Followed;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Audio;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class FollowPlaylist extends AsyncExecutor<Boolean> {

    private final PlaylistItem playlistItem;
    private final User user;
    private Followed followed;

    public FollowPlaylist(PlaylistItem playlistItem){
        this.playlistItem = playlistItem;
        user = StorageUtil.get().currentUser();
    }

    @Override
    public Boolean inBackground() {
        try {
            Call<JSONObject> call;
            if(playlistItem.getOriginal()==null){
                call = Audio.followPlaylist(
                        playlistItem.getId(),
                        playlistItem.getOwnerId(),
                        playlistItem.getAccessKey(),
                        user.getAccessToken());
            } else {
                call = Audio.followPlaylist(
                        playlistItem.getOriginal().getId(),
                        playlistItem.getOriginal().getOwnerId(),
                        playlistItem.getOriginal().getAccessKey(),
                        user.getAccessToken());
            }
            Response<JSONObject> response = call.execute();
            JSONObject jsonObject = validateBody(response);
            followed = new Followed(jsonObject.getJSONObject("response"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onExecute(Boolean object) {
        if(object){
            playlistItem.setFollowing(true);
            playlistItem.setFollowed(followed);
        }
    }
}
