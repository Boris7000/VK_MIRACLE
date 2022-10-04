package com.vkontakte.miracle.model.audio.fields;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Photo implements Serializable {

    private final int width;
    private final int height;

    private String photo34;
    private String photo68;
    private String photo135;
    private String photo270;
    private String photo300;
    private String photo600;
    private String photo1200;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPhoto34() {
        return photo34;
    }

    public String getPhoto68() {
        return photo68;
    }

    public String getPhoto135() {
        return photo135;
    }

    public String getPhoto270() {
        return photo270;
    }

    public String getPhoto300() {
        return photo300;
    }

    public String getPhoto600() {
        return photo600;
    }

    public String getPhoto1200() {
        return photo1200;
    }

    public Photo(JSONObject jsonObject) throws JSONException {
        width = jsonObject.getInt("width");
        height = jsonObject.getInt("height");

        if (jsonObject.has("photo_34")) {
            photo34 = jsonObject.getString("photo_34");
        }
        if (jsonObject.has("photo_68")) {
            photo68 = jsonObject.getString("photo_68");
        }
        if (jsonObject.has("photo_135")) {
            photo135 = jsonObject.getString("photo_135");
        }
        if (jsonObject.has("photo_270")) {
            photo270 = jsonObject.getString("photo_270");
        }
        if (jsonObject.has("photo_300")) {
            photo300 = jsonObject.getString("photo_300");
        }
        if (jsonObject.has("photo_600")) {
            photo600 = jsonObject.getString("photo_600");
        }
        if (jsonObject.has("photo_1200")) {
            photo1200 = jsonObject.getString("photo_1200");
        }
    }

    public String getOptimalSizeUrl(int size){
        if(size>51){
            if(size>102){
                if(size>203){
                    if(size>285){
                        if(size>450){
                            if(size>900){
                                return photo1200;
                            } else {
                                return photo600;
                            }
                        } else {
                            return photo300;
                        }
                    } else {
                        return photo270;
                    }
                } else {
                    return photo135;
                }
            } else {
                return photo68;
            }
        } else {
            return photo34;
        }
    }
}
