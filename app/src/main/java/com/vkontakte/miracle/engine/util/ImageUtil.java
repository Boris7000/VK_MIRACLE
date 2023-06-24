package com.vkontakte.miracle.engine.util;

import com.vkontakte.miracle.model.catalog.fields.Image;

import java.util.ArrayList;

public class ImageUtil {

    public static Image getOptimalSize(ArrayList<Image> images, int width, int height){

        if(images.isEmpty()){
            return null;
        }

        int minDif;
        int index = 0;

        Image image = images.get(0);

        if(width>0&&height>0){
            minDif = Math.abs(image.getWidth()-width) + Math.abs(image.getHeight()-height);
            if(images.size()>1){
                for (int i=1; i<images.size(); i++){
                    image = images.get(i);
                    int curr = Math.abs(image.getWidth()-width) + Math.abs(image.getHeight()-height);
                    if(curr<minDif){
                        minDif = curr;
                        index = i;
                    }
                }
                return images.get(index);
            } else {
                return image;
            }
        } else {
            if(width>0){
                minDif = Math.abs(image.getWidth()-width);
                if(images.size()>1){
                    for (int i=1; i<images.size(); i++){
                        image = images.get(i);
                        int curr = Math.abs(image.getWidth()-width);
                        if(curr<minDif){
                            minDif = curr;
                            index = i;
                        }
                    }
                    return images.get(index);
                } else {
                    return image;
                }
            } else {
                if(height>0){
                    minDif = Math.abs(image.getHeight()-height);
                    if(images.size()>1){
                        for (int i=1; i<images.size(); i++){
                            image = images.get(i);
                            int curr = Math.abs(image.getHeight()-height);
                            if(curr<minDif){
                                minDif = curr;
                                index = i;
                            }
                        }
                        return images.get(index);
                    } else {
                        return image;
                    }
                }
            }
        }

        return image;

    }

}
