package com.vkontakte.miracle.adapter.catalog.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.groups.GroupItem;

public class GroupViewHolder extends GroupViewHolderHorizontal {

    private final TextView subtitle;

    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);
        subtitle = itemView.findViewById(R.id.subtitle);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        super.bind(itemDataHolder);
        GroupItem groupItem = (GroupItem) itemDataHolder;

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
    }
}
