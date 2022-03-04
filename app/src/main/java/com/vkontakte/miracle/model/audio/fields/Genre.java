package com.vkontakte.miracle.model.audio.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class Genre{

        private final String id;
        private final String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Genre(JSONObject jsonObject) throws JSONException {
            id = jsonObject.getString("id");
            name = jsonObject.getString("name");
        }
}