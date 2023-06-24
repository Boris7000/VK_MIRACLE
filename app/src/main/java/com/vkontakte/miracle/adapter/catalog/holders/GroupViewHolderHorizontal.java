package com.vkontakte.miracle.adapter.catalog.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.groups.GroupItem;

public class GroupViewHolderHorizontal extends MiracleViewHolder
        implements View.OnClickListener{

    private final ImageView photo;
    private final TextView title;
    private final ViewStub verifiedStub;
    private ImageView verified;

    public GroupViewHolderHorizontal(@NonNull View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        verifiedStub = itemView.findViewById(R.id.verifiedStub);
        itemView.setOnClickListener(this);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        GroupItem groupItem = (GroupItem) itemDataHolder;

        title.setText(groupItem.getName());

        Picasso.get().cancelRequest(photo);

        if(!groupItem.getPhoto200().isEmpty()) {
            Picasso.get().load(groupItem.getPhoto200()).into(photo);
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
    }

    @Override
    public void onClick(View v) {}
}
