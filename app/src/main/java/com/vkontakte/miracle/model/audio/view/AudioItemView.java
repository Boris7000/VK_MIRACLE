package com.vkontakte.miracle.model.audio.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;

public class AudioItemView extends FrameLayout {

    private ImageView imageView;
    private TextView title;
    private TextView subtitle;
    private TextView duration;
    private ViewStub explicitStub;
    private ImageView explicit;


    public AudioItemView(@NonNull Context context) {
        super(context);
    }

    public AudioItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageView = findViewById(R.id.photo);
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        explicitStub = findViewById(R.id.explicitStub);
        duration = findViewById(R.id.duration);
    }

    public void setValues(AudioItem audioItem){
        title.setText(audioItem.getTitle());
        subtitle.setText(audioItem.getArtist());
        duration.setText(audioItem.getDurationString());

        Picasso.get().cancelRequest(imageView);

        if(audioItem.isLicensed()) {
            if (audioItem.getAlbum() != null) {
                Album album = audioItem.getAlbum();
                if (album.getThumb() != null) {
                    Photo thumb = album.getThumb();
                    if (thumb.getPhoto135() != null) {
                        Picasso.get().load(thumb.getPhoto135()).noFade().into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.audio_placeholder_image_small);
                    }
                } else {
                    imageView.setImageResource(R.drawable.audio_placeholder_image_small);
                }
            } else {
                imageView.setImageResource(R.drawable.audio_placeholder_image_small);
            }
        } else {
            imageView.setImageResource(R.drawable.audio_no_license_image_small);
        }

        if(audioItem.isExplicit()){
            if(explicit==null) {
                if(explicitStub!=null) {
                    explicit = (ImageView) explicitStub.inflate();
                } else {
                    explicit = findViewById(R.id.explicit);
                }
            }
            if(explicit.getVisibility()!=VISIBLE) {
                explicit.setVisibility(VISIBLE);
            }
        } else {
            if(explicit!=null&&explicit.getVisibility()!=GONE){
                explicit.setVisibility(GONE);
            }
        }
    }

}
