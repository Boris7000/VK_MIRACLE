package com.vkontakte.miracle.model.catalog;

import static com.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ERROR;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_ARTIST_BANNER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_BANNER_SLIDER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_CATEGORIES_LIST;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_HEADER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_HEADER_EXTENDED;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_SEPARATOR;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_SLIDER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_TRIPLE_STACKED_SLIDER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_HORIZONTAL_BUTTONS;

import android.util.Log;

import androidx.annotation.Nullable;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWF;
import com.vkontakte.miracle.model.audio.wraps.LoadableAudioItemWC;
import com.vkontakte.miracle.model.audio.wraps.PlaylistItemWC;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.model.catalog.fields.CatalogBadge;
import com.vkontakte.miracle.model.catalog.fields.CatalogLayout;
import com.vkontakte.miracle.model.catalog.wraps.CatalogUserWC;
import com.vkontakte.miracle.model.groups.wraps.GroupItemWC;
import com.vkontakte.miracle.service.player.loader.AudioItemWCLoader;
import com.vkontakte.miracle.service.player.loader.CatalogBlockLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CatalogBlock implements ItemDataHolder, LoadableAudioItemWC, GroupItemWC,
        PlaylistItemWC, CatalogUserWC {

    private final String id;
    private final String dataType;
    private String nextFrom;
    private final CatalogLayout layout;
    private CatalogBadge badge;
    private ArrayList<ItemDataHolder> actions;
    private final ArrayList<ItemDataHolder> items = new ArrayList<>();


    public String getId() {
        return id;
    }

    public String getDataType() {
        return dataType;
    }

    public String getNextFrom() {
        return nextFrom;
    }

    public CatalogLayout getLayout() {
        return layout;
    }

    public CatalogBadge getBadge() {
        return badge;
    }

    public ArrayList<ItemDataHolder> getActions() {
        return actions;
    }

    public CatalogBlock(JSONObject jsonObject) throws JSONException {

        id = jsonObject.getString("id");
        dataType = jsonObject.getString("data_type");

        if(jsonObject.has("next_from")){
            nextFrom = jsonObject.getString("next_from");
        } else {
            nextFrom = "";
        }

        layout = new CatalogLayout(jsonObject.getJSONObject("layout"));

        if(jsonObject.has("badge")){
            badge = new CatalogBadge(jsonObject.getJSONObject("badge"));
        }

        if(jsonObject.has("actions")){
            actions = new ArrayList<>();
            JSONArray ja_actions = jsonObject.getJSONArray("actions");

            for (int j = 0; j < ja_actions.length(); j++) {
                JSONObject action = ja_actions.getJSONObject(j);
                actions.add(new CatalogAction(action));
            }
        }
    }

    public CatalogBlock(CatalogBlock catalogBlock){
        id = catalogBlock.id;
        dataType = catalogBlock.dataType;
        nextFrom = catalogBlock.nextFrom;
        layout = catalogBlock.layout;
        badge = catalogBlock.badge;
        actions = catalogBlock.actions;
    }

    public void copyItems(CatalogBlock catalogBlock){
        AudioItemWF audioItemWF = new AudioItemWF();
        for (ItemDataHolder itemDataHolder:catalogBlock.items) {
            DataItemWrap<?,?> dataItemWrap = (DataItemWrap<?,?>) itemDataHolder;
            Object item = dataItemWrap.getItem();
            if(item instanceof AudioItem){
                items.add(audioItemWF.create((AudioItem) item, this));
            }
        }
    }

    public void setNextFrom(String nextFrom) {
        this.nextFrom = nextFrom;
    }

    @Override
    public int getViewHolderType() {
        switch (layout.getName()){
            default:{
                Log.d("unknown layout type", layout.getName());
                return TYPE_ERROR;
            }

            case "header_extended":{
                return TYPE_CATALOG_HEADER_EXTENDED;
            }
            case "header_compact":
            case "header":{
                return TYPE_CATALOG_HEADER;
            }
            case "separator":{
                return TYPE_CATALOG_SEPARATOR;
            }
            case "slider":
            case "large_slider":
            case "music_chart_large_slider":{
                return TYPE_CATALOG_SLIDER;
            }
            case "promo_banners_slider":{
                return TYPE_CATALOG_BANNER_SLIDER;
            }
            case "horizontal_buttons":{
                return TYPE_HORIZONTAL_BUTTONS;
            }
            case "triple_stacked_slider":
            case "music_chart_triple_stacked_slider":{
                return TYPE_CATALOG_TRIPLE_STACKED_SLIDER;
            }
            case "banner":{
                switch (dataType){
                    default:{
                        return TYPE_ERROR;
                    }
                    case "artist":{
                        return TYPE_ARTIST_BANNER;
                    }
                }
            }
            case "categories_list":{
                return TYPE_CATALOG_CATEGORIES_LIST;
            }
        }
    }

    public ArrayList<ItemDataHolder> getItems() {
        return items;
    }

    @Override
    public ArrayList<ItemDataHolder> getAudioItems() {
        return items;
    }

    @Override
    public ArrayList<ItemDataHolder> getGroupItems() {
        return items;
    }

    @Override
    public ArrayList<ItemDataHolder> getPlaylistItems() {
        return items;
    }

    @Override
    public ArrayList<ItemDataHolder> getCatalogUserItems() {
        return items;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null){
            if(obj instanceof CatalogBlock){
                CatalogBlock catalogBlock = (CatalogBlock) obj;
                return id.equals(catalogBlock.id);
            }
        }
        return false;
    }

    @Override
    public AudioItemWCLoader createAudioLoader() {
        return new CatalogBlockLoader(this);
    }
}
