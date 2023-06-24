package com.miracle.engine.context;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import androidx.annotation.Nullable;

import com.miracle.engine.MiracleApp;
import com.miracle.engine.activity.MiracleActivity;
import com.miracle.engine.activity.tabs.TabsActivity;

public class ContextExtractor {

    @Nullable
    public static Activity extractActivity(@Nullable Context context){
        if(context!=null){
            context = extractFromWrapper(context);
            if(context instanceof Activity){
                return (Activity) context;
            }
        }
        return null;
    }

    @Nullable
    public static MiracleActivity extractMiracleActivity(@Nullable Context context){
        if(context!=null){
            context = extractFromWrapper(context);
            if(context instanceof MiracleActivity){
                return (MiracleActivity) context;
            }
        }
        return null;
    }

    @Nullable
    public static TabsActivity extractTabsActivity(@Nullable Context context){
        if(context!=null){
            context = extractFromWrapper(context);
            if(context instanceof TabsActivity){
                return (TabsActivity) context;
            }
        }
        return null;
    }

    @Nullable
    public static MiracleApp extractMiracleApp(@Nullable Context context){
        if(context!=null){
            context = extractFromWrapper(context);
            if(context instanceof MiracleApp){
                return (MiracleApp) context;
            }
        }
        return null;
    }

    public static Context extractFromWrapper(Context context){
        if(context instanceof androidx.appcompat.view.ContextThemeWrapper){
            ContextWrapper contextThemeWrapper = (ContextWrapper) context;
            return contextThemeWrapper.getBaseContext();
        }
        return context;
    }

}
