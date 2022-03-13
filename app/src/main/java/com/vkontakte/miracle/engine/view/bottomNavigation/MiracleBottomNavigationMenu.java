package com.vkontakte.miracle.engine.view.bottomNavigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.util.DimensionsUtil;
import com.vkontakte.miracle.engine.view.fragmentContainer.TabHolder;
import com.vkontakte.miracle.engine.view.fragmentContainer.FragmentContainer;

import java.util.ArrayList;

import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByAttributeId;


public class MiracleBottomNavigationMenu extends LinearLayout {

    private int selectedIndex = -1;
    private final int selectedColor;
    private final int defaultColor;
    private final int maxItemLength;
    private final ArrayList<OnTabSelectListener> onTabSelectListeners = new ArrayList<>();

    public MiracleBottomNavigationMenu(Context context) {
        this(context,null);
    }

    public MiracleBottomNavigationMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if(attrs!=null) {
            TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.MiracleBottomNavigationView, 0, 0);

            try {
                selectedColor = array.getColor(R.styleable.MiracleBottomNavigationView_selectedColor, getColorByAttributeId(getContext(), R.attr.colorPrimary));
                defaultColor = array.getColor(R.styleable.MiracleBottomNavigationView_defaultColor, getColorByAttributeId(getContext(), R.attr.colorPrimaryNeutral_60));
            } finally {
                array.recycle();
            }
        }else {
            selectedColor = getColorByAttributeId(getContext(), R.attr.colorPrimary);
            defaultColor = getColorByAttributeId(getContext(), R.attr.colorPrimaryNeutral_60);
        }
        maxItemLength = (int) DimensionsUtil.dpToPx(80, context);
    }

    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();

        for(int i=0; i<getChildCount(); i++){

            View view = getChildAt(i);
            final int fi = i;

            view.setOnLongClickListener(view1 -> {
                if(!onTabSelectListeners.isEmpty()){
                    for(OnTabSelectListener listener: onTabSelectListeners){
                        listener.onLongClick(fi);
                    }
                }
                return true;
            });

            view.setOnClickListener(view12 -> {
                if(selectedIndex!=fi) {
                    select(fi);
                }else {
                    if(!onTabSelectListeners.isEmpty()){
                        for(OnTabSelectListener listener: onTabSelectListeners){
                            listener.onReselect(fi);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if(child instanceof MiracleBottomNavigationItem){
            ((MiracleBottomNavigationItem) child).unselect(defaultColor);
        }
    }

    public void addOnTabSelectListener(OnTabSelectListener onTabSelectListener){
        onTabSelectListeners.add(onTabSelectListener);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void select(int pos){
        select(pos, true);
    }
    public void select(int pos, boolean needListener){

        if(selectedIndex>=0) {
            ((MiracleBottomNavigationItem) getChildAt(selectedIndex)).unselect(defaultColor);
        }

        if (needListener && !onTabSelectListeners.isEmpty()) {
            for (OnTabSelectListener listener : onTabSelectListeners) {
                listener.onSelect(pos, selectedIndex);
            }
        }

        selectedIndex = pos;
        ((MiracleBottomNavigationItem) getChildAt(selectedIndex)).select(selectedColor);
    }

    public void setupWithFragmentContainer(FragmentContainer fragmentContainer){
        fragmentContainer.setUpWithBottomNavigationMenu(this);

        addOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onSelect(int pos, int previous) {
                fragmentContainer.selectTab(pos);
            }

            @Override
            public void onReselect(int pos) {

                TabHolder whoReselectedTab = fragmentContainer.getCurrentTabHolder();
                MiracleFragment whoReselected = whoReselectedTab.getLastFragment();

                if(whoReselected.notTop()){
                    whoReselected.scrollToTop();
                } else {
                    fragmentContainer.goToFirstFragment();
                }
            }

            @Override
            public void onLongClick(int pos) { }
        });

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int childCount = getChildCount();
        int maxTotalHeight = childCount*maxItemLength;
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec-getPaddingEnd()-getPaddingStart());
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec-getPaddingTop()-getPaddingBottom());

        if(getOrientation()==LinearLayout.HORIZONTAL){

            int childLength = maxItemLength;
            if(maxTotalHeight>parentWidth){
                childLength = parentWidth/childCount;

            }
            for (int c = 0; c < childCount; c++) {
                View child = (View) getChildAt(c);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                params.width = childLength;
                child.measure(params.width, params.height);
            }
        } else {

            int childLength = maxItemLength;
            if(maxTotalHeight>parentHeight){
                childLength = parentHeight/childCount;

            }
            for (int c = 0; c < childCount; c++) {
                View child = getChildAt(c);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                params.height = childLength;
                child.measure(params.width, params.height);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();
        int maxTotalHeight = childCount*maxItemLength;

        int parentWidth = getWidth()-getPaddingEnd()-getPaddingStart();
        int parentHeight = getHeight()-getPaddingTop()-getPaddingBottom();

        if(getOrientation()==LinearLayout.HORIZONTAL){

            int childLength = maxItemLength;
            if(maxTotalHeight>parentWidth){
                childLength = parentWidth/childCount;

            }
            for (int c = 0; c < childCount; c++) {
                View child = (View) getChildAt(c);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                params.width = childLength;
                child.layout(0,0,0,0);
            }
        } else {

            int childLength = maxItemLength;
            if(maxTotalHeight>parentHeight){
                childLength = parentHeight/childCount;

            }
            for (int c = 0; c < childCount; c++) {
                View child = (View) getChildAt(c);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                params.height = childLength;
                child.layout(0,0,0,0);
            }
        }

        super.onLayout(changed, l, t, r, b);
    }
}
