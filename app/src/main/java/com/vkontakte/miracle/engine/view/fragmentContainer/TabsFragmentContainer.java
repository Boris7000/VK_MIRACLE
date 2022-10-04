package com.vkontakte.miracle.engine.view.fragmentContainer;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.IMiracleFragment;

import java.util.ArrayList;
import java.util.Map;

public class TabsFragmentContainer extends FrameLayout {

    private Adapter adapter;

    private OnFragmentChangeListener onFragmentChangeListener;

    public TabsFragmentContainer(@NonNull Context context) {
        super(context);
    }

    public TabsFragmentContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TabsFragmentContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(Adapter controller) {
        this.adapter = controller;
        this.adapter.setContainer(this);
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public OnFragmentChangeListener getOnFragmentChangeListener() {
        return onFragmentChangeListener;
    }

    public void setOnFragmentChangeListener(OnFragmentChangeListener onFragmentChangeListener) {
        this.onFragmentChangeListener = onFragmentChangeListener;
    }

    private void checkChangeListener(){
        if(onFragmentChangeListener!=null){
            onFragmentChangeListener.onFragmentChange();
        }
    }

    private void hide(Fragment fragment, FragmentTransaction fragmentTransaction){
        if(fragment!=null){
            checkChangeListener();
            try {
                if(fragment.getParentFragmentManager()!= adapter.fm){
                    fragment.getParentFragmentManager().beginTransaction().detach(fragment).commit();
                    fragmentTransaction.add(getId(), fragment);
                } else {
                    if(!fragment.isAdded()){
                        fragmentTransaction.add(getId(), fragment);
                    }
                }
                fragmentTransaction.hide(fragment);
            } catch (IllegalStateException ignore){
                fragmentTransaction.add(getId(), fragment);
                fragmentTransaction.hide(fragment);
            }
        }
    }

    private void show(Fragment Fragment, FragmentTransaction fragmentTransaction){
        if(Fragment!=null){
            checkChangeListener();
            try {
                if(Fragment.getParentFragmentManager()!= adapter.fm){
                    Fragment.getParentFragmentManager().beginTransaction().detach(Fragment).commit();
                    fragmentTransaction.add(getId(), Fragment);
                } else {
                    if(!Fragment.isAdded()){
                        fragmentTransaction.add(getId(), Fragment);
                    } else {
                        fragmentTransaction.show(Fragment);
                    }
                }
            } catch (IllegalStateException ignore){
                fragmentTransaction.add(getId(), Fragment);
            }
        }
    }

    private void add(Fragment Fragment, FragmentTransaction fragmentTransaction){
        if(Fragment!=null){
            checkChangeListener();
            try {
                if(Fragment.getParentFragmentManager()!= adapter.fm){
                    Fragment.getParentFragmentManager().beginTransaction().detach(Fragment).commit();
                    fragmentTransaction.add(getId(), Fragment);
                }
            } catch (IllegalStateException ignore){
                fragmentTransaction.add(getId(), Fragment);
            }
        }
    }

    private void remove(Fragment Fragment, FragmentTransaction fragmentTransaction){
        checkChangeListener();
        try {
            if(Fragment.getParentFragmentManager()!= adapter.fm){
                Fragment.getParentFragmentManager().beginTransaction().remove(Fragment).commit();
            } else {
                fragmentTransaction.remove(Fragment);
            }
        } catch (IllegalStateException ignore){}
    }

    public static class Adapter {
        private final FragmentManager fm;
        private TabsFragmentContainer container;
        private NavigationBarView navigationBarView;

        private ArrayMap<Integer, FragmentFabric> fabrics;
        private ArrayMap<Integer, TabHolder> tabs;
        private ArrayList<Integer> stack;
        private boolean handleNavigationBarAction = true;

        public Adapter(FragmentManager fm){
            this.fm = fm;
        }

        public Adapter(FragmentManager fm, ArrayMap<Integer, FragmentFabric> fabrics){
            this(fm);
            this.fabrics = fabrics;
            this.stack = new ArrayList<>();
            this.tabs = new ArrayMap<>();
            for (Map.Entry<Integer, FragmentFabric> set:fabrics.entrySet()) {
                tabs.put(set.getKey(), new TabHolder());
            }
        }

        private void setContainer(TabsFragmentContainer container){
            this.container = container;
        }

        public void setUpWithNavigationBarView(NavigationBarView navigationBarView){
            this.navigationBarView = navigationBarView;

            navigationBarView.setOnItemSelectedListener(item -> {
                FragmentFabric fabric = fabrics.get(item.getItemId());
                if(fabric!=null){
                    if(handleNavigationBarAction){
                        selectTab(item.getItemId());
                    }
                    return true;
                }
                return false;
            });

            navigationBarView.setOnItemReselectedListener(item -> {
                TabHolder whoReselectedTab = tabs.get(item.getItemId());
                if(whoReselectedTab!=null){
                    Fragment whoReselectedFragment = whoReselectedTab.getLastFragment();
                    if(whoReselectedFragment!=null){
                        if(whoReselectedFragment instanceof IMiracleFragment) {
                            IMiracleFragment miracleFragment = (IMiracleFragment) whoReselectedFragment;
                            if (handleNavigationBarAction) {
                                if (miracleFragment.notTop()) {
                                    miracleFragment.scrollToTop();
                                } else {
                                    goToFirstFragment(item.getItemId());
                                }
                            }
                        }
                    }
                }
            });

        }

        public void back(){
            TabHolder tabHolder = tabs.get(getLastInStack());
            if(tabHolder!=null){
                Fragment whoNeedRemove = tabHolder.getLastFragment();
                Fragment whoNeedShow;
                tabHolder.removeLastFragment();
                if(tabHolder.getCount()==0){
                    removeFromStack(getLastInStack());
                    int newItemId = getLastInStack();
                    tabHolder = tabs.get(newItemId);
                    if(tabHolder!=null) {
                        handleNavigationBarAction = false;
                        navigationBarView.setSelectedItemId(newItemId);
                        handleNavigationBarAction = true;
                    }
                }

                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if(tabHolder!=null) {
                    whoNeedShow = tabHolder.getLastFragment();
                    container.show(whoNeedShow, fragmentTransaction);
                }
                container.remove(whoNeedRemove, fragmentTransaction);
                fragmentTransaction.commit();
            }
        }

        public void addFragment(Fragment miracleFragment){
            TabHolder tabHolder = tabs.get(getLastInStack());
            if(tabHolder!=null){
                Fragment whoNeedHide = tabHolder.getLastFragment();
                tabHolder.addFragment(miracleFragment);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                container.add(miracleFragment, fragmentTransaction);
                if(whoNeedHide!=null){
                    container.hide(whoNeedHide, fragmentTransaction);
                }
                fragmentTransaction.commit();
            }
        }

        public void goToFirstFragment(int itemId){

            TabHolder whoNeedShowTab = tabs.get(itemId);
            if(whoNeedShowTab!=null){
                if(whoNeedShowTab.getFragments().size()>1) {
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();

                    Fragment whoNeedShow = whoNeedShowTab.getFirstFragment();
                    container.show(whoNeedShow, fragmentTransaction);

                    while (whoNeedShowTab.getFragments().size() > 1) {
                        Fragment whoNeedRemove = whoNeedShowTab.getLastFragment();
                        whoNeedShowTab.removeLastFragment();
                        container.remove(whoNeedRemove, fragmentTransaction);
                    }
                    fragmentTransaction.commit();
                }
            }
        }

        public void selectTab(int itemId){

            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            TabHolder whoNeedShowTab = tabs.get(itemId);

            if(whoNeedShowTab!=null){
                if(whoNeedShowTab.getCount()==0){
                    Fragment fragment;
                    FragmentFabric fabric = fabrics.get(itemId);
                    if(fabric!=null){
                        fragment = fabric.createFragment();
                        container.add(fragment, fragmentTransaction);
                        whoNeedShowTab.addFragment(fragment);
                    }
                } else {
                    Fragment whoNeedShow = whoNeedShowTab.getLastFragment();
                    container.show(whoNeedShow, fragmentTransaction);
                }
            }

            removeFromStack(itemId);

            if(!stack.isEmpty()){
                TabHolder whoNeedHideTab = tabs.get(getLastInStack());
                if(whoNeedHideTab!=null){
                    Fragment whoNeedHideFragment = whoNeedHideTab.getLastFragment();
                    if (whoNeedHideFragment != null) {
                        container.hide(whoNeedHideFragment, fragmentTransaction);
                    }
                }
            }

            stack.add(itemId);
            fragmentTransaction.commit();

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

        public int getFragmentCount(){
            int count = 0;
            for (Map.Entry<Integer, TabHolder> set:tabs.entrySet()) {
                TabHolder tabHolder = set.getValue();
                count+=tabHolder.getCount();
            }
            return count;
        }

        private boolean checkFragmentManager(Fragment fragment){
            try {
                FragmentManager fragmentManager = fragment.getParentFragmentManager();
                if(fragmentManager!=fm){
                    return false;
                }
            } catch (IllegalStateException ignore){
                return false;
            }
            return true;
        }

        public void saveState(Bundle outState){

            ArrayList<String> keys = new ArrayList<>();

            for (Map.Entry<Integer, TabHolder> set:tabs.entrySet()) {
                TabHolder tabHolder = set.getValue();
                Integer tabKey = set.getKey();
                ArrayList<Fragment> fragments = tabHolder.getFragments();
                for(int i = 0; i<fragments.size(); i++){
                    Fragment fragment = fragments.get(i);
                    boolean inFM = checkFragmentManager(fragment);
                    /*Log.d("furfurhfufrf","f" + i + "_" + set.getKey()
                            +",  added: "+fragment.isAdded()+", removing: "+fragment.isRemoving()
                            +", isStateSaved: "+fragment.isStateSaved()+", inLayout: "+fragment.isInLayout()
                    +", inFm: "+inFM);*/
                    if(inFM) {
                        String key = "t:"+tabKey+"_f:" + i +"_k:" + set.getKey();
                        fm.putFragment(outState, key, fragment);
                        keys.add(i, key);
                    }
                }
            }
            outState.putParcelable("AdapterSavedState",new AdapterSavedState(keys, stack));
        }

        public void restoreFromSavedState(Bundle savedInstanceState){

            AdapterSavedState adapterSavedState =
                    savedInstanceState.getParcelable("AdapterSavedState");
            if(adapterSavedState !=null) {
                this.stack = adapterSavedState.stack;
                for (String key : adapterSavedState.keys) {
                    String find = "t:";
                    int index;
                    String m = key.substring(find.length());
                    find = "_f:";
                    index = m.indexOf(find);
                    String t = m.substring(0, index);
                    m = m.substring(t.length() + find.length());
                    find = "_k:";
                    index = m.indexOf(find);
                    String f = m.substring(0, index);

                    Integer tabKey = Integer.valueOf(t);
                    int fKey = Integer.parseInt(f);
                    TabHolder tabHolder = tabs.get(tabKey);
                    if (tabHolder != null) {
                        ArrayList<Fragment> fragments = tabHolder.getFragments();
                        Fragment fragment = fm.getFragment(savedInstanceState, key);
                        if (fragment != null) {
                            fragments.add(fKey, fragment);
                        }
                    }
                }
            }
        }

        public static class AdapterSavedState implements Parcelable {
            private final ArrayList<String> keys;
            private final ArrayList<Integer> stack;
            public AdapterSavedState(ArrayList<String> keys,
                                     ArrayList<Integer> stack){
                this.keys = keys;
                this.stack = stack;
            }

            protected AdapterSavedState(Parcel in) {
                keys = in.createStringArrayList();
                stack = new ArrayList<>();
                in.readList(stack,Integer.class.getClassLoader());
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeStringList(keys);
                parcel.writeList(stack);
            }

            public static final Creator<AdapterSavedState> CREATOR = new Creator<AdapterSavedState>() {
                @Override
                public AdapterSavedState createFromParcel(Parcel in) {
                    return new AdapterSavedState(in);
                }

                @Override
                public AdapterSavedState[] newArray(int size) {
                    return new AdapterSavedState[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }
        }

    }

    public interface OnFragmentChangeListener{
        void onFragmentChange();
    }
}
