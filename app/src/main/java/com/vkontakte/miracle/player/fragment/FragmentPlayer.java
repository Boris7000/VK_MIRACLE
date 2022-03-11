package com.vkontakte.miracle.player.fragment;

import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByResId;
import static com.vkontakte.miracle.engine.util.ImageUtil.bitmapFromLayerDrawable;
import static com.vkontakte.miracle.engine.util.ImageUtil.fastBlur;
import static com.vkontakte.miracle.engine.util.ImageUtil.getAverageHSLFromBitmap;
import static com.vkontakte.miracle.engine.view.PicassoDrawableCopy.setBitmap;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.exoplayer2.Player;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.AudioDialog;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.fragment.tabs.NestedMiracleFragment;
import com.vkontakte.miracle.engine.fragment.tabs.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.engine.util.DimensionsUtil;
import com.vkontakte.miracle.engine.util.FragmentUtil;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.util.TimeUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.player.AudioPlayerData;
import com.vkontakte.miracle.player.OnPlayerEventListener;

import java.util.Locale;

public class FragmentPlayer extends NestedMiracleFragment {

    private int color;
    private String previousImageUrl = " ";

    private View rootView;
    private MiracleActivity miracleActivity;
    private MiracleApp miracleApp;

    private Drawable play_drawable_48;
    private Drawable pause_drawable_48;
    private Bitmap placeholderImage;

    private boolean seekBarDragging = false;

    private ImageView image;
    private ImageView blur;
    private ImageView explicit;
    private TextView title;
    private TextView subtitle;
    private TextView currentTime;
    private TextView remainingTime;
    private ImageView pausePlayButton;
    private ImageView optionsButton;
    private ImageView repeatButton;
    private SeekBar seekBar;
    private final Locale locale = Locale.getDefault();
    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            new AsyncExecutor<Boolean>() {
                Bitmap blurBitmap;
                int averageColor;
                @Override
                public Boolean inBackground() {
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
                    blurBitmap = fastBlur(scaled, 1, 6);
                    float[] hsl = getAverageHSLFromBitmap(scaled);
                    float[] hslEdited = new float[]{hsl[0],
                            Math.min(hsl[1],0.52f), Math.max(Math.min(hsl[2],0.43f),0.18f)};
                    if(Math.abs(hsl[2]-hslEdited[2])<0.035f){
                        hslEdited[2]-=0.06f;
                    }
                    averageColor = ColorUtils.HSLToColor(hslEdited);
                    return true;
                }
                @Override
                public void onExecute(Boolean object) {
                    setBitmap(blur, getMiracleActivity(), blurBitmap);
                    setBitmap(image, getMiracleActivity(), bitmap);
                    animateToColor(averageColor);
                }
            }.start();
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    private final OnPlayerEventListener onPlayerEventListener = new OnPlayerEventListener() {
        @Override
        public void onBufferChange(AudioPlayerData playerData) {
            int secondProgress = (int)(1000*((float)playerData.getBufferedPosition()/(float)playerData.getDuration()));
            seekBar.setSecondaryProgress(secondProgress);
        }

        @Override
        public void onPlaybackPositionChange(AudioPlayerData playerData) {

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
            AudioItem audioItem = playerData.getCurrentItem();

            title.setText(audioItem.getTitle());
            subtitle.setText(audioItem.getArtist());

            //TODO добавить переход к исполнителю

            if(audioItem.isExplicit()){
                if(explicit.getVisibility()!=View.VISIBLE){
                    explicit.setVisibility(View.VISIBLE);
                }
            } else {
                if(explicit.getVisibility()!=View.GONE){
                    explicit.setVisibility(View.GONE);
                }
            }

            optionsButton.setOnClickListener(view -> {
                AudioDialog audioDialog = new AudioDialog(getMiracleActivity(), audioItem,
                        getMiracleActivity().getUserItem());
                audioDialog.setDialogActionListener(new AudioDialogActionListener() {
                    @Override
                    public void add() {

                    }

                    @Override
                    public void remove() {

                    }

                    @Override
                    public void playNext() {
                        getMiracleApp().getPlayerServiceController().
                                setPlayNext(new AudioPlayerData(playerData.getCurrentItem()));
                    }

                    @Override
                    public void addToPlaylist() {

                    }

                    @Override
                    public void goToAlbum() {
                        FragmentUtil.goToAlbum(audioItem,miracleActivity);
                    }

                    @Override
                    public void goToArtist() {
                        FragmentUtil.goToArtist(audioItem,miracleActivity);
                    }
                });
                audioDialog.show(view.getContext());
            });

            createTarget(audioItem);
        }

        @Override
        public void onPlayWhenReadyChange(AudioPlayerData playerData, boolean animate) {
            pausePlayButton.setImageDrawable(playerData.getPlayWhenReady()?
                    pause_drawable_48:play_drawable_48);

            pausePlayButton.setOnClickListener(playerData.getPlayWhenReady()?
                    view -> miracleApp.getPlayerServiceController().actionPause() :
                    view -> miracleApp.getPlayerServiceController().actionResume());
        }

        @Override
        public void onRepeatModeChange(AudioPlayerData playerData) {
            updateRepeatButton(playerData.getRepeatMode());
        }

        @Override
        public void onPlayerClose() {

        }
    };
    private final OnApplyWindowInsetsListener onApplyWindowInsetsListener = (v, windowInsets) -> {
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        rootView.setPadding(0,insets.top,0,insets.bottom);
        return windowInsets;
    };
    private ViewPager2 viewPager2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        iniContext();
        miracleActivity = getMiracleActivity();
        miracleApp = getMiracleApp();
        rootView = inflater.inflate(R.layout.fragment_player, container, false);

