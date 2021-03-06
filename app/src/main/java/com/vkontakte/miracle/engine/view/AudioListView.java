package com.vkontakte.miracle.engine.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.AudioDialog;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.util.FragmentUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.view.AudioItemView;
import com.vkontakte.miracle.player.AudioPlayerData;
import com.vkontakte.miracle.player.PlayerServiceController;

import java.util.ArrayList;

public class AudioListView extends LinearLayout{

    private final int layoutResId;
    private boolean canApplyChanges = true;
    private ArrayList<ItemDataHolder> audioItems;
    private int measuredWidth = -1;

    public AudioListView(Context context) {
        this(context,null);
    }

    public AudioListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if(attrs!=null){
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AudioListView, 0, 0);
            layoutResId = attributes.getResourceId(R.styleable.AudioListView_listItemLayout, R.layout.view_audio_item_post);
            attributes.recycle();
        } else {
            layoutResId = R.layout.view_audio_item_post;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if(!canApplyChanges) return;

        measuredWidth = MeasureSpec.getSize(widthMeasureSpec);

        for (int p = 0; p < audioItems.size(); p++) {
            View child = getChildAt(p);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
            params.width = measuredWidth;
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(!canApplyChanges) return;

        measuredWidth = MeasureSpec.getSize(getWidth());

        for (int p = 0; p < audioItems.size(); p++) {
            View child = getChildAt(p);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
            params.width = measuredWidth;
            child.layout(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin);
        }
        super.onLayout(changed, l, t, r, b);
    }

    public void setItems(MiracleActivity miracleActivity, ArrayList<ItemDataHolder> audioItems){

        this.audioItems = audioItems;
        MiracleApp miracleApp = miracleActivity.getMiracleApp();

        if (audioItems.isEmpty()){
            return;
        }

        canApplyChanges = false;

        int k = audioItems.size() - getChildCount();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int j = 0;j<k;j++){
            inflater.inflate(layoutResId, this, true);
        }

        for(int i=audioItems.size();i<getChildCount();i++) {
            View child = getChildAt(i);
            if (child.getVisibility()!=GONE){
                child.setVisibility(GONE);
            }
        }

        for (int g=0; g<audioItems.size();g++) {
            View child = getChildAt(g);
            if (child.getVisibility() != View.VISIBLE) {
                child.setVisibility(View.VISIBLE);
            }
            AudioItem audioItem = (AudioItem) audioItems.get(g);
            AudioItemView audioItemView = (AudioItemView)child;

            audioItemView.setValues(audioItem);

            if(audioItem.isLicensed()){
                audioItemView.setOnClickListener(view -> PlayerServiceController.get().
                        playNewAudio(new AudioPlayerData(audioItem)));
            } else {
                audioItemView.setOnClickListener(null);
            }

            audioItemView.setOnLongClickListener(view -> {
                AudioDialog audioDialog = new AudioDialog(miracleActivity, audioItem, miracleActivity.getUserItem());
                audioDialog.setDialogActionListener(new AudioDialogActionListener() {
                    @Override
                    public void add() {

                    }

                    @Override
                    public void remove() {

                    }

                    @Override
                    public void playNext() {
                        PlayerServiceController.get().setPlayNext(new AudioPlayerData(audioItem));
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
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            });

        }

        canApplyChanges = true;
    }

}
