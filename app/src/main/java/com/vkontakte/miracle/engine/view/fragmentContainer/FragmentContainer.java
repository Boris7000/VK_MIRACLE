package com.vkontakte.miracle.engine.view.fragmentContainer;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.view.bottomNavigation.MiracleBottomNavigationMenu;
import com.vkontakte.miracle.fragment.base.FragmentMenu;

import java.util.ArrayList;

public class FragmentContainer extends FrameLayout {

    private TabsFragmentController controller;

    public FragmentContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setController(TabsFragmentController controller){
        this.controller = controller;
        controller.setContainer(this);
    }

    public void setUpWithBottomNavigationMenu(MiracleBottomNavigationMenu bottomNavigationMenu) {
        if(controller!=null){
            controller.setBottomNavigationMenu(bottomNavigationMenu);
        }
    }

    public void selectTab(int pos){
        if(controller!=null){
            controller.selectTab(pos);
        }
    }

    public void back(){
        if(controller!=null){
            controller.back();
        }
    }

    public void addFragment(MiracleFragment miracleFragment){
        if(controller!=null){
            controller.addFragment(miracleFragment);
        }
    }

    public void goToFirstFragment(){
        if(controller!=null){
            controller.goToFirstFragment();
        }
    }

    public int getFragmentCount(){
        if(controller!=null){
            return controller.getFragmentCount();
        } else {
            return 0;
        }
    }

    public TabHolder getCurrentTabHolder(){
        if(controller!=null){
            return controller.getCurrentTabHolder();
        } else {
            return null;
        }
    }

    public ControllerSavedData saveState(Bundle outState){
        if(controller!=null){
            return controller.saveState(outState);
        } else {
            return null;
        }
    }

    public static class TabsFragmentController {

        private final FragmentManager fm;
        private FragmentContainer container;
        private MiracleBottomNavigationMenu bottomNavigationMenu;
        private final ArrayList<FragmentFabric> baseFragmentsClasses;
        private final ArrayList<TabHolder> tabs;
        private final ArrayList<Integer> stack;

        public TabsFragmentController(FragmentManager fm, ArrayList<FragmentFabric> baseFragmentsClasses){
            this.fm = fm;
            this.baseFragmentsClasses = baseFragmentsClasses;
            this.tabs = new ArrayList<>();
            this.stack = new ArrayList<>();

            int tabsCount = baseFragmentsClasses.size();
            for(int i=0; i<tabsCount; i++){
                tabs.add(new TabHolder());
            }
        }

        public TabsFragmentController(FragmentManager fm, ControllerSavedData controllerSavedData, Bundle savedInstanceState){
            this.fm = fm;
            this.baseFragmentsClasses = controllerSavedData.baseFragmentsClasses;
            this.tabs = controllerSavedData.tabs;
            this.stack = controllerSavedData.stack;

            for(int i = 0; i<tabs.size(); i++){
                TabHolder tabHolder = tabs.get(i);
                ArrayList<MiracleFragment> miracleFragments = tabHolder.getFragments();
                for(int j = 0; j<miracleFragments.size(); j++){
                    miracleFragments.set(j, (MiracleFragment) fm.getFragment(savedInstanceState,"f"+i+"_"+j));
                }
            }
        }

        public void selectTab(int pos){

            TabHolder whoNeedShowTab = tabs.get(pos);

            if(whoNeedShowTab.getCount()==0){
                MiracleFragment miracleFragment;
                FragmentFabric fabric = baseFragmentsClasses.get(pos);
                if(fabric!=null){
                    miracleFragment = fabric.createFragment();
                } else {
                    miracleFragment = new FragmentMenu();
                }
                add(miracleFragment);
                whoNeedShowTab.addFragment(miracleFragment);
            } else {
                MiracleFragment whoNeedShow = whoNeedShowTab.getLastFragment();
                show(whoNeedShow);
            }

            removeFromStack(pos);

            if(!stack.isEmpty()){
                TabHolder whoNeedHideTab = tabs.get(getLastInStack());
                MiracleFragment whoNeedHide = whoNeedHideTab.getLastFragment();

                if (whoNeedHide != null) {
                    hide(whoNeedHide);
                }
            }

            stack.add(pos);

        }

