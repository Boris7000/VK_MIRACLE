package com.vkontakte.miracle.engine.context;

import static com.miracle.engine.context.ContextExtractor.extractFromWrapper;

import android.content.Context;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.MainApp;

public class ContextExtractor {

    @Nullable
    public static MainActivity extractMainActivity(@Nullable Context context){
        if(context!=null){
            context = extractFromWrapper(context);
            if(context instanceof MainActivity){
                return (MainActivity) context;
            }
        }
        return null;
    }

    public static MainApp extractMainApp(@Nullable Context context){
        if(context!=null){
            context = extractFromWrapper(context);
            if(context instanceof MainApp){
                return (MainApp) context;
            }
        }
        return null;
    }


}
