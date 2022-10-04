package com.vkontakte.miracle.model.wall.fields;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WALL_COUNTER;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

public class Counter implements ItemDataHolder {

    private final String dataType;
    private final int count;
    private String ownerId;

    public String getDataType() {
        return dataType;
    }

    public int getCount() {
        return count;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Counter(String dataType, int count) {
        this.dataType = dataType;
        this.count = count;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_WALL_COUNTER;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null){
            if(obj instanceof Counter){
                Counter counter = (Counter) obj;
                return counter.dataType.equals(dataType);
            }
        }
        return false;
    }

    public boolean equalsContent(@Nullable Object obj){
        if(obj!=null){
            if(obj instanceof Counter){
                Counter counter = (Counter) obj;
                return counter.count==count;
            }
        }
        return false;
    }

}
