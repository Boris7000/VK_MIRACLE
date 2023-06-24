package com.vkontakte.miracle.model.catalog;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_SUGGESTION;

import com.miracle.engine.adapter.holder.ItemDataHolder;

import org.json.JSONException;
import org.json.JSONObject;

public class CatalogSuggestion implements ItemDataHolder {

    private final String id;
    private final String title;
    private final String subtitle;
    private final String context;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getContext() {
        return context;
    }

    public CatalogSuggestion(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        title = jsonObject.getString("title");
        subtitle = jsonObject.getString("subtitle");
        context = jsonObject.getString("context");
    }

    @Override
    public int getViewHolderType() {
        return TYPE_CATALOG_SUGGESTION;
    }
}
