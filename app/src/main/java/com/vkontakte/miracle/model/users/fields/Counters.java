package com.vkontakte.miracle.model.users.fields;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Counters implements Serializable {
    private final int friendsCount;
    private final int videosCount;
    private final int audiosCount;
    private final int photosCount;
    private final int followersCount;
    private final int pagesCount;
    private final int subscriptionsCount;
    private final int groupsCount;
    private final int giftsCount;
    private final int topicsCount;
    private final int articlesCount;

    public int getAudiosCount() {
        return audiosCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public int getGiftsCount() {
        return giftsCount;
    }

    public int getGroupsCount() {
        return groupsCount;
    }

    public int getPhotosCount() {
        return photosCount;
    }

    public int getSubscriptionsCount() {
        return subscriptionsCount + pagesCount;
    }

    public int getVideosCount() {
        return videosCount;
    }

    public int getArticlesCount() {
        return articlesCount;
    }

    public int getTopicsCount() {
        return topicsCount;
    }

    public Counters(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("audios")){
            audiosCount = jsonObject.getInt("audios");
        } else audiosCount = 0;
        if(jsonObject.has("videos")){
            videosCount = jsonObject.getInt("videos");
        } else videosCount = 0;
        if(jsonObject.has("photos")){
            photosCount = jsonObject.getInt("photos");
        } else photosCount = 0;
        if(jsonObject.has("gifts")){
            giftsCount = jsonObject.getInt("gifts");
        } else giftsCount = 0;
        if(jsonObject.has("friends")){
            friendsCount = jsonObject.getInt("friends");
        } else friendsCount = 0;
        if(jsonObject.has("followers")){
            followersCount = jsonObject.getInt("followers");
        } else followersCount = 0;
        if(jsonObject.has("subscriptions")){
            subscriptionsCount = jsonObject.getInt("subscriptions");
        } else subscriptionsCount = 0;
        if(jsonObject.has("pages")){
            pagesCount = jsonObject.getInt("pages");
        } else pagesCount = 0;
        if(jsonObject.has("groups")){
            groupsCount = jsonObject.getInt("groups");
        } else groupsCount = 0;
        if (jsonObject.has("topics")) {
            topicsCount = jsonObject.getInt("topics");
        } else topicsCount = 0;
        if (jsonObject.has("articles")) {
            articlesCount = jsonObject.getInt("articles");
        } else articlesCount = 0;
    }
}
