package com.vkontakte.miracle.model.users.fields;

import com.vkontakte.miracle.model.groups.GroupItem;

import org.json.JSONException;
import org.json.JSONObject;

public class Career {

    private String group_id;
    private String company;
    private GroupItem groupItem;

    public Career(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("group_id")){
            group_id = jsonObject.getString("group_id");
        }
        if(jsonObject.has("company")){
            company = jsonObject.getString("company");
        }
    }

    public String getGroup_id() {
        return group_id;
    }

    public String getCompany() {
        return company;
    }

    public GroupItem getGroupItem() {
        return groupItem;
    }

    public void setGroupItem(GroupItem groupItem) {
        this.groupItem = groupItem;
    }
}
