package com.vkontakte.miracle.fragment.player;

import static com.miracle.engine.util.ColorUtil.getColorByResId;
import static com.miracle.engine.util.ImageUtil.bitmapFromDrawable;
import static com.miracle.engine.util.ImageUtil.bitmapFromLayerDrawable;
import static com.miracle.engine.util.ImageUtil.fastBlur;
import static com.miracle.engine.util.ImageUtil.maskBitmap;
import static com.miracle.engine.util.TimeUtil.getDurationStringMills;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveAdd;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveAddToPlaylist;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveDelete;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveDownload;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveFindArtist;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveGoToAlbum;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveGoToArtist;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolvePlayNext;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.showAudioDialog;
import static com.vkontakte.miracle.engine.view.PicassoDrawableCopy.setBitmap;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.common.Player;

import com.miracle.engine.activity.MiracleActivity;
import com.miracle.engine.async.AsyncExecutor;
import com.miracle.engine.context.ContextExtractor;
import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.MiracleFragment;
import com.miracle.engine.util.DimensionsUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.MainApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.engine.picasso.ATarget;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.service.player.AudioPlayerEventListener;
import com.vkontakte.miracle.service.player.AudioPlayerMedia;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;
import com.vkontakte.miracle.service.player.AudioPlayerState;

import java.util.Locale;

public class FragmentPlayer extends MiracleFragment {

    private MainApp mainApp;
    private final AudioPlayerServiceController audioPlayerServiceController = AudioPlayerServiceController.get();
    private AudioPlayerMedia audioPlayerMedia;
    private AudioPlayerState audioPlayerState;

    private View rootView;
    private ImageView image;
    private ImageView blur;
    private ImageView explicit;
    private TextView title;
    private TextView subtitle;
    private TextView currentTime;
    private TextView remainingTime;
    private ImageView pausePlayButton;
    private ImageView repeatButton;
    private SeekBar seekBar;

    private Drawable play_drawable_48;
    private Drawable pause_drawable_48;
    private Bitmap placeholderImage;
    private Bitmap maskBitmap;

    private boolean seekBarDragging = false;
    private final Locale locale = Locale.getDefault();
    private String previousImageUrl = "none";

    private final AudioPlayerEventListener audioPlayerEventListener = new AudioPlayerEventListener() {

        @Override
        public void onDurationChange(AudioPlayerState audioPlayerState) {
            seekBar.setMax((int) (audioPlayerState.getDuration()/1000));
        }

        @Override
        public void onPlaybackPositionChange(AudioPlayerState audioPlayerState) {
            if(!seekBarDragging) {
                int seekBarMax = seekBar.getMax();
                int progress = (int) (seekBarMax*((float)audioPlayerState.getPlaybackPosition()/audioPlayerState.getDuration()));
                seekBar.setProgress(progress);
                currentTime.setText(getDurationStringMills(locale, audioPlayerState.getPlaybackPosition()));
                remainingTime.setText( String.format(locale,"-%s",getDurationStringMills(locale,
                        audioPlayerState.getDuration()-audioPlayerState.getPlaybackPosition())));
            }
        }

        @Override
        public void onBufferChange(AudioPlayerState audioPlayerState) {
            int seekBarMax = seekBar.getMax();
            int progress = (int) (seekBarMax*((float)audioPlayerState.getBufferedPosition()/audioPlayerState.getDuration()));
            seekBar.setSecondaryProgress(progress);
        }

        @Override
        public void onPlayWhenReadyChange(AudioPlayerState audioPlayerState) {
            onPlayWhenReadyChange(audioPlayerState, true);
        }
        public void onPlayWhenReadyChange(AudioPlayerState audioPlayerState, boolean animate) {
            float alpha;
            float size;
            Drawable drawable;
            if(audioPlayerState.getPlayWhenReady()){
                alpha = 1f;
                size = 1f;
                drawable = pause_drawable_48;
            } else {
                alpha = 0f;
                size = 0.9f;
                drawable = play_drawable_48;
            }
            pausePlayButton.setImageDrawable(drawable);

            if(animate) {
                image.animate().scaleX(size).scaleY(size);
                blur.animate().alpha(alpha);
            } else {
                image.setScaleX(size);
                image.setScaleY(size);
                blur.setAlpha(alpha);
            }
        }

        @Override
        public void onRepeatModeChange(AudioPlayerState audioPlayerState) {
            updateRepeatButton(audioPlayerState.getRepeatMode());
        }

        @Override
        public void onMediaItemChange(AudioPlayerMedia audioPlayerMedia) {
            FragmentPlayer.this.audioPlayerMedia = audioPlayerMedia;
            AudioItem audioItem = audioPlayerMedia.getCurrentAudioItem();

            title.setText(audioItem.getTitle());
            subtitle.setText(audioItem.getArtist());

            if(audioItem.isExplicit()){
                if(explicit.getVisibility()!=View.VISIBLE){
                    explicit.setVisibility(View.VISIBLE);
                }
            } else {
                if(explicit.getVisibility()!=View.GONE){
                    explicit.setVisibility(View.GONE);
                }
            }
            createTarget(audioItem);
        }

        @Override
        public void onPlayerBind(AudioPlayerState audioPlayerState) {
            FragmentPlayer.this.audioPlayerState = audioPlayerState;
            onPlaybackPositionChange(audioPlayerState);
            onBufferChange(audioPlayerState);
            onPlayWhenReadyChange(audioPlayerState, false);
            onRepeatModeChange(audioPlayerState);
        }

        @Override
        public void onPlayerClose() {
            FragmentPlayer.this.audioPlayerMedia = null;
            FragmentPlayer.this.audioPlayerState = null;
        }
    };

