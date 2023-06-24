package com.vkontakte.miracle.engine.view.photoGridView;

import androidx.collection.ArrayMap;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.photos.fields.Size;

public abstract class MediaItem implements ItemDataHolder {

    private ArrayMap<String,Size> sizes;

    public ArrayMap<String, Size> getSizes(){
        return sizes;
    }

    public void setSizes(ArrayMap<String, Size> sizes) {
        this.sizes = sizes;
    }

    public Size getSizeForWidth(int width, boolean need3x2) {
        if(width<=75){
            return sizes.get("s");
        } else {
            if(width<=130){
                if(need3x2){
                    return findFor("o", 75, true);
                } else {
                    return findFor("m", 75, false);
                }
            } else {
                if(need3x2){
                    if(width<=200){
                        return findFor("p", 130, true);
                    } else {
                        if(width<=320){
                            return findFor("q", 200, true);
                        } else {
                            if(width<=510){
                                return findFor("r", 320, true);
                            }
                        }
                    }
                } else {
                    if(width<=604){
                        return findFor("x", 130, false);
                    } else {
                        if(width<=807){
                            return findFor("y", 604, false);
                        } else {
                            if(width<=1024){
                                return findFor("z", 807, false);
                            }else {
                                return findFor("w", 1024, false);
                            }
                        }
                    }
                }
            }
        }
        return sizes.get("s");
    }

    private Size findFor(String type, int reserveWidth, boolean need3x2){
        Size size = sizes.get(type);
        if(size==null){
            return getSizeForWidth(reserveWidth, need3x2);
        } else {
            return size;
        }
    }
}
