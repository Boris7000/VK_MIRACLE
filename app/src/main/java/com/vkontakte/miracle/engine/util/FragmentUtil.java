package com.vkontakte.miracle.engine.util;

import static com.vkontakte.miracle.engine.util.DialogUtil.openArtistDialog;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.fragment.audio.FragmentPlaylist;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogArtist;
import com.vkontakte.miracle.fragment.wall.FragmentGroup;
import com.vkontakte.miracle.fragment.wall.FragmentProfile;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.ProfileItem;

public class FragmentUtil {

    public static void goToArtist(PlaylistItem playlistItem, MiracleActivity miracleActivity){
        if(playlistItem.getArtists().size()==1){
            FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
            fragmentCatalogArtist.setArtistId(playlistItem.getArtists().get(0));
            miracleActivity.addFragment(fragmentCatalogArtist);
        } else {
            openArtistDialog(playlistItem.getArtists(), miracleActivity);
        }
    }

    public static void goToArtist(AudioItem audioItem, MiracleActivity miracleActivity){
        if(audioItem.getArtists().size()==1){
            FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
            fragmentCatalogArtist.setArtistId(audioItem.getArtists().get(0));
            miracleActivity.addFragment(fragmentCatalogArtist);
        } else {
            openArtistDialog(audioItem.getArtists(), miracleActivity);
        }
    }

    public static void goToOwner(PlaylistItem playlistItem, MiracleActivity miracleActivity){
        if(playlistItem.getOwner()!=null){
            Owner owner = playlistItem.getOwner();
            if(owner.getProfileItem()!=null){
                goToProfile(owner.getProfileItem(),miracleActivity);
            } else {
                if(owner.getGroupItem()!=null){
                    goToGroup(owner.getGroupItem(),miracleActivity);
                }
            }
        }
    }

    public static void goToAlbum(AudioItem audioItem, MiracleActivity miracleActivity){
        FragmentPlaylist fragmentPlaylist = new FragmentPlaylist();
        fragmentPlaylist.setAlbum(audioItem.getAlbum());
        miracleActivity.addFragment(fragmentPlaylist);
    }

    public static void goToPlaylist(PlaylistItem playlistItem, MiracleActivity miracleActivity){
        FragmentPlaylist fragmentPlaylist = new FragmentPlaylist();
        fragmentPlaylist.setPlaylistItem(playlistItem);
        miracleActivity.addFragment(fragmentPlaylist);
    }

    public static void goToGroup(GroupItem groupItem, MiracleActivity miracleActivity){
        FragmentGroup fragmentGroup = new FragmentGroup();
        fragmentGroup.setGroupItem(groupItem);
        miracleActivity.addFragment(fragmentGroup);
    }

    public static void goToProfile(ProfileItem profileItem, MiracleActivity miracleActivity){
        FragmentProfile fragmentProfile = new FragmentProfile();
        fragmentProfile.setProfileItem(profileItem);
        miracleActivity.addFragment(fragmentProfile);
    }

}
