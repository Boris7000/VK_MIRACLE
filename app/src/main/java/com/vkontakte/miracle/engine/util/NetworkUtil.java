package com.vkontakte.miracle.engine.util;

import static com.vkontakte.miracle.engine.util.StringsUtil.stringFromArrayList;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.catalog.CatalogUser;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Groups;
import com.vkontakte.miracle.network.methods.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import retrofit2.Response;

public class NetworkUtil {

    public static void CheckConnection(int timeOutMs) throws Exception {
        Socket socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);
        socket.connect(socketAddress, timeOutMs);
        socket.close();
    }

    @NonNull
    public static JSONObject validateBody(Response<JSONObject> response) throws Exception {
        if(response.errorBody()!=null||response.body()==null){
            throwError(response);
            return new JSONObject();
        }
        JSONObject jsonObject = response.body();
        if(jsonObject.has("response")&&(jsonObject.get("response") instanceof JSONObject)) {
            JSONObject responseO = jsonObject.getJSONObject("response");
            if (responseO.has("error")) {
                throwError(response);
                return new JSONObject();
            }
        }
        return jsonObject;
    }


    public static void throwError(Response<JSONObject> response) throws Exception {
        JSONObject jsonObject;
        if (response.errorBody() != null) {
            jsonObject = new JSONObject(response.errorBody().string());
            throw new Exception(jsonObject.getString("error_description"));
        } else {
            jsonObject = response.body();
            if(jsonObject!=null&&jsonObject.has("response")&&(jsonObject.get("response") instanceof JSONObject)){
                throw new Exception(jsonObject.getJSONObject("response").getString("error_description"));
            }else throw new Exception("just_error");
        }
    }

    public static void logError(Exception e){ Log.i("API_error", e.toString()); }

    public static void logError(String error){ Log.i("API_error", error); }

    public static ArrayMap<String,Owner> loadOwners(ArrayList<String> ownerIds, String accessToken) throws Exception{

        ArrayMap<String,Owner> ownerArrayMap = new ArrayMap<>();
        ArrayList<String> profilesIds = new ArrayList<>();
        ArrayList<String> groupsIds = new ArrayList<>();

        for (String s:ownerIds) {
            if(s.charAt(0)=='-'){
                groupsIds.add(s.substring(1));
            } else {
                profilesIds.add(s);
            }
        }

        if(!groupsIds.isEmpty()) {
            Response<JSONObject> response = Groups.getWithMessageFields(stringFromArrayList(groupsIds, ","), accessToken).execute();
            JSONObject jo_response = validateBody(response);
            JSONArray groups = jo_response.getJSONObject("response").getJSONArray("groups");
            for(int i=0;i < groups.length(); i++){
                GroupItem groupItem = new GroupItem(groups.getJSONObject(i));
                ownerArrayMap.put(groupItem.getId(),new Owner(groupItem));
            }
        }

        if(!profilesIds.isEmpty()) {
            Response<JSONObject> response = Users.getWithMessageFields(stringFromArrayList(profilesIds, ","), accessToken).execute();
            JSONObject jo_response = validateBody(response);
            JSONArray profiles = jo_response.getJSONArray("response");

            for (int i = 0; i < profiles.length(); i++) {
                ProfileItem profileItem = new ProfileItem(profiles.getJSONObject(i));
                ownerArrayMap.put(profileItem.getId(), new Owner(profileItem));
            }
        }


        return ownerArrayMap;
    }

    public static ArrayMap<String, Owner> createOwnersMap(JSONObject jsonObject) throws JSONException {

        ArrayMap<String,Owner> ownerArrayMap = new ArrayMap<>();

        if(jsonObject.has("profiles")){
            JSONArray profiles = jsonObject.getJSONArray("profiles");
            for(int i=0;i < profiles.length(); i++){
                ProfileItem profileItem = new ProfileItem(profiles.getJSONObject(i));
                ownerArrayMap.put(profileItem.getId(),new Owner(profileItem));
            }
        }

        if(jsonObject.has("groups")){
            JSONArray groups = jsonObject.getJSONArray("groups");
            for(int i=0;i < groups.length(); i++){
                GroupItem groupItem = new GroupItem(groups.getJSONObject(i));
                ownerArrayMap.put(groupItem.getId(),new Owner(groupItem));
            }
        }
        return ownerArrayMap;
    }

    public static ArrayMap<String, ProfileItem> createProfilesMap(JSONArray profiles) throws JSONException {
        ArrayMap<String,ProfileItem> profilesMap = new ArrayMap<>();
        for(int i=0;i < profiles.length(); i++){
            ProfileItem profileItem = new ProfileItem(profiles.getJSONObject(i));
            profilesMap.put(profileItem.getId(),profileItem);
        }
        return profilesMap;
    }

    public static ArrayMap<String, GroupItem> createGroupsMap(JSONArray groups) throws JSONException {
        ArrayMap<String,GroupItem> groupsMap = new ArrayMap<>();
        for(int i=0;i < groups.length(); i++){
            GroupItem groupItem = new GroupItem(groups.getJSONObject(i));
            groupsMap.put(groupItem.getId(),groupItem);
        }
        return groupsMap;
    }

    public static ArrayMap<String, CatalogUser> createCatalogUsersMap(JSONArray catalogUsers, ArrayMap<String,ProfileItem> profilesMap) throws JSONException {
        ArrayMap<String,CatalogUser> catalogUsersMap = new ArrayMap<>();
        for(int i=0;i < catalogUsers.length(); i++){
            CatalogUser catalogUser = new CatalogUser(catalogUsers.getJSONObject(i), profilesMap);
            catalogUsersMap.put(catalogUser.getItemId(), catalogUser);
        }
        return catalogUsersMap;
    }

    public static ArrayMap<String, AudioItem> createAudiosMap(JSONArray audios) throws JSONException {
        ArrayMap<String, AudioItem> audiosMap = new ArrayMap<>();
        for(int i=0;i < audios.length(); i++){
            AudioItem audioItem = new AudioItem(audios.getJSONObject(i));
            audiosMap.put(audioItem.getOwnerId()+"_"+audioItem.getId(), audioItem);
        }
        return audiosMap;
    }

    public static ArrayMap<String, PlaylistItem> createPlaylistsMap(JSONArray playlists, ArrayMap<String,ProfileItem> profilesMap, ArrayMap<String,GroupItem> groupsMap) throws JSONException {
        ArrayMap<String, PlaylistItem> playlistsMap = new ArrayMap<>();
        for(int i=0;i < playlists.length(); i++){
            PlaylistItem playlistItem = new PlaylistItem(playlists.getJSONObject(i), profilesMap, groupsMap);
            playlistsMap.put(playlistItem.getOwnerId()+"_"+playlistItem.getId(), playlistItem);
        }
        return playlistsMap;
    }

    public static int[] parseIntArray(JSONArray array) throws JSONException {
        int[] list = new int[array.length()];
        for (int i = 0; i < array.length(); i++) {
            list[i] = array.getInt(i);
        }
        return list;
    }

    public static String[] parseStringArray(JSONArray array) throws JSONException {
        String[] list = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            list[i] = array.getString(i);
        }
        return list;
    }

    public static ArrayList<String> parseStringArrayList(JSONArray array) throws JSONException {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }

    public static boolean hasFlag(int mask, int flag) {
        return (mask & flag) != 0;
    }

}
