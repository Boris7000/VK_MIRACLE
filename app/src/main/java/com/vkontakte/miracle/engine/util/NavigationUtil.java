package com.vkontakte.miracle.engine.util;

import static com.vkontakte.miracle.engine.util.DialogUtil.openArtistDialog;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.fragment.audio.FragmentOfflineAudio;
import com.vkontakte.miracle.fragment.audio.FragmentPlaylist;
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

    public static void goToOwner(PlaylistItem playlistItem, MainActivity mainActivity){
        if(playlistItem.getOwner()!=null){
            goToOwner(playlistItem.getOwner(), mainActivity);
        }
    }

    public static void goToOwner(Owner owner, MainActivity mainActivity){
        if(owner.getProfileItem()!=null){
            goToProfile(owner.getProfileItem(), mainActivity);
        } else {
            if(owner.getGroupItem()!=null){
                goToGroup(owner.getGroupItem(), mainActivity);
            }
        }
    }

    public static void goToGroup(GroupItem groupItem, MainActivity mainActivity){
        goToGroup(groupItem.getId(), groupItem.getName(), mainActivity);
    }

    public static void goToGroup(String groupId, String groupName, MainActivity mainActivity){
        FragmentGroup fragmentGroup = new FragmentGroup();
        fragmentGroup.setGroupId(groupId);
        fragmentGroup.setGroupName(groupName);
        mainActivity.addFragment(fragmentGroup);
    }

    public static void goToProfile(ProfileItem profileItem, MainActivity mainActivity){
        goToProfile(profileItem.getId(), profileItem.getFullName(), mainActivity);
    }

    public static void goToProfile(String profileId, String profileName, MainActivity mainActivity){
        FragmentProfile fragmentProfile = new FragmentProfile();
        fragmentProfile.setProfileId(profileId);
        fragmentProfile.setProfileName(profileName);
        mainActivity.addFragment(fragmentProfile);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToAlbum(AudioItem audioItem, MainActivity mainActivity){
        Album album = audioItem.getAlbum();
        goToPlaylist(album.getId(), album.getOwnerId(), album.getAccessKey(), mainActivity);
    }

    public static void goToPlaylist(PlaylistItem playlistItem, MainActivity mainActivity){
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

        goToPlaylist(playlistId, ownerId, accessKey, mainActivity);
    }

    public static void goToPlaylist(String playlistId, String ownerId, String accessKey, MainActivity mainActivity){
        FragmentPlaylist fragmentPlaylist = new FragmentPlaylist();
        fragmentPlaylist.setPlaylistId(playlistId);
        fragmentPlaylist.setOwnerId(ownerId);
        fragmentPlaylist.setAccessKey(accessKey);
        mainActivity.addFragment(fragmentPlaylist);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToArtist(PlaylistItem playlistItem, MainActivity mainActivity){
        goToArtist(playlistItem.getArtists(), mainActivity);
    }

    public static void goToArtist(AudioItem audioItem, MainActivity mainActivity){
        goToArtist(audioItem.getArtists(), mainActivity);
    }

    public static void goToArtist(ArrayList<Artist> artists, MainActivity mainActivity){
        if(artists.size()==1){
            goToArtist(artists.get(0), mainActivity);
        } else {
            openArtistDialog(artists, mainActivity);
        }
    }

    public static void goToArtist(Artist artist, MainActivity mainActivity){
        goToArtist(artist.getId(), artist.getName(), mainActivity);
    }

    public static void goToArtist(String artist, String artistName, MainActivity mainActivity){
        FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
        fragmentCatalogArtist.setArtistId(artist);
        fragmentCatalogArtist.setArtistName(artistName);
        mainActivity.addFragment(fragmentCatalogArtist);
    }

    public static void goToArtist(String url, MainActivity mainActivity){
        FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
        fragmentCatalogArtist.setUrl(url);
        mainActivity.addFragment(fragmentCatalogArtist);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerMusic(Owner owner, MainActivity mainActivity){
        goToOwnerMusic(owner.getId(), mainActivity);
    }

    public static void goToOwnerMusic(String ownerId, MainActivity mainActivity){
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(profileItem.getId().equals(ownerId)){
            goToUserMusic(mainActivity);
        } else {
            FragmentOwnerCatalogMusic fragmentOwnerCatalogMusic = new FragmentOwnerCatalogMusic();
            fragmentOwnerCatalogMusic.setOwnerId(ownerId);
            mainActivity.addFragment(fragmentOwnerCatalogMusic);
        }
    }

    public static void goToUserMusic(MainActivity mainActivity){
        FragmentMusicSide fragmentMusicSide = new FragmentMusicSide();
        mainActivity.addFragment(fragmentMusicSide);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerPhotos(Owner owner, MainActivity mainActivity){
        goToOwnerPhotos(owner.getId(), mainActivity);
    }

    public static void goToOwnerPhotos(String ownerId, MainActivity mainActivity){
        FragmentOwnerPhotos fragmentOwnerPhotos = new FragmentOwnerPhotos();
        fragmentOwnerPhotos.setOwnerId(ownerId);
        mainActivity.addFragment(fragmentOwnerPhotos);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToPhotoAlbum(PhotoAlbumItem photoAlbumItem, MainActivity mainActivity){
        goToPhotoAlbum(photoAlbumItem.getId(), photoAlbumItem.getOwnerId(), photoAlbumItem.getTitle(), mainActivity);
    }

    public static void goToPhotoAlbum(String photoAlbumId, String ownerId, String photoAlbumTitle, MainActivity mainActivity){
        FragmentPhotoAlbum fragmentPhotoAlbum = new FragmentPhotoAlbum();
        fragmentPhotoAlbum.setPhotoAlbumId(photoAlbumId);
        fragmentPhotoAlbum.setOwnerId(ownerId);
        fragmentPhotoAlbum.setPhotoAlbumTitle(photoAlbumTitle);
        mainActivity.addFragment(fragmentPhotoAlbum);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerFriends(Owner owner, MainActivity mainActivity){
        goToOwnerFriends(owner.getId(), mainActivity);
    }

    public static void goToOwnerFriends(String ownerId, MainActivity mainActivity){
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(profileItem.getId().equals(ownerId)){
            goToUserFriends(ownerId, mainActivity);
        } else {
            FragmentFriends fragmentFriends = new FragmentFriends();
            fragmentFriends.setOwnerId(ownerId);
            mainActivity.addFragment(fragmentFriends);
        }
    }

    public static void goToUserFriends(String ownerId, MainActivity mainActivity){
        FragmentCatalogFriends fragmentCatalogFriends = new FragmentCatalogFriends();
        fragmentCatalogFriends.setOwnerId(ownerId);
        mainActivity.addFragment(fragmentCatalogFriends);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToOwnerGroups(Owner owner, MainActivity mainActivity){
        goToOwnerGroups(owner.getId(), mainActivity);
    }

    public static void goToOwnerGroups(String ownerId, MainActivity mainActivity){
        FragmentCatalogGroups fragmentCatalogGroups = new FragmentCatalogGroups();
        fragmentCatalogGroups.setOwnerId(ownerId);
        mainActivity.addFragment(fragmentCatalogGroups);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public static void goToWall(PostItem postItem, MainActivity mainActivity){
        Owner owner = postItem.getFrom();
        if(owner==null){
            owner = postItem.getSource();
        }
        if(owner==null){
            owner = postItem.getOwner();
        }
        goToWall(postItem.getId(), owner.getId(), mainActivity);
    }

    public static void goToWall(String postId, String ownerId, MainActivity mainActivity) {
        FragmentWall fragmentWall  = new FragmentWall();
        fragmentWall.setPostId(postId);
        fragmentWall.setOwnerId(ownerId);
        mainActivity.addFragment(fragmentWall);
    }

    public static boolean hardResolveVKURL(String url, MainActivity mainActivity){

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
                        goToPlaylist(new PlaylistItem(fields[0], fields[1], fields[2]), mainActivity);
                        return true;
                    }
                    s = "music/album/";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        String[] fields = e.split("_");
                        goToPlaylist(new PlaylistItem(fields[0], fields[1], fields[2]), mainActivity);
                        return true;
                    }
                    s = "music/curator/";
                    if(e.indexOf(s)==0){
                        return false;
                    }
                    s = "artist/";
                    if(e.indexOf(s)==0){
                        goToArtist(url, mainActivity);
                        return true;
                    }
                    //TODO добавлять по возможности новые каталоги
                    s = "audio?section=";
                    if(e.indexOf(s)==0){
                        FragmentCatalogSectionUrl fragmentCatalogSectionUrl = new FragmentCatalogSectionUrl();
                        fragmentCatalogSectionUrl.setCatalogSectionUrl(url);
                        mainActivity.addFragment(fragmentCatalogSectionUrl);
                        return true;
                    }
                    pos = e.indexOf('&');
                    if(pos>0){
                        e = e.substring(0, pos);
                    }
                    s = "friends?id=";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerFriends(e, mainActivity);
                        return true;
                    }
                    s = "groups?id=";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerGroups(e, mainActivity);
                        return true;
                    }
                    pos = e.indexOf('?');
                    if(pos>0){
                        e = e.substring(0, pos);
                    }
                    s = "albums";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerPhotos(e, mainActivity);
                        return true;
                    }
                    s = "album";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        String[] ownerAndId = e.split("_");
                        goToPhotoAlbum(ownerAndId[1], ownerAndId[0], null, mainActivity);
                        return true;
                    }
                    s = "videos";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        return false;
                    }
                    s = "id";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToProfile(e,null, mainActivity);
                        return true;
                    }
                    s = "public";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToGroup(e, null, mainActivity);
                        return true;
                    }
                    s = "audios";
                    if(e.indexOf(s)==0){
                        e = e.substring(s.length());
                        goToOwnerMusic(e, mainActivity);
                        return true;
                    }
                    s = "audio_offline";
                    if(e.indexOf(s)==0){
                        FragmentOfflineAudio fragmentOfflineAudio = new FragmentOfflineAudio();
                        mainActivity.addFragment(fragmentOfflineAudio);
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
