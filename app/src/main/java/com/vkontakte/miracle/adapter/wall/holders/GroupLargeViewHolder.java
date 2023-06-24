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

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.widget.ExtendedMaterialButton;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.executors.groups.JoinToGroup;
import com.vkontakte.miracle.executors.groups.LeaveFromGroup;
import com.vkontakte.miracle.model.groups.GroupItem;

public class GroupLargeViewHolder extends MiracleViewHolder {

    private GroupItem groupItem;
    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final ViewStub verifiedStub;
    private ImageView verified;
    private final ViewStub adminButtonsStub;
    private LinearLayout adminButtonsHolder;
    private final ViewStub defaultButtonsStub;
    private LinearLayout defaultButtonsHolder;
    private ExtendedMaterialButton followButton;

    public GroupLargeViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        verifiedStub = itemView.findViewById(R.id.verifiedStub);
        adminButtonsStub = itemView.findViewById(R.id.adminButtonsStub);
        defaultButtonsStub = itemView.findViewById(R.id.defaultButtonsStub);
    }

    private void initFollowButton(){
        followButton = defaultButtonsHolder.findViewById(R.id.followButton);
        followButton.setOnClickListener(v -> {
            if(groupItem.isMember()){
                resolveLeave();
            } else {
                resolveJoin();
            }
        });
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        groupItem = (GroupItem) itemDataHolder;

        title.setText(groupItem.getName());

        if(!groupItem.getStatus().isEmpty()){
            subtitle.setText(groupItem.getStatus());
            if(subtitle.getVisibility()!=VISIBLE){
                subtitle.setVisibility(VISIBLE);
            }
        } else {
            if(!groupItem.getDescription().isEmpty()){
                subtitle.setText(groupItem.getDescription());
                if(subtitle.getVisibility()!=VISIBLE){
                    subtitle.setVisibility(VISIBLE);
                }
            } else {
                if(subtitle.getVisibility()!=GONE){
                    subtitle.setVisibility(GONE);
                }
            }
        }

        if(groupItem.isVerified()){
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

        if(groupItem.getPhoto200().isEmpty()){
            imageView.setImageDrawable(null);
        } else {
            Picasso.get().load(groupItem.getPhoto200()).into(imageView);
        }

        if(groupItem.isAdmin()){
            if(defaultButtonsHolder!=null&&defaultButtonsHolder.getVisibility()!=GONE) {
                defaultButtonsHolder.setVisibility(GONE);
            }
            if(adminButtonsHolder==null) {
                if(adminButtonsStub!=null) {
                    adminButtonsHolder = (LinearLayout) adminButtonsStub.inflate();
                } else {
                    adminButtonsHolder = itemView.findViewById(R.id.adminButtonsHolder);
                }
            }
            if(adminButtonsHolder.getVisibility()!=VISIBLE) {
                adminButtonsHolder.setVisibility(VISIBLE);
            }
        } else {
            if(adminButtonsHolder!=null&&adminButtonsHolder.getVisibility()!=GONE) {
                adminButtonsHolder.setVisibility(GONE);
            }
            if(defaultButtonsHolder==null) {
                if(defaultButtonsStub!=null) {
                    defaultButtonsHolder = (LinearLayout) defaultButtonsStub.inflate();
                } else {
                    defaultButtonsHolder = itemView.findViewById(R.id.defaultButtonsHolder);
                }
                initFollowButton();
            }
            if(defaultButtonsHolder.getVisibility()!=VISIBLE) {
                defaultButtonsHolder.setVisibility(VISIBLE);
            }

            switchFollowButtonState(groupItem.isMember(), false, true);

        }

    }

    private void resolveJoin(){
        switchFollowButtonState(true,true,false);
        new JoinToGroup(groupItem){
            @Override
            public void onExecute(Boolean object) {
                super.onExecute(object);
                switchFollowButtonState(groupItem.isMember(), false, true);
            }
        }.start();
    }

    private void resolveLeave(){
        switchFollowButtonState(false,true, false);
        new LeaveFromGroup(groupItem){
            @Override
            public void onExecute(Boolean object) {
                super.onExecute(object);
                switchFollowButtonState(groupItem.isMember(), false,true);
            }
        }.start();
    }

    private void switchFollowButtonState(boolean isMember, boolean animate, boolean clickable){
        followButton.setToggled(animate);
        followButton.setClickable(clickable);
        followButton.setChecked(isMember);
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new GroupLargeViewHolder(inflater.inflate(R.layout.view_group_item_large, viewGroup, false));
        }
    }

}
