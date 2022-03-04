package com.vkontakte.miracle.model.catalog;

import android.util.Log;

import com.vkontakte.miracle.model.catalog.fields.CatalogOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CatalogButton {

    private String sectionId;
    private String title;
    private final String actionType;
    private ArrayList<CatalogOption> options;

    public String getSectionId() {
        return sectionId;
    }

    public String getTitle() {
        return title;
    }

    public String getActionType() {
        return actionType;
    }

    public ArrayList<CatalogOption> getOptions() {
        return options;
    }

    public CatalogButton(JSONObject jsonObject)throws JSONException {
        JSONObject action = jsonObject.getJSONObject("action");
        actionType = action.getString("type");

        if (jsonObject.has("section_id")) {
            sectionId = jsonObject.getString("section_id");
        }

        if (jsonObject.has("title")) {
            title = jsonObject.getString("title");
        }

        //TODO сделать строки из ресурсов
        switch (actionType){
            default:{
                Log.d("ABOBUS", jsonObject.toString());
                if(title==null){
                    title = actionType;
                }
                break;
            }
            case "qr_camera":{
                //imageResourceId = R.drawable.ic_scan_viewfinder_outline_24;
                if(title==null){
                    title = "Сканировать QR";
                }
                break;
            }
            case "open_screen":{
                //hasNotTitle(jsonObject);
                break;
            }
            case "add_friend":{
                //imageResourceId = R.drawable.ic_add_user_24;
                if(title==null){
                    title = "Добавить друга";
                }
                break;
            }
            case "clear_recent_groups":{
                if(title==null){
                    title = "Очистить";
                }
                break;
            }
            case "play_shuffled_audios_from_block":{
                //imageResourceId = R.drawable.ic_mix_24;
                if(title==null){
                    title = "Перемешать все";
                }
                break;
            }
            case "play_audios_from_block":{
                //imageResourceId = R.drawable.ic_play_24;
                if(title==null){
                    title = "Слушать";
                }
                break;
            }
            case "open_section":{
                if(title==null){
                    title = "Показать все";
                }
                break;
            }
            case "select_sorting":
            case "friends_lists":{
                //imageResourceId = R.drawable.ic_dropdown_20;
                if(title==null){
                    title = "Списки";
                }
                options = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("options");
                for (int i=0; i<jsonArray.length();i++){
                    options.add(new CatalogOption(jsonArray.getJSONObject(i)));
                }
                break;
            }
        }

    }


}
