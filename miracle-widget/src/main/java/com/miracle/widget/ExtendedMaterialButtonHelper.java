package com.miracle.widget;

import static com.miracle.widget.ExtendedTextHelper.ICON_POS_BOTTOM;
import static com.miracle.widget.ExtendedTextHelper.ICON_POS_LEFT;
import static com.miracle.widget.ExtendedTextHelper.ICON_POS_RIGHT;
import static com.miracle.widget.ExtendedTextHelper.ICON_POS_TOP;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.miracle.miracle_widget.R;

public class ExtendedMaterialButtonHelper {

    @NonNull private final MaterialButton mView;
    @NonNull private final ExtendedTextHelper mTextHelper;

    @Nullable
    private ValueAnimator valueAnimator;

    private static final ArgbEvaluator colorEvaluator = new ArgbEvaluator();
    private int[] oldStateSet;
    private int mAnimationDuration = 300;

    ExtendedMaterialButtonHelper(@NonNull MaterialButton view, @NonNull ExtendedTextHelper textHelper) {
        mView = view;
        mTextHelper = textHelper;
    }

    void loadFromAttributes(@Nullable AttributeSet attrs, int defStyleAttr) {
        final Context context = mView.getContext();

        Resources.Theme theme = context.getTheme();

        final TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.ExtendedMaterialButton, defStyleAttr, R.style.ExtendedMaterialButton);

        mAnimationDuration = a.getInt(R.styleable.ExtendedMaterialButton_android_animationDuration, mAnimationDuration);

