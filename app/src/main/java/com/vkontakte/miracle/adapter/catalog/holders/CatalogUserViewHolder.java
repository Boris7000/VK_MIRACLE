package com.vkontakte.miracle.adapter.catalog.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.fragment.wall.FragmentProfile;
import com.vkontakte.miracle.model.catalog.fields.CatalogDescription;
import com.vkontakte.miracle.model.catalog.CatalogUser;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.fields.OnlineInfo;

import java.util.ArrayList;

public class CatalogUserViewHolder extends MiracleViewHolder {

    private final ImageView imageView;
    private final ViewStub onlineStatusStub;
    private ImageView onlineStatus;
    private final TextView title;
    private final TextView subtitle;
    private final ViewStub verifiedStub;
    private ImageView verified;

    public CatalogUserViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        onlineStatusStub = itemView.findViewById(R.id.onlineStatusStub);
        verifiedStub = itemView.findViewById(R.id.verifiedStub);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        CatalogUser catalogUser = (CatalogUser) itemDataHolder;
        ProfileItem profileItem = catalogUser.getProfileItem();

        title.setText(profileItem.getFullName());

        if(catalogUser.getDescriptions()!=null&&!catalogUser.getDescriptions().isEmpty()){
            ArrayList<CatalogDescription> descriptions = catalogUser.getDescriptions();
            StringBuilder descriptionSB = new StringBuilder();
            for(CatalogDescription des: descriptions){
                descriptionSB.append(des.getText()).append(" ");
            }
            if(subtitle.getVisibility()!=VISIBLE) {
                subtitle.setVisibility(VISIBLE);
            }
            subtitle.setText(descriptionSB.toString());
        } else {
            if(subtitle.getVisibility()!=GONE) {
                subtitle.setVisibility(GONE);
            }
        }


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

            OnlineInfo onlineInfo = profileItem.getOnlineInfo();
            onlineStatus.setImageResource(onlineInfo.isMobile()?R.drawable.ic_online_mobile_16:R.drawable.ic_online_16);
            onlineStatus.setBackgroundResource(onlineInfo.isMobile()?R.drawable.ic_online_mobile_substract_16:R.drawable.ic_online_substract_16);

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

        itemView.setOnClickListener(view -> {
            FragmentProfile fragmentProfile = new FragmentProfile();
            fragmentProfile.setProfileItem(profileItem);
            getMiracleActivity().addFragment(fragmentProfile);
        });

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogUserViewHolder(inflater.inflate(R.layout.view_profile_item, viewGroup, false));
        }
    }

}