        private void back(){

            TabHolder tabHolder = tabs.get(getLastInStack());

            MiracleFragment whoNeedRemove = tabHolder.getLastFragment();
            MiracleFragment whoNeedShow;
            tabHolder.removeLastFragment();

            if(tabHolder.getCount()==0){
                removeFromStack(getLastInStack());
                int newPos = getLastInStack();
                tabHolder = tabs.get(newPos);
                bottomNavigationMenu.select(newPos, false);
            }
            whoNeedShow = tabHolder.getLastFragment();
            show(whoNeedShow);

            remove(whoNeedRemove);
        }

        private void addFragment(MiracleFragment miracleFragment){

            TabHolder tabHolder = tabs.get(getLastInStack());

            MiracleFragment whoNeedHide = tabHolder.getLastFragment();

            tabHolder.addFragment(miracleFragment);

            add(miracleFragment);

            if(whoNeedHide!=null){
                hide(whoNeedHide);
            }
        }

        public void goToFirstFragment(){

            TabHolder tabHolder = getCurrentTabHolder();
            MiracleFragment whoNeedRemove = tabHolder.getLastFragment();

            if(tabHolder.getFragments().size()>1) {
                while (tabHolder.getFragments().size() > 1) {
                    tabHolder.removeLastFragment();
                }
                MiracleFragment whoNeedShow = tabHolder.getLastFragment();
                show(whoNeedShow);
                remove(whoNeedRemove);
            }
        }

        private void hide(MiracleFragment miracleFragment){
            if(miracleFragment!=null){
                fm.beginTransaction().hide(miracleFragment).commit();
            }
        }

        private void show(MiracleFragment miracleFragment){
            if(miracleFragment!=null){
                fm.beginTransaction().show(miracleFragment).commit();
            }
        }

        private void add(MiracleFragment miracleFragment){
            if(miracleFragment!=null){
                fm.beginTransaction().add(container.getId(), miracleFragment).commit();
            }
        }

        private void remove(MiracleFragment miracleFragment){
            fm.beginTransaction().remove(miracleFragment).commit();
        }

        private int getLastInStack(){
            return stack.get(stack.size() - 1);
        }

        private void removeFromStack(int stackItem){
            for(int i =0; i<stack.size();i++){
                if(stack.get(i)==stackItem){
                    stack.remove(i);
                    break;
                }
            }
        }

        private void setContainer(FragmentContainer container) {
            this.container = container;
        }

        private void setBottomNavigationMenu(MiracleBottomNavigationMenu bottomNavigationMenu) {
            this.bottomNavigationMenu = bottomNavigationMenu;
        }

        private int getFragmentCount(){
            int count = 0;
            for(TabHolder th:tabs){
                count+=th.getCount();
            }
            return count;
        }

        private ControllerSavedData saveState(Bundle outState){

            for(int i = 0; i<tabs.size(); i++){
                TabHolder tabHolder = tabs.get(i);
                ArrayList<MiracleFragment> miracleFragments = tabHolder.getFragments();
                for(int j = 0; j<miracleFragments.size(); j++){
                    Fragment fragment = miracleFragments.get(j);
                    if(fragment.isAdded()) {
                        fm.putFragment(outState, "f" + i + "_" + j, fragment);
                    }
                }
            }

            return new ControllerSavedData(tabs, stack, baseFragmentsClasses);
        }

        private TabHolder getCurrentTabHolder(){
            return tabs.get(getLastInStack());
        }

    }

    public static class ControllerSavedData {
        private final ArrayList<TabHolder> tabs;
        private final ArrayList<Integer> stack;
        private final ArrayList<FragmentFabric> baseFragmentsClasses;
        public ControllerSavedData(ArrayList<TabHolder> tabs, ArrayList<Integer> stack, ArrayList<FragmentFabric> baseFragmentsClasses){
            this.tabs = tabs;
            this.stack = stack;
            this.baseFragmentsClasses = baseFragmentsClasses;
        }
    }
}
