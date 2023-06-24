package com.vkontakte.miracle.engine.util;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Users;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import retrofit2.Response;

public class UserDataUtil {

    public static void updateUserData(@NonNull User user) throws Exception {

        StorageUtil storageUtil = StorageUtil.get();

        ArrayList<User> users = storageUtil.loadUsers();

        int pos = users.indexOf(user);

        if(pos>-1){
            users.remove(user);
            users.add(0,user);
            storageUtil.saveUsers(users);
        } else {
            users.add(0,user);
            storageUtil.saveUsers(users);
            storageUtil.initializeDirectories();
        }
        downloadAndSaveAccountImage(user);
    }

    public static void downloadAndSaveAccountImage(User user) throws Exception {
        if(!user.getPhoto200().isEmpty()) {
            Bitmap image = BitmapFactory.decodeStream((new URL(user.getPhoto200())).openConnection().getInputStream());
            StorageUtil.get().saveBitmap(image, "userImage200.png");
        }
    }

    @NonNull
    public static User downloadUserData(String user_id, String accessToken) throws Exception {
        Response<JSONObject> response = Users.get(user_id, "photo_200,photo_100,online,last_seen,status",accessToken).execute();

        JSONArray jsonArray = validateBody(response).getJSONArray("response");

        ProfileItem profileItem = new ProfileItem(jsonArray.getJSONObject(0));

        User user = new User(profileItem.getId());
        user.setFirstName(profileItem.getFirstName());
        user.setLastName(profileItem.getLastName());
        user.setAccessToken(accessToken);
        user.setPhoto100(profileItem.getPhoto100());
        user.setPhoto200(profileItem.getPhoto200());
        user.setOnline(profileItem.isOnline());
        user.setLastSeen(profileItem.getLastSeen());

        return user;
    }

    public static void removeUserData(User user){
        StorageUtil storageUtil = StorageUtil.get();
        ArrayList<User> users = storageUtil.loadUsers();
        storageUtil.removeDirectory(storageUtil.getUserCachesDir(user));
        users.remove(user);
        storageUtil.saveUsers(users);
    }

}
