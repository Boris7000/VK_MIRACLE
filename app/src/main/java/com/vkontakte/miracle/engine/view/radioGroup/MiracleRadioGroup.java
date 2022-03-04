package com.vkontakte.miracle.engine.view.radioGroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.R;

public class MiracleRadioGroup extends LinearLayout {

    private int checkedId = View.NO_ID;
    private final OnRadioCheckedChangeListener onRadioCheckedChangeListener;
    private OnRadioCheckedListener onRadioCheckedListener;

    public MiracleRadioGroup(Context context) {
        this(context, null);
    }

    public MiracleRadioGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if(attrs!=null){
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MiracleRadioGroup, 0, 0);
            int value = attributes.getResourceId(R.styleable.MiracleRadioGroup_RadioGroup_checkedButton, View.NO_ID);
            if (value != View.NO_ID) {
                checkedId = value;
            }
            attributes.recycle();
        }

        onRadioCheckedChangeListener = (radioView, checked) -> {
            if(checked){
                if(radioView.getId()!=checkedId){
                    View previousView = findViewById(checkedId);
                    if(previousView instanceof MiracleRadioView){
                        uncheckView((MiracleRadioView) previousView);
                    }
                    checkedId = radioView.getId();
                    radioView.setChecked(true);
                    if(onRadioCheckedListener!=null){
                        onRadioCheckedListener.onCheck(radioView);
                    }
                }
            }
        };

    }

    @Override
    public void onViewAdded(View child) {
        initChildView(child);
        super.onViewAdded(child);
    }

    @Override
    public void onViewRemoved(View child) {
        removeChildView(child);
        super.onViewRemoved(child);
    }

    private void initChildView(View child){
        if(child instanceof ViewGroup){
            ViewGroup viewGroupChild = (ViewGroup) child;
            if(viewGroupChild instanceof MiracleRadioView){
                int id = viewGroupChild.getId();
                if (id == View.NO_ID) {
                    id = View.generateViewId();
                    viewGroupChild.setId(id);
                }
                MiracleRadioView miracleRadioView = (MiracleRadioView) viewGroupChild;
                miracleRadioView.setOnCheckedChangeListener(onRadioCheckedChangeListener);
                if(miracleRadioView.getId()==checkedId){
                    checkView(miracleRadioView);
                }
            } else {
                for (int i=0; i<viewGroupChild.getChildCount(); i++) {
                    initChildView(viewGroupChild.getChildAt(i));
                }
            }
        }
    }

    private void removeChildView(View child){
        if(child instanceof ViewGroup){
            ViewGroup viewGroupChild = (ViewGroup) child;
            if(viewGroupChild instanceof MiracleRadioView){
                MiracleRadioView miracleRadioView = (MiracleRadioView) viewGroupChild;
                miracleRadioView.setOnCheckedChangeListener(null);
                if(miracleRadioView.getId()==checkedId){
                    checkedId = View.NO_ID;
                }
            } else {
                for (int i=0; i<viewGroupChild.getChildCount(); i++) {
                    removeChildView(viewGroupChild.getChildAt(i));
                }
            }
        }
    }

    public void setCheckedId(@IdRes int id) {
        View view = findViewById(id);
        if(view instanceof MiracleRadioView){
            checkView((MiracleRadioView) view);
        }
    }

    public void setOnRadioCheckedListener(OnRadioCheckedListener onRadioCheckedListener){
        this.onRadioCheckedListener = onRadioCheckedListener;
    }

    private void checkView(MiracleRadioView radioView){
        if(!radioView.isChecked()){
            radioView.setChecked(true);
        }
    }

    private void uncheckView(MiracleRadioView radioView){
        if(radioView.isChecked()){
            radioView.setChecked(false);
        }
    }

}
