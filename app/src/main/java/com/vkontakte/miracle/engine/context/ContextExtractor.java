package com.vkontakte.miracle.engine.context;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.engine.activity.MiracleActivity;
import com.vkontakte.miracle.engine.activity.tabs.TabsActivity;

public class ContextExtractor {

    @Nullable
    public static Activity extractActivity(@Nullable Context context){
        if(context!=null){
            if(context instanceof Activity){
                return (Activity) context;
            }
        }
        return null;
    }

    @Nullable
    public static MiracleActivity extractMiracleActivity(@Nullable Context context){
        if(context!=null){
            if(context instanceof MiracleActivity){
                return (MiracleActivity) context;
            }
        }
        return null;
    }

    @Nullable
    public static TabsActivity extractTabsActivity(@Nullable Context context){
        if(context!=null){
            if(context instanceof TabsActivity){
                return (TabsActivity) context;
            }
        }
        return null;
    }

    @Nullable
    public static MainActivity extractMainActivity(@Nullable Context context){
        if(context!=null){
            if(context instanceof MainActivity){
                return (MainActivity) context;
            }
        }
        return null;
    }

    @Nullable
    public static MiracleApp extractMiracleApp(@Nullable Context context){
        if(context!=null){
            if(context instanceof MiracleApp){
                return (MiracleApp) context;
            }
        }
        return null;
    }

}
