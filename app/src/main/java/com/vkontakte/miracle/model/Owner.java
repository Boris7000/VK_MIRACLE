package com.vkontakte.miracle.model;

import android.util.ArrayMap;

import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.ProfileItem;

import org.json.JSONException;
import org.json.JSONObject;

public class Owner {

    private String id;
    private String name;
    private String shortName;
    private String nameWithInitials;
    private String photo100;
    private String photo200;
    private GroupItem groupItem;
    private ProfileItem profileItem;
    private boolean verified;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getNameWithInitials() {
        return nameWithInitials;
    }

    public String getPhoto100() {
        return photo100;
    }

    public String getPhoto200() {
        return photo200;
    }

    public GroupItem getGroupItem() {
        return groupItem;
    }

    public ProfileItem getProfileItem() {
        return profileItem;
    }

    public boolean isVerified() {
        return verified;
    }

    public void createFromProfile(ProfileItem profileItem){
        this.profileItem = profileItem;
        id = profileItem.getId();
        name = profileItem.getFullName();
        shortName = profileItem.getFirstName();
        nameWithInitials = profileItem.getFirstName();
        if(profileItem.getLastName()!=null&&!profileItem.getLastName().isEmpty()) {
            nameWithInitials = nameWithInitials + " " + profileItem.getLastName().charAt(0) + ".";
        }
        photo100 = profileItem.getPhoto100();
        photo200 = profileItem.getPhoto200();
        verified = profileItem.isVerified();
    }

    public void createFromGroup(GroupItem groupItem){
        this.groupItem = groupItem;
        id = groupItem.getId();
        name = groupItem.getName();
        shortName = name;
        nameWithInitials = name;
        photo100 = groupItem.getPhoto100();
        photo200 = groupItem.getPhoto200();
        verified = groupItem.isVerified();
    }

    public Owner(JSONObject jsonObject,
                 ArrayMap<String,ProfileItem> profilesMap,
                 ArrayMap<String,GroupItem> groupsMap) throws JSONException {

        if(jsonObject.has("from")){
            JSONObject from = jsonObject.getJSONObject("from");
            String type = from.getString("type");
            if(type.equals("profile")){
                createFromProfile(new ProfileItem(from));
            }else {
                if(type.equals("group")){
                    createFromGroup(new GroupItem(from));
                }
            }
        }else {
            if(jsonObject.has("from_id")){
                id = jsonObject.getString("from_id");
            }else {
                if(jsonObject.has("owner_id")){
                    id = jsonObject.getString("owner_id");
                }else {
                    if(jsonObject.has("source_id")){
                        id = jsonObject.getString("source_id");
                    }else {
                        if(jsonObject.has("member_id")){
                            id = jsonObject.getString("member_id");
                        }
                    }
                }
            }
            if (id.charAt(0) != '-') {
                if (profilesMap!=null&&!profilesMap.isEmpty()) {
                    ProfileItem profileItem = profilesMap.get(id);
                    if(profileItem!=null){
                        createFromProfile(profileItem);
                    }
                }
            } else {
                if (groupsMap!=null&&!groupsMap.isEmpty()) {
                    GroupItem groupItem = groupsMap.get(id);
                    if(groupItem!=null){
                        createFromGroup(groupItem);
                    }
                }
            }
        }
    }

    public Owner(ProfileItem profileItem){
        createFromProfile(profileItem);
    }

    public Owner(GroupItem groupItem){
        createFromGroup(groupItem);
    }
}
