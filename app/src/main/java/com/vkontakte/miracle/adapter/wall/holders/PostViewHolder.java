package com.vkontakte.miracle.adapter.wall.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PHOTO;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToOwner;
import static com.vkontakte.miracle.engine.util.StringsUtil.reduceTheNumber;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.MiracleViewRecycler;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.ColorUtil;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.engine.util.TimeUtil;
import com.vkontakte.miracle.engine.view.AudioListView;
import com.vkontakte.miracle.engine.view.PostTextView;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoGridView;
import com.vkontakte.miracle.executors.wall.AddLike;
import com.vkontakte.miracle.executors.wall.DeleteLike;
import com.vkontakte.miracle.model.Attachments;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.wall.PostItem;

import java.util.ArrayList;

public class PostViewHolder extends MiracleViewHolder {

    private final ImageView imageView;
    private PostTextView text;
    private final ViewStub textStub;
    private final TextView title;
    private final TextView date;
    private final ViewStub verifiedStub;
    private PhotoGridView photoGridView;
    private CardView photoGridViewHolder;
    private final ViewStub photosViewStub;
    private AudioListView audiosView;
    private final ViewStub audiosViewStub;
    private ImageView verified;

    private final FrameLayout header;
    private final LinearLayout likesHolder;
    private final LinearLayout commentsHolder;
    private final LinearLayout repostsHolder;
    private final LinearLayout viewsHolder;
    private final LinearLayout ownerDataHolder;
    private final ImageView likesIcon;
    private final TextView likesText;
    private final TextView commentsText;
    private final TextView repostsText;
    private final TextView viewsText;

    private PostItem postItem;
    private Owner owner;

