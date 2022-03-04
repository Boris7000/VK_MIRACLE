package com.vkontakte.miracle.engine.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class BSBNestedRecyclerView  extends RecyclerView {

    private final int touchSlop;
    private float initialX = 0f;
    private float initialY = 0f;
    private boolean scrolled = false;
    private LockableSheetBehavior<?> bottomSheetBehavior;

    public BSBNestedRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public BSBNestedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        addOnItemTouchListener(new SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent ev) {
                return handleChild(ev);
            }
        });

    }


    private boolean handleChild(MotionEvent ev){
        if (bottomSheetBehavior == null) {
            findBottomSheetBehavior();
            if(bottomSheetBehavior==null){
                return false;
            }
        }

        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            scrolled = false;
            getParent().requestDisallowInterceptTouchEvent(false);
            bottomSheetBehavior.setScrollEnabled(true);
            return false;
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            initialX = ev.getX();
            initialY = ev.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if(!scrolled) {
                float dx = ev.getX() - initialX;
                float dy = ev.getY() - initialY;

                float absDX = Math.abs(dx);
                float absDY = Math.abs(dy);
                // assuming ViewPager2 touch-slop is 2x touch-slop of child
                float scaledDx = absDX * .5f;
                float scaledDy = absDY * 1;

                if (scaledDx > touchSlop || scaledDy > touchSlop) {
                    if (scaledDy > scaledDx) {
                        boolean canScroll = canScroll(dy);
                        scrolled = canScroll;
                        getParent().requestDisallowInterceptTouchEvent(canScroll);
                        bottomSheetBehavior.setScrollEnabled(false);
                        return !scrolled;
                    } else {
                        scrolled = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                        bottomSheetBehavior.setScrollEnabled(true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canScroll(float delta){
        int direction = delta>0?-1:1;
        return canScrollVertically(direction);
    }

    private void findBottomSheetBehavior(){
        ViewParent viewParent = getParent();
        while (bottomSheetBehavior==null&&viewParent!=null) {
            viewParent = viewParent.getParent();
            View view = (View) viewParent;

            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (!(params instanceof CoordinatorLayout.LayoutParams)) {
                continue;
                //throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
            }
            CoordinatorLayout.Behavior<?> behavior =
                    ((CoordinatorLayout.LayoutParams) params).getBehavior();
            if (behavior instanceof BottomSheetBehavior) {
                bottomSheetBehavior = (LockableSheetBehavior<?>) behavior;
                break;
            }

            //throw new IllegalArgumentException("The view is not associated with BottomSheetBehavior");

        }

    }

    private void handle(MotionEvent ev){

        if (bottomSheetBehavior == null) {
            findBottomSheetBehavior();
            if(bottomSheetBehavior==null){
                return;
            }
        }

        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            scrolled = false;
            getParent().requestDisallowInterceptTouchEvent(false);
            bottomSheetBehavior.setScrollEnabled(true);
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            initialX = ev.getX();
            initialY = ev.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if(!scrolled) {
                float dx = ev.getX() - initialX;
                float dy = ev.getY() - initialY;

                float absDX = Math.abs(dx);
                float absDY = Math.abs(dy);
                // assuming ViewPager2 touch-slop is 2x touch-slop of child
                float scaledDx = absDX * .5f;
                float scaledDy = absDY * 1;

                if (scaledDx > touchSlop || scaledDy > touchSlop) {
                    if (scaledDy > scaledDx) {
                        scrolled = true;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        bottomSheetBehavior.setScrollEnabled(false);
                    } else {
                        scrolled = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                        bottomSheetBehavior.setScrollEnabled(true);
                    }

                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        handle(e);
        return super.onTouchEvent(e);
    }

}

