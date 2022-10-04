package com.vkontakte.miracle.engine.util;

import static com.vkontakte.miracle.engine.util.NavigationUtil.goToArtist;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.dialog.audio.GoToArtistDialog;
import com.vkontakte.miracle.model.audio.fields.Artist;

import java.util.ArrayList;

public class DialogUtil {

    public static void openArtistDialog(ArrayList<Artist> artists, MainActivity mainActivity){
        GoToArtistDialog goToArtistDialog = new GoToArtistDialog(mainActivity, artists);
        goToArtistDialog.setDialogActionListener(artist -> goToArtist(artist, mainActivity));
        goToArtistDialog.show(mainActivity);
    }

}
