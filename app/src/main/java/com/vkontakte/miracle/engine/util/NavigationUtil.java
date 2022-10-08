package com.vkontakte.miracle.engine.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.vkontakte.miracle.dialog.audio.GoToArtistDialog;
import com.vkontakte.miracle.engine.activity.tabs.TabsActivity;
import com.vkontakte.miracle.engine.context.ContextExtractor;
import com.vkontakte.miracle.fragment.audio.FragmentOfflineAudio;
import com.vkontakte.miracle.fragment.audio.FragmentPlaylist;
import com.vkontakte.miracle.fragment.settings.FragmentTest;
import com.vkontakte.miracle.fragment.catalog.FragmentAudioSearch;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogArtist;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogFriends;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogGroups;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogSection;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogSectionUrl;
import com.vkontakte.miracle.fragment.catalog.FragmentMusicSide;
import com.vkontakte.miracle.fragment.catalog.FragmentOwnerCatalogMusic;
import com.vkontakte.miracle.fragment.friends.FragmentFriends;
import com.vkontakte.miracle.fragment.messages.FragmentChat;
import com.vkontakte.miracle.fragment.photos.FragmentOwnerPhotos;
import com.vkontakte.miracle.fragment.photos.FragmentPhotoAlbum;
import com.vkontakte.miracle.fragment.settings.FragmentDebugSettings;
import com.vkontakte.miracle.fragment.settings.FragmentInterfaceSettings;
import com.vkontakte.miracle.fragment.settings.FragmentSettings;
import com.vkontakte.miracle.fragment.settings.FragmentThemeExtractor;
import com.vkontakte.miracle.fragment.settings.FragmentUrlOpenTest;
import com.vkontakte.miracle.fragment.wall.FragmentGroup;
import com.vkontakte.miracle.fragment.wall.FragmentProfile;
import com.vkontakte.miracle.fragment.wall.FragmentWall;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Artist;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.messages.ConversationItem;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.wall.PostItem;

import java.util.ArrayList;

public class NavigationUtil {

    public static void back(Context context){
        Activity activity = ContextExtractor.extractActivity(context);
        if(activity!=null){
            activity.onBackPressed();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToSettings(Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentSettings fragmentSettings = new FragmentSettings();
            tabsActivity.addFragment(fragmentSettings);
        }
    }

    public static void goToInterfaceSettings(Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentInterfaceSettings fragmentSettings = new FragmentInterfaceSettings();
            tabsActivity.addFragment(fragmentSettings);
        }
    }

