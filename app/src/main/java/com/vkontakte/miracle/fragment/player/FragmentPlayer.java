package com.vkontakte.miracle.fragment.player;

import static com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder.resolveAdd;
import static com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder.resolveDelete;
import static com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder.resolveDownload;
import static com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder.resolvePlayNext;
import static com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder.showAudioDialog;
import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByResId;
import static com.vkontakte.miracle.engine.util.ImageUtil.bitmapFromLayerDrawable;
import static com.vkontakte.miracle.engine.util.ImageUtil.fastBlur;
import static com.vkontakte.miracle.engine.util.ImageUtil.maskBitmap;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.exoplayer2.Player;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.engine.activity.MiracleActivity;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.context.ContextExtractor;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.picasso.ATarget;
import com.vkontakte.miracle.engine.util.DimensionsUtil;
import com.vkontakte.miracle.engine.util.ImageUtil;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.util.TimeUtil;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.service.player.AOnPlayerEventListener;
import com.vkontakte.miracle.service.player.AudioPlayerData;
import com.vkontakte.miracle.service.player.OnPlayerEventListener;
import com.vkontakte.miracle.service.player.PlayerServiceController;

import java.util.Locale;

public class FragmentPlayer extends MiracleFragment {

    private MiracleApp miracleApp;
    private final PlayerServiceController playerServiceController = PlayerServiceController.get();
    private AudioPlayerData playerData;

    private View rootView;
    private ImageView image;
    private ImageView blur;
    private ImageView explicit;
    private TextView title;
    private TextView subtitle;
    private TextView currentTime;
    private TextView remainingTime;
    private ImageView previousButton;
    private ImageView pausePlayButton;
    private ImageView nextButton;
    private ImageView optionsButton;
    private ImageView repeatButton;
    private SeekBar seekBar;

    private Drawable play_drawable_48;
    private Drawable pause_drawable_48;
    private Bitmap placeholderImage;
    private Bitmap maskBitmap;

    private boolean seekBarDragging = false;
    private final Locale locale = Locale.getDefault();
    private String previousImageUrl = "none";

