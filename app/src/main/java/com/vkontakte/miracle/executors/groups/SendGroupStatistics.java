package com.vkontakte.miracle.executors.groups;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Groups;

public class SendGroupStatistics extends AsyncExecutor<Boolean> {

    private final String groupId;

    public SendGroupStatistics(String groupId){
        this.groupId = groupId;
    }

    @Override
    public Boolean inBackground() {
        try {
            User user = StorageUtil.get().currentUser();
            if(user!=null) {
                Groups.sendGroupStatistics(groupId,user.getAccessToken()).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
