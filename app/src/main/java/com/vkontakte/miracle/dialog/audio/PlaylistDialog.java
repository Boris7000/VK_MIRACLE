package com.vkontakte.miracle.dialog.audio;

import static com.vkontakte.miracle.engine.util.ImageUtil.bitmapFromLayerDrawable;
import static com.vkontakte.miracle.engine.util.ImageUtil.getAverageHSLFromBitmap;
import static com.vkontakte.miracle.engine.view.PicassoDrawableCopy.setBitmap;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.dialog.MiracleBottomDialog;
import com.vkontakte.miracle.engine.util.DimensionsUtil;
import com.vkontakte.miracle.engine.view.MiracleButton;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.model.users.ProfileItem;

public class PlaylistDialog extends MiracleBottomDialog {

    private View rootView;
    private final PlaylistItem playlistItem;
    private final ProfileItem userItem;
    private PlaylistDialogActionListener dialogActionListener;
    private int color;
    private ImageView imageView;
    private Bitmap placeholderImage;
    private FrameLayout container;
    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            new AsyncExecutor<Boolean>() {
                int averageColor;
                @Override
                public Boolean inBackground() {
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
                    float[] hsl = getAverageHSLFromBitmap(scaled);
                    hsl[1] =  Math.min(hsl[1],0.52f);
                    hsl[2] = Math.max(Math.min(hsl[2],0.43f),0.23f);
                    averageColor = ColorUtils.HSLToColor(hsl);
                    return true;
                }
                @Override
                public void onExecute(Boolean object) {
                    setBitmap(imageView, getContext(), bitmap);
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

    public PlaylistDialog(@NonNull Context context, PlaylistItem playlistItem, ProfileItem userItem) {
        super(context);
        this.playlistItem = playlistItem;
        this.userItem = userItem;
    }

    @Override
    public void show(Context context) {
        setContentView(rootView =  View.inflate(context, R.layout.dialog_audio, null));

        int size = (int) DimensionsUtil.dpToPx(52, context);
        placeholderImage = bitmapFromLayerDrawable(R.drawable.audio_placeholder_image, context, size, size);

        container = rootView.findViewById(R.id.container);

        applyColor(color = ColorUtils.HSLToColor(new float[]{0,0,0.25f}));

        imageView = container.findViewById(R.id.photo);
        TextView title = container.findViewById(R.id.title);
        TextView subtitle = container.findViewById(R.id.subtitle);
        ViewStub explicitStub = container.findViewById(R.id.explicitStub);

        title.setText(playlistItem.getTitle());
        subtitle.setText(playlistItem.getSubtitle());

        createTarget(playlistItem);

        if(playlistItem.isExplicit()){
            explicitStub.inflate();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout linearLayout = rootView.findViewById(R.id.buttonsContainer);
        MiracleButton miracleButton;

        if(!playlistItem.getArtists().isEmpty()){
            miracleButton = (MiracleButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            miracleButton.setText(context.getString(R.string.go_to_artist));
            miracleButton.setImageResource(R.drawable.ic_microphone_28);
            miracleButton.setOnClickListener(view -> {
                dialogActionListener.goToArtist();
                hide();
            });
            linearLayout.addView(miracleButton);
        }


        miracleButton = (MiracleButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
        miracleButton.setText(context.getString(R.string.play_next));
        miracleButton.setImageResource(R.drawable.ic_play_next_28);
        miracleButton.setOnClickListener(view -> {
            dialogActionListener.playNext();
            hide();
        });
        linearLayout.addView(miracleButton);


        if(!((playlistItem.getOriginal()==null&&playlistItem.getOwnerId().equals(userItem.getId()))
                ||(playlistItem.getOriginal()!=null&&playlistItem.getOriginal().getOwnerId().equals(userItem.getId())))){
            if(playlistItem.isFollowing()){
                miracleButton = (MiracleButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
                miracleButton.setText(context.getString(R.string.delete_from_audio));
                miracleButton.setImageResource(R.drawable.ic_cancel_28);
                linearLayout.addView(miracleButton);
            } else {
                miracleButton = (MiracleButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
                miracleButton.setText(context.getString(R.string.add_to_audio));
                miracleButton.setImageResource(R.drawable.ic_list_add_28);
                linearLayout.addView(miracleButton);
            }
        }

        miracleButton.setOnClickListener(v -> {
            if(playlistItem.isFollowing()){
                dialogActionListener.delete();
            } else {
                dialogActionListener.follow();
            }
            hide();
        });

        if(playlistItem.getOwnerId().equals(userItem.getId())){
            miracleButton = (MiracleButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            miracleButton.setText(context.getString(R.string.delete_from_device));
            miracleButton.setImageResource(R.drawable.ic_delete_28);
            linearLayout.addView(miracleButton);
        } else {
            miracleButton = (MiracleButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            miracleButton.setText(context.getString(R.string.download));
            miracleButton.setImageResource(R.drawable.ic_download_28);
            linearLayout.addView(miracleButton);
        }


        MiracleButton cancelButton = rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(view -> hide());

        show();
        expand();
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
        container.getBackground().setColorFilter(colorFilter);
    }

    private void createTarget(PlaylistItem playlistItem){

        if(playlistItem.getPhoto()!=null){
            Photo photo = playlistItem.getPhoto();
            if(photo.getPhoto135()!=null){
                Picasso.get().load(photo.getPhoto135()).into(target);
                return;
            }
        }

        target.onBitmapLoaded(placeholderImage, null);
    }

    public void setDialogActionListener(PlaylistDialogActionListener dialogActionListener) {
        this.dialogActionListener = dialogActionListener;
    }
}
