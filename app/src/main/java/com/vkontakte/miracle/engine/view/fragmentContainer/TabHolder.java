package com.vkontakte.miracle.engine.view.fragmentContainer;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class TabHolder {

    private final ArrayList<Fragment> fragments = new ArrayList<>();

    public void addFragment(Fragment fragment){
        fragments.add(fragment);
    }

    public void removeLastFragment(){
        if(!fragments.isEmpty()){
            fragments.remove(fragments.size()-1);
        }
    }

    public Fragment getLastFragment(){
        if(fragments.isEmpty()){
            return null;
        }else {
            return fragments.get(fragments.size()-1);
        }
    }

    public Fragment getFirstFragment(){
        if(fragments.isEmpty()){
            return null;
        }else {
            return fragments.get(0);
        }
    }

    public int getCount(){
        return fragments.size();
    }

    public ArrayList<Fragment> getFragments() {
        return fragments;
    }

    public void clear(){
        fragments.clear();
    }

}
