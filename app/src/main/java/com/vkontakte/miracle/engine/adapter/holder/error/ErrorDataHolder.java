package com.vkontakte.miracle.engine.adapter.holder.error;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ERROR;

public class ErrorDataHolder implements ItemDataHolder {

    private String errorString = "";
    private int errorStringResource = 0;

    public ErrorDataHolder(String errorString){
        this.errorString = errorString;
    }
    public ErrorDataHolder(int errorStringResource){
        this.errorStringResource = errorStringResource;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_ERROR;
    }

    public String getErrorString() {
        return errorString;
    }

    public int getErrorStringResource() {
        return errorStringResource;
    }
}
