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
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.model.users.User;

public class PlaylistDialog extends MiracleBottomDialog {

    private final PlaylistItem playlistItem;
    private final User user;
    private PlaylistDialogActionListener dialogActionListener;
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

    public PlaylistDialog(@NonNull Context context, PlaylistItem playlistItem) {
        super(context);
        this.playlistItem = playlistItem;
        this.user = StorageUtil.get().currentUser();
    }

    @Override
    public void show(Context context) {
        View rootView;
        setContentView(rootView =  View.inflate(context, R.layout.dialog_audio, null));

        int size = (int) DimensionsUtil.dpToPx(context,52);
        placeholderImage = bitmapFromLayerDrawable(context,
                R.drawable.audio_placeholder_image_colored_neutral_small, size, size);

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
        ExtendedMaterialButton button;

        if(!playlistItem.getArtists().isEmpty()){
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

        if(playlistItem.getOwner()!=null){
            button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            button.setText(context.getString(R.string.go_to_owner));
            button.setIconResource(R.drawable.ic_link_28, ICON_POS_LEFT);
            button.setIconResource(R.drawable.ic_chevron_24, ICON_POS_RIGHT);
            button.setOnClickListener(view -> {
                dialogActionListener.goToOwner();
                cancel();
            });
            linearLayout.addView(button);
        }


        button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
        button.setText(context.getString(R.string.play_next));
        button.setIconResource(R.drawable.ic_play_next_28, ICON_POS_LEFT);
        button.setOnClickListener(view -> {
            dialogActionListener.playNext();
            cancel();
        });
        linearLayout.addView(button);


        if(!((playlistItem.getOriginal()==null&&playlistItem.getOwnerId().equals(user.getId()))
                ||(playlistItem.getOriginal()!=null&&playlistItem.getOriginal().getOwnerId().equals(user.getId())))){
            button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
            if(playlistItem.isFollowing()){
                button.setText(context.getString(R.string.delete_from_audio));
                button.setIconResource(R.drawable.ic_cancel_28, ICON_POS_LEFT);
            } else {
                button.setText(context.getString(R.string.add_to_audio));
                button.setIconResource(R.drawable.ic_list_add_28, ICON_POS_LEFT);
            }
            button.setOnClickListener(v -> {
                if(playlistItem.isFollowing()){
                    dialogActionListener.delete();
                } else {
                    dialogActionListener.follow();
                }
                cancel();
            });
            linearLayout.addView(button);
        }

        button = (ExtendedMaterialButton) inflater.inflate(R.layout.dialog_button_stub, linearLayout, false);
        if(playlistItem.getOwnerId().equals(user.getId())){
            button.setText(context.getString(R.string.delete_from_device));
            button.setIconResource(R.drawable.ic_delete_28, ICON_POS_LEFT);
        } else {
            button.setText(context.getString(R.string.download));
            button.setIconResource(R.drawable.ic_download_28, ICON_POS_LEFT);
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
