package com.vkontakte.miracle.engine.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Users;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import retrofit2.Response;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.StorageUtil.removeBitmap;
import static com.vkontakte.miracle.engine.util.StorageUtil.saveBitmap;

public class UserDataUtil {

    public static void updateUserData(ProfileItem profileItem, Context context) throws Exception {

        ArrayList<ProfileItem> profileItems = StorageUtil.loadUsers(context);

        int pos = profileItems.indexOf(profileItem);

        if(pos>-1){
            ProfileItem profileItemOld = profileItems.get(pos);
            if(profileItem.getPhoto200().equals(profileItemOld.getPhoto200())&&!profileItem.getPhoto200().isEmpty()) {
                downloadAndSaveAccountImage(profileItem, context);
            }
            profileItems.remove(profileItem);
        } else {
            downloadAndSaveAccountImage(profileItem, context);
        }

        profileItems.add(0,profileItem);
        StorageUtil.saveUsers(profileItems,context);
    }

    public static void downloadAndSaveAccountImage(ProfileItem profileItem, Context context) throws Exception {
        Bitmap image = BitmapFactory.decodeStream((new URL(profileItem.getPhoto200())).openConnection().getInputStream());
        saveBitmap(image, profileItem.getId()+"_200.png",context);
    }

    public static ProfileItem downloadUserData(String user_id, String accessToken) throws Exception {
        Response<JSONObject> response = Users.get(user_id, "photo_200,photo_100,online,last_seen,status",accessToken).execute();

        JSONArray jsonArray = validateBody(response).getJSONArray("response");

        ProfileItem profileItem = new ProfileItem(jsonArray.getJSONObject(0));

        profileItem.setAccessToken(accessToken);

        return profileItem;
    }

    public static void removeUserData(String user_id, Context context){
        ArrayList<ProfileItem> profileItems = StorageUtil.loadUsers(context);
        ProfileItem profileItem = new ProfileItem(user_id);

        int pos = profileItems.indexOf(profileItem);

        if(pos>-1){
            ProfileItem profileItemOld = profileItems.get(pos);
            if(!profileItemOld.getPhoto200().isEmpty()) {
                removeBitmap(profileItemOld.getPhoto200()+"_200.png",context);
            }
            profileItems.remove(profileItem);
            StorageUtil.saveUsers(profileItems,context);
        }
    }

}
