package com.vkontakte.miracle.model;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

public abstract class DataItemWrap<T,H> implements ItemDataHolder {

    private T item;
    private H holder;

    public DataItemWrap(T item, H holder){
        this.item = item;
        this.holder = holder;
    }

    public T getItem() {
        return item;
    }

    public H getHolder() {
        return holder;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public void setHolder(H holder) {
        this.holder = holder;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null){
            if(obj instanceof DataItemWrap){
                DataItemWrap<?,?> dataItemWrap = (DataItemWrap<?,?>) obj;
                Object item = dataItemWrap.getItem();
                if(this.item!=null) {
                    return this.item.equals(item);
                }
            }
        }
        return false;
    }
}
