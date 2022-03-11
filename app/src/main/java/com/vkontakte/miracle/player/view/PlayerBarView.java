package com.vkontakte.miracle.player.view;

import static com.vkontakte.miracle.engine.util.ImageUtil.bitmapFromLayerDrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.DimensionsUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.player.AudioPlayerData;
import com.vkontakte.miracle.player.OnPlayerEventListener;

public class PlayerBarView extends FrameLayout {

    private TextView title;
    private TextView subtitle;
    private ImageView image;
    private final Drawable play_drawable_28;
    private final Drawable pause_drawable_28;
    private Bitmap placeholderImage;
    private String previousImageUrl = " ";
    private ImageView pausePlayButton;
    private final MiracleApp miracleApp;
    private final OnPlayerEventListener onPlayerEventListener = new OnPlayerEventListener() {
        @Override
        public void onBufferChange(AudioPlayerData playerData) {

        }

        @Override
        public void onPlaybackPositionChange(AudioPlayerData playerData) {

        }

        @Override
        public void onSongChange(AudioPlayerData playerData, boolean animate) {
            AudioItem audioItem = playerData.getCurrentItem();

            title.setText(audioItem.getTitle());
            subtitle.setText(audioItem.getArtist());

            createTarget(audioItem);
        }

        @Override
        public void onPlayWhenReadyChange(AudioPlayerData playerData, boolean animate) {
            pausePlayButton.setImageDrawable(playerData.getPlayWhenReady()?
                    pause_drawable_28:play_drawable_28);

            pausePlayButton.setOnClickListener(playerData.getPlayWhenReady()?
                    view -> miracleApp.getPlayerServiceController().actionPause() :
                    view -> miracleApp.getPlayerServiceController().actionResume());
        }

        @Override
        public void onRepeatModeChange(AudioPlayerData playerData) {

        }

        @Override
        public void onPlayerClose() {
        }
    };


    public PlayerBarView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        pause_drawable_28 = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_pause_28,null);
        play_drawable_28 = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_play_28,null);
        miracleApp = (MiracleApp)getContext().getApplicationContext();
    }


    private void createTarget(AudioItem audioItem){
        Picasso.get().cancelRequest(image);
        if(audioItem.getAlbum()!=null){
            Album album = audioItem.getAlbum();
            if(album.getThumb()!=null){
                Photo thumb = album.getThumb();
                if(thumb.getPhoto135()!=null){
                    if(!thumb.getPhoto135().equals(previousImageUrl)) {
                        Picasso.get().load(previousImageUrl = thumb.getPhoto135()).into(image);
                    }
                    return;
                }
            }
        }
        if(!previousImageUrl.equals("")) {
            previousImageUrl = "";
            image.setImageBitmap(placeholderImage);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        image = findViewById(R.id.photo);
        int size = (int) DimensionsUtil.dpToPx(56,getContext());
        placeholderImage = bitmapFromLayerDrawable(R.drawable.audio_placeholder_image_mono_small, getContext(),size,size);

        ImageView previousButton = findViewById(R.id.previousButton);
        pausePlayButton = findViewById(R.id.playButton);
        ImageView nextButton = findViewById(R.id.nextButton);
        previousButton.setOnClickListener(view -> miracleApp.getPlayerServiceController().actionPrevious());
        nextButton.setOnClickListener(view -> miracleApp.getPlayerServiceController().actionNext());
        miracleApp.getPlayerServiceController().addOnPlayerEventListener(onPlayerEventListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        miracleApp.getPlayerServiceController().removeOnPlayerEventListener(onPlayerEventListener);
        super.onDetachedFromWindow();
    }
}