    public static void goToDebug(Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentDebugSettings fragmentSettings = new FragmentDebugSettings();
            tabsActivity.addFragment(fragmentSettings);
        }
    }

    public static void goToTest(Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentTest fragmentTest = new FragmentTest();
            tabsActivity.addFragment(fragmentTest);
        }
    }

    public static void goToUrlTest(Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentUrlOpenTest urlOpenTest = new FragmentUrlOpenTest();
            tabsActivity.addFragment(urlOpenTest);
        }
    }

    public static void goToSystemThemeExtractor(Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentThemeExtractor themeExtractor = new FragmentThemeExtractor();
            tabsActivity.addFragment(themeExtractor);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwner(PlaylistItem playlistItem, Context context){
        if(playlistItem.getOwner()!=null){
            goToOwner(playlistItem.getOwner(), context);
        }
    }

    public static void goToOwner(Owner owner, Context context){
        if(owner.getProfileItem()!=null){
            goToProfile(owner.getProfileItem(), context);
        } else {
            if(owner.getGroupItem()!=null){
                goToGroup(owner.getGroupItem(), context);
            }
        }
    }

    public static void goToGroup(DataItemWrap<?,?> itemWrap, Context context){
        Object object = itemWrap.getItem();
        if(object instanceof GroupItem) {
            goToGroup((GroupItem) itemWrap.getItem(), context);
        }
    }

    public static void goToGroup(GroupItem groupItem, Context context){
        goToGroup(groupItem.getId(), groupItem.getName(), context);
    }

    public static void goToGroup(String groupId, String groupName, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentGroup fragmentGroup = new FragmentGroup();
            fragmentGroup.setGroupId(groupId);
            fragmentGroup.setGroupName(groupName);
            tabsActivity.addFragment(fragmentGroup);
        }
    }

    public static void goToProfile(ProfileItem profileItem, Context context){
        goToProfile(profileItem.getId(), profileItem.getFullName(), context);
    }

    public static void goToProfile(String profileId, String profileName, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentProfile fragmentProfile = new FragmentProfile();
            fragmentProfile.setProfileId(profileId);
            fragmentProfile.setProfileName(profileName);
            tabsActivity.addFragment(fragmentProfile);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToAlbum(DataItemWrap<?,?> itemWrap, Context context){
        Object object = itemWrap.getItem();
        if(object instanceof AudioItem) {
            AudioItem audioItem = (AudioItem) itemWrap.getItem();
            goToAlbum(audioItem, context);
        }
    }

    public static void goToAlbum(AudioItem audioItem, Context context){
        Album album = audioItem.getAlbum();
        goToPlaylist(album.getId(), album.getOwnerId(), album.getAccessKey(), context);
    }

    public static void goToPlaylist(DataItemWrap<?,?> itemWrap, Context context){
        Object object = itemWrap.getItem();
        if(object instanceof PlaylistItem) {
            PlaylistItem playlistItem = (PlaylistItem) object;
            goToPlaylist(playlistItem, context);
        }
    }

    public static void goToPlaylist(PlaylistItem playlistItem, Context context){
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

        goToPlaylist(playlistId, ownerId, accessKey, context);
    }

    public static void goToPlaylist(String playlistId, String ownerId, String accessKey, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentPlaylist fragmentPlaylist = new FragmentPlaylist();
            fragmentPlaylist.setPlaylistId(playlistId);
            fragmentPlaylist.setOwnerId(ownerId);
            fragmentPlaylist.setAccessKey(accessKey);
            tabsActivity.addFragment(fragmentPlaylist);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToArtist(DataItemWrap<?,?> itemWrap, Context context){
        Object object = itemWrap.getItem();
        if(object instanceof AudioItem) {
            AudioItem audioItem = (AudioItem) itemWrap.getItem();
            goToArtist(audioItem, context);
        } else {
            if(object instanceof PlaylistItem){
                PlaylistItem playlistItem = (PlaylistItem) object;
                goToArtist(playlistItem, context);
            }
        }
    }

    public static void goToArtist(PlaylistItem playlistItem, Context context){
        goToArtist(playlistItem.getArtists(), context);
    }

    public static void goToArtist(AudioItem audioItem, Context context){
        goToArtist(audioItem.getArtists(), context);
    }

    public static void goToArtist(ArrayList<Artist> artists, Context context){
        if(artists.size()==1){
            goToArtist(artists.get(0), context);
        } else {
            GoToArtistDialog goToArtistDialog = new GoToArtistDialog(context, artists);
            goToArtistDialog.setDialogActionListener(artist -> goToArtist(artist, context));
            goToArtistDialog.show(context);
        }
    }

    public static void goToArtist(Artist artist, Context context){
        goToArtist(artist.getId(), artist.getName(), context);
    }

    public static void goToArtist(String artist, String artistName, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
            fragmentCatalogArtist.setArtistId(artist);
            fragmentCatalogArtist.setArtistName(artistName);
            tabsActivity.addFragment(fragmentCatalogArtist);
        }
    }

    public static void goToArtist(String url, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
            fragmentCatalogArtist.setUrl(url);
            tabsActivity.addFragment(fragmentCatalogArtist);
        }
    }

    public static void goToArtistSearch(DataItemWrap<?,?> itemWrap, Context context){
        Object object = itemWrap.getItem();
        if(object instanceof AudioItem) {
            AudioItem audioItem = (AudioItem) itemWrap.getItem();
            goToAudioSearch(audioItem.getArtist(), context);
        }
    }

    public static void goToAudioSearch(String q, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentAudioSearch fragmentAudioSearch = new FragmentAudioSearch();
            if(q!=null) {
                Bundle args = new Bundle();
                args.putString("initialQ", q);
                fragmentAudioSearch.setArguments(args);
            }
            tabsActivity.addFragment(fragmentAudioSearch);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerMusic(Owner owner, Context context){
        goToOwnerMusic(owner.getId(), context);
    }

    public static void goToOwnerMusic(String ownerId, Context context){
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(profileItem.getId().equals(ownerId)){
            goToUserMusic(context);
        } else {
            TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
            if(tabsActivity!=null) {
                FragmentOwnerCatalogMusic fragmentOwnerCatalogMusic = new FragmentOwnerCatalogMusic();
                fragmentOwnerCatalogMusic.setOwnerId(ownerId);
                tabsActivity.addFragment(fragmentOwnerCatalogMusic);
            }
        }
    }

    public static void goToUserMusic(Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentMusicSide fragmentMusicSide = new FragmentMusicSide();
            tabsActivity.addFragment(fragmentMusicSide);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerPhotos(Owner owner, Context context){
        goToOwnerPhotos(owner.getId(), context);
    }

    public static void goToOwnerPhotos(String ownerId, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentOwnerPhotos fragmentOwnerPhotos = new FragmentOwnerPhotos();
            fragmentOwnerPhotos.setOwnerId(ownerId);
            tabsActivity.addFragment(fragmentOwnerPhotos);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToPhotoAlbum(PhotoAlbumItem photoAlbumItem, Context context){
        goToPhotoAlbum(photoAlbumItem.getId(), photoAlbumItem.getOwnerId(), photoAlbumItem.getTitle(), context);
    }

    public static void goToPhotoAlbum(String photoAlbumId, String ownerId, String photoAlbumTitle, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentPhotoAlbum fragmentPhotoAlbum = new FragmentPhotoAlbum();
            fragmentPhotoAlbum.setPhotoAlbumId(photoAlbumId);
            fragmentPhotoAlbum.setOwnerId(ownerId);
            fragmentPhotoAlbum.setPhotoAlbumTitle(photoAlbumTitle);
            tabsActivity.addFragment(fragmentPhotoAlbum);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerFriends(Owner owner, Context context){
        goToOwnerFriends(owner.getId(), context);
    }

    public static void goToOwnerFriends(String ownerId, Context context){
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(profileItem.getId().equals(ownerId)){
            goToUserFriends(ownerId, context);
        } else {
            TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
            if(tabsActivity!=null) {
                FragmentFriends fragmentFriends = new FragmentFriends();
                fragmentFriends.setOwnerId(ownerId);
                tabsActivity.addFragment(fragmentFriends);
            }
        }
    }

    public static void goToUserFriends(String ownerId, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentCatalogFriends fragmentCatalogFriends = new FragmentCatalogFriends();
            fragmentCatalogFriends.setOwnerId(ownerId);
            tabsActivity.addFragment(fragmentCatalogFriends);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerGroups(Owner owner, Context context){
        goToOwnerGroups(owner.getId(), context);
    }

    public static void goToOwnerGroups(String ownerId, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentCatalogGroups fragmentCatalogGroups = new FragmentCatalogGroups();
            fragmentCatalogGroups.setOwnerId(ownerId);
            tabsActivity.addFragment(fragmentCatalogGroups);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToWall(PostItem postItem, Context context){
        Owner owner = postItem.getFrom();
        if(owner==null){
            owner = postItem.getSource();
        }
        if(owner==null){
            owner = postItem.getOwner();
        }
        goToWall(postItem.getId(), owner.getId(), context);
    }

    public static void goToWall(String postId, String ownerId, Context context) {
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentWall fragmentWall = new FragmentWall();
            fragmentWall.setPostId(postId);
            fragmentWall.setOwnerId(ownerId);
            tabsActivity.addFragment(fragmentWall);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToCatalogSection(CatalogAction catalogAction, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentCatalogSection fragmentCatalogSection = new FragmentCatalogSection();
            fragmentCatalogSection.setCatalogSectionId(catalogAction.getSectionId());
            tabsActivity.addFragment(fragmentCatalogSection);
        }
    }

    public static void goToCatalogSection(String url, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentCatalogSectionUrl fragmentCatalogSectionUrl = new FragmentCatalogSectionUrl();
            fragmentCatalogSectionUrl.setCatalogSectionUrl(url);
            tabsActivity.addFragment(fragmentCatalogSectionUrl);
        }
    }

    public static void goToOfflineAudio(Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentOfflineAudio fragmentOfflineAudio = new FragmentOfflineAudio();
            tabsActivity.addFragment(fragmentOfflineAudio);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToChat(ConversationItem conversationItem, Context context){
        TabsActivity tabsActivity = ContextExtractor.extractTabsActivity(context);
        if(tabsActivity!=null) {
            FragmentChat fragmentChat = new FragmentChat();
            fragmentChat.setConversationItem(conversationItem);
            tabsActivity.addFragment(fragmentChat);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static boolean hardResolveVKURL(String url, Context context){

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
                        goToPlaylist(new PlaylistItem(fields[0], fields[1], fields[2]), context);
                        return true;
                    }
                    s = "music/album/";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        String[] fields = e.split("_");
                        goToPlaylist(new PlaylistItem(fields[0], fields[1], fields[2]), context);
                        return true;
                    }
                    s = "music/curator/";
                    if(e.indexOf(s)==0){
                        return false;
                    }
                    s = "artist/";
                    if(e.indexOf(s)==0){
                        goToArtist(url, context);
                        return true;
                    }
                    //TODO добавлять по возможности новые каталоги
                    s = "audio?section=";
                    if(e.indexOf(s)==0){
                        goToCatalogSection(url, context);
                        return true;
                    }
                    pos = e.indexOf('&');
                    if(pos>0){
                        e = e.substring(0, pos);
                    }
                    s = "friends?id=";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerFriends(e, context);
                        return true;
                    }
                    s = "groups?id=";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerGroups(e, context);
                        return true;
                    }
                    pos = e.indexOf('?');
                    if(pos>0){
                        e = e.substring(0, pos);
                    }
                    s = "albums";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerPhotos(e, context);
                        return true;
                    }
                    s = "album";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        String[] ownerAndId = e.split("_");
                        goToPhotoAlbum(ownerAndId[1], ownerAndId[0], null, context);
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
                        goToProfile(e,null, context);
                        return true;
                    }
                    s = "public";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToGroup(e, null, context);
                        return true;
                    }
                    s = "audios";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerMusic(e, context);
                        return true;
                    }
                    s = "audio_offline";
                    if(e.indexOf(s)==0){
                        goToOfflineAudio(context);
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
