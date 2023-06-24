package com.miracle.engine.fragment.tabs;

import android.content.Context;

import com.miracle.engine.R;
import com.miracle.engine.fragment.FragmentError;
import com.miracle.engine.fragment.tabs.nested.NestedMiracleFragmentFabric;

import java.util.ArrayList;

public interface ITabsFragment {

    TabsFragmentController<?> getTabsFragmentController();

    default ArrayList<NestedMiracleFragmentFabric> loadTabs(){
        return new ArrayList<>();
    }

    default ArrayList<NestedMiracleFragmentFabric> getErrorTabs(){
        ArrayList<NestedMiracleFragmentFabric> fabrics = new ArrayList<>();
        Context context = getTabsFragmentController().getContext();
        String title="";
        String text="";
        if(context!=null){
            title = context.getString(R.string.error);
            text = context.getString(R.string.unknownError);
        }
        fabrics.add(new FragmentError.Fabric(text, title,-1));
        return fabrics;
    }

    default int defaultTab(){return 0;}

    default int customTabItemViewResourceId(){return R.layout.view_custom_tab_item;}

    default boolean asyncLoadTabs(){return false;}

    default void onPageSelected(int pageIndex){}

}
