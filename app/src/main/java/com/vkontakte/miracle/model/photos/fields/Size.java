package com.vkontakte.miracle.model.photos.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class Size {

    private int width;
    private int height;
    private String type;
    private String url;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public Size(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("url")){
            url = jsonObject.getString("url");
        } else {
            if(jsonObject.has("src")){
                url = jsonObject.getString("src");
            }
        }
        if(jsonObject.has("width")){
            width = jsonObject.getInt("width");
        }
        if(jsonObject.has("width")){
            height = jsonObject.getInt("height");
        }
        if(jsonObject.has("type")){
            type = jsonObject.getString("type");
        }
    }

    public double getAspectRatio(){
        return (double) width/(double) height;
    }
}
    /*
        s — пропорциональная копия изображения с максимальной стороной 75px;
        m — пропорциональная копия изображения с максимальной стороной 130px;
        x — пропорциональная копия изображения с максимальной стороной 604px;
        o — если соотношение "ширина/высота" исходного изображения меньше или равно 3:2, то пропорциональная копия с максимальной шириной 130px.
        Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева изображения с максимальной шириной 130px и соотношением сторон 3:2.
        p — если соотношение "ширина/высота" исходного изображения меньше или равно 3:2, то пропорциональная копия с максимальной шириной 200px.
        Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева и справа изображения с максимальной шириной 200px и соотношением сторон 3:2.
        q — если соотношение "ширина/высота" исходного изображения меньше или равно 3:2, то пропорциональная копия с максимальной шириной 320px.
        Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева и справа изображения с максимальной шириной 320px и соотношением сторон 3:2.
        r — если соотношение "ширина/высота" исходного изображения меньше или равно 3:2, то пропорциональная копия с максимальной шириной 510px.
        Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева и справа изображения с максимальной шириной 510px и соотношением сторон 3:2
        y — пропорциональная копия изображения с максимальной стороной 807px;
        z — пропорциональная копия изображения с максимальным размером 1080x1024;
        w — пропорциональная копия изображения с максимальным размером 2560x2048px.
     */
