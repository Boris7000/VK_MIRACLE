package com.vkontakte.miracle.engine.util;

import android.os.Bundle;

import com.vkontakte.miracle.dialog.audio.GoToArtistDialog;
import com.vkontakte.miracle.engine.activity.tabs.TabsActivity;
import com.vkontakte.miracle.fragment.audio.FragmentOfflineAudio;
import com.vkontakte.miracle.fragment.audio.FragmentPlaylist;
import com.vkontakte.miracle.fragment.catalog.FragmentAudioSearch;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogArtist;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogFriends;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogGroups;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogSectionUrl;
import com.vkontakte.miracle.fragment.catalog.FragmentMusicSide;
import com.vkontakte.miracle.fragment.catalog.FragmentOwnerCatalogMusic;
import com.vkontakte.miracle.fragment.friends.FragmentFriends;
import com.vkontakte.miracle.fragment.photos.FragmentOwnerPhotos;
import com.vkontakte.miracle.fragment.photos.FragmentPhotoAlbum;
import com.vkontakte.miracle.fragment.wall.FragmentGroup;
import com.vkontakte.miracle.fragment.wall.FragmentProfile;
import com.vkontakte.miracle.fragment.wall.FragmentWall;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Artist;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.wall.PostItem;

import java.util.ArrayList;

public class NavigationUtil {

    public static void goToOwner(PlaylistItem playlistItem, TabsActivity tabsActivity){
        if(playlistItem.getOwner()!=null){
            goToOwner(playlistItem.getOwner(), tabsActivity);
        }
    }

    public static void goToOwner(Owner owner, TabsActivity tabsActivity){
        if(owner.getProfileItem()!=null){
            goToProfile(owner.getProfileItem(), tabsActivity);
        } else {
            if(owner.getGroupItem()!=null){
                goToGroup(owner.getGroupItem(), tabsActivity);
            }
        }
    }

    public static void goToGroup(GroupItem groupItem, TabsActivity tabsActivity){
        goToGroup(groupItem.getId(), groupItem.getName(), tabsActivity);
    }

    public static void goToGroup(String groupId, String groupName, TabsActivity tabsActivity){
        FragmentGroup fragmentGroup = new FragmentGroup();
        fragmentGroup.setGroupId(groupId);
        fragmentGroup.setGroupName(groupName);
        tabsActivity.addFragment(fragmentGroup);
    }

    public static void goToProfile(ProfileItem profileItem, TabsActivity tabsActivity){
        goToProfile(profileItem.getId(), profileItem.getFullName(), tabsActivity);
    }

