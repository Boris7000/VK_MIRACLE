package com.vkontakte.miracle.login;

import static com.vkontakte.miracle.engine.util.LogTags.LOGIN_TAG;
import static com.vkontakte.miracle.engine.util.NetworkUtil.CheckConnection;

import android.util.Log;

import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.util.UserDataUtil;
import com.vkontakte.miracle.model.users.ProfileItem;

import java.util.ArrayList;

public class UpdateCurrentUserData extends AsyncExecutor<Boolean> {

    private ProfileItem profileItemNew;
    private final onCompleteListener onCompleteListener;

    public UpdateCurrentUserData(){
        this(null);
    }

    public UpdateCurrentUserData(onCompleteListener onCompleteListener){
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    public Boolean inBackground() {

        try {

            CheckConnection(3500);

            ArrayList<ProfileItem> profileItems = StorageUtil.get().loadUsers();

            ProfileItem profileItemOld = profileItems.get(0);

            profileItemNew = UserDataUtil.downloadUserData(profileItemOld.getId(), profileItemOld.getAccessToken());

            boolean hasChanges = !profileItemNew.getPhoto200().equals(profileItemOld.getPhoto200())||
                    !profileItemNew.getFullName().equals(profileItemOld.getFullName())||
                    !profileItemNew.getStatus().equals(profileItemOld.getStatus())||
                    profileItemNew.isOnline()!=profileItemOld.isOnline()||
                    profileItemNew.getLastSeen().getPlatform()!=profileItemOld.getLastSeen().getPlatform()||
                    profileItemNew.getLastSeen().getTime()!=profileItemOld.getLastSeen().getTime();

            if(hasChanges){
                UserDataUtil.updateUserData(profileItemNew);
            }

            return hasChanges;

        } catch (Exception e) {
            String eString = e.toString();
            Log.d(LOGIN_TAG,eString);
        }
        return false;
    }

    @Override
    public void onExecute(Boolean object) {
        if(onCompleteListener!=null){
            onCompleteListener.onComplete(profileItemNew, object);
        }
    }

    public interface onCompleteListener{
        void onComplete(ProfileItem profileItem, boolean hasChanges);
    }
}
