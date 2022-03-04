package com.vkontakte.miracle.engine.view.catalog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;

public class CatalogHeaderButton extends FrameLayout {

    private final LayoutInflater layoutInflater;
    private TextView simpleText;
    private TextView dropdownText;
    private View clearButton;

    public CatalogHeaderButton(@NonNull Context context) {
        this(context, null);
    }

    public CatalogHeaderButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        layoutInflater = LayoutInflater.from(context);
    }


    public void setUpWithCatalogAction(CatalogAction catalogAction){

        switch (catalogAction.getType()){
            case "open_section":{
                if(clearButton!=null&&clearButton.getVisibility()!=GONE) {
                    clearButton.setVisibility(GONE);
                }

                if(dropdownText!=null&&dropdownText.getVisibility()!=GONE) {
                    dropdownText.setVisibility(GONE);
                }

                if(simpleText==null){
                    simpleText = (TextView) layoutInflater.inflate(R.layout.catalog_header_button,
                            this, false);
                    addView(simpleText);
                }

                if(simpleText.getVisibility()!=VISIBLE) {
                    simpleText.setVisibility(VISIBLE);
                }

                simpleText.setText(catalogAction.getTitle());
                break;
            }
            case "clear_recent_groups":{

                if(simpleText!=null&&simpleText.getVisibility()!=GONE) {
                    simpleText.setVisibility(GONE);
                }

                if(dropdownText!=null&&dropdownText.getVisibility()!=GONE) {
                    dropdownText.setVisibility(GONE);
                }

                if (clearButton == null) {
                    clearButton = layoutInflater.inflate(R.layout.catalog_header_button_clear, this, false);
                    addView(clearButton);
                }

                if(clearButton.getVisibility()!=VISIBLE) {
                    clearButton.setVisibility(VISIBLE);
                }
                //ClearRecentGroupsAction clearRecentGroupsAction = (ClearRecentGroupsAction) catalogAction;
                break;
            }

            case "select_sorting":{
                if(simpleText!=null&&simpleText.getVisibility()!=GONE) {
                    simpleText.setVisibility(GONE);
                }

                if(clearButton!=null&&clearButton.getVisibility()!=GONE) {
                    clearButton.setVisibility(GONE);
                }

                if (dropdownText == null) {
                    dropdownText = (TextView) layoutInflater.inflate(R.layout.catalog_header_button_dropdown, this, false);
                    addView(dropdownText);
                }

                if(dropdownText.getVisibility()!=VISIBLE) {
                    dropdownText.setVisibility(VISIBLE);
                }

                dropdownText.setText(catalogAction.getTitle());
                break;
            }
            case "friends_lists": {
                if(simpleText!=null&&simpleText.getVisibility()!=GONE) {
                    simpleText.setVisibility(GONE);
                }

                if(clearButton!=null&&clearButton.getVisibility()!=GONE) {
                    clearButton.setVisibility(GONE);
                }

                if (dropdownText == null) {
                    dropdownText = (TextView) layoutInflater.inflate(R.layout.catalog_header_button_dropdown, this, false);
                    addView(dropdownText);
                }

                if(dropdownText.getVisibility()!=VISIBLE) {
                    dropdownText.setVisibility(VISIBLE);
                }

                dropdownText.setText(catalogAction.getTitle());
                break;
            }
        }
    }
}
