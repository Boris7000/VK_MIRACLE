package com.vkontakte.miracle.executors.util;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Utils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class ResolveScreenName extends AsyncExecutor<String[]> {

    private final String screen_name;
    private final ProfileItem profileItem;

    public ResolveScreenName(String screen_name) {
        this.screen_name = screen_name;
        profileItem = StorageUtil.get().currentUser();
    }


    @Override
    public String[] inBackground() {
        try {
            if(profileItem!=null) {
                Call<JSONObject> call = Utils.resolveScreenName(screen_name, profileItem.getAccessToken());
                Response<JSONObject> response = call.execute();
                JSONObject jo_response = validateBody(response);

                Object object = jo_response.get("response");
                if(object instanceof JSONObject){
                    jo_response = (JSONObject) object;
                    return new String[]{jo_response.getString("object_id"),
                            jo_response.getString("type")};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