    public static void goToProfile(String profileId, String profileName, TabsActivity tabsActivity){
        FragmentProfile fragmentProfile = new FragmentProfile();
        fragmentProfile.setProfileId(profileId);
        fragmentProfile.setProfileName(profileName);
        tabsActivity.addFragment(fragmentProfile);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToAlbum(AudioItem audioItem, TabsActivity tabsActivity){
        Album album = audioItem.getAlbum();
        goToPlaylist(album.getId(), album.getOwnerId(), album.getAccessKey(), tabsActivity);
    }

    public static void goToPlaylist(PlaylistItem playlistItem, TabsActivity tabsActivity){
        String playlistId;
        String ownerId;
        String accessKey;

        if(playlistItem.getOriginal()!=null) {
            playlistId = playlistItem.getOriginal().getId();
            ownerId = playlistItem.getOriginal().getOwnerId();
            accessKey = playlistItem.getOriginal().getAccessKey();
        } else {
            playlistId = playlistItem.getId();
            ownerId = playlistItem.getOwnerId();
            accessKey = playlistItem.getAccessKey();
        }

        goToPlaylist(playlistId, ownerId, accessKey, tabsActivity);
    }

    public static void goToPlaylist(String playlistId, String ownerId, String accessKey, TabsActivity tabsActivity){
        FragmentPlaylist fragmentPlaylist = new FragmentPlaylist();
        fragmentPlaylist.setPlaylistId(playlistId);
        fragmentPlaylist.setOwnerId(ownerId);
        fragmentPlaylist.setAccessKey(accessKey);
        tabsActivity.addFragment(fragmentPlaylist);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToArtist(PlaylistItem playlistItem, TabsActivity tabsActivity){
        goToArtist(playlistItem.getArtists(), tabsActivity);
    }

    public static void goToArtist(AudioItem audioItem, TabsActivity tabsActivity){
        goToArtist(audioItem.getArtists(), tabsActivity);
    }

    public static void goToArtist(ArrayList<Artist> artists, TabsActivity tabsActivity){
        if(artists.size()==1){
            goToArtist(artists.get(0), tabsActivity);
        } else {
            GoToArtistDialog goToArtistDialog = new GoToArtistDialog(tabsActivity, artists);
            goToArtistDialog.setDialogActionListener(artist -> goToArtist(artist, tabsActivity));
            goToArtistDialog.show(tabsActivity);
        }
    }

    public static void goToArtist(Artist artist, TabsActivity tabsActivity){
        goToArtist(artist.getId(), artist.getName(), tabsActivity);
    }

    public static void goToArtist(String artist, String artistName, TabsActivity tabsActivity){
        FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
        fragmentCatalogArtist.setArtistId(artist);
        fragmentCatalogArtist.setArtistName(artistName);
        tabsActivity.addFragment(fragmentCatalogArtist);
    }

    public static void goToArtist(String url, TabsActivity tabsActivity){
        FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
        fragmentCatalogArtist.setUrl(url);
        tabsActivity.addFragment(fragmentCatalogArtist);
    }

    public static void goToArtistSearch(String q, TabsActivity tabsActivity){
        FragmentAudioSearch fragmentAudioSearch = new FragmentAudioSearch();
        Bundle args = new Bundle();
        args.putString("initialQ", q);
        fragmentAudioSearch.setArguments(args);
        tabsActivity.addFragment(fragmentAudioSearch);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerMusic(Owner owner, TabsActivity tabsActivity){
        goToOwnerMusic(owner.getId(), tabsActivity);
    }

    public static void goToOwnerMusic(String ownerId, TabsActivity tabsActivity){
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(profileItem.getId().equals(ownerId)){
            goToUserMusic(tabsActivity);
        } else {
            FragmentOwnerCatalogMusic fragmentOwnerCatalogMusic = new FragmentOwnerCatalogMusic();
            fragmentOwnerCatalogMusic.setOwnerId(ownerId);
            tabsActivity.addFragment(fragmentOwnerCatalogMusic);
        }
    }

    public static void goToUserMusic(TabsActivity tabsActivity){
        FragmentMusicSide fragmentMusicSide = new FragmentMusicSide();
        tabsActivity.addFragment(fragmentMusicSide);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerPhotos(Owner owner, TabsActivity tabsActivity){
        goToOwnerPhotos(owner.getId(), tabsActivity);
    }

    public static void goToOwnerPhotos(String ownerId, TabsActivity tabsActivity){
        FragmentOwnerPhotos fragmentOwnerPhotos = new FragmentOwnerPhotos();
        fragmentOwnerPhotos.setOwnerId(ownerId);
        tabsActivity.addFragment(fragmentOwnerPhotos);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToPhotoAlbum(PhotoAlbumItem photoAlbumItem, TabsActivity tabsActivity){
        goToPhotoAlbum(photoAlbumItem.getId(), photoAlbumItem.getOwnerId(), photoAlbumItem.getTitle(), tabsActivity);
    }

    public static void goToPhotoAlbum(String photoAlbumId, String ownerId, String photoAlbumTitle, TabsActivity tabsActivity){
        FragmentPhotoAlbum fragmentPhotoAlbum = new FragmentPhotoAlbum();
        fragmentPhotoAlbum.setPhotoAlbumId(photoAlbumId);
        fragmentPhotoAlbum.setOwnerId(ownerId);
        fragmentPhotoAlbum.setPhotoAlbumTitle(photoAlbumTitle);
        tabsActivity.addFragment(fragmentPhotoAlbum);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerFriends(Owner owner, TabsActivity tabsActivity){
        goToOwnerFriends(owner.getId(), tabsActivity);
    }

    public static void goToOwnerFriends(String ownerId, TabsActivity tabsActivity){
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(profileItem.getId().equals(ownerId)){
            goToUserFriends(ownerId, tabsActivity);
        } else {
            FragmentFriends fragmentFriends = new FragmentFriends();
            fragmentFriends.setOwnerId(ownerId);
            tabsActivity.addFragment(fragmentFriends);
        }
    }

    public static void goToUserFriends(String ownerId, TabsActivity tabsActivity){
        FragmentCatalogFriends fragmentCatalogFriends = new FragmentCatalogFriends();
        fragmentCatalogFriends.setOwnerId(ownerId);
        tabsActivity.addFragment(fragmentCatalogFriends);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerGroups(Owner owner, TabsActivity tabsActivity){
        goToOwnerGroups(owner.getId(), tabsActivity);
    }

    public static void goToOwnerGroups(String ownerId, TabsActivity tabsActivity){
        FragmentCatalogGroups fragmentCatalogGroups = new FragmentCatalogGroups();
        fragmentCatalogGroups.setOwnerId(ownerId);
        tabsActivity.addFragment(fragmentCatalogGroups);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToWall(PostItem postItem, TabsActivity tabsActivity){
        Owner owner = postItem.getFrom();
        if(owner==null){
            owner = postItem.getSource();
        }
        if(owner==null){
            owner = postItem.getOwner();
        }
        goToWall(postItem.getId(), owner.getId(), tabsActivity);
    }

    public static void goToWall(String postId, String ownerId, TabsActivity tabsActivity) {
        FragmentWall fragmentWall  = new FragmentWall();
        fragmentWall.setPostId(postId);
        fragmentWall.setOwnerId(ownerId);
        tabsActivity.addFragment(fragmentWall);
    }

    public static boolean hardResolveVKURL(String url, TabsActivity tabsActivity){

        if(!url.isEmpty()) {
            String e = url;
            String s = "https://";
            int pos = e.indexOf(s);
            if(pos==0){
                e = e.substring(s.length());
            }
            s = "vk.com/";
            pos = e.indexOf(s);
            if (pos == 0) {
                e = e.substring(s.length());
                if(!e.isEmpty()){
                    s = "music/playlist/";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        String[] fields = e.split("_");
                        goToPlaylist(new PlaylistItem(fields[0], fields[1], fields[2]), tabsActivity);
                        return true;
                    }
                    s = "music/album/";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        String[] fields = e.split("_");
                        goToPlaylist(new PlaylistItem(fields[0], fields[1], fields[2]), tabsActivity);
                        return true;
                    }
                    s = "music/curator/";
                    if(e.indexOf(s)==0){
                        return false;
                    }
                    s = "artist/";
                    if(e.indexOf(s)==0){
                        goToArtist(url, tabsActivity);
                        return true;
                    }
                    //TODO добавлять по возможности новые каталоги
                    s = "audio?section=";
                    if(e.indexOf(s)==0){
                        FragmentCatalogSectionUrl fragmentCatalogSectionUrl = new FragmentCatalogSectionUrl();
                        fragmentCatalogSectionUrl.setCatalogSectionUrl(url);
                        tabsActivity.addFragment(fragmentCatalogSectionUrl);
                        return true;
                    }
                    pos = e.indexOf('&');
                    if(pos>0){
                        e = e.substring(0, pos);
                    }
                    s = "friends?id=";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerFriends(e, tabsActivity);
                        return true;
                    }
                    s = "groups?id=";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerGroups(e, tabsActivity);
                        return true;
                    }
                    pos = e.indexOf('?');
                    if(pos>0){
                        e = e.substring(0, pos);
                    }
                    s = "albums";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerPhotos(e, tabsActivity);
                        return true;
                    }
                    s = "album";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        String[] ownerAndId = e.split("_");
                        goToPhotoAlbum(ownerAndId[1], ownerAndId[0], null, tabsActivity);
                        return true;
                    }
                    s = "videos";
                    if(e.indexOf(s)==0){
                        //e = e.substring(s.length());
                        return false;
                    }
                    s = "id";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToProfile(e,null, tabsActivity);
                        return true;
                    }
                    s = "public";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToGroup(e, null, tabsActivity);
                        return true;
                    }
                    s = "audios";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerMusic(e, tabsActivity);
                        return true;
                    }
                    s = "audio_offline";
                    if(e.indexOf(s)==0){
                        FragmentOfflineAudio fragmentOfflineAudio = new FragmentOfflineAudio();
                        tabsActivity.addFragment(fragmentOfflineAudio);
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
