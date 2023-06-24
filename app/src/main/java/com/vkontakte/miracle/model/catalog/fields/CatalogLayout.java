package com.vkontakte.miracle.model.catalog.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class CatalogLayout {

    private final String name;
    private String title;
    private String style;
    private TopTitle topTitle;
    private boolean isList = false;

    public String getTitle() {
        return title;
    }

    public TopTitle getTopTitle() {
        return topTitle;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public boolean isList() {
        return isList;
    }

    public CatalogLayout(JSONObject jsonObject) throws JSONException {

        name = jsonObject.getString("name");

        switch (name){
            case "list_friend_suggests":
            case "music_chart_list":
            case "large_list":
            case "list":{
                isList = true;
                break;
            }
        }

        if(jsonObject.has("title")){
            title = jsonObject.getString("title");
        }

        if(jsonObject.has("style")){
            style = jsonObject.getString("style");
        }

        if(jsonObject.has("top_title")){
            topTitle = new TopTitle(jsonObject.getJSONObject("top_title"));
        }
    }
}