    private int color;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);



        header = itemView.findViewById(R.id.post_header);

        ownerDataHolder = header.findViewById(R.id.post_owner_link);

        imageView = ownerDataHolder.findViewById(R.id.photo);
        title = ownerDataHolder.findViewById(R.id.title);
        date = ownerDataHolder.findViewById(R.id.date);
        verifiedStub = ownerDataHolder.findViewById(R.id.verifiedStub);
        textStub = itemView.findViewById(R.id.textStub);
        photosViewStub = itemView.findViewById(R.id.photosViewStub);
        audiosViewStub = itemView.findViewById(R.id.audiosViewStub);

        likesHolder = itemView.findViewById(R.id.likesHolder);
        commentsHolder = itemView.findViewById(R.id.commentsHolder);
        repostsHolder = itemView.findViewById(R.id.repostsHolder);
        viewsHolder = itemView.findViewById(R.id.viewsHolder);

        likesIcon = likesHolder.findViewById(R.id.likesIcon);
        likesText = likesHolder.findViewById(R.id.likesText);
        commentsText = commentsHolder.findViewById(R.id.commentsText);
        repostsText = repostsHolder.findViewById(R.id.repostsText);
        viewsText = viewsHolder.findViewById(R.id.viewsText);

        likesHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PostItem finalPostItem = PostViewHolder.this.postItem;
                if(finalPostItem.getUserLikes()){
                    postItem.setUserLikes(false);
                    postItem.setLikesCount(Math.max(0, postItem.getLikesCount()-1));
                    new DeleteLike(postItem){
                        @Override
                        public void onExecute(Boolean object) {
                            super.onExecute(object);
                            if(object) {
                                if (finalPostItem.getId().equals(postItem.getId())) {
                                    updateLikesCount(true);
                                }
                            }
                        }
                    }.start();
                } else {
                    postItem.setUserLikes(true);
                    postItem.setLikesCount(postItem.getLikesCount()+1);
                    new AddLike(postItem){
                        @Override
                        public void onExecute(Boolean object) {
                            super.onExecute(object);
                            if(object) {
                                if (finalPostItem.getId().equals(postItem.getId())) {
                                    updateLikesCount(true);
                                }
                            }
                        }
                    }.start();
                }
                updateLikesCount(true);
                colorLikesButton(true);
            }
        });

        ownerDataHolder.setOnClickListener(view -> goToOwner(owner, getMiracleActivity()));

        header.setOnClickListener(view -> NavigationUtil.goToWall(postItem, getMiracleActivity()));


    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        super.bind(itemDataHolder);

        postItem = (PostItem) itemDataHolder;

        owner = postItem.getFrom();
        if(owner==null){
            owner = postItem.getSource();
        }
        if(owner==null){
            owner = postItem.getOwner();
        }

        title.setText(owner.getName());

        date.setText(TimeUtil.getPostDateString(itemView.getContext(), postItem.getDate()));

        if(!owner.getPhoto200().isEmpty()) {
            Picasso.get().load(owner.getPhoto200()).into(imageView);
        }

        if(owner.isVerified()){
            if(verified==null) {
                if(verifiedStub!=null) {
                    verified = (ImageView) verifiedStub.inflate();
                } else {
                    verified = itemView.findViewById(R.id.verified);
                }
            }
            if(verified.getVisibility()!=VISIBLE) {
                verified.setVisibility(VISIBLE);
            }
        } else {
            if(verified!=null&&verified.getVisibility()!=GONE){
                verified.setVisibility(GONE);
            }
        }

        if(!postItem.getText().isEmpty()){
            if(text==null) {
                if(textStub!=null) {
                    text = (PostTextView) textStub.inflate();
                } else {
                    text = itemView.findViewById(R.id.text);
                }
                text.setParent((ViewGroup) itemView.getParent());
            }
            if(text.getVisibility()!=VISIBLE) {
                text.setVisibility(VISIBLE);
            }
            text.setText(postItem.getText(),postItem.getZoomText());
        } else {
            if(text!=null&&text.getVisibility()!=GONE){
                text.setText("",false);
                text.setVisibility(GONE);
            }
        }

        if(postItem.getAttachments()!=null){
            Attachments attachments = postItem.getAttachments();
            if(!attachments.getMediaItems().isEmpty()){
                if(photoGridView==null) {
                    if(photosViewStub!=null) {
                        photoGridViewHolder = (CardView) photosViewStub.inflate();
                    } else {
                        photoGridViewHolder = itemView.findViewById(R.id.photosView);
                    }
                    photoGridView = (PhotoGridView) photoGridViewHolder.getChildAt(0);
                    MiracleViewRecycler miracleViewRecycler =
                            getMiracleAdapter().getMiracleViewRecycler(itemDataHolder.getViewHolderType());
                    miracleViewRecycler.setMaxRecycledViews(TYPE_PHOTO, 15);
                    photoGridView.setRecycledViewPool(miracleViewRecycler);
                }
                if(photoGridViewHolder.getVisibility()!=VISIBLE) {
                    photoGridViewHolder.setVisibility(VISIBLE);
                }
                photoGridView.setPhotos(attachments.getMediaItems());
            } else {
                if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                    photoGridView.setPhotos(attachments.getMediaItems());
                    photoGridViewHolder.setVisibility(GONE);
                }
            }

            if(!attachments.getAudioItems().isEmpty()){
                if(audiosView==null) {
                    if(photosViewStub!=null) {
                        audiosView = (AudioListView) audiosViewStub.inflate();
                    } else {
                        audiosView = itemView.findViewById(R.id.audiosView);
                    }
                    MiracleViewRecycler miracleViewRecycler =
                            getMiracleAdapter().getMiracleViewRecycler(itemDataHolder.getViewHolderType());
                    miracleViewRecycler.setMaxRecycledViews(TYPE_WRAPPED_AUDIO, 15);
                    audiosView.setRecycledViewPool(miracleViewRecycler);
                }

                if(audiosView.getVisibility()!=VISIBLE) {
                    audiosView.setVisibility(VISIBLE);
                }
                audiosView.setItems(attachments.getAudioItems());
            }else {
                if(audiosView!=null&&audiosView.getVisibility()!=GONE){
                    audiosView.setItems(attachments.getAudioItems());
                    audiosView.setVisibility(GONE);
                }
            }

        } else {
            if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                photoGridView.setPhotos(new ArrayList<>());
                photoGridViewHolder.setVisibility(GONE);
            }
            if(audiosView!=null&&audiosView.getVisibility()!=GONE){
                audiosView.setItems( new ArrayList<>());
                audiosView.setVisibility(GONE);
            }
        }

        bindButtons(postItem);

    }

    private void bindButtons(PostItem postItem){

        updateLikesCount();

        colorLikesButton();

        if(postItem.getCommentsCount()>0){
            if(commentsHolder.getVisibility()!=VISIBLE){
                commentsHolder.setVisibility(VISIBLE);
            }
            if(commentsText.getVisibility()!=VISIBLE){
                commentsText.setVisibility(VISIBLE);
            }
            commentsText.setText(reduceTheNumber(postItem.getCommentsCount()));
        } else {
            if(!postItem.getCanPostComments()&&!postItem.getCanViewComments()){
                if(commentsHolder.getVisibility()!=GONE){
                    commentsHolder.setVisibility(GONE);
                }
            } else {
                if(commentsHolder.getVisibility()!=VISIBLE){
                    commentsHolder.setVisibility(VISIBLE);
                }
                if (commentsText.getVisibility() != GONE) {
                    commentsText.setVisibility(GONE);
                }
            }
        }

        if(postItem.getRepostsCount()>0){
            if(repostsText.getVisibility()!=VISIBLE){
                repostsText.setVisibility(VISIBLE);
            }
            repostsText.setText(reduceTheNumber(postItem.getRepostsCount()));
        } else {
            if(repostsText.getVisibility()!=GONE){
                repostsText.setVisibility(GONE);
            }
        }

        viewsText.setText(reduceTheNumber(postItem.getViewsCount()));
    }

    private void updateLikesCount(){
        updateLikesCount(false);
    }

    private void updateLikesCount(boolean animate){

        if(animate){
            Transition transition = new Slide();
            transition.setDuration(200);
            transition.addTarget(likesText);
            TransitionManager.beginDelayedTransition(likesHolder, transition);
            transition = new AutoTransition();
            transition.setDuration(200);
            transition.addTarget(likesIcon);
            transition.addTarget(likesHolder);
            transition.addTarget(commentsHolder);
            transition.addTarget(repostsHolder);
            transition.addTarget((View) likesHolder.getParent());
            TransitionManager.beginDelayedTransition((ViewGroup) likesHolder.getParent().getParent(), transition);
        }

        if(postItem.getLikesCount()>0){
            if(likesText.getVisibility()!=VISIBLE){
                likesText.setVisibility(VISIBLE);
            }
            likesText.setText(reduceTheNumber(postItem.getLikesCount()));
        } else {
            if(likesText.getVisibility()!=GONE){
                likesText.setVisibility(GONE);
            }
        }
    }

    private void colorLikesButton(){
        colorLikesButton(false);
    }

    private void colorLikesButton(boolean animate){
        Context context = itemView.getContext();

        if(postItem.getUserLikes()){

            int onLikeButton = ColorUtil.getColorByAttributeId(context.getTheme(), R.attr.colorOnLikeButton);
            int likeButton = ColorUtil.getColorByAttributeId(context.getTheme(), R.attr.colorLikeButton);

            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.ic_like_filled_24, context.getTheme());
            if(drawable!=null) {
                drawable = DrawableCompat.wrap(drawable).mutate();
                drawable.setColorFilter(new PorterDuffColorFilter(onLikeButton, PorterDuff.Mode.SRC_IN));
            }

            likesIcon.setImageDrawable(drawable);
            likesText.setTextColor(onLikeButton);


            if(animate) {
                animateToColor(likeButton);
                Animation bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.like_bounce);
                likesIcon.startAnimation(bounceAnimation);
            } else {
                this.color = likeButton;
                PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(likeButton, PorterDuff.Mode.SRC_IN);
                likesHolder.getBackground().setColorFilter(colorFilter);
            }
        } else {

            int onLikeButton = ColorUtil.getColorByAttributeId(context.getTheme(), R.attr.colorOnButtonNeutral);
            int likeButton = ColorUtil.getColorByAttributeId(context.getTheme(), R.attr.colorButtonNeutral);

            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.ic_like_24, context.getTheme());

            if(drawable!=null) {
                drawable = DrawableCompat.wrap(drawable).mutate();
                drawable.setColorFilter(new PorterDuffColorFilter(onLikeButton, PorterDuff.Mode.SRC_IN));
            }

            likesIcon.setImageDrawable(drawable);
            likesText.setTextColor(onLikeButton);


            if(animate) {
                animateToColor(likeButton);
                Animation bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.like_bounce);
                likesIcon.startAnimation(bounceAnimation);
            } else {
                this.color = likeButton;
                PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(likeButton, PorterDuff.Mode.SRC_IN);
                likesHolder.getBackground().setColorFilter(colorFilter);
            }
        }
    }

    private void animateToColor(int toColor){
        int old = color;
        ValueAnimator animator = ValueAnimator.ofArgb(old, toColor);
        animator.addUpdateListener(animation -> {
            color = (int) animation.getAnimatedValue();
            PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
            likesHolder.getBackground().setColorFilter(colorFilter);
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200);
        animator.start();
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PostViewHolder(inflater.inflate(R.layout.view_post_item, viewGroup, false));
        }
    }

}
