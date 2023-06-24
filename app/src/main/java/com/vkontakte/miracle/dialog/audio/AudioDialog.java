package com.vkontakte.miracle.dialog.audio;

import static com.miracle.engine.util.ImageUtil.bitmapFromLayerDrawable;
import static com.miracle.widget.ExtendedTextHelper.ICON_POS_LEFT;
import static com.miracle.widget.ExtendedTextHelper.ICON_POS_RIGHT;
import static com.vkontakte.miracle.engine.view.PicassoDrawableCopy.setBitmap;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import com.miracle.engine.dialog.MiracleBottomDialog;
import com.miracle.engine.util.DimensionsUtil;
import com.miracle.widget.ExtendedMaterialButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.picasso.ATarget;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.executors.color.CalculateAverage;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.model.users.User;

public class AudioDialog extends MiracleBottomDialog {

    private final AudioItem audioItem;
    private final User user;
    private AudioDialogActionListener dialogActionListener;
    private int color;
    private ImageView imageView;
    private Bitmap placeholderImage;
    private FrameLayout container;
    private final Target target = new ATarget() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            new CalculateAverage(bitmap){
                @Override
                public void onExecute(Integer object) {
                    setBitmap(imageView, getContext(), bitmap);
                    animateToColor(object);
                }
            }.start();
        }
    };

    public AudioDialog(@NonNull Context context, AudioItem audioItem) {
        super(context);
        this.audioItem = audioItem;
        this.user = StorageUtil.get().currentUser();
    }

    @Override
    public void show(Context context) {
        View rootView;
        setContentView(rootView =  View.inflate(context, R.layout.dialog_audio, null));

        int size = (int) DimensionsUtil.dpToPx(context,52);
        placeholderImage = bitmapFromLayerDrawable(context, R.drawable.audio_placeholder_image_colored_neutral_small, size, size);

        container = rootView.findViewById(R.id.container);

        applyColor(color = ColorUtils.HSLToColor(new float[]{0,0,0.25f}));

        imageView = container.findViewById(R.id.photo);
        TextView title = container.findViewById(R.id.title);
        TextView subtitle = container.findViewById(R.id.subtitle);
        ViewStub explicitStub = container.findViewById(R.id.explicitStub);

        title.setText(audioItem.getTitle());
        subtitle.setText(audioItem.getArtist());

        createTarget(audioItem);

        if(audioItem.isExplicit()){
            explicitStub.inflate();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout linearLayout = rootView.findViewById(R.id.buttonsContainer);
        ExtendedMaterialButton button;

        if(audioItem.getArtists().isEmpty()){
            button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            button.setText(context.getString(R.string.find_artist));
            button.setIconResource(R.drawable.ic_search_28, ICON_POS_LEFT);
            button.setIconResource(R.drawable.ic_chevron_24, ICON_POS_RIGHT);
            button.setOnClickListener(view -> {
                dialogActionListener.findArtist();
                cancel();
            });
            linearLayout.addView(button);
        } else {
            button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            button.setText(context.getString(R.string.go_to_artist));
            button.setIconResource(R.drawable.ic_microphone_28, ICON_POS_LEFT);
            button.setIconResource(R.drawable.ic_chevron_24, ICON_POS_RIGHT);
            button.setOnClickListener(view -> {
                dialogActionListener.goToArtist();
                cancel();
            });
            linearLayout.addView(button);
        }

        if(audioItem.getAlbum()!=null){
            button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            button.setText(context.getString(R.string.go_to_album));
            button.setIconResource(R.drawable.ic_playlist_28, ICON_POS_LEFT);
            button.setIconResource(R.drawable.ic_chevron_24, ICON_POS_RIGHT);
            button.setOnClickListener(view -> {
                dialogActionListener.goToAlbum();
                cancel();
            });
            linearLayout.addView(button);
        }

        if(audioItem.isLicensed()) {
            button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            button.setText(context.getString(R.string.play_next));
            button.setIconResource(R.drawable.ic_play_next_28, ICON_POS_LEFT);
            button.setOnClickListener(view -> {
                dialogActionListener.playNext();
                cancel();
            });
            linearLayout.addView(button);
        }

        button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
        if(audioItem.getOwnerId().equals(user.getId())){
            button.setText(context.getString(R.string.delete_from_audio));
            button.setIconResource(R.drawable.ic_cancel_28, ICON_POS_LEFT);
            button.setOnClickListener(view -> {
                dialogActionListener.delete();
                cancel();
            });
        } else {
            button.setText(context.getString(R.string.add_to_audio));
            button.setIconResource(R.drawable.ic_list_add_28, ICON_POS_LEFT);
            button.setOnClickListener(view -> {
                dialogActionListener.add();
                cancel();
            });
        }
        linearLayout.addView(button);

        button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
        if(audioItem.getDownloaded()!=null){
            button.setText(context.getString(R.string.delete_from_device));
            button.setIconResource(R.drawable.ic_delete_28, ICON_POS_LEFT);
            button.setOnClickListener(view -> {
                dialogActionListener.erase();
                cancel();
            });
        } else {
            button.setText(context.getString(R.string.download));
            button.setIconResource(R.drawable.ic_download_28, ICON_POS_LEFT);
            button.setOnClickListener(view -> {
                dialogActionListener.download();
                cancel();
            });
        }
        linearLayout.addView(button);

        Button cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> cancel());

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

    private void createTarget(AudioItem audioItem){
        if(audioItem.getAlbum()!=null){
            Album album = audioItem.getAlbum();
            if(album.getThumb()!=null){
                Photo thumb = album.getThumb();
                if(thumb.getPhoto135()!=null){
                    Picasso.get().load(thumb.getPhoto135()).into(target);
                    return;
                }
            }
        }
        target.onBitmapLoaded(placeholderImage, null);
    }

    public void setDialogActionListener(AudioDialogActionListener dialogActionListener) {
        this.dialogActionListener = dialogActionListener;
    }
}
