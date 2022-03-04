package com.vkontakte.miracle.model.messages;

import android.util.ArrayMap;

import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.ProfileItem;

import org.json.JSONException;
import org.json.JSONObject;

public class MemberItem {

    private Owner owner;
    private String memberId;
    private String invitedBy;
    private boolean isAdmin;
    private boolean isOwner;
    private boolean canKick;

    public Owner getOwner() {
        return owner;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public boolean canKick() {
        return canKick;
    }

    public MemberItem(JSONObject jsonObject, ArrayMap<String, ProfileItem> profilesMap, ArrayMap<String, GroupItem> groupsMap) throws JSONException {

        memberId = jsonObject.getString("member_id");

        if(jsonObject.has("invited_by")){
            invitedBy = jsonObject.getString("invited_by");
        }

        if(jsonObject.has("is_admin")){
            isAdmin = jsonObject.getBoolean("is_admin");
        }

        if(jsonObject.has("is_owner")){
            isOwner = jsonObject.getBoolean("is_owner");
        }

        if(jsonObject.has("can_kick")){
            canKick = jsonObject.getBoolean("can_kick");
        }

        owner = new Owner(jsonObject,profilesMap,groupsMap);

    }

}
