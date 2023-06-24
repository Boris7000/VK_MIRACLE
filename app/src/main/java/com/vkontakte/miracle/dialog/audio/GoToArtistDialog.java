package com.vkontakte.miracle.dialog.audio;

import static com.miracle.widget.ExtendedTextHelper.ICON_POS_LEFT;
import static com.miracle.widget.ExtendedTextHelper.ICON_POS_RIGHT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.miracle.engine.dialog.MiracleBottomDialog;
import com.miracle.widget.ExtendedMaterialButton;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.audio.fields.Artist;

import java.util.ArrayList;

public class GoToArtistDialog extends MiracleBottomDialog {

    private final ArrayList<Artist> artists;
    private GoToArtistDialogActionListener dialogActionListener;

    public GoToArtistDialog(@NonNull Context context, ArrayList<Artist> artists) {
        super(context);
        this.artists = artists;
    }

    @Override
    public void show(Context context) {
        View rootView = View.inflate(context, R.layout.dialog_go_to_artist, null);
        setContentView(rootView);

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout linearLayout = rootView.findViewById(R.id.buttonsContainer);
        ExtendedMaterialButton button;

        for (Artist artist: artists) {
            button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            button.setText(artist.getName());
            button.setIconResource(R.drawable.ic_microphone_28, ICON_POS_LEFT);
            button.setIconResource(R.drawable.ic_chevron_24, ICON_POS_RIGHT);
            button.setOnClickListener(view -> {
                dialogActionListener.onSelect(artist);
                cancel();
            });
            linearLayout.addView(button);
        }

        Button cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> cancel());

        show();
        expand();
    }

    public void setDialogActionListener(GoToArtistDialogActionListener dialogActionListener) {
        this.dialogActionListener = dialogActionListener;
    }
}
