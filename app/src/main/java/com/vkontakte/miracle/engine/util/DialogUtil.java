package com.vkontakte.miracle.engine.util;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.dialog.audio.GoToArtistDialog;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogArtist;
import com.vkontakte.miracle.model.audio.fields.Artist;

import java.util.ArrayList;

public class DialogUtil {

    public static void openArtistDialog(ArrayList<Artist> artists, MiracleActivity miracleActivity){
        GoToArtistDialog goToArtistDialog = new GoToArtistDialog(miracleActivity, artists);
        goToArtistDialog.setDialogActionListener(artist -> {
            FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
            fragmentCatalogArtist.setArtistId(artist);
            miracleActivity.addFragment(fragmentCatalogArtist);
        });
        goToArtistDialog.show(miracleActivity);
    }

}
