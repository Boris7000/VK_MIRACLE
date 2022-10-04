package com.vkontakte.miracle.executors.audio;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Audio;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class DeleteAudio extends AsyncExecutor<Boolean> {

    private final AudioItem audioItem;
    private final ProfileItem profileItem;

    public DeleteAudio(AudioItem audioItem){
        this.audioItem = audioItem;
        profileItem = StorageUtil.get().currentUser();
    }

    @Override
    public Boolean inBackground() {
        try {
            if(profileItem!=null) {
                Call<JSONObject> call = Audio.delete(
                        audioItem.getOwnerId(),
                        audioItem.getId(),
                        profileItem.getAccessToken());
                Response<JSONObject> response = call.execute();
                JSONObject jo_response = validateBody(response);
                return jo_response.getInt("response")==1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onExecute(Boolean object) {
        if(object){
            audioItem.restoreIds();
        }
    }
}
