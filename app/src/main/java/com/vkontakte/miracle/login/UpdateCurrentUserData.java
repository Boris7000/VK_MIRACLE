package com.vkontakte.miracle.login;

import android.util.Log;

import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.util.UserDataUtil;
import com.vkontakte.miracle.model.users.ProfileItem;

public class UpdateCurrentUserData extends AsyncExecutor<Boolean> {

    public static final String LOGIN_TAG = "UpdateCurrentUserData";
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

            ProfileItem profileItemOld = StorageUtil.get().currentUser();

            if (profileItemOld != null){

                profileItemNew = UserDataUtil.downloadUserData(profileItemOld.getId(), profileItemOld.getAccessToken());

                boolean hasChanges = !profileItemNew.getPhoto200().equals(profileItemOld.getPhoto200()) ||
                        !profileItemNew.getFullName().equals(profileItemOld.getFullName()) ||
                        !profileItemNew.getStatus().equals(profileItemOld.getStatus()) ||
                        profileItemNew.isOnline() != profileItemOld.isOnline() ||
                        profileItemNew.getLastSeen().getPlatform() != profileItemOld.getLastSeen().getPlatform() ||
                        profileItemNew.getLastSeen().getTime() != profileItemOld.getLastSeen().getTime();

                if (hasChanges) {
                    UserDataUtil.updateUserData(profileItemNew);
                }

                return hasChanges;
            }

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
