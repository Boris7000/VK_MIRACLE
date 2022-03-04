package com.vkontakte.miracle.adapter.wall.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.PlaylistHorizontalViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.TimeUtil;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.fields.LastSeen;

public class ProfileLargeViewHolder extends PlaylistHorizontalViewHolder {

    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final TextView subtitle2;
    private final ViewStub onlineStatusStub;
    private ImageView onlineStatus;
    private final ViewStub verifiedStub;
    private ImageView verified;
    private final ViewStub profileButtonsStub;
    private LinearLayout profileButtonsHolder;
    private final ViewStub userButtonsStub;
    private LinearLayout userButtonsHolder;

    public ProfileLargeViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        subtitle2 = itemView.findViewById(R.id.subtitle2);
        onlineStatusStub = itemView.findViewById(R.id.onlineStatusStub);
        verifiedStub = itemView.findViewById(R.id.verifiedStub);
        profileButtonsStub = itemView.findViewById(R.id.profileButtonsStub);
        userButtonsStub = itemView.findViewById(R.id.userButtonsStub);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        ProfileItem profileItem = (ProfileItem) itemDataHolder;

        title.setText(profileItem.getScreenName());
        subtitle.setText(profileItem.getStatus());


        LastSeen lastSeen = profileItem.getLastSeen();
        if(lastSeen.getPlatform()==7){
            if(onlineStatus!=null&&onlineStatus.getVisibility()!=GONE){
                onlineStatus.setVisibility(GONE);
                onlineStatus.setImageDrawable(null);
            }
        } else {
            if(onlineStatus ==null) {
                if(onlineStatusStub !=null) {
                    onlineStatus = (ImageView) onlineStatusStub.inflate();
                } else {
                    onlineStatus = itemView.findViewById(R.id.onlineStatus);
                }
            }
            onlineStatus.setImageResource(R.drawable.ic_online_mobile_16);
        }

        if(profileItem.isOnline()) {
            subtitle2.setText(getMiracleActivity().getString(R.string.online));
        } else {
            subtitle2.setText(TimeUtil.getOnlineDateString(
                    lastSeen.getTime(),profileItem.getSex(),itemView.getContext()));
        }

        if(profileItem.isVerified()){
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

        Picasso.get().cancelRequest(imageView);

        if(profileItem.getPhotoMax().isEmpty()){
            imageView.setImageDrawable(null);
        } else {
            Picasso.get().load(profileItem.getPhotoMax()).noFade().into(imageView);
        }

        if(profileItem.getId().equals(getMiracleActivity().getUserItem().getId())){
            if(profileButtonsHolder!=null&&profileButtonsHolder.getVisibility()!=GONE) {
                profileButtonsHolder.setVisibility(GONE);
            }
            if(userButtonsHolder==null) {
                if(userButtonsStub!=null) {
                    userButtonsHolder = (LinearLayout) userButtonsStub.inflate();
                } else {
                    userButtonsHolder = itemView.findViewById(R.id.userButtonsHolder);
                }
            }
            if(userButtonsHolder.getVisibility()!=VISIBLE) {
                userButtonsHolder.setVisibility(VISIBLE);
            }
        } else {
            if(userButtonsHolder!=null&&userButtonsHolder.getVisibility()!=GONE) {
                userButtonsHolder.setVisibility(GONE);
            }
            if(profileButtonsHolder==null) {
                if(profileButtonsStub!=null) {
                    profileButtonsHolder = (LinearLayout) profileButtonsStub.inflate();
                } else {
                    profileButtonsHolder = itemView.findViewById(R.id.userButtonsHolder);
                }
            }
            if(profileButtonsHolder.getVisibility()!=VISIBLE) {
                profileButtonsHolder.setVisibility(VISIBLE);
            }
        }

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new ProfileLargeViewHolder(inflater.inflate(R.layout.view_profile_item_large, viewGroup, false));
        }
    }

}
