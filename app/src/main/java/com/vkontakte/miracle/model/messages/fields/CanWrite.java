package com.vkontakte.miracle.model.messages.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class CanWrite {
    private final boolean allowed;
    private int reason;
    public CanWrite(JSONObject jsonObject) throws JSONException {
        allowed = jsonObject.getBoolean("allowed");
        if(jsonObject.has("reason")){
            reason = jsonObject.getInt("reason");
        }
    }
}
