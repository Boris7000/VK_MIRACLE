package com.vkontakte.miracle.dialog.audio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.miracle.button.TextViewButton;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.dialog.MiracleBottomDialog;
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
        TextViewButton miracleButton;

        for (Artist artist: artists) {
            miracleButton = (TextViewButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            miracleButton.setText(artist.getName());
            miracleButton.setIconStartImageResource(R.drawable.ic_microphone_28);
            miracleButton.setIconEndImageResource(R.drawable.ic_chevron_24);
            miracleButton.setOnClickListener(view -> {
                dialogActionListener.onSelect(artist);
                cancel();
            });
            linearLayout.addView(miracleButton);
        }

        TextViewButton cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> cancel());

        show();
        expand();
    }

    public void setDialogActionListener(GoToArtistDialogActionListener dialogActionListener) {
        this.dialogActionListener = dialogActionListener;
    }
}
