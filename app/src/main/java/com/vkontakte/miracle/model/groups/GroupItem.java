package com.vkontakte.miracle.model.groups;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_GROUP;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.wall.fields.Counters;
import com.vkontakte.miracle.model.wall.fields.Cover;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupItem implements ItemDataHolder {

    private final String id;
    private final String name;
    private String photo100 = "";
    private String photo200 = "";
    private String photoMax = "";
    private String screenName = "";
    private String status = "";
    private String activity = "";
    private String description = "";

    private int membersCount = 0;

    private boolean isAdmin;
    private boolean isMember;
    private boolean isClosed;
    private boolean verified;

    private Counters counters;
    private Cover cover;

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

    public boolean isAdmin() {
        return isAdmin;
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

    public Cover getCover() {
        return cover;
    }

    public void setIsMember(boolean isMember) {
        this.isMember = isMember;
    }

    public GroupItem(String id){
        if(id.charAt(0)!='-'){
            this.id = "-"+id;
        } else {
            this.id = id;
        }
        this.name = "";
    }

    public GroupItem(String name, String id){
        if(id.charAt(0)!='-'){
            this.id = "-"+id;
        } else {
            this.id = id;
        }
        this.name = name;
    }

    public GroupItem(JSONObject jsonObject)throws JSONException {

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
        if (jsonObject.has("is_admin")){
            isAdmin = jsonObject.getInt("is_admin")==1;
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
            if(jsonObject.get("status") instanceof JSONObject){
                JSONObject jo_status = jsonObject.getJSONObject("status");
                status = jo_status.getString("text");
            } else {
                status = jsonObject.getString("status");
            }
        }
        if (jsonObject.has("description")){
            description = jsonObject.getString("description");
        }

        if(jsonObject.has("cover")){
            cover = new Cover(jsonObject.getJSONObject("cover"));
        }

        if(jsonObject.has("counters")) {
            Object counters = jsonObject.get("counters");
            if (counters instanceof JSONObject) {
                this.counters = new Counters((JSONObject) counters);
            }
        }
    }

    @Override
    public int getViewHolderType() {
        return TYPE_GROUP;
    }
}
