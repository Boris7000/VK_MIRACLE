package com.vkontakte.miracle.model.groups.wraps;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_GROUP;

import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.ItemDataWrapFabric;
import com.vkontakte.miracle.model.groups.GroupItem;

public class GroupItemWrapFabric implements ItemDataWrapFabric<GroupItem, GroupItemWC> {
    @Override
    public DataItemWrap<GroupItem, GroupItemWC> create(GroupItem item, GroupItemWC holder) {
        return new DataItemWrap<GroupItem, GroupItemWC>(item, holder) {
            @Override
            public int getViewHolderType() {
                return TYPE_WRAPPED_GROUP;
            }
        };
    }
}
