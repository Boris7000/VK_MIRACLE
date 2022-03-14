package com.vkontakte.miracle.engine.util;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Users;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import retrofit2.Response;

public class UserDataUtil {

    public static void updateUserData(@NonNull ProfileItem profileItem) throws Exception {

        StorageUtil storageUtil = StorageUtil.get();

        ArrayList<ProfileItem> profileItems = storageUtil.loadUsers();

        int pos = profileItems.indexOf(profileItem);

        if(pos>-1){
            profileItems.remove(profileItem);
            profileItems.add(0,profileItem);
            storageUtil.saveUsers(profileItems);
        } else {
            profileItems.add(0,profileItem);
            storageUtil.saveUsers(profileItems);
            storageUtil.initializeDirectories();
        }
        downloadAndSaveAccountImage(profileItem);
    }

    public static void downloadAndSaveAccountImage(ProfileItem profileItem) throws Exception {
        if(!profileItem.getPhoto200().isEmpty()) {
            Bitmap image = BitmapFactory.decodeStream((new URL(profileItem.getPhoto200())).openConnection().getInputStream());
            StorageUtil.get().saveBitmap(image, "profileImage200.png");
        }
    }

    @NonNull
    public static ProfileItem downloadUserData(String user_id, String accessToken) throws Exception {
        Response<JSONObject> response = Users.get(user_id, "photo_200,photo_100,online,last_seen,status",accessToken).execute();

        JSONArray jsonArray = validateBody(response).getJSONArray("response");

        ProfileItem profileItem = new ProfileItem(jsonArray.getJSONObject(0));

        profileItem.setAccessToken(accessToken);

        return profileItem;
    }

    public static void removeUserData(ProfileItem profileItem){
        StorageUtil storageUtil = StorageUtil.get();
        ArrayList<ProfileItem> profileItems = storageUtil.loadUsers();
        storageUtil.removeDirectory(storageUtil.getUserCachesDir(profileItem));
        profileItems.remove(profileItem);
        storageUtil.saveUsers(profileItems);
    }

}