        a.recycle();

    }

    void setAnimationDuration(int animationDuration){
        mAnimationDuration = animationDuration;
    }

    public void setOldStateSet(int[] oldStateSet) {
        this.oldStateSet = oldStateSet;
    }

    void startStateTransition(){

        if(mAnimationDuration<50) return;

        int[] newStateSet = mView.getDrawableState();

        if (newStateSet != null && oldStateSet != null && newStateSet != oldStateSet) {

            if (valueAnimator != null) {
                valueAnimator.cancel();
            }

            final int oldTxColor;
            final int newTxColor;
            final ColorStateList txColorStateList = mView.getTextColors();
            if (txColorStateList != null) {
                oldTxColor = txColorStateList.getColorForState(oldStateSet, 0);
                newTxColor = txColorStateList.getColorForState(newStateSet, 0);
            } else {
                oldTxColor = 0;
                newTxColor = 0;
            }

            final int oldBgColor;
            final int newBgColor;
            final ColorStateList bgColorStateList = mView.getBackgroundTintList();
            final Drawable background = mView.getBackground();
            final MaterialShapeDrawable shapeDrawable = extractMaterialShapeDrawable(background);
            if (bgColorStateList != null) {
                oldBgColor = bgColorStateList.getColorForState(oldStateSet, 0);
                newBgColor = bgColorStateList.getColorForState(newStateSet, 0);
            } else {
                oldBgColor = 0;
                newBgColor = 0;
            }

            final int oldStColor;
            final int newStColor;
            final int stWidth = mView.getStrokeWidth();
            final ColorStateList stColorStateList = mView.getStrokeColor();
            if (stColorStateList != null) {
                oldStColor = stColorStateList.getColorForState(oldStateSet, 0);
                newStColor = stColorStateList.getColorForState(newStateSet, 0);
            } else {
                oldStColor = 0;
                newStColor = 0;
            }

            final int oldRpColor;
            final int newRpColor;
            final ColorStateList rpColorStateList = mView.getRippleColor();
            final RippleDrawable rippleDrawable = extractRippleDrawable(background);
            if (rpColorStateList != null) {
                oldRpColor = rpColorStateList.getColorForState(oldStateSet, 0);
                newRpColor = rpColorStateList.getColorForState(newStateSet, 0);
            } else {
                oldRpColor = 0;
                newRpColor = 0;
            }

            final int oldItColor;
            final int newItColor;
            final ColorStateList itColorStateList = mTextHelper.getIconTint(ICON_POS_TOP);
            final Drawable icT = mTextHelper.getIconDrawable(ICON_POS_TOP);
            if (itColorStateList != null) {
                oldItColor = itColorStateList.getColorForState(oldStateSet, 0);
                newItColor = itColorStateList.getColorForState(newStateSet, 0);
            } else {
                oldItColor = 0;
                newItColor = 0;
            }

            final int oldIbColor;
            final int newIbColor;
            final ColorStateList ibColorStateList = mTextHelper.getIconTint(ICON_POS_BOTTOM);
            final Drawable icB = mTextHelper.getIconDrawable(ICON_POS_BOTTOM);
            if (ibColorStateList != null) {
                oldIbColor = ibColorStateList.getColorForState(oldStateSet, 0);
                newIbColor = ibColorStateList.getColorForState(newStateSet, 0);
            } else {
                oldIbColor = 0;
                newIbColor = 0;
            }

            final int oldIlColor;
            final int newIlColor;
            final ColorStateList ilColorStateList = mTextHelper.getIconTint(ICON_POS_LEFT);
            final Drawable icL = mTextHelper.getIconDrawable(ICON_POS_LEFT);
            if (ilColorStateList != null) {
                oldIlColor = ilColorStateList.getColorForState(oldStateSet, 0);
                newIlColor = ilColorStateList.getColorForState(newStateSet, 0);
            } else {
                oldIlColor = 0;
                newIlColor = 0;
            }

            final int oldIrColor;
            final int newIrColor;
            final ColorStateList irColorStateList = mTextHelper.getIconTint(ICON_POS_RIGHT);
            final Drawable icR = mTextHelper.getIconDrawable(ICON_POS_RIGHT);
            if (ilColorStateList != null) {
                oldIrColor = irColorStateList.getColorForState(oldStateSet, 0);
                newIrColor = irColorStateList.getColorForState(newStateSet, 0);
            } else {
                oldIrColor = 0;
                newIrColor = 0;
            }

            valueAnimator = ValueAnimator.ofFloat(0f, 1f);
            valueAnimator.setDuration(mAnimationDuration);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(
                    valueAnimator -> {
                        float fraction = (float) valueAnimator.getAnimatedValue();

                        int txColor = (int) colorEvaluator.evaluate(fraction, oldTxColor, newTxColor);
                        mView.setTextColor(txColor);

                        int bgColor = (int) colorEvaluator.evaluate(fraction, oldBgColor, newBgColor);
                        int stColor = (int) colorEvaluator.evaluate(fraction, oldStColor, newStColor);
                        if (shapeDrawable != null) {
                            shapeDrawable.setTint(bgColor);
                            shapeDrawable.setStroke(stWidth, stColor);
                        } else {
                            if (background != null) {
                                background.setTint(bgColor);
                            }
                        }

                        int rpColor = (int) colorEvaluator.evaluate(fraction, oldRpColor, newRpColor);
                        if (rippleDrawable != null) {
                            rippleDrawable.setColor(ColorStateList.valueOf(rpColor));
                        }

                        int itColor = (int) colorEvaluator.evaluate(fraction, oldItColor, newItColor);
                        if (icT != null) {
                            icT.setTint(itColor);
                        }

                        int ibColor = (int) colorEvaluator.evaluate(fraction, oldIbColor, newIbColor);
                        if (icB != null) {
                            icB.setTint(ibColor);
                        }

                        int ilColor = (int) colorEvaluator.evaluate(fraction, oldIlColor, newIlColor);
                        if (icL != null) {
                            icL.setTint(ilColor);
                        }

                        int irColor = (int) colorEvaluator.evaluate(fraction, oldIrColor, newIrColor);
                        if (icR != null) {
                            icR.setTint(irColor);
                        }
                    });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mView.setTextColor(txColorStateList);

                    setTintForDrawable(icT, itColorStateList);
                    setTintForDrawable(icB, itColorStateList);
                    setTintForDrawable(icL, itColorStateList);
                    setTintForDrawable(icR, itColorStateList);

                    if (rippleDrawable != null) {
                        rippleDrawable.setColor(rpColorStateList);
                    }

                    if (shapeDrawable != null) {
                        shapeDrawable.setTintList(bgColorStateList);
                        shapeDrawable.setStroke(stWidth, stColorStateList);
                        shapeDrawable.setState(mView.getDrawableState());
                    } else {
                        if (background != null) {
                            background.setTintList(bgColorStateList);
                        }
                    }
                }
            });
            valueAnimator.start();

        }
    }

    private void setTintForDrawable(Drawable drawable, ColorStateList colorStateList){
        if (drawable != null) {
            drawable.setTintList(colorStateList);
        }
    }

    private MaterialShapeDrawable extractMaterialShapeDrawable(Drawable background) {
        if(background instanceof RippleDrawable) {
            RippleDrawable rippleDrawable = (RippleDrawable) background;
            if (rippleDrawable.getNumberOfLayers() > 0) {
                InsetDrawable insetDrawable = (InsetDrawable) rippleDrawable.getDrawable(0);
                LayerDrawable layerDrawable = (LayerDrawable) insetDrawable.getDrawable();
                return (MaterialShapeDrawable) layerDrawable.getDrawable(1);
            }
        }
        return null;
    }

    private RippleDrawable extractRippleDrawable(Drawable background) {
        if(background instanceof RippleDrawable) {
            return  (RippleDrawable) background;
        }
        return null;
    }
}
