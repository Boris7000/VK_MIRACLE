package com.vkontakte.miracle.adapter.wall.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.util.ImageUtil.getOptimalSize;
import static com.vkontakte.miracle.engine.util.StringsUtil.reduceTheNumber;

import android.content.Context;
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
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.DeviceUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.util.TimeUtil;
import com.vkontakte.miracle.model.catalog.fields.Image;
import com.vkontakte.miracle.model.wall.fields.Cover;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.fields.LastSeen;

public class ProfileLargeViewHolder extends MiracleViewHolder {

    private final ImageView image;
    private final ImageView coverImage;
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
    private final ProfileItem userItem;

    public ProfileLargeViewHolder(@NonNull View itemView) {
        super(itemView);
        userItem = StorageUtil.get().currentUser();
        image = itemView.findViewById(R.id.photo);
        coverImage = itemView.findViewById(R.id.cover);
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

        Context context = itemView.getContext();

        ProfileItem profileItem = (ProfileItem) itemDataHolder;

        title.setText(profileItem.getFullName());

        if(profileItem.getStatus().isEmpty()){
            if(subtitle.getVisibility()!=GONE){
                subtitle.setVisibility(GONE);
            }
        } else {
            subtitle.setText(profileItem.getStatus());
            if(subtitle.getVisibility()!=VISIBLE){
                subtitle.setVisibility(VISIBLE);
            }
        }


        LastSeen lastSeen = profileItem.getLastSeen();
        if(lastSeen!=null) {
            if (lastSeen.getPlatform() == 7) {
                if (onlineStatus != null && onlineStatus.getVisibility() != GONE) {
                    onlineStatus.setVisibility(GONE);
                    onlineStatus.setImageDrawable(null);
                }
            } else {
                if (onlineStatus == null) {
                    if (onlineStatusStub != null) {
                        onlineStatus = (ImageView) onlineStatusStub.inflate();
                    } else {
                        onlineStatus = itemView.findViewById(R.id.onlineStatus);
                    }
                }
                onlineStatus.setImageResource(R.drawable.ic_online_mobile_16);
            }

            if (profileItem.isOnline()) {
                subtitle2.setText(context.getString(R.string.online));
            } else {
                subtitle2.setText(TimeUtil.getOnlineDateString(context,
                        lastSeen.getTime(), profileItem.getSex()));
            }
        } else {
            if (onlineStatus != null && onlineStatus.getVisibility() != GONE) {
                onlineStatus.setVisibility(GONE);
                onlineStatus.setImageDrawable(null);
            }
            subtitle2.setText(TimeUtil.getOnlineDateString(context,
                    System.currentTimeMillis()/1000L, profileItem.getSex()));
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

        Picasso.get().cancelRequest(image);

        if(profileItem.getPhotoMax().isEmpty()){
            image.setImageDrawable(null);
        } else {
            Picasso.get().load(profileItem.getPhotoMax()).into(image);
        }


        if(profileItem.getId().equals(userItem.getId())){
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

        Picasso.get().cancelRequest(coverImage);

        if(profileItem.getCover()!=null){
            Cover cover = profileItem.getCover();
            if(cover.getEnabled()){
                if(!cover.getImages().isEmpty()) {
                    Image image = getOptimalSize(cover.getImages(),
                            itemView.getWidth() == 0 ? DeviceUtil.getDisplayWidth(itemView.getContext()) : itemView.getWidth(),
                            itemView.getHeight());
                    if (image != null) {
                        Picasso.get().load(image.getUrl()).into(coverImage);
                    }
                }
            }
        }

    }

    private  static boolean processCounter(int count, LinearLayout holder, TextView counter){
        if(count!=0){
            if(holder.getVisibility()!=VISIBLE) {
                holder.setVisibility(VISIBLE);
            }
            counter.setText(reduceTheNumber(count));
            return true;
        } else {
            if(holder.getVisibility()!=GONE) {
                holder.setVisibility(GONE);
            }
            return false;
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new ProfileLargeViewHolder(inflater.inflate(R.layout.view_profile_item_large, viewGroup, false));
        }
    }

}
