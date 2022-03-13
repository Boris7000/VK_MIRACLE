package com.vkontakte.miracle.engine.view.bottomNavigation;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.player.AudioPlayerData;
import com.vkontakte.miracle.player.OnPlayerEventListener;
import com.vkontakte.miracle.player.PlayerServiceController;

public class MusicMiracleBottomNavigationItem extends FrameLayout implements MiracleBottomNavigationItem {

    private final OnPlayerEventListener onPlayerEventListener = new OnPlayerEventListener() {
        @Override
        public void onBufferChange(AudioPlayerData playerData) {

        }

        @Override
        public void onPlaybackPositionChange(AudioPlayerData playerData) {
            int progress = (int)(100*((float)playerData.getCurrentPosition()/(float)playerData.getDuration()));
            progressBar.setProgress(progress);
        }

        @Override
        public void onSongChange(AudioPlayerData playerData, boolean animate) {
            showProgressBar();
        }

        @Override
        public void onPlayWhenReadyChange(AudioPlayerData playerData, boolean animate) {

        }

        @Override
        public void onRepeatModeChange(AudioPlayerData playerData) {

        }

        @Override
        public void onPlayerClose() {
           hideProgressBar();
        }
    };
    private SimpleMiracleBottomNavigationItem simpleMiracleBottomNavigationItem;
    private ProgressBar progressBar;

    public MusicMiracleBottomNavigationItem(@NonNull Context context) {
        this(context,null);
    }

    public MusicMiracleBottomNavigationItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MusicMiracleBottomNavigationItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        progressBar = findViewById(R.id.progressBar);
        simpleMiracleBottomNavigationItem = findViewById(R.id.icon);
        PlayerServiceController.get().addOnPlayerEventListener(onPlayerEventListener);
    }

    @Override
    public void select(int color) {
        simpleMiracleBottomNavigationItem.select(color);
    }

    @Override
    public void unselect(int color) {
        simpleMiracleBottomNavigationItem.unselect(color);
    }

    public void showProgressBar(){
        if(progressBar.getAlpha()!=1) {
            progressBar.animate().alpha(1).setDuration(200).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    progressBar.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).start();
        }
    }

    public void hideProgressBar(){
        if(progressBar.getAlpha()!=0) {
            progressBar.animate().alpha(0).setDuration(200).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    progressBar.setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        PlayerServiceController.get().removeOnPlayerEventListener(onPlayerEventListener);
        super.onDetachedFromWindow();
    }
}
