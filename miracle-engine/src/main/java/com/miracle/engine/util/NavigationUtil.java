package com.miracle.engine.util;

import android.app.Activity;
import android.content.Context;

import com.miracle.engine.context.ContextExtractor;

public class NavigationUtil {

    public static void back(Context context){
        Activity activity = ContextExtractor.extractActivity(context);
        if(activity!=null){
            activity.onBackPressed();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
}
