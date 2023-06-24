package com.vkontakte.miracle.adapter.friends.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.fields.LastSeen;
import com.vkontakte.miracle.model.users.fields.OnlineInfo;

public class ProfileViewHolder extends MiracleViewHolder
        implements View.OnClickListener{

    private final ImageView imageView;
    private final ViewStub onlineStatusStub;
    private ImageView onlineStatus;
    private final TextView title;
    private final ViewStub verifiedStub;
    private ImageView verified;
    private ProfileItem profileItem;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        TextView subtitle = itemView.findViewById(R.id.subtitle);
        subtitle.setVisibility(GONE);
        onlineStatusStub = itemView.findViewById(R.id.onlineStatusStub);
        verifiedStub = itemView.findViewById(R.id.verifiedStub);
        itemView.setOnClickListener(this);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        profileItem = (ProfileItem) itemDataHolder;

        title.setText(profileItem.getFullName());

        Picasso.get().cancelRequest(imageView);

        if(!profileItem.getPhoto200().isEmpty()) {
            Picasso.get().load(profileItem.getPhoto200()).into(imageView);
        }

        if(profileItem.isOnline()) {
            if(onlineStatus ==null) {
                if(onlineStatusStub !=null) {
                    onlineStatus = (ImageView) onlineStatusStub.inflate();
                } else {
                    onlineStatus = itemView.findViewById(R.id.onlineStatus);
                }
            }
            if(onlineStatus.getVisibility()!=VISIBLE) {
                onlineStatus.setVisibility(VISIBLE);
            }

            if(profileItem.getOnlineInfo()!=null) {
                OnlineInfo onlineInfo = profileItem.getOnlineInfo();
                onlineStatus.setImageResource(onlineInfo.isMobile() ? R.drawable.ic_online_mobile_16 : R.drawable.ic_online_16);
                onlineStatus.setBackgroundResource(onlineInfo.isMobile() ? R.drawable.ic_online_mobile_subtract_16 : R.drawable.ic_online_subtract_16);
            } else {
                if(profileItem.getLastSeen()!=null){
                    LastSeen lastSeen = profileItem.getLastSeen();
                    if(lastSeen!=null) {
                        if (lastSeen.getPlatform() == 7) {
                            onlineStatus.setImageResource(R.drawable.ic_online_16);
                            onlineStatus.setBackgroundResource(R.drawable.ic_online_subtract_16);
                        } else {
                            onlineStatus.setImageResource(R.drawable.ic_online_mobile_16);
                            onlineStatus.setBackgroundResource(R.drawable.ic_online_mobile_subtract_16);
                        }
                    }
                }
            }
        } else {
            if(onlineStatus !=null&& onlineStatus.getVisibility()!=GONE){
                onlineStatus.setVisibility(GONE);
            }
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
    }

    @Override
    public void onClick(View v) {
        NavigationUtil.goToProfile(profileItem, getContext());
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new ProfileViewHolder(
                    inflater.inflate(R.layout.view_profile_item, viewGroup, false));
        }
    }
}
