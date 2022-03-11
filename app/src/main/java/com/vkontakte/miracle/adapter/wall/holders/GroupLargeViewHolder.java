package com.vkontakte.miracle.adapter.wall.holders;

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
import com.vkontakte.miracle.adapter.audio.holders.PlaylistHorizontalViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.groups.GroupItem;

public class GroupLargeViewHolder extends PlaylistHorizontalViewHolder {

    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final TextView subtitle2;
    private final ViewStub onlineStatusStub;
    private ImageView onlineStatus;
    private final ViewStub verifiedStub;
    private ImageView verified;

    public GroupLargeViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        subtitle2 = itemView.findViewById(R.id.subtitle2);
        onlineStatusStub = itemView.findViewById(R.id.onlineStatusStub);
        verifiedStub = itemView.findViewById(R.id.verifiedStub);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        GroupItem groupItem = (GroupItem) itemDataHolder;

        title.setText(groupItem.getScreenName());
        subtitle.setText(groupItem.getStatus());

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

        if(groupItem.getPhotoMax().isEmpty()){
            imageView.setImageDrawable(null);
        } else {
            Picasso.get().load(groupItem.getPhotoMax()).into(imageView);
        }

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new GroupLargeViewHolder(inflater.inflate(R.layout.view_group_item_large, viewGroup, false));
        }
    }

}
