package com.vkontakte.miracle.model.users;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_PROFILE;

import androidx.annotation.Nullable;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.users.fields.Career;
import com.vkontakte.miracle.model.users.fields.City;
import com.vkontakte.miracle.model.users.fields.LastSeen;
import com.vkontakte.miracle.model.users.fields.OnlineInfo;
import com.vkontakte.miracle.model.users.fields.RelationPartner;
import com.vkontakte.miracle.model.users.fields.University;
import com.vkontakte.miracle.model.wall.fields.Counters;
import com.vkontakte.miracle.model.wall.fields.Cover;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ProfileItem implements Serializable, ItemDataHolder {

    private final String id;
    private final String firstName;
    private final String lastName;
    private String accessToken;
    private String birthDate;
    private String countryName;
    private String homeTown;
    private String photo100 = "";
    private String photo200 = "";
    private String photoMax = "";
    private String screenName = "";
    private String status = "";

    private int sex;
    private int timeZone;
    private int relation;

    private boolean canAccess;
    private boolean isClosed;
    private boolean verified;
    private boolean online;

    private LastSeen lastSeen;
    private OnlineInfo onlineInfo;
    private City city;
    private Career career;
    private University university;
    private RelationPartner relationPartner;
    private Counters counters;
    private Cover cover;

    public String getId(){
        return id;
    }
    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getFullName(){
        if(lastName.isEmpty()){
            return firstName;
        } else {
            return firstName +" "+ lastName;
        }
    }
    public String getAccessToken(){
        return accessToken;
    }
    public String getBirthDate(){
        return birthDate;
    }
    public String getCountryName(){
        return countryName;
    }
    public String getHomeTown(){
        return homeTown;
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
    public String getScreenName(){
        return screenName;
    }
    public String getStatus(){
        return status;
    }


    public int getSex(){
        return sex;
    }
    public int getTimeZone(){
        return timeZone;
    }
    public int getRelation(){
        return relation;
    }


    public boolean isCanAccess(){
        return canAccess;
    }
    public boolean isClosed(){
        return isClosed;
    }
    public boolean isVerified(){
        return verified;
    }
    public boolean isOnline(){
        return online;
    }


    public Counters getCounters() {
        return counters;
    }
    public Cover getCover() {
        return cover;
    }
    public LastSeen getLastSeen() {
        return lastSeen;
    }
    public OnlineInfo getOnlineInfo() {
        return onlineInfo;
    }
    public City getCity() {
        return city;
    }
    public Career getCareer() {
        return career;
    }
    public University getEducation() {
        return university;
    }

    public RelationPartner getRelationPartner() {
        return relationPartner;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setOnline(boolean online) {
        this.online = online;
    }
    public void setLastSeen(LastSeen lastSeen) {
        this.lastSeen = lastSeen;
    }

    public ProfileItem(String id){
        this.id = id;
        firstName = "";
        lastName = "";
    }

    public ProfileItem(String name, String id){
        this.id = id;
        String[] names = name.split(" ");
        firstName = names[0];
        lastName = names[1];
    }

    public ProfileItem(JSONObject jsonObject)throws JSONException {

        id = jsonObject.getString("id");
        firstName = jsonObject.getString("first_name");
        lastName = jsonObject.getString("last_name");

        if(jsonObject.has("screen_name")){
            screenName = jsonObject.getString("screen_name");
        }
        if(jsonObject.has("photo_100")){
            photo100 = jsonObject.getString("photo_100");
        }
        if(jsonObject.has("photo_200")){
            photo200 = jsonObject.getString("photo_200");
        }
        if(jsonObject.has("photo_max")){
            photoMax = jsonObject.getString("photo_max");
        }

        if(jsonObject.isNull("deactivated")){

            isClosed = jsonObject.getBoolean("is_closed");
            canAccess = jsonObject.getBoolean("can_access_closed");
            if(jsonObject.has("online")){
                online = jsonObject.getInt("online") == 1;
            }
            if(jsonObject.has("verified")){
                verified = jsonObject.getInt("verified") == 1;
            }
            if(jsonObject.has("sex")){
                sex = jsonObject.getInt("sex");
            }
            if(jsonObject.has("bdate")){
                birthDate = jsonObject.getString("bdate");
            }
            if(jsonObject.has("status")){
                status = jsonObject.getString("status");
            }
            if(jsonObject.has("home_town")){
                homeTown = jsonObject.getString("home_town");
            }
            if(jsonObject.has("city")){
                city = new City(jsonObject.getJSONObject("city"));
            }
            if (jsonObject.has("career")){
                JSONArray jsonArray = jsonObject.getJSONArray("career");
                if(jsonArray.length()>0) {
                    career = new Career(jsonArray.getJSONObject(0));
                }
            }
            if (jsonObject.has("university")&&jsonObject.getInt("university")!=0){
                university = new University(jsonObject);
            }
            if(jsonObject.has("country")){
                countryName = jsonObject.getString("country");
            }
            if(jsonObject.has("timezone")){
                timeZone = jsonObject.getInt("timezone");
            }
            if(jsonObject.has("country")){
                countryName = jsonObject.getJSONObject("country").getString("title");
            }
            if(jsonObject.has("last_seen")){
                lastSeen = new LastSeen(jsonObject.getJSONObject("last_seen"));
            }
            if(jsonObject.has("online_info")){
                onlineInfo = new OnlineInfo(jsonObject.getJSONObject("online_info"));
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
            if(jsonObject.has("relation")){
                relation = jsonObject.getInt("relation");
                if(jsonObject.has("relation_partner")) {
                    relationPartner = new RelationPartner(jsonObject.getJSONObject("relation_partner"));
                }
            }
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof ProfileItem){
            return id.equals(((ProfileItem)obj).id);
        } else {
            return false;
        }
    }

    @Override
    public int getViewHolderType() {
        return TYPE_PROFILE;
    }
}
