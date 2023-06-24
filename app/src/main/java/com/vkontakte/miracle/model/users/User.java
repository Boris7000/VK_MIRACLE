package com.vkontakte.miracle.model.users;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_PROFILE;

import androidx.annotation.Nullable;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.users.fields.LastSeen;

import java.io.Serializable;

public class User implements Serializable, ItemDataHolder {

    private final String id;
    private String firstName;
    private String lastName;
    private String photo100 = "";
    private String photo200 = "";
    private boolean online;
    public boolean verified;
    private LastSeen lastSeen;
    private String accessToken;
    private long tokenTS;

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName(){
        if(lastName.isEmpty()){
            return firstName;
        } else {
            return firstName +" "+ lastName;
        }
    }

    public String getPhoto100() {
        return photo100;
    }

    public void setPhoto100(String photo100) {
        this.photo100 = photo100;
    }

    public String getPhoto200() {
        return photo200;
    }

    public void setPhoto200(String photo200) {
        this.photo200 = photo200;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public LastSeen getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LastSeen lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getTokenTS() {
        return tokenTS;
    }

    public void setTokenTS(long tokenTS) {
        this.tokenTS = tokenTS;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof User){
            return id.equals(((User)obj).id);
        } else {
            return false;
        }
    }

    @Override
    public int getViewHolderType() {
        return TYPE_PROFILE;
    }
}
