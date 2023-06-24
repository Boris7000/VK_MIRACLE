package com.vkontakte.miracle.adapter.wall.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.miracle.engine.util.TimeUtil.getDateString;
import static com.vkontakte.miracle.engine.util.StringsUtil.reduceTheNumber;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_PHOTO;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.material.transition.MaterialContainerTransform;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.recycler.MiracleViewRecycler;
import com.miracle.widget.ExtendedMaterialButton;
import com.miracle.widget.ExtendedTextView;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.adapter.photos.holders.PhotoGridItemViewHolder;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.engine.view.PostTextView;
import com.vkontakte.miracle.engine.view.RecycleListView;
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
    private RecycleListView audiosView;
    private final ViewStub audiosViewStub;
    private ImageView verified;

    private final ExtendedMaterialButton likeButton;
    private final ExtendedMaterialButton commentButton;
    private final ExtendedMaterialButton repostButton;
    private final ExtendedTextView viewsCounter;

    private final LinearLayout ownerDataHolder;

    private PostItem postItem;
    private Owner owner;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        FrameLayout header = itemView.findViewById(R.id.post_header);
        ownerDataHolder = header.findViewById(R.id.post_owner_link);

        imageView = ownerDataHolder.findViewById(R.id.photo);
        title = ownerDataHolder.findViewById(R.id.title);
        date = ownerDataHolder.findViewById(R.id.date);
        verifiedStub = ownerDataHolder.findViewById(R.id.verifiedStub);
        textStub = itemView.findViewById(R.id.textStub);
        photosViewStub = itemView.findViewById(R.id.photosViewStub);
        audiosViewStub = itemView.findViewById(R.id.audiosViewStub);

        likeButton = itemView.findViewById(R.id.likeButton);
        commentButton = itemView.findViewById(R.id.commentButton);
        repostButton = itemView.findViewById(R.id.repostButton);
        viewsCounter = itemView.findViewById(R.id.viewsCounter);


        likeButton.setOnClickListener(view -> {
            final PostItem finalPostItem = PostViewHolder.this.postItem;
            if(finalPostItem.getUserLikes()){
                postItem.setUserLikes(false);
                postItem.setLikesCount(Math.max(0, postItem.getLikesCount()-1));
                resolveDeleteLike();
            } else {
                postItem.setUserLikes(true);
                postItem.setLikesCount(postItem.getLikesCount()+1);
                resolveLike();
            }
            updateLikesCount(true);
        });

        ownerDataHolder.setOnClickListener(view -> NavigationUtil.goToOwner(owner, getContext()));

        header.setOnClickListener(view -> NavigationUtil.goToWall(postItem, getContext()));


    }

    private void stubText(){
        if(text==null) {
            if(textStub!=null) {
                text = (PostTextView) textStub.inflate();
            } else {
                text = itemView.findViewById(R.id.text);
            }
            text.setParent((ViewGroup) itemView.getParent());
            text.getPostTextView().setOnOwnerClickListener(ownerLink ->
                    NavigationUtil.goToOwner(ownerLink, getContext()));
        }
    }

    private void stubVerified(){
        if(verified==null) {
            if(verifiedStub!=null) {
                verified = (ImageView) verifiedStub.inflate();
            } else {
                verified = itemView.findViewById(R.id.verified);
            }
        }
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        postItem = (PostItem) itemDataHolder;

        owner = postItem.getFrom();
        if(owner==null){
            owner = postItem.getSource();
        }
        if(owner==null){
            owner = postItem.getOwner();
        }

        title.setText(owner.getName());

        date.setText(getDateString(itemView.getContext(), postItem.getDate()));

        if(!owner.getPhoto200().isEmpty()) {
            Picasso.get().load(owner.getPhoto200()).into(imageView);
        }

        if(owner.isVerified()){
            stubVerified();
            if(verified.getVisibility()!=VISIBLE) {
                verified.setVisibility(VISIBLE);
            }
        } else {
            if(verified!=null&&verified.getVisibility()!=GONE){
                verified.setVisibility(GONE);
            }
        }

        if(!postItem.getText().isEmpty()){
            stubText();
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
                    photoGridView.getViewHolderFabricMap().put(TYPE_WRAPPED_PHOTO,
                            new PhotoGridItemViewHolder.Fabric());
                    MiracleViewRecycler miracleViewRecycler =
                            getBindingMiracleAdapter().getMiracleViewRecycler(itemDataHolder.getViewHolderType());
                    miracleViewRecycler.setMaxRecycledViews(TYPE_WRAPPED_PHOTO, 20);
                    photoGridView.setViewRecycler(miracleViewRecycler);
                }
                if(photoGridViewHolder.getVisibility()!=VISIBLE) {
                    photoGridViewHolder.setVisibility(VISIBLE);
                }
                photoGridView.setItems(attachments.getMediaItems());
            } else {
                if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                    photoGridView.setItems(attachments.getMediaItems());
                    photoGridViewHolder.setVisibility(GONE);
                }
            }

            if(!attachments.getAudioItems().isEmpty()){
                if(audiosView==null) {
                    if(photosViewStub!=null) {
                        audiosView = (RecycleListView) audiosViewStub.inflate();
                    } else {
                        audiosView = itemView.findViewById(R.id.audiosView);
                    }
                    audiosView.getViewHolderFabricMap().put(TYPE_WRAPPED_AUDIO,
                            new WrappedAudioViewHolder.FabricPost());
                    MiracleViewRecycler miracleViewRecycler =
                            getBindingMiracleAdapter().getMiracleViewRecycler(itemDataHolder.getViewHolderType());
                    miracleViewRecycler.setMaxRecycledViews(TYPE_WRAPPED_AUDIO, 15);
                    audiosView.setViewRecycler(miracleViewRecycler);
                }

                if(audiosView.getVisibility()!=VISIBLE) {
                    audiosView.setVisibility(VISIBLE);
                }
                audiosView.setItems(attachments.getAudioItems(), false);
            }else {
                if(audiosView!=null&&audiosView.getVisibility()!=GONE){
                    audiosView.clearItems();
                    audiosView.setVisibility(GONE);
                }
            }

        } else {
            if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                photoGridView.setItems(new ArrayList<>());
                photoGridViewHolder.setVisibility(GONE);
            }
            if(audiosView!=null&&audiosView.getVisibility()!=GONE){
                audiosView.clearItems();
                audiosView.setVisibility(GONE);
            }
        }

        bindButtons(postItem);

    }

    private void bindButtons(PostItem postItem){

        switchLikeButtonState(postItem.getUserLikes());

        updateLikesCount();

        if(postItem.getCommentsCount()>0) {
            commentButton.setText(reduceTheNumber(postItem.getCommentsCount()));
        } else {
            commentButton.setText("");
        }

        if(postItem.getRepostsCount()>0) {
            repostButton.setText(reduceTheNumber(postItem.getRepostsCount()));
        } else {
            repostButton.setText("");
        }

        viewsCounter.setText(reduceTheNumber(postItem.getViewsCount()));
    }

    private void updateLikesCount(){
        updateLikesCount(false);
    }

    private void updateLikesCount(boolean animate){
        if(animate){

            Transition transition = new AutoTransition();
            transition.setDuration(200);
            transition.addTarget((View) likeButton.getParent());
            TransitionManager.beginDelayedTransition((ViewGroup) likeButton.getParent().getParent(), transition);
            transition = new AutoTransition();
            transition.setDuration(200);
            transition.addTarget(likeButton);
            transition.addTarget(commentButton);
            transition.addTarget(repostButton);
            TransitionManager.beginDelayedTransition((ViewGroup) likeButton.getParent(), transition);

        }

        if(postItem.getLikesCount()>0) {
            likeButton.setText(reduceTheNumber(postItem.getLikesCount()));
        } else {
            likeButton.setText("");
        }
    }

    private void resolveLike(){
        switchLikeButtonState(true,true);
        likeButton.setClickable(false);
        final PostItem finalPostItem = PostViewHolder.this.postItem;
        new AddLike(postItem){
            @Override
            public void onExecute(Boolean object) {
                super.onExecute(object);
                if(object) {
                    if (finalPostItem.getId().equals(postItem.getId())) {
                        updateLikesCount(true);
                        switchLikeButtonState(postItem.getUserLikes());
                    }
                }
            }
        }.start();
    }

    private void resolveDeleteLike(){
        switchLikeButtonState(false,true);
        likeButton.setClickable(false);
        final PostItem finalPostItem = PostViewHolder.this.postItem;
        new DeleteLike(postItem){
            @Override
            public void onExecute(Boolean object) {
                super.onExecute(object);
                if(object) {
                    if (finalPostItem.getId().equals(postItem.getId())) {
                        updateLikesCount(true);
                        switchLikeButtonState(postItem.getUserLikes());
                    }
                }
            }
        }.start();
    }

    private void switchLikeButtonState(boolean liked){
        switchLikeButtonState(liked, false);
    }

    private void switchLikeButtonState(boolean liked, boolean animate){
        likeButton.setToggled(animate);
        likeButton.setClickable(true);
        likeButton.setChecked(liked);
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PostViewHolder(inflater.inflate(R.layout.view_post_item, viewGroup, false));
        }
    }

}
