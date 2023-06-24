package com.vkontakte.miracle.executors.audio;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Audio;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class AddAudio extends AsyncExecutor<Boolean> {

    private final AudioItem audioItem;
    private final User user;
    private String newId;

    public AddAudio(AudioItem audioItem){
        this.audioItem = audioItem;
        user = StorageUtil.get().currentUser();
    }

    @Override
    public Boolean inBackground() {
        try {
            if(user !=null) {
                Call<JSONObject> call = Audio.add(audioItem.getOwnerId(), audioItem.getId(),
                        user.getAccessToken());
                Response<JSONObject> response = call.execute();
                JSONObject jo_response = validateBody(response);
                newId = jo_response.getString("response");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onExecute(Boolean object) {
        if(object){
            audioItem.setAddedIds(user.getId(),newId);
        }
    }
}
