package com.vkontakte.miracle.adapter.wall.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.util.ImageUtil.getOptimalSize;

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
import com.vkontakte.miracle.engine.util.DeviceUtil;
import com.vkontakte.miracle.model.catalog.fields.Image;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.groups.fields.Cover;

public class GroupLargeViewHolder extends MiracleViewHolder {

    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final ViewStub verifiedStub;
    private ImageView verified;
    private ImageView coverImage;

    public GroupLargeViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        verifiedStub = itemView.findViewById(R.id.verifiedStub);
        coverImage = itemView.findViewById(R.id.cover);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        GroupItem groupItem = (GroupItem) itemDataHolder;

        title.setText(groupItem.getScreenName());

        if(groupItem.getStatus().isEmpty()){
            if(subtitle.getVisibility()!=GONE){
                subtitle.setVisibility(GONE);
            }
        } else {
            subtitle.setText(groupItem.getStatus());
            if(subtitle.getVisibility()!=VISIBLE){
                subtitle.setVisibility(VISIBLE);
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

        if(groupItem.getPhotoMax().isEmpty()){
            imageView.setImageDrawable(null);
        } else {
            Picasso.get().load(groupItem.getPhotoMax()).into(imageView);
        }

        Picasso.get().cancelRequest(coverImage);

        if(groupItem.getCover()!=null){
            Cover cover = groupItem.getCover();
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

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new GroupLargeViewHolder(inflater.inflate(R.layout.view_group_item_large, viewGroup, false));
        }
    }

}