    private final Target target = new ATarget() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            new AsyncExecutor<Boolean>() {
                Bitmap blurBitmap;
                @Override
                public Boolean inBackground() {
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 255, 255, false);
                    blurBitmap = fastBlur(scaled, 1, 30);
                    blurBitmap = maskBitmap(blurBitmap, maskBitmap);
                    return true;
                }
                @Override
                public void onExecute(Boolean object) {
                    MainApp mainApp = MainApp.getInstance();
                    setBitmap(blur, mainApp, blurBitmap);
                    setBitmap(image, mainApp, bitmap);
                }
            }.start();
        }
        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            onBitmapLoaded(placeholderImage, null);
        }
    };

    private final OnApplyWindowInsetsListener onApplyWindowInsetsListener = (v, windowInsets) -> {
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        rootView.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        return windowInsets;
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainApp = MainApp.getInstance();
        pause_drawable_48 = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_pause_48,null);
        play_drawable_48 = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_play_48,null);

        int size = (int) DimensionsUtil.dpToPx(mainApp, 280);
        placeholderImage = bitmapFromLayerDrawable(
                mainApp, R.drawable.audio_placeholder_image_colored_large, size, size);
        size = (int) DimensionsUtil.dpToPx(mainApp, 320);
        maskBitmap = bitmapFromDrawable(ResourcesCompat.getDrawable(
                mainApp.getResources(), R.drawable.blur_mask_nine_patch, null), size,size);
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        this.rootView = rootView;
        image = rootView.findViewById(R.id.photo);
        blur = rootView.findViewById(R.id.blur);
        explicit = rootView.findViewById(R.id.explicit);
        title = rootView.findViewById(R.id.title);
        subtitle = rootView.findViewById(R.id.subtitle);
        currentTime = rootView.findViewById(R.id.currentTime);
        remainingTime = rootView.findViewById(R.id.remainingTime);
        pausePlayButton = rootView.findViewById(R.id.playButton);
        repeatButton = rootView.findViewById(R.id.repeatButton);
        seekBar = rootView.findViewById(R.id.seekBar);
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        image.setTag(target);

        subtitle.setOnClickListener(view -> {
            if(audioPlayerMedia!=null){
                AudioItem audioItem = audioPlayerMedia.getCurrentAudioItem();
                DataItemWrap<?,?> itemWrap = audioPlayerMedia.getCurrentWrap();
                if(audioItem.getArtists().isEmpty()){
                    resolveFindArtist(itemWrap, getContext());
                } else {
                    resolveGoToArtist(itemWrap, getContext());
                }
            }
        });

        rootView.findViewById(R.id.previousButton).setOnClickListener(view ->
                audioPlayerServiceController.skipToPrevious());

        pausePlayButton.setOnClickListener(view -> {
            if(audioPlayerState!=null){
                if(audioPlayerState.getPlayWhenReady()){
                    audioPlayerServiceController.pause();
                } else {
                    audioPlayerServiceController.play();
                }
            }
        });

        rootView.findViewById(R.id.nextButton).setOnClickListener(view ->
                audioPlayerServiceController.skipToNext());

        rootView.findViewById(R.id.optionsButton).setOnClickListener(view -> {
            if(audioPlayerMedia!=null) {
                AudioItem audioItem = audioPlayerMedia.getCurrentAudioItem();
                DataItemWrap<?, ?> itemWrap = audioPlayerMedia.getCurrentWrap();
                showAudioDialog(audioItem, getContext(), new AudioDialogActionListener() {
                    @Override
                    public void add() {
                        resolveAdd(itemWrap);
                    }
                    @Override
                    public void delete() {
                        resolveDelete(itemWrap, null);
                    }
                    @Override
                    public void playNext() {
                        resolvePlayNext(itemWrap);
                    }
                    @Override
                    public void addToPlaylist() {
                        resolveAddToPlaylist(itemWrap, getContext());
                    }
                    @Override
                    public void goToAlbum() {
                        resolveGoToAlbum(itemWrap, getContext());
                    }
                    @Override
                    public void goToArtist() {
                        resolveGoToArtist(itemWrap, getContext());
                    }
                    @Override
                    public void findArtist() {
                        resolveFindArtist(itemWrap, getContext());
                    }
                    @Override
                    public void download() {
                        resolveDownload(itemWrap);
                    }
                    @Override
                    public void erase() {
                        resolveDownload(itemWrap);
                    }
                });
            }
        });

        repeatButton.setOnClickListener(view -> {
            if(audioPlayerState!=null) {
                switch (audioPlayerState.getRepeatMode()) {
                    case Player.REPEAT_MODE_OFF: {
                        audioPlayerServiceController.changeRepeatMode(Player.REPEAT_MODE_ONE);
                        break;
                    }
                    case Player.REPEAT_MODE_ONE: {
                        audioPlayerServiceController.changeRepeatMode(Player.REPEAT_MODE_ALL);
                        break;
                    }
                    case Player.REPEAT_MODE_ALL: {
                        audioPlayerServiceController.changeRepeatMode(Player.REPEAT_MODE_OFF);
                        break;
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(audioPlayerState!=null&&fromUser) {
                    int seekBarMax = seekBar.getMax();
                    long newPosition = (long) (((float)progress/seekBarMax)*audioPlayerState.getDuration());
                    currentTime.setText(getDurationStringMills(locale, newPosition));
                    remainingTime.setText(String.format(locale, "-%s", getDurationStringMills(locale,
                            audioPlayerState.getDuration() - newPosition)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarDragging=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioPlayerServiceController.seekToPercent(((float)seekBar.getProgress())/seekBar.getMax());
                seekBarDragging=false;
            }
        });

        MiracleActivity miracleActivity = ContextExtractor.extractMiracleActivity(getContext());
        if(miracleActivity!=null) {
            miracleActivity.addOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        }

        audioPlayerServiceController.addOnPlayerEventListener(audioPlayerEventListener);

    }

    private void createTarget(AudioItem audioItem){
        Picasso.get().cancelRequest(target);
        if(audioItem.getAlbum()!=null){
            Album album = audioItem.getAlbum();
            if(album.getThumb()!=null){
                Photo thumb = album.getThumb();
                if(thumb.getPhoto600()!=null){
                    if(!thumb.getPhoto600().equals(previousImageUrl)) {
                        Picasso.get().load(previousImageUrl = thumb.getPhoto600()).into(target);
                    }
                    return;
                }
            }
        }
        if(!previousImageUrl.equals("")) {
            previousImageUrl = "";
            target.onBitmapLoaded(placeholderImage, null);
        }
    }

    private void updateRepeatButton(int repeatMode){
        switch (repeatMode){
            case Player.REPEAT_MODE_OFF:{
                repeatButton.setImageDrawable(ResourcesCompat.getDrawable(mainApp.getResources(),
                        R.drawable.ic_repeat_24, mainApp.getTheme()));
                repeatButton.setColorFilter(getColorByResId(mainApp, R.color.white_half_50),
                        PorterDuff.Mode.SRC_IN);
                break;
            }
            case Player.REPEAT_MODE_ONE:{
                repeatButton.setImageDrawable(ResourcesCompat.getDrawable(mainApp.getResources(),
                        R.drawable.ic_repeat_one_24, mainApp.getTheme()));
                repeatButton.setColorFilter(getColorByResId(mainApp, R.color.white),
                        PorterDuff.Mode.SRC_IN);
                break;
            }
            case Player.REPEAT_MODE_ALL:{
                repeatButton.setImageDrawable(ResourcesCompat.getDrawable(mainApp.getResources(),
                        R.drawable.ic_repeat_24, mainApp.getTheme()));
                repeatButton.setColorFilter(getColorByResId(mainApp, R.color.white),
                        PorterDuff.Mode.SRC_IN);
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        MiracleActivity miracleActivity = ContextExtractor.extractMiracleActivity(getContext());
        if(miracleActivity!=null) {
            miracleActivity.removeOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        }
        audioPlayerServiceController.removeOnPlayerEventListener(audioPlayerEventListener);
        super.onDestroy();
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public Fragment createFragment() {
            return new FragmentPlayer();
        }
    }

}
