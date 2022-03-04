package com.vkontakte.miracle.engine.view.fragmentContainer;

import com.vkontakte.miracle.engine.fragment.MiracleFragment;

import java.util.ArrayList;

public class TabHolder {


    private final ArrayList<MiracleFragment> fragments = new ArrayList<>();

    public void addFragment(MiracleFragment fragment){
        fragments.add(fragment);
    }

    public void removeLastFragment(){
        if(!fragments.isEmpty()){
            fragments.remove(fragments.size()-1);
        }
    }

    public MiracleFragment getLastFragment(){
        if(fragments.isEmpty()){
            return null;
        }else {
            return fragments.get(fragments.size()-1);
        }
    }

    public int getCount(){
        return fragments.size();
    }

    public ArrayList<MiracleFragment> getFragments() {
        return fragments;
    }

    public void clear(){
        fragments.clear();
    }

}
