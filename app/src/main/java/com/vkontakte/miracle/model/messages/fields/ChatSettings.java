package com.vkontakte.miracle.model.messages.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatSettings {

    private final String title;
    private String photo100 ="";
    private String photo200 ="";
    private int membersCount;

    public String getTitle() {
        return title;
    }

    public String getPhoto100() {
        return photo100;
    }

    public String getPhoto200() {
        return photo200;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public ChatSettings(JSONObject jsonObject) throws JSONException {

        title = jsonObject.getString("title");

        if(jsonObject.has("members_count")) {
            membersCount = jsonObject.getInt("members_count");
        }

        if(jsonObject.has("photo")){
            JSONObject photo = jsonObject.getJSONObject("photo");
            photo100 = photo.getString("photo_100");
            photo200 = photo.getString("photo_200");
        }
    }

}
