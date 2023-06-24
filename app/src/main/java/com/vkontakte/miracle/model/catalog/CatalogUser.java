package com.vkontakte.miracle.model.catalog;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_USER;

import android.util.ArrayMap;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.model.catalog.fields.CatalogDescription;
import com.vkontakte.miracle.model.users.ProfileItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CatalogUser implements ItemDataHolder {

    private final String itemId;
    private final ProfileItem profileItem;
    private ArrayList<CatalogDescription> descriptions;
    private ArrayList<CatalogAction> actions;

    public String getItemId() {
        return itemId;
    }

    public ProfileItem getProfileItem() {
        return profileItem;
    }

    public ArrayList<CatalogDescription> getDescriptions() {
        return descriptions;
    }

    public ArrayList<CatalogAction> getActions() {
        return actions;
    }

    public CatalogUser(JSONObject jsonObject, ArrayMap<String, ProfileItem> profilesMap) throws JSONException {

        itemId = jsonObject.getString("item_id");
        profileItem = profilesMap.get(jsonObject.getString("user_id"));

        if(jsonObject.has("descriptions")){
            descriptions = new ArrayList<>();
            JSONArray ja_descriptions = jsonObject.getJSONArray("descriptions");
            for(int i=0; i<ja_descriptions.length();i++){
                descriptions.add(new CatalogDescription(ja_descriptions.getJSONObject(i)));
            }
        }

        if(jsonObject.has("actions")){
            actions = new ArrayList<>();
            JSONArray ja_actions = jsonObject.getJSONArray("actions");

            for (int j = 0; j < ja_actions.length(); j++) {
                JSONObject action = ja_actions.getJSONObject(j);
                actions.add(new CatalogAction(action.getJSONObject("action")));
            }
        }
    }

    @Override
    public int getViewHolderType() {
        return TYPE_CATALOG_USER;
    }
}
