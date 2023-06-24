package com.vkontakte.miracle.model.wall.fields;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WALL_COUNTERS;

import com.miracle.engine.adapter.holder.ItemDataHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Counters implements Serializable, ItemDataHolder{
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

    private final ArrayList<ItemDataHolder> counters = new ArrayList<>();

    public ArrayList<ItemDataHolder> getCounters() {
        return counters;
    }

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
            if(audiosCount>0) {
                counters.add(new Counter("audios", audiosCount));
            }
        } else audiosCount = 0;
        if(jsonObject.has("videos")){
            videosCount = jsonObject.getInt("videos");
            if(videosCount>0) {
                counters.add(new Counter("videos", videosCount));
            }
        } else videosCount = 0;
        if(jsonObject.has("photos")){
            photosCount = jsonObject.getInt("photos");
            if(photosCount>0) {
                counters.add(new Counter("photos", photosCount));
            }
        } else photosCount = 0;
        if(jsonObject.has("gifts")){
            giftsCount = jsonObject.getInt("gifts");
            if(giftsCount>0) {
                counters.add(new Counter("gifts", giftsCount));
            }
        } else giftsCount = 0;
        if(jsonObject.has("friends")){
            friendsCount = jsonObject.getInt("friends");
            if(friendsCount>0) {
                counters.add(new Counter("friends", friendsCount));
            }
        } else friendsCount = 0;
        if(jsonObject.has("pages")){
            pagesCount = jsonObject.getInt("pages");
            if(pagesCount>0) {
                counters.add(new Counter("pages", pagesCount));
            }
        } else pagesCount = 0;
        if(jsonObject.has("groups")){
            groupsCount = jsonObject.getInt("groups");
            if(groupsCount>0) {
                counters.add(new Counter("groups", groupsCount));
            }
        } else groupsCount = 0;
        if(jsonObject.has("subscriptions")){
            subscriptionsCount = jsonObject.getInt("subscriptions");
            if(subscriptionsCount>0) {
                counters.add(new Counter("subscriptions", subscriptionsCount));
            }
        } else subscriptionsCount = 0;
        if(jsonObject.has("followers")){
            followersCount = jsonObject.getInt("followers");
            if(followersCount>0) {
                counters.add(new Counter("followers", followersCount));
            }
        } else followersCount = 0;
        if (jsonObject.has("topics")) {
            topicsCount = jsonObject.getInt("topics");
            if(topicsCount>0) {
                counters.add(new Counter("topics", topicsCount));
            }
        } else topicsCount = 0;
        if (jsonObject.has("articles")) {
            articlesCount = jsonObject.getInt("articles");
            if(articlesCount>0) {
                counters.add(new Counter("articles", articlesCount));
            }
        } else articlesCount = 0;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_WALL_COUNTERS;
    }
}
