package com.vkontakte.miracle.executors.groups;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Groups;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class JoinToGroup extends AsyncExecutor<Boolean> {

    private final GroupItem groupItem;
    private final User user;

    public JoinToGroup(GroupItem groupItem){
        this.groupItem = groupItem;
        user = StorageUtil.get().currentUser();
    }

    @Override
    public Boolean inBackground() {
        try {
            Call<JSONObject> call = Groups.join(
                    groupItem.getId().replace("-",""),
                    0,
                    user.getAccessToken());
            Response<JSONObject> response = call.execute();
            validateBody(response);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onExecute(Boolean object) {
        if(object){
            groupItem.setIsMember(true);
        }
    }
}
