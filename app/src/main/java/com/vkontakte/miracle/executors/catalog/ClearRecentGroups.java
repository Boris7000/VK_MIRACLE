package com.vkontakte.miracle.executors.catalog;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Groups;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class ClearRecentGroups extends AsyncExecutor<Boolean> {

    private final User user;

    public ClearRecentGroups(){
        user = StorageUtil.get().currentUser();
    }

    @Override
    public Boolean inBackground() {
        try {
            Call<JSONObject> call = Groups.removeRecents(user.getAccessToken());
            Response<JSONObject> response = call.execute();
            validateBody(response);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