        SettingsUtil settingsUtil = miracleApp.getSettingsUtil();

        pause_drawable_48 = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_pause_48,null);
        play_drawable_48 = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_play_48,null);

        int size = (int) DimensionsUtil.dpToPx(280,miracleActivity);
        placeholderImage = bitmapFromLayerDrawable(R.drawable.audio_placeholder_image_large, miracleActivity,size,size);

        image = rootView.findViewById(R.id.photo);
        image.setTag(target);
        blur = rootView.findViewById(R.id.blur);
        explicit = rootView.findViewById(R.id.explicit);
        title = rootView.findViewById(R.id.title);
        subtitle = rootView.findViewById(R.id.subtitle);
        currentTime = rootView.findViewById(R.id.currentTime);
        remainingTime = rootView.findViewById(R.id.remainingTime);
        optionsButton = rootView.findViewById(R.id.optionsButton);
        repeatButton = rootView.findViewById(R.id.repeatButton);
        seekBar = rootView.findViewById(R.id.seekBar);
        ImageView previousButton = rootView.findViewById(R.id.previousButton);
        pausePlayButton = rootView.findViewById(R.id.playButton);
        ImageView nextButton = rootView.findViewById(R.id.nextButton);

        miracleActivity.addOnApplyWindowInsetsListener(onApplyWindowInsetsListener);

        previousButton.setOnClickListener(view -> miracleApp.getPlayerServiceController().actionPrevious());
        nextButton.setOnClickListener(view -> miracleApp.getPlayerServiceController().actionNext());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarDragging=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                miracleApp.getPlayerServiceController()
                        .actionSeekToPercent(((float)seekBar.getProgress())/1000f);
                seekBarDragging=false;
            }
        });

        updateRepeatButton(settingsUtil.playerRepeatMode());

        repeatButton.setOnClickListener(view -> {
            switch (settingsUtil.playerRepeatMode()){
                case Player.REPEAT_MODE_OFF:{
                    miracleApp.getPlayerServiceController().actionChangeRepeatMode(Player.REPEAT_MODE_ONE);
                    break;
                }
                case Player.REPEAT_MODE_ONE:{
                    miracleApp.getPlayerServiceController().actionChangeRepeatMode(Player.REPEAT_MODE_ALL);
                    break;
                }
                case Player.REPEAT_MODE_ALL:{
                    miracleApp.getPlayerServiceController().actionChangeRepeatMode(Player.REPEAT_MODE_OFF);
                    break;
                }
            }
        });

        viewPager2 = miracleActivity.getViewPager2();

        applyColor(color = ColorUtils.HSLToColor(new float[]{0,0,0.13f}));

        getMiracleApp().getPlayerServiceController().addOnPlayerEventListener(onPlayerEventListener);

       return rootView;
    }

    private void animateToColor(int toColor) {
        final int old = color;
        ValueAnimator animator = ValueAnimator.ofArgb(old, toColor);
        animator.addUpdateListener(animation -> applyColor(color = (int) animation.getAnimatedValue()));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
    }

    private void applyColor(int color){
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        blur.getForeground().setColorFilter(colorFilter);
        viewPager2.getBackground().setColorFilter(colorFilter);
    }

    private void createTarget(AudioItem audioItem){
        Picasso.get().cancelRequest(target);
        if(audioItem.getAlbum()!=null){
            Album album = audioItem.getAlbum();
            if(album.getThumb()!=null){
                Photo thumb = album.getThumb();
                if(thumb.getPhoto600()!=null){
                    if(!thumb.getPhoto600().equals(previousImageUrl)) {
                        previousImageUrl = thumb.getPhoto600();
                        Picasso.get().load(thumb.getPhoto600()).into(target);
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
                repeatButton.setColorFilter(getColorByResId(miracleApp,R.color.white_half_50),
                        PorterDuff.Mode.SRC_IN);
                break;
            }
            case Player.REPEAT_MODE_ONE:{
                repeatButton.setImageDrawable(ResourcesCompat.getDrawable(miracleApp.getResources(),
                        R.drawable.ic_repeat_one_24, miracleApp.getTheme()));
                repeatButton.setColorFilter(getColorByResId(miracleApp,R.color.white),
                        PorterDuff.Mode.SRC_IN);
                break;
            }
            case Player.REPEAT_MODE_ALL:{
                repeatButton.setImageDrawable(ResourcesCompat.getDrawable(miracleApp.getResources(),
                        R.drawable.ic_repeat_24, miracleApp.getTheme()));
                repeatButton.setColorFilter(getColorByResId(miracleApp,R.color.white),
                        PorterDuff.Mode.SRC_IN);
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        miracleActivity.removeOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        getMiracleApp().getPlayerServiceController().removeOnPlayerEventListener(onPlayerEventListener);
        super.onDestroy();
    }

    public static class Fabric extends NestedMiracleFragmentFabric {

        public Fabric(){
            super("", -1);
        }

        @NonNull
        @Override
        public NestedMiracleFragment createFragment() {
            return new FragmentPlayer();
        }
    }
}
