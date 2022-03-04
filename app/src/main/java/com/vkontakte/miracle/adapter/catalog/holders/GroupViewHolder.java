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
import com.vkontakte.miracle.fragment.wall.FragmentGroup;
import com.vkontakte.miracle.model.groups.GroupItem;

public class GroupViewHolder extends MiracleViewHolder {

    private final ImageView photo;
    private final TextView title;
    private final TextView subtitle;
    private final ViewStub verifiedStub;
    private ImageView verified;

    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        verifiedStub = itemView.findViewById(R.id.verifiedStub);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        GroupItem groupItem = (GroupItem) itemDataHolder;

        title.setText(groupItem.getName());

        if(groupItem.getActivity().isEmpty()){
            if(subtitle.getVisibility()!=GONE) {
                subtitle.setVisibility(GONE);
            }
        } else {
            if(subtitle.getVisibility()!=VISIBLE) {
                subtitle.setVisibility(VISIBLE);
            }
            subtitle.setText(groupItem.getActivity());
        }

        Picasso.get().cancelRequest(photo);

        if(!groupItem.getPhoto200().isEmpty()) {
            Picasso.get().load(groupItem.getPhoto200()).noFade().into(photo);
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

        itemView.setOnClickListener(view -> {
            FragmentGroup fragmentGroup = new FragmentGroup();
            fragmentGroup.setGroupItem(groupItem);
            getMiracleActivity().addFragment(fragmentGroup);
        });

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new GroupViewHolder(inflater.inflate(R.layout.view_group_item, viewGroup, false));
        }
    }

}
