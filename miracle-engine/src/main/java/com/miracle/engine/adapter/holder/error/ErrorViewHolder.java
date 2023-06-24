package com.miracle.engine.adapter.holder.error;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.R;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;

public class ErrorViewHolder extends MiracleViewHolder {

    private final TextView textView;

    public ErrorViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.errorText);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        if(itemDataHolder instanceof ErrorDataHolder) {
            ErrorDataHolder errorDataHolder = (ErrorDataHolder) itemDataHolder;
            if(!errorDataHolder.getErrorString().isEmpty()){
                textView.setText(((ErrorDataHolder) itemDataHolder).getErrorString());
            } else {
                if(errorDataHolder.getErrorStringResource()!=0) {
                    textView.setText(errorDataHolder.getErrorStringResource());
                }
            }

        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new ErrorViewHolder(inflater.inflate(R.layout.view_error_item, viewGroup, false));
        }
    }

}
