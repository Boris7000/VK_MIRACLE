package com.vkontakte.miracle.engine.view.radioGroup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.widget.Checkable;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class MiracleRadioView extends LinearLayout implements Checkable {

    private boolean checked = false;
    private OnRadioCheckedChangeListener onRadioCheckedChangeListener;
    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    public MiracleRadioView(Context context) {
        this(context, null);
    }

    public MiracleRadioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
    }

    @Override
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            refreshDrawableState();
            if(onRadioCheckedChangeListener!=null){
                onRadioCheckedChangeListener.onChange(this, checked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        if (!isChecked()) {
            setChecked(!checked);
        }
    }

    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        final Drawable buttonDrawable = getBackground();
        if (buttonDrawable != null && buttonDrawable.isStateful()
                && buttonDrawable.setState(getDrawableState())) {
            invalidateDrawable(buttonDrawable);
        }
    }

    public void setOnCheckedChangeListener(OnRadioCheckedChangeListener onRadioCheckedChangeListener){
        this.onRadioCheckedChangeListener = onRadioCheckedChangeListener;
    }

}
