package com.vkontakte.miracle.model.audio;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST;

import android.util.ArrayMap;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.audio.fields.Artist;
import com.vkontakte.miracle.model.audio.fields.Followed;
import com.vkontakte.miracle.model.audio.fields.Genre;
import com.vkontakte.miracle.model.audio.fields.Original;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.ProfileItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaylistItem implements ItemDataHolder {

    private final String id;
    private final String ownerId;
    private final int type;
    private String title;
    private String description;
    private int count;
    private int followers;
    private int plays;
    private final long createTime;
    private long updateTime;
    private boolean isFollowing;
    private Original original;
    private Followed followed;
    private Photo photo;
    private final String accessKey;
    private boolean isExplicit;
    private final String albumType;

    private ArrayList<Artist> mainArtists = new ArrayList<>();
    private ArrayList<Artist> featuredArtists = new ArrayList<>();
    private ArrayList<Artist> artists = new ArrayList<>();
    private ArrayList<Genre> genres = new ArrayList<>();
    private ArrayList<ItemDataHolder> items = new ArrayList<>();

    private Owner owner;
    private String subtitle;
    private String year;
    private String genresString;

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getCount() {
        return count;
    }

    public int getFollowers() {
        return followers;
    }

    public int getPlays() {
        return plays;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public Original getOriginal() {
        return original;
    }

    public Owner getOwner() {
        return owner;
    }

    public Followed getFollowed() {
        return followed;
    }

    public Photo getPhoto() {
        return photo;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public boolean isExplicit() {
        return isExplicit;
    }

    public String getAlbumType() {
        return albumType;
    }

    public ArrayList<Artist> getMainArtists() {
        return mainArtists;
    }

    public ArrayList<Artist> getFeaturedArtists() {
        return featuredArtists;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public String getYear() {
        return year;
    }

    public String getGenresString() {
        return genresString;
    }

    public ArrayList<ItemDataHolder> getItems() {
        return items;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public void setFollowed(Followed followed) {
        this.followed = followed;
    }

    public PlaylistItem(JSONObject jsonObject, ArrayMap<String, ProfileItem> profilesMap, ArrayMap<String, GroupItem> groupsMap) throws JSONException {

        id = jsonObject.getString("id");
        ownerId = jsonObject.getString("owner_id");
        type = jsonObject.getInt("type");
        title = jsonObject.getString("title");
        description = jsonObject.getString("description");
        count = jsonObject.getInt("count");
        followers = jsonObject.getInt("followers");
        plays = jsonObject.getInt("plays");
        createTime = jsonObject.getLong("create_time");
        updateTime = jsonObject.getLong("update_time");

        isFollowing = jsonObject.getBoolean("is_following");

        if(jsonObject.has("original")){
            original = new Original(jsonObject.getJSONObject("original"));
        }

        if(jsonObject.has("followed")){
            followed = new Followed(jsonObject.getJSONObject("followed"));
        }

        if(jsonObject.has("photo")){
            photo = new Photo(jsonObject.getJSONObject("photo"));
        }

        accessKey = jsonObject.getString("access_key");

        albumType = jsonObject.getString("album_type");

        switch (type){
            case 0:{
                isExplicit = false;
                year = "";
                genresString = "";

                if(original!=null){
                    if (original.getOwnerId().charAt(0) == '-') {
                        if(groupsMap!=null) {
                            GroupItem groupItem = groupsMap.get(original.getOwnerId());
                            if (groupItem != null) {
                                subtitle = groupItem.getName();
                                owner = new Owner(groupItem);
                            }
                        }
                    } else {
                        if(profilesMap!=null) {
                            ProfileItem profileItem = profilesMap.get(original.getOwnerId());
                            if (profileItem != null) {
                                subtitle = profileItem.getFullName();
                                owner = new Owner(profileItem);
                            }
                        }
                    }
                } else {
                    if (ownerId.charAt(0) == '-') {
                        if(groupsMap!=null) {
                            GroupItem groupItem = groupsMap.get(ownerId);
                            if (groupItem != null) {
                                subtitle = groupItem.getName();
                                owner = new Owner(groupItem);
                            }
                        }
                    } else {
                        if(profilesMap!=null) {
                            ProfileItem profileItem = profilesMap.get(ownerId);
                            if (profileItem != null) {
                                subtitle = profileItem.getFullName();
                                owner = new Owner(profileItem);
                            }
                        }
                    }
                }
                break;
            }
            case 1:{
                isExplicit = jsonObject.getBoolean("is_explicit");
                year = jsonObject.getString("year");

                if(jsonObject.has("genres")){
                    StringBuilder stringBuilder = new StringBuilder();
                    JSONArray ja_genres = jsonObject.getJSONArray("genres");

                    for(int i=0; i<ja_genres.length();){
                        Genre genre = new Genre(ja_genres.getJSONObject(i));
                        genres.add(genre);
                        stringBuilder.append(genre.getName());
                        if (++i < ja_genres.length()) {
                            stringBuilder.append(", ");
                        } else {
                            break;
                        }
                    }

                    genresString = stringBuilder.toString();
                }

                switch (albumType) {
                    case "main_feat": {
                        StringBuilder stringBuilder = new StringBuilder();
                        JSONArray main_artists = jsonObject.getJSONArray("main_artists");

                        for(int i=0; i<main_artists.length();){
                            Artist artist = new Artist(main_artists.getJSONObject(i));
                            if(artist.getId()!=null){
                                mainArtists.add(artist);
                            }
                            stringBuilder.append(artist.getName());
                            if (++i < main_artists.length()) {
                                stringBuilder.append(", ");
                            } else {
                                break;
                            }
                        }

                        artists.addAll(mainArtists);

                        if (jsonObject.has("featured_artists")) {
                            stringBuilder.append(" feat. ");
                            JSONArray featured_artists = jsonObject.getJSONArray("featured_artists");

                            for(int i=0; i<featured_artists.length();){
                                Artist artist = new Artist(featured_artists.getJSONObject(i));
                                if(artist.getId()!=null){
                                    featuredArtists.add(artist);
                                }
                                stringBuilder.append(artist.getName());
                                if (++i < featured_artists.length()) {
                                    stringBuilder.append(", ");
                                } else {
                                    break;
                                }
                            }
                            artists.addAll(featuredArtists);
                        }
                        subtitle = stringBuilder.toString();
                        break;
                    }
                    case "collection":
                    case "main_only": {

                        StringBuilder stringBuilder = new StringBuilder();
                        JSONArray main_artists = jsonObject.getJSONArray("main_artists");

                        for(int i=0; i<main_artists.length();){
                            Artist artist = new Artist(main_artists.getJSONObject(i));
                            if(artist.getId()!=null){
                                mainArtists.add(artist);
                            }
                            stringBuilder.append(artist.getName());
                            if (++i < main_artists.length()) {
                                stringBuilder.append(", ");
                            }
                        }

                        artists.addAll(mainArtists);
                        subtitle = stringBuilder.toString();
                        break;
                    }
                }

                break;
            }
        }
    }

    public void update(JSONObject jsonObject) throws JSONException{
        title = jsonObject.getString("title");
        description = jsonObject.getString("description");
        count = jsonObject.getInt("count");
        followers = jsonObject.getInt("followers");
        plays = jsonObject.getInt("plays");
        updateTime = jsonObject.getLong("update_time");

        isFollowing = jsonObject.getBoolean("is_following");

        if(jsonObject.has("followed")){
            followed = new Followed(jsonObject.getJSONObject("followed"));
        }

        if(jsonObject.has("photo")){
            photo = new Photo(jsonObject.getJSONObject("photo"));
        }
    }

    public PlaylistItem(PlaylistItem playlistItem){
        id = playlistItem.id;
        ownerId = playlistItem.ownerId;
        type = playlistItem.type;
        title = playlistItem.title;
        description = playlistItem.description;
        count = playlistItem.count;
        followers = playlistItem.followers;
        plays = playlistItem.plays;
        createTime = playlistItem.createTime;
        updateTime = playlistItem.updateTime;
        isFollowing = playlistItem.isFollowing;
        original = playlistItem.original;
        followed = playlistItem.followed;
        photo = playlistItem.photo;
        accessKey = playlistItem.accessKey;
        isExplicit = playlistItem.isExplicit;
        albumType = playlistItem.albumType;

        mainArtists = new ArrayList<>(playlistItem.mainArtists);
        featuredArtists = new ArrayList<>(playlistItem.featuredArtists);
        artists = new ArrayList<>(playlistItem.artists);
        genres = new ArrayList<>(playlistItem.genres);
        items = new ArrayList<>(playlistItem.items);

        owner = playlistItem.owner;
        subtitle = playlistItem.subtitle;
        year = playlistItem.year;
        genresString = playlistItem.genresString;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_PLAYLIST;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null){
            if(obj instanceof PlaylistItem){
                PlaylistItem playlistItem = (PlaylistItem) obj;
                return id.equals(playlistItem.id)&&ownerId.equals(playlistItem.ownerId);
            }
        }
        return false;
    }

}
