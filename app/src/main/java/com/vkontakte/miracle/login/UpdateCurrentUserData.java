package com.vkontakte.miracle.login;

import android.util.Log;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.util.UserDataUtil;
import com.vkontakte.miracle.model.users.User;

public class UpdateCurrentUserData extends AsyncExecutor<Boolean> {

    public static final String LOGIN_TAG = "UpdateCurrentUserData";
    private User userNew;
    private final OnCompleteListener onCompleteListener;

    public UpdateCurrentUserData(){
        this(null);
    }

    public UpdateCurrentUserData(OnCompleteListener onCompleteListener){
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    public Boolean inBackground() {

        try {

            User userOld = StorageUtil.get().currentUser();

            if (userOld != null){

                userNew = UserDataUtil.downloadUserData(userOld.getId(), userOld.getAccessToken());

                boolean hasChanges = !userNew.getPhoto200().equals(userOld.getPhoto200()) ||
                        !userNew.getFullName().equals(userOld.getFullName()) ||
                        userNew.isOnline() != userOld.isOnline() ||
                        userNew.getLastSeen().getPlatform() != userOld.getLastSeen().getPlatform() ||
                        userNew.getLastSeen().getTime() != userOld.getLastSeen().getTime();

                if (hasChanges) {
                    UserDataUtil.updateUserData(userNew);
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
            onCompleteListener.onComplete(userNew, object);
        }
    }

    public interface OnCompleteListener{
        void onComplete(User user, boolean hasChanges);
    }
}
