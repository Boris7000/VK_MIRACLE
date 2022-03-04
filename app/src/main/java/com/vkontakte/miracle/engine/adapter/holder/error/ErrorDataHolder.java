package com.vkontakte.miracle.engine.adapter.holder.error;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ERROR;

public class ErrorDataHolder implements ItemDataHolder {

    private final String errorString;

    public ErrorDataHolder(String errorString){
        this.errorString = errorString;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_ERROR;
    }

    public String getErrorString() {
        return errorString;
    }
}
