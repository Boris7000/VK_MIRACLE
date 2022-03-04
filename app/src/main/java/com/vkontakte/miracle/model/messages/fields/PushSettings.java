package com.vkontakte.miracle.model.messages.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class PushSettings {

    private final boolean disabledForever;
    private final boolean noSound;
    private final boolean disabledMentions;
    private final boolean disabledMassMentions;

    public boolean isDisabledForever() {
        return disabledForever;
    }

    public boolean isNoSound() {
        return noSound;
    }

    public boolean isDisabledMentions() {
        return disabledMentions;
    }

    public boolean isDisabledMassMentions() {
        return disabledMassMentions;
    }

    public PushSettings(JSONObject jsonObject) throws JSONException{
        disabledForever = jsonObject.getBoolean("disabled_forever");
        noSound = jsonObject.getBoolean("no_sound");
        disabledMentions = jsonObject.getBoolean("disabled_mentions");
        disabledMassMentions = jsonObject.getBoolean("disabled_mass_mentions");
    }

}
