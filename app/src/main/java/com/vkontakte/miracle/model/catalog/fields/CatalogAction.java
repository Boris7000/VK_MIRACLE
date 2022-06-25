package com.vkontakte.miracle.model.catalog.fields;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_BUTTON_CREATE_PLAYLIST;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_BUTTON_OPEN_SECTION;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_BUTTON_PLAY_SHUFFLED;

import android.util.Log;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CatalogAction implements ItemDataHolder {
    private String type;
    private String sectionId;
    private String title;
    private String target;
    private String url;
    private String ownerId;
    private final ArrayList<String> targetBlockIds = new ArrayList<>();
    private int refItemsCount;
    private String refLayoutName;
    private String refDataType;

    public String getOwnerId() {
        return ownerId;
    }

    public ArrayList<String> getTargetBlockIds() {
        return targetBlockIds;
    }

    public int getRefItemsCount() {
        return refItemsCount;
    }

    public String getRefLayoutName() {
        return refLayoutName;
    }

    public String getRefDataType() {
        return refDataType;
    }

    public String getType() {
        return type;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getTitle() {
        return title;
    }

    public String getTarget() {
        return target;
    }

    public String getUrl() {
        return url;
    }

    public CatalogAction(JSONObject jsonObject) throws JSONException {

        if(jsonObject.has("action")) {
            JSONObject action = jsonObject.getJSONObject("action");

            type = action.getString("type");

            if (action.has("target")) {
                target = action.getString("target");
            }

            if (action.has("url")) {
                url = action.getString("url");
            }
        }

        if(jsonObject.has("section_id")) {
            sectionId = jsonObject.getString("section_id");
        }

        if(jsonObject.has("title")) {
            title = jsonObject.getString("title");
        }

        if(jsonObject.has("owner_id")){
            ownerId = jsonObject.getString("owner_id");
        }

        if(jsonObject.has("target_block_ids")) {
            JSONArray target_block_ids = jsonObject.getJSONArray("target_block_ids");
            for (int j = 0; j < target_block_ids.length(); j++) {
                targetBlockIds.add(target_block_ids.getString(j));
            }
        }

        if(jsonObject.has("ref_items_count")) {
            refItemsCount = jsonObject.getInt("ref_items_count");
        }

        if(jsonObject.has("ref_layout_name")) {
            refLayoutName = jsonObject.getString("ref_layout_name");
        }

        if(jsonObject.has("ref_data_type")) {
            refDataType = jsonObject.getString("ref_data_type");
        }
    }

    @Override
    public int getViewHolderType() {
        switch (type){
            default:{
                Log.d("ofkokefe",type);
                return R.layout.view_loading_item;
            }
            case "play_shuffled_audios_from_block":{
                return TYPE_BUTTON_PLAY_SHUFFLED;
            }
            case "open_section":{
                return TYPE_BUTTON_OPEN_SECTION;
            }
            case "create_playlist":{
                return TYPE_BUTTON_CREATE_PLAYLIST;
            }
        }
    }
}