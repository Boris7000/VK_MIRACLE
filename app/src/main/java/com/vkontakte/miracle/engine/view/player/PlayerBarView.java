package com.vkontakte.miracle.engine.view.player;

import static com.miracle.engine.util.ImageUtil.bitmapFromLayerDrawable;

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

import com.miracle.engine.util.DimensionsUtil;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.service.player.AudioPlayerEventListener;
import com.vkontakte.miracle.service.player.AudioPlayerMedia;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;
import com.vkontakte.miracle.service.player.AudioPlayerState;

public class PlayerBarView extends FrameLayout {

    private final AudioPlayerEventListener audioPlayerEventListener = new AudioPlayerEventListener() {
        @Override
        public void onPlayWhenReadyChange(AudioPlayerState audioPlayerState) {
            pausePlayButton.setImageDrawable(audioPlayerState.getPlayWhenReady()?
                    pause_drawable_28:play_drawable_28);

            pausePlayButton.setOnClickListener(audioPlayerState.getPlayWhenReady()?
                    view -> audioPlayerServiceController.pause() :
                    view -> audioPlayerServiceController.play());
        }

        @Override
        public void onMediaItemChange(AudioPlayerMedia audioPlayerMedia) {
            AudioItem audioItem = audioPlayerMedia.getCurrentAudioItem();

            title.setText(audioItem.getTitle());
            subtitle.setText(audioItem.getArtist());

            createTarget(audioItem);
        }

        @Override
        public void onPlayerBind(AudioPlayerState audioPlayerState) {
            onPlayWhenReadyChange(audioPlayerState);
        }
    };
    private final AudioPlayerServiceController audioPlayerServiceController = AudioPlayerServiceController.get();
    private TextView title;
    private TextView subtitle;
    private ImageView image;
    private final Drawable play_drawable_28;
    private final Drawable pause_drawable_28;
    private Bitmap placeholderImage;
    private String previousImageUrl = " ";
    private ImageView pausePlayButton;

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
        int size = (int) DimensionsUtil.dpToPx(getContext(),56);
        placeholderImage = bitmapFromLayerDrawable(getContext(),
                R.drawable.audio_placeholder_image_mono_small, size, size);

        ImageView previousButton = findViewById(R.id.previousButton);
        pausePlayButton = findViewById(R.id.playButton);
        ImageView nextButton = findViewById(R.id.nextButton);
        ImageView closeButton = findViewById(R.id.closeButton);
        previousButton.setOnClickListener(view -> audioPlayerServiceController.skipToPrevious());
        nextButton.setOnClickListener(view -> audioPlayerServiceController.skipToNext());
        closeButton.setOnClickListener(view -> audioPlayerServiceController.stop());
        audioPlayerServiceController.addOnPlayerEventListener(audioPlayerEventListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        audioPlayerServiceController.removeOnPlayerEventListener(audioPlayerEventListener);
        super.onDetachedFromWindow();
    }
}
