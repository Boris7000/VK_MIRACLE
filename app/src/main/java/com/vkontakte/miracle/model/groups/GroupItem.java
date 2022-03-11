package com.vkontakte.miracle.model.groups;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_GROUP;

import android.util.Log;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.users.fields.Counters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GroupItem implements ItemDataHolder {

    private final String id;
    private final String name;
    private String photo100 = "";
    private String photo200 = "";
    private String photoMax = "";
    private String coverUrl = "";
    private String screenName = "";
    private String status = "";
    private String activity = "";
    private String description = "";

    private int membersCount = 0;

    private boolean isMember;
    private boolean isClosed;
    private boolean verified;

    private Counters counters;

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getPhoto100(){
        return photo100;
    }
    public String getPhoto200(){
        return photo200;
    }
    public String getPhotoMax(){
        return photoMax;
    }
    public String getCoverUrl(){
        return coverUrl;
    }
    public String getScreenName() {
        return screenName;
    }
    public String getStatus(){
        return status;
    }
    public String getActivity() {
        return activity;
    }
    public String getDescription() {
        return description;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public boolean isMember() {
        return isMember;
    }
    public boolean isClosed() {
        return isClosed;
    }
    public boolean isVerified() {
        return verified;
    }

    public Counters getCounters() {
        return counters;
    }


    public GroupItem(String id){
        this.id = id;
        this.name = "";
    }

    public GroupItem(String name, String id){
        this.id = id;
        this.name = name;
    }

    public GroupItem(JSONObject jsonObject)throws JSONException {

        Log.d("roorkgorkg",jsonObject.toString());

        String id = jsonObject.getString("id");
        if(id.charAt(0)!='-'){
            this.id = "-"+id;
        } else {
            this.id = id;
        }

        name = jsonObject.getString("name");

        if(jsonObject.has("screen_name")) {
            screenName = jsonObject.getString("screen_name");
        }

        if(jsonObject.has("deactivated")){
            return;
        }

        if(jsonObject.has("is_closed")){
            isClosed = jsonObject.getInt("is_closed")==1;
        }
        if (jsonObject.has("is_member")){
            isMember = jsonObject.getInt("is_member")==1;
        }
        if (jsonObject.has("verified")){
            verified = jsonObject.getInt("verified") == 1;
        }
        if (jsonObject.has("members_count")){
            membersCount = jsonObject.getInt("members_count");
        }
        if (jsonObject.has("photo_100")){
            photo100 = jsonObject.getString("photo_100");
        }
        if (jsonObject.has("photo_200")){
            photo200 = jsonObject.getString("photo_200");
        }
        if (jsonObject.has("photo_max")){
            photoMax = jsonObject.getString("photo_max");
        }
        if (jsonObject.has("activity")){
            activity = jsonObject.getString("activity");
        }
        if (jsonObject.has("status")){
            status = jsonObject.getString("status");
        }
        if (jsonObject.has("description")){
            description = jsonObject.getString("description");
        }

        if(jsonObject.has("counters")) {
            Object counters = jsonObject.get("counters");
            if (counters instanceof JSONObject) {
                this.counters = new Counters((JSONObject) counters);
            }
        }

        if(jsonObject.has("cover")){
            JSONObject cover = jsonObject.getJSONObject("cover");
            if(cover.getInt("enabled") == 1){
                if(cover.has("images")){
                    JSONArray images = cover.getJSONArray("images");
                    JSONObject image = images.getJSONObject(images.length()-1);
                    coverUrl = image.getString("url");
                }
            }
        }
    }

    @Override
    public int getViewHolderType() {
        return TYPE_GROUP;
    }
}