    private final OnPlayerEventListener onPlayerEventListener = new AOnPlayerEventListener() {
        @Override
        public void onBufferChange(AudioPlayerData playerData) {
            FragmentPlayer.this.playerData = playerData;
            int secondProgress = (int)(1000*((float)playerData.getBufferedPosition()/(float)playerData.getDuration()));
            seekBar.setSecondaryProgress(secondProgress);
        }

        @Override
        public void onPlaybackPositionChange(AudioPlayerData playerData) {
            FragmentPlayer.this.playerData = playerData;
            if(!seekBarDragging) {
                int progress = (int) (1000 * ((float) playerData.getCurrentPosition() / (float) playerData.getDuration()));
                seekBar.setProgress(progress);
            }
            currentTime.setText(TimeUtil.getDurationStringMills(locale,playerData.getCurrentPosition()));
            remainingTime.setText( String.format(locale,"-%s",TimeUtil.getDurationStringMills(locale,
                    playerData.getDuration()-playerData.getCurrentPosition())));
        }

        @Override
        public void onSongChange(AudioPlayerData playerData, boolean animate) {
            FragmentPlayer.this.playerData = playerData;
            AudioItem audioItem = playerData.getCurrentItem();

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
        public void onPlayWhenReadyChange(AudioPlayerData playerData, boolean animate) {
            FragmentPlayer.this.playerData = playerData;
            float alpha;
            float size;
            Drawable drawable;
            if(playerData.getPlayWhenReady()){
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
        public void onRepeatModeChange(AudioPlayerData playerData) {
            FragmentPlayer.this.playerData = playerData;
            updateRepeatButton(playerData.getRepeatMode());
        }

        @Override
        public void onPlayerClose() {
            FragmentPlayer.this.playerData = null;
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
                    MiracleApp miracleApp = MiracleApp.getInstance();
                    setBitmap(blur, miracleApp, blurBitmap);
                    setBitmap(image, miracleApp, bitmap);
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

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        miracleApp = MiracleApp.getInstance();

        rootView = super.onCreateView(inflater, container, savedInstanceState);

        pause_drawable_48 = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_pause_48,null);
        play_drawable_48 = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_play_48,null);

        int size = (int) DimensionsUtil.dpToPx(miracleApp, 280);
        placeholderImage = bitmapFromLayerDrawable(
                miracleApp, R.drawable.audio_placeholder_image_colored_large,
                size, size);
        size = (int) DimensionsUtil.dpToPx(miracleApp, 320);
        maskBitmap = ImageUtil.bitmapFromDrawable(ResourcesCompat.getDrawable(
                miracleApp.getResources(), R.drawable.blur_mask_nine_patch, null),
                size,size);

        MiracleActivity miracleActivity = ContextExtractor.extractMiracleActivity(getContext());
        if(miracleActivity!=null) {
            miracleActivity.addOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        }

        playerServiceController.addOnPlayerEventListener(onPlayerEventListener);

        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        image = rootView.findViewById(R.id.photo);
        blur = rootView.findViewById(R.id.blur);
        explicit = rootView.findViewById(R.id.explicit);
        title = rootView.findViewById(R.id.title);
        subtitle = rootView.findViewById(R.id.subtitle);
        currentTime = rootView.findViewById(R.id.currentTime);
        remainingTime = rootView.findViewById(R.id.remainingTime);
        previousButton = rootView.findViewById(R.id.previousButton);
        pausePlayButton = rootView.findViewById(R.id.playButton);
        nextButton = rootView.findViewById(R.id.nextButton);
        optionsButton = rootView.findViewById(R.id.optionsButton);
        repeatButton = rootView.findViewById(R.id.repeatButton);
        seekBar = rootView.findViewById(R.id.seekBar);
    }

    @Override
    public void initViews() {
        super.initViews();
        image.setTag(target);

        subtitle.setOnClickListener(view -> {
            if(playerData!=null){
                AudioItem audioItem = playerData.getCurrentItem();
                DataItemWrap<?,?> itemWrap = playerData.getCurrentItemWrap();
                if(audioItem.getArtists().isEmpty()){
                    NavigationUtil.goToArtistSearch(itemWrap, getContext());
                } else {
                    NavigationUtil.goToArtist(itemWrap, getContext());
                }
            }
        });
        previousButton.setOnClickListener(view -> {
            if(playerServiceController!=null) {
                playerServiceController.actionPrevious();
            }
        });
        pausePlayButton.setOnClickListener(view -> {
            if (playerData!=null && playerServiceController!=null) {
                if (playerData.getPlayWhenReady()) {
                    playerServiceController.actionPause();
                } else {
                    playerServiceController.actionResume();
                }
            }
        });
        nextButton.setOnClickListener(view -> {
            if(playerServiceController!=null) {
                playerServiceController.actionNext();
            }
        });
        optionsButton.setOnClickListener(view -> {
            if(playerData!=null){
                AudioItem audioItem = playerData.getCurrentItem();
                DataItemWrap<?,?> itemWrap = playerData.getCurrentItemWrap();
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

                    }

                    @Override
                    public void goToAlbum() {
                        NavigationUtil.goToAlbum(itemWrap, getContext());
                    }

                    @Override
                    public void goToArtist() {
                        NavigationUtil.goToArtist(itemWrap, getContext());
                    }

                    @Override
                    public void findArtist() {
                        NavigationUtil.goToArtistSearch(itemWrap, getContext());
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
            switch (SettingsUtil.get().playerRepeatMode()){
                case Player.REPEAT_MODE_OFF:{
                    playerServiceController.actionChangeRepeatMode(Player.REPEAT_MODE_ONE);
                    break;
                }
                case Player.REPEAT_MODE_ONE:{
                    playerServiceController.actionChangeRepeatMode(Player.REPEAT_MODE_ALL);
                    break;
                }
                case Player.REPEAT_MODE_ALL:{
                    playerServiceController.actionChangeRepeatMode(Player.REPEAT_MODE_OFF);
                    break;
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarDragging=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(playerServiceController!=null) {
                    playerServiceController.actionSeekToPercent(((float) seekBar.getProgress()) / 1000f);
                }
                seekBarDragging=false;
            }
        });
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
                repeatButton.setImageDrawable(ResourcesCompat.getDrawable(miracleApp.getResources(),
                        R.drawable.ic_repeat_24, miracleApp.getTheme()));
                repeatButton.setColorFilter(getColorByResId(miracleApp, R.color.white_half_50),
                        PorterDuff.Mode.SRC_IN);
                break;
            }
            case Player.REPEAT_MODE_ONE:{
                repeatButton.setImageDrawable(ResourcesCompat.getDrawable(miracleApp.getResources(),
                        R.drawable.ic_repeat_one_24, miracleApp.getTheme()));
                repeatButton.setColorFilter(getColorByResId(miracleApp, R.color.white),
                        PorterDuff.Mode.SRC_IN);
                break;
            }
            case Player.REPEAT_MODE_ALL:{
                repeatButton.setImageDrawable(ResourcesCompat.getDrawable(miracleApp.getResources(),
                        R.drawable.ic_repeat_24, miracleApp.getTheme()));
                repeatButton.setColorFilter(getColorByResId(miracleApp, R.color.white),
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
        playerServiceController.removeOnPlayerEventListener(onPlayerEventListener);
        super.onDestroy();
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public MiracleFragment createFragment() {
            return new FragmentPlayer();
        }
    }

}
