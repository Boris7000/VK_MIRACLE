package com.miracle.widget;

import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.View.TEXT_ALIGNMENT_GRAVITY;
import static android.view.View.TEXT_ALIGNMENT_INHERIT;
import static android.view.View.TEXT_ALIGNMENT_TEXT_END;
import static android.view.View.TEXT_ALIGNMENT_TEXT_START;
import static android.view.View.TEXT_ALIGNMENT_VIEW_END;
import static android.view.View.TEXT_ALIGNMENT_VIEW_START;
import static android.view.View.VISIBLE;
import static com.miracle.widget.ExtendedTextHelper.Icons.BOTTOM;
import static com.miracle.widget.ExtendedTextHelper.Icons.LEFT;
import static com.miracle.widget.ExtendedTextHelper.Icons.RIGHT;
import static com.miracle.widget.ExtendedTextHelper.Icons.TOP;
import static com.miracle.widget.Utils.extractTintInfo;
import static com.miracle.widget.Utils.isLayoutRtl;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.miracle.miracle_widget.R;

public class ExtendedTextHelper{

    public static final int ICON_POS_TOP = TOP;
    public static final int ICON_POS_BOTTOM = BOTTOM;
    public static final int ICON_POS_LEFT = LEFT;
    public static final int ICON_POS_RIGHT = RIGHT;

    private static final int DEF_STYLE_RES = R.style.ExtendedTextView;
    private final static int UNIDENTIFIED = -1;

    @NonNull
    private final TextView mView;

    private final Icons icons = new Icons();

    ExtendedTextHelper(@NonNull TextView view) {
        mView = view;
    }

    void loadFromAttributes(@Nullable AttributeSet attrs, int defStyleAttr) {
        final Context context = mView.getContext();

        Resources.Theme theme = context.getTheme();

        final TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.ExtendedTextView, defStyleAttr, DEF_STYLE_RES);

        //////////////////////////////////////////////////////////////////

        TintInfo mDrawableTint = extractTintInfo(a, R.styleable.ExtendedTextView_iconsTint, R.styleable.ExtendedTextView_iconsTintMode, null);

        icons.iconsTintInfo[TOP] = extractTintInfo(a,R.styleable.ExtendedTextView_iconTopTint,R.styleable.ExtendedTextView_iconTopTintMode,mDrawableTint);
        icons.iconsTintInfo[BOTTOM] = extractTintInfo(a,R.styleable.ExtendedTextView_iconBottomTint, R.styleable.ExtendedTextView_iconBottomTintMode,mDrawableTint);
        icons.iconsTintInfo[LEFT] = extractTintInfo(a,R.styleable.ExtendedTextView_iconLeftTint, R.styleable.ExtendedTextView_iconLeftTintMode,mDrawableTint);
        icons.iconsTintInfo[RIGHT] = extractTintInfo(a,R.styleable.ExtendedTextView_iconRightTint, R.styleable.ExtendedTextView_iconRightTintMode,mDrawableTint);

        //////////////////////////////////////////////////////////////////

        int mIconSize = a.getDimensionPixelSize(R.styleable.ExtendedTextView_iconsSize, UNIDENTIFIED);

        icons.iconsSizes[TOP] = a.getDimensionPixelSize(R.styleable.ExtendedTextView_iconTopSize, mIconSize);
        icons.iconsSizes[BOTTOM] = a.getDimensionPixelSize(R.styleable.ExtendedTextView_iconBottomSize, mIconSize);
        icons.iconsSizes[LEFT] = a.getDimensionPixelSize(R.styleable.ExtendedTextView_iconLeftSize, mIconSize);
        icons.iconsSizes[RIGHT] = a.getDimensionPixelSize(R.styleable.ExtendedTextView_iconRightSize, mIconSize);

        //////////////////////////////////////////////////////////////////

        boolean mIconStickingToText = a.getBoolean(R.styleable.ExtendedTextView_iconsStickingToText, false);

        icons.iconsStickingToText[TOP] = a.getBoolean(R.styleable.ExtendedTextView_iconTopStickingToText,mIconStickingToText);
        icons.iconsStickingToText[BOTTOM] = a.getBoolean(R.styleable.ExtendedTextView_iconBottomStickingToText,mIconStickingToText);
        icons.iconsStickingToText[LEFT] = a.getBoolean(R.styleable.ExtendedTextView_iconLeftStickingToText,mIconStickingToText);
        icons.iconsStickingToText[RIGHT] = a.getBoolean(R.styleable.ExtendedTextView_iconRightStickingToText,mIconStickingToText);

        //////////////////////////////////////////////////////////////////

        final Drawable topD = a.getDrawable(R.styleable.ExtendedTextView_iconTop);
        if (topD != null) {
            setIconDrawable(topD, TOP);
        }

        final Drawable bottomD = a.getDrawable(R.styleable.ExtendedTextView_iconBottom);
        if (bottomD != null) {
            setIconDrawable(bottomD, BOTTOM);
        }

        final Drawable leftD = a.getDrawable(R.styleable.ExtendedTextView_iconLeft);
        if (leftD != null) {
            setIconDrawable(leftD, LEFT);
        }

        final Drawable rightD = a.getDrawable(R.styleable.ExtendedTextView_iconRight);
        if (rightD != null) {
            setIconDrawable(rightD, RIGHT);
        }

        a.recycle();

    }

    //////////////////////////////////////////////////////////////////

    public Drawable getIconDrawable(int index){
        return icons.icons[index];
    }

    public ColorStateList getIconTint(int index){
        return icons.iconsTintInfo[index].mTintList;
    }

    //////////////////////////////////////////////////////////////////

    private void setIconDrawable(@Nullable Drawable drawable, int index){
        Drawable oldDrawable = icons.icons[index];
        if (oldDrawable != drawable) {
            if (oldDrawable != null) {
                oldDrawable.setCallback(null);
                mView.unscheduleDrawable(oldDrawable);
            }

            icons.icons[index] = drawable;

            if (drawable != null) {
                drawable.setCallback(mView);
                drawable.setLayoutDirection(mView.getLayoutDirection());
                applyTintForDrawable(index);
                drawable.setVisible(mView.getVisibility() == VISIBLE, false);
            }
        }
    }

    private void applyTintForDrawable(int index){
        Drawable drawable = icons.icons[index];
        TintInfo tintInfo = icons.iconsTintInfo[index];
        if(drawable!=null){
            icons.icons[index] = drawable.mutate();
            drawable = icons.icons[index];

            if (tintInfo.mHasTintList) {
                drawable.setTintList(tintInfo.mTintList);
            } else {
                drawable.setTintList(null);
            }

            if (tintInfo.mHasTintMode) {
                drawable.setTintMode(tintInfo.mTintMode);
            } else {
                drawable.setTintMode(null);
            }

            if(tintInfo.mHasTintList||tintInfo.mHasTintMode) {
                if (drawable.isStateful()) {
                    drawable.setState(mView.getDrawableState());
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////

    void drawIcons(Canvas canvas){

        final Drawable iconTop = icons.icons[TOP];
        final Drawable iconBottom = icons.icons[BOTTOM];
        final Drawable iconLeft = icons.icons[LEFT];
        final Drawable iconRight = icons.icons[RIGHT];

        if(iconTop!=null||iconBottom!=null ||iconLeft!=null||iconRight!=null) {
            boolean rtl = isLayoutRtl(mView);

            final int paddingTop = mView.getPaddingTop();
            final int paddingBottom = mView.getPaddingBottom();
            final int paddingStart = rtl?mView.getPaddingEnd():mView.getPaddingStart();
            final int paddingEnd = rtl?mView.getPaddingStart():mView.getPaddingEnd();

            final int compoundPaddingTop = mView.getCompoundPaddingTop();
            final int compoundPaddingBottom = mView.getCompoundPaddingBottom();
            final int compoundPaddingLeft = mView.getCompoundPaddingLeft();
            final int compoundPaddingRight = mView.getCompoundPaddingRight();

            final int drawablePadding = mView.getText().length()>0?mView.getCompoundDrawablePadding():0;

            int width = mView.getWidth();
            int height = mView.getHeight();
            int freeWidth = width-compoundPaddingLeft-compoundPaddingRight;
            int freeHeight = height-compoundPaddingTop-compoundPaddingBottom;
            int textWidth = getTextWidth();
            int textHeight = getTextHeight();

            Layout.Alignment textVerticalAlignment = getGravityVerticalTextAlignment();
            Layout.Alignment textHorizontalAlignment = getActualTextAlignment();

            if (rtl) {

                if (iconTop != null) {
                    final int drawableHeight = getDrawableHeight(iconTop,icons.iconsSizes[TOP]);
                    final int drawableWidth = getDrawableWidth(iconTop,icons.iconsSizes[TOP]);

                    final int top;
                    final int bottom;

                    switch (textVerticalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            bottom = compoundPaddingTop-drawablePadding;
                            top = bottom - drawableHeight;
                            break;
                        }

                        case ALIGN_CENTER:{
                            if(icons.iconsStickingToText[TOP]){
                                bottom = ((height-textHeight)/2)-drawablePadding-((compoundPaddingBottom-compoundPaddingTop)/2);
                                top = bottom - drawableHeight;
                            } else {
                                top = paddingTop;
                                bottom = top + drawableHeight;
                            }
                            break;
                        }

                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[TOP]){
                                top = height-compoundPaddingBottom-textHeight-drawablePadding;
                                bottom = top - drawableHeight;
                            } else {
                                top = paddingTop;
                                bottom = top + drawableHeight;
                            }
                            break;
                        }
                    }

                    final int left;
                    final int right;

                    switch (textHorizontalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[TOP]) {
                                left = ((textWidth-drawableWidth)/2)+compoundPaddingLeft;
                                right = left + drawableWidth;
                            } else {
                                left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                                right = left+drawableWidth;
                            }
                            break;
                        }
                        case ALIGN_CENTER:{
                            left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                            right = left+drawableWidth;
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[TOP]){
                                right = width-compoundPaddingRight-((textWidth-drawableWidth)/2);
                                left = right - drawableWidth;
                            } else {
                                left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                                right = left+drawableWidth;
                            }
                            break;
                        }
                    }

                    iconTop.setBounds(left, top, right, bottom);
                }

                if (iconBottom != null) {
                    final int drawableHeight = getDrawableHeight(iconBottom,icons.iconsSizes[BOTTOM]);
                    final int drawableWidth = getDrawableWidth(iconBottom,icons.iconsSizes[BOTTOM]);

                    final int bottom;
                    final int top;

                    switch (textVerticalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[BOTTOM]){
                                top = compoundPaddingTop+textHeight+drawablePadding;
                                bottom = top+drawableHeight;
                            } else {
                                bottom = height-paddingBottom;
                                top = bottom-drawableHeight;
                            }
                            break;
                        }

                        case ALIGN_CENTER:{
                            if(icons.iconsStickingToText[BOTTOM]){
                                top = ((height-textHeight)/2)+textHeight+drawablePadding-((compoundPaddingBottom-compoundPaddingTop)/2);
                                bottom = top+drawableHeight;
                            } else {
                                bottom = height-paddingBottom;
                                top = bottom-drawableHeight;
                            }
                            break;
                        }

                        case ALIGN_OPPOSITE:{
                            top = height-compoundPaddingBottom+drawablePadding;
                            bottom = top+drawableHeight;
                            break;
                        }
                    }

                    final int left;
                    final int right;

                    switch (textHorizontalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[BOTTOM]) {
                                left = ((textWidth-drawableWidth)/2)+compoundPaddingLeft;
                                right = left + drawableWidth;
                            } else {
                                left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                                right = left+drawableWidth;
                            }
                            break;
                        }
                        case ALIGN_CENTER:{
                            left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                            right = left+drawableWidth;
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[BOTTOM]){
                                right = width-compoundPaddingRight-((textWidth-drawableWidth)/2);
                                left = right - drawableWidth;
                            } else {
                                left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                                right = left+drawableWidth;
                            }
                            break;
                        }
                    }

                    iconBottom.setBounds(left, top, right, bottom);
                }

                if (iconLeft != null) {
                    final int drawableHeight = getDrawableHeight(iconLeft,icons.iconsSizes[LEFT]);
                    final int drawableWidth = getDrawableWidth(iconLeft,icons.iconsSizes[LEFT]);

                    final int top;
                    final int bottom;

                    switch (textVerticalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[LEFT]) {
                                top = compoundPaddingTop+((textHeight-drawableHeight)/2);
                                bottom = top + drawableHeight;
                            } else {
                                top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                                bottom = top+drawableHeight;
                            }
                            break;
                        }
                        case ALIGN_CENTER:{
                            top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                            bottom = top+drawableHeight;
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[LEFT]){
                                bottom = height-compoundPaddingBottom-((textHeight-drawableHeight)/2);
                                top = bottom - drawableHeight;
                            } else {
                                top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                                bottom = top+drawableHeight;
                            }
                            break;
                        }
                    }

                    final int left;
                    final int right;

                    switch (textHorizontalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[LEFT]){
                                left = textWidth+compoundPaddingLeft+drawablePadding;
                                right = left+drawableWidth;
                            } else {
                                right = width-paddingEnd;
                                left = right-drawableWidth;
                            }
                            break;
                        }
                        case ALIGN_CENTER:{
                            if(icons.iconsStickingToText[LEFT]){
                                left = ((width-textWidth)/2)+textWidth-((compoundPaddingRight-compoundPaddingLeft)/2)+drawablePadding;
                                right = left+drawableWidth;
                            } else {
                                right = width-paddingEnd;
                                left = right-drawableWidth;
                            }
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            right = width-paddingEnd;
                            left = right-drawableWidth;
                            break;
                        }
                    }

                    iconLeft.setBounds(left, top, right, bottom);
                }

                if (iconRight != null) {
                    final int drawableHeight = getDrawableHeight(iconRight,icons.iconsSizes[RIGHT]);
                    final int drawableWidth = getDrawableWidth(iconRight,icons.iconsSizes[RIGHT]);

                    final int top;
                    final int bottom;

                    switch (textVerticalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[RIGHT]) {
                                top = compoundPaddingTop+((textHeight-drawableHeight)/2);
                                bottom = top + drawableHeight;
                            } else {
                                top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                                bottom = top+drawableHeight;
                            }
                            break;
                        }
                        case ALIGN_CENTER:{
                            top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                            bottom = top+drawableHeight;
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[RIGHT]){
                                bottom = height-compoundPaddingBottom-((textHeight-drawableHeight)/2);
                                top = bottom - drawableHeight;
                            } else {
                                top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                                bottom = top+drawableHeight;
                            }
                            break;
                        }
                    }

                    final int right;
                    final int left;

                    switch (textHorizontalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            left = paddingStart;
                            right = left+drawableWidth;
                            break;
                        }
                        case ALIGN_CENTER:{
                            if(icons.iconsStickingToText[RIGHT]){
                                right = ((width-textWidth)/2)-drawablePadding-((compoundPaddingRight-compoundPaddingLeft)/2);
                                left = right - drawableWidth;
                            } else {
                                left = paddingStart;
                                right = left + drawableWidth;
                            }
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[RIGHT]){
                                right = width-compoundPaddingRight-textWidth-drawablePadding;
                                left = right - drawableWidth;
                            } else {
                                left = paddingStart;
                                right = left + drawableWidth;
                            }
                            break;
                        }
                    }

                    iconRight.setBounds(left, top, right, bottom);
                }

            } else {

                if (iconTop != null) {
                    final int drawableHeight = getDrawableHeight(iconTop,icons.iconsSizes[TOP]);
                    final int drawableWidth = getDrawableWidth(iconTop,icons.iconsSizes[TOP]);

                    final int top;
                    final int bottom;

                    switch (textVerticalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            bottom = compoundPaddingTop-drawablePadding;
                            top = bottom - drawableHeight;
                            break;
                        }

                        case ALIGN_CENTER:{
                            if(icons.iconsStickingToText[TOP]){
                                bottom = ((height-textHeight)/2)-drawablePadding-((compoundPaddingBottom-compoundPaddingTop)/2);
                                top = bottom - drawableHeight;
                            } else {
                                top = paddingTop;
                                bottom = top + drawableHeight;
                            }
                            break;
                        }

                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[TOP]){
                                top = height-compoundPaddingBottom-textHeight-drawablePadding;
                                bottom = top - drawableHeight;
                            } else {
                                top = paddingTop;
                                bottom = top + drawableHeight;
                            }
                            break;
                        }
                    }

                    final int left;
                    final int right;

                    switch (textHorizontalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[TOP]) {
                                left = ((textWidth-drawableWidth)/2)+compoundPaddingLeft;
                                right = left + drawableWidth;
                            } else {
                                left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                                right = left+drawableWidth;
                            }
                            break;
                        }
                        case ALIGN_CENTER:{
                            left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                            right = left+drawableWidth;
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[TOP]){
                                right = width-compoundPaddingRight-((textWidth-drawableWidth)/2);
                                left = right - drawableWidth;
                            } else {
                                left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                                right = left+drawableWidth;
                            }
                            break;
                        }
                    }

                    iconTop.setBounds(left, top, right, bottom);
                }

                if (iconBottom != null) {
                    final int drawableHeight = getDrawableHeight(iconBottom,icons.iconsSizes[BOTTOM]);
                    final int drawableWidth = getDrawableWidth(iconBottom,icons.iconsSizes[BOTTOM]);

                    final int bottom;
                    final int top;

                    switch (textVerticalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[BOTTOM]){
                                top = compoundPaddingTop+textHeight+drawablePadding;
                                bottom = top+drawableHeight;
                            } else {
                                bottom = height-paddingBottom;
                                top = bottom-drawableHeight;
                            }
                            break;
                        }

                        case ALIGN_CENTER:{
                            if(icons.iconsStickingToText[BOTTOM]){
                                top = ((height-textHeight)/2)+textHeight+drawablePadding-((compoundPaddingBottom-compoundPaddingTop)/2);
                                bottom = top+drawableHeight;
                            } else {
                                bottom = height-paddingBottom;
                                top = bottom-drawableHeight;
                            }
                            break;
                        }

                        case ALIGN_OPPOSITE:{
                            top = height-compoundPaddingBottom+drawablePadding;
                            bottom = top+drawableHeight;
                            break;
                        }
                    }

                    final int left;
                    final int right;

                    switch (textHorizontalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[BOTTOM]) {
                                left = ((textWidth-drawableWidth)/2)+compoundPaddingLeft;
                                right = left + drawableWidth;
                            } else {
                                left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                                right = left+drawableWidth;
                            }
                            break;
                        }
                        case ALIGN_CENTER:{
                            left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                            right = left+drawableWidth;
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[BOTTOM]){
                                right = width-compoundPaddingRight-((textWidth-drawableWidth)/2);
                                left = right - drawableWidth;
                            } else {
                                left = compoundPaddingLeft+(((freeWidth)-drawableHeight)/2);
                                right = left+drawableWidth;
                            }
                            break;
                        }
                    }

                    iconBottom.setBounds(left, top, right, bottom);
                }

                if (iconLeft != null) {
                    final int drawableHeight = getDrawableHeight(iconLeft,icons.iconsSizes[LEFT]);
                    final int drawableWidth = getDrawableWidth(iconLeft,icons.iconsSizes[LEFT]);

                    final int top;
                    final int bottom;

                    switch (textVerticalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[LEFT]) {
                                top = compoundPaddingTop+((textHeight-drawableHeight)/2);
                                bottom = top + drawableHeight;
                            } else {
                                top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                                bottom = top+drawableHeight;
                            }
                            break;
                        }
                        case ALIGN_CENTER:{
                            top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                            bottom = top+drawableHeight;
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[LEFT]){
                                bottom = height-compoundPaddingBottom-((textHeight-drawableHeight)/2);
                                top = bottom - drawableHeight;
                            } else {
                                top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                                bottom = top+drawableHeight;
                            }
                            break;
                        }
                    }

                    final int left;
                    final int right;

                    switch (textHorizontalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            left = paddingStart;
                            right = left+drawableWidth;
                            break;
                        }
                        case ALIGN_CENTER:{
                            if(icons.iconsStickingToText[LEFT]){
                                right = ((width-textWidth)/2)-drawablePadding-((compoundPaddingRight-compoundPaddingLeft)/2);
                                left = right - drawableWidth;
                            } else {
                                left = paddingStart;
                                right = left + drawableWidth;
                            }
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[LEFT]){
                                right = width-compoundPaddingRight-textWidth-drawablePadding;
                                left = right - drawableWidth;
                            } else {
                                left = paddingStart;
                                right = left + drawableWidth;
                            }
                            break;
                        }
                    }

                    iconLeft.setBounds(left, top, right, bottom);
                }

                if (iconRight != null) {
                    final int drawableHeight = getDrawableHeight(iconRight,icons.iconsSizes[RIGHT]);
                    final int drawableWidth = getDrawableWidth(iconRight,icons.iconsSizes[RIGHT]);

                    final int top;
                    final int bottom;

                    switch (textVerticalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[RIGHT]) {
                                top = compoundPaddingTop+((textHeight-drawableHeight)/2);
                                bottom = top + drawableHeight;
                            } else {
                                top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                                bottom = top+drawableHeight;
                            }
                            break;
                        }
                        case ALIGN_CENTER:{
                            top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                            bottom = top+drawableHeight;
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            if(icons.iconsStickingToText[RIGHT]){
                                bottom = height-compoundPaddingBottom-((textHeight-drawableHeight)/2);
                                top = bottom - drawableHeight;
                            } else {
                                top = compoundPaddingTop+(((freeHeight)-drawableHeight)/2);
                                bottom = top+drawableHeight;
                            }
                            break;
                        }
                    }

                    final int right;
                    final int left;

                    switch (textHorizontalAlignment){
                        default:
                        case ALIGN_NORMAL:{
                            if(icons.iconsStickingToText[RIGHT]){
                                left = textWidth+compoundPaddingLeft+drawablePadding;
                                right = left+drawableWidth;
                            } else {
                                right = width-paddingEnd;
                                left = right-drawableWidth;
                            }
                            break;
                        }
                        case ALIGN_CENTER:{
                            if(icons.iconsStickingToText[RIGHT]){
                                left = ((width-textWidth)/2)+textWidth-((compoundPaddingRight-compoundPaddingLeft)/2)+drawablePadding;
                                right = left+drawableWidth;
                            } else {
                                right = width-paddingEnd;
                                left = right-drawableWidth;
                            }
                            break;
                        }
                        case ALIGN_OPPOSITE:{
                            right = width-paddingEnd;
                            left = right-drawableWidth;
                            break;
                        }
                    }

                    iconRight.setBounds(left, top, right, bottom);
                }

            }
        }

        if (iconLeft!=null) {
            iconLeft.draw(canvas);
        }

        if (iconTop!=null) {
            iconTop.draw(canvas);
        }

        if (iconRight!=null) {
            iconRight.draw(canvas);
        }

        if (iconBottom!=null) {
            iconBottom.draw(canvas);
        }

    }

    void jumpIconsDrawablesToState(){
        final Drawable iconTop = icons.icons[TOP];
        final Drawable iconBottom = icons.icons[BOTTOM];
        final Drawable iconLeft = icons.icons[LEFT];
        final Drawable iconRight = icons.icons[RIGHT];

        if (iconTop != null) iconTop.jumpToCurrentState();
        if (iconBottom != null) iconBottom.jumpToCurrentState();
        if (iconLeft != null) iconLeft.jumpToCurrentState();
        if (iconRight != null) iconRight.jumpToCurrentState();
    }

    void changeIconsDrawablesStates(int[] drawableState){
        final Drawable iconTop = icons.icons[TOP];
        final Drawable iconBottom = icons.icons[BOTTOM];
        final Drawable iconLeft = icons.icons[LEFT];
        final Drawable iconRight = icons.icons[RIGHT];

        if (iconTop != null && iconTop.isStateful() && iconTop.setState(drawableState)) {
            mView.invalidateDrawable(iconTop);
        }

        if (iconBottom != null && iconBottom.isStateful() && iconBottom.setState(drawableState)) {
            mView.invalidateDrawable(iconBottom);
        }

        if (iconLeft != null && iconLeft.isStateful() && iconLeft.setState(drawableState)) {
            mView.invalidateDrawable(iconLeft);
        }

        if (iconRight != null && iconRight.isStateful() && iconRight.setState(drawableState)) {
            mView.invalidateDrawable(iconRight);
        }
    }

    //////////////////////////////////////////////////////////////////

    int getCompoundPaddingTop(int oldTop) {
        int newTop = oldTop;
        int paddingTop = mView.getPaddingTop();
        int drawablePadding = mView.getText().length()>0?mView.getCompoundDrawablePadding():0;
        int textHeight = getTextHeight();

        Drawable iconLeft = icons.icons[LEFT];

        if (iconLeft != null) {
            final int drawableHeight = getDrawableHeight(iconLeft, icons.iconsSizes[LEFT]);
            if(drawableHeight>textHeight) {
                newTop = Math.max(newTop, paddingTop+((drawableHeight-textHeight)/2));
            }
        }

        Drawable iconRight = icons.icons[RIGHT];

        if (iconRight != null) {
            final int drawableHeight = getDrawableHeight(iconRight, icons.iconsSizes[RIGHT]);
            if(drawableHeight>textHeight) {
                newTop = Math.max(newTop, paddingTop+((drawableHeight-textHeight)/2));
            }
        }

        Drawable iconTop = icons.icons[TOP];

        if (iconTop != null) {
            final int drawableHeight =  getDrawableHeight(iconTop, icons.iconsSizes[TOP]);
            newTop = Math.max(newTop, paddingTop+drawablePadding+drawableHeight);
        }

        return newTop;
    }

    int getCompoundPaddingBottom(int oldBottom) {
        int newBottom = oldBottom;
        int paddingBottom = mView.getPaddingBottom();
        int drawablePadding = mView.getText().length()>0?mView.getCompoundDrawablePadding():0;
        int textHeight = getTextHeight();

        Drawable iconLeft = icons.icons[LEFT];

        if (iconLeft != null) {
            final int drawableHeight = getDrawableHeight(iconLeft, icons.iconsSizes[LEFT]);
            if(drawableHeight>textHeight) {
                newBottom = Math.max(newBottom, paddingBottom+((drawableHeight-textHeight)/2));
            }
        }

        Drawable iconRight = icons.icons[RIGHT];

        if (iconRight != null) {
            final int drawableHeight = getDrawableHeight(iconRight, icons.iconsSizes[RIGHT]);
            if(drawableHeight>textHeight) {
                newBottom = Math.max(newBottom, paddingBottom+((drawableHeight-textHeight)/2));
            }
        }

        Drawable iconBottom = icons.icons[BOTTOM];

        if (iconBottom != null) {
            final int drawableHeight = getDrawableHeight(iconBottom, icons.iconsSizes[BOTTOM]);
            newBottom = Math.max(newBottom, paddingBottom+drawablePadding+drawableHeight);
        }

        return newBottom;
    }

    int getCompoundPaddingLeft(int oldLeft) {
        int newStart = oldLeft;
        boolean rtl = isLayoutRtl(mView);
        int paddingSide = rtl?mView.getPaddingEnd():mView.getPaddingStart();
        int drawablePadding = mView.getText().length()>0?mView.getCompoundDrawablePadding():0;

        int textWidth = getTextWidth();

        Drawable iconTop = icons.icons[TOP];

        if (iconTop != null) {
            final int drawableWidth = getDrawableWidth(iconTop, icons.iconsSizes[TOP]);
            if(drawableWidth>textWidth){
                newStart = Math.max(newStart, paddingSide+((drawableWidth-textWidth)/2));
            }
        }

        Drawable iconBottom = icons.icons[BOTTOM];

        if (iconBottom != null) {
            final int drawableWidth = getDrawableWidth(iconBottom, icons.iconsSizes[BOTTOM]);
            if(drawableWidth>textWidth){
                newStart = Math.max(newStart, paddingSide+((drawableWidth-textWidth)/2));
            }
        }

        int sideIconIndex = isLayoutRtl(mView)?RIGHT:LEFT;

        Drawable iconSide = icons.icons[sideIconIndex];

        if (iconSide != null) {
            final int drawableWidth = getDrawableWidth(iconSide, icons.iconsSizes[sideIconIndex]);
            newStart = Math.max(newStart, paddingSide+drawablePadding+drawableWidth);
        }

        return newStart;
    }

    int getCompoundPaddingRight(int oldRight) {
        int newEnd = oldRight;
        boolean rtl = isLayoutRtl(mView);
        int paddingSide = rtl?mView.getPaddingStart():mView.getPaddingEnd();
        int drawablePadding = mView.getText().length()>0?mView.getCompoundDrawablePadding():0;
        int textWidth = getTextWidth();

        Drawable iconTop = icons.icons[TOP];

        if (iconTop != null) {
            final int drawableWidth = getDrawableWidth(iconTop, icons.iconsSizes[TOP]);
            if(drawableWidth>textWidth){
                newEnd = Math.max(newEnd, paddingSide+((drawableWidth-textWidth)/2));
            }
        }

        Drawable iconBottom = icons.icons[BOTTOM];

        if (iconBottom != null) {
            final int drawableWidth = getDrawableWidth(iconBottom, icons.iconsSizes[BOTTOM]);
            if(drawableWidth>textWidth){
                newEnd = Math.max(newEnd, paddingSide+((drawableWidth-textWidth)/2));
            }
        }

        int sideIconIndex = isLayoutRtl(mView)?LEFT:RIGHT;

        Drawable iconSide = icons.icons[sideIconIndex];

        if (iconSide != null) {
            final int drawableWidth = getDrawableWidth(iconSide, icons.iconsSizes[sideIconIndex]);
            newEnd = Math.max(newEnd, paddingSide+drawablePadding+drawableWidth);
        }

        return newEnd;
    }

    //////////////////////////////////////////////////////////////////

    private int getTextWidth(){
        Paint textPaint = mView.getPaint();
        String text = mView.getText().toString();
        if (mView.getTransformationMethod() != null) {
            // if text is transformed, add that transformation to to ensure correct calculation
            // of icon padding.
            text = mView.getTransformationMethod().getTransformation(text, mView).toString();
        }
        return (int) textPaint.measureText(text);
    }

    private int getTextHeight() {
        Paint.FontMetrics fm = mView.getPaint().getFontMetrics();
        return (int) (fm.descent - fm.ascent)*mView.getLineCount();
    }

    //////////////////////////////////////////////////////////////////

    private Layout.Alignment getGravityVerticalTextAlignment() {
        switch (mView.getGravity() & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.CENTER_VERTICAL:
                return Layout.Alignment.ALIGN_CENTER;
            case Gravity.BOTTOM:
                return Layout.Alignment.ALIGN_OPPOSITE;
            case Gravity.TOP:
            default:
                return Layout.Alignment.ALIGN_NORMAL;
        }
    }

    private Layout.Alignment getGravityHorizontalTextAlignment() {
        switch (mView.getGravity() & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                return Layout.Alignment.ALIGN_CENTER;
            case Gravity.END:
            case Gravity.RIGHT:
                return Layout.Alignment.ALIGN_OPPOSITE;
            case Gravity.START:
            case Gravity.LEFT:
            default:
                return Layout.Alignment.ALIGN_NORMAL;
        }
    }

    private Layout.Alignment getActualTextAlignment() {
        switch (mView.getTextAlignment()) {
            case TEXT_ALIGNMENT_GRAVITY:
                return getGravityHorizontalTextAlignment();
            case TEXT_ALIGNMENT_CENTER:
                return Layout.Alignment.ALIGN_CENTER;
            case TEXT_ALIGNMENT_TEXT_END:
            case TEXT_ALIGNMENT_VIEW_END:
                return Layout.Alignment.ALIGN_OPPOSITE;
            case TEXT_ALIGNMENT_TEXT_START:
            case TEXT_ALIGNMENT_VIEW_START:
            case TEXT_ALIGNMENT_INHERIT:
            default:
                return Layout.Alignment.ALIGN_NORMAL;
        }
    }

    //////////////////////////////////////////////////////////////////

    private int getDrawableWidth(Drawable drawable, int defSize){
        return defSize!=UNIDENTIFIED?defSize:drawable.getIntrinsicWidth();
    }

    private int getDrawableHeight(Drawable drawable, int defSize){
        return defSize!=UNIDENTIFIED?defSize:drawable.getIntrinsicHeight();
    }

    //////////////////////////////////////////////////////////////////

    void setIcon(@Nullable Drawable drawable, int pos) {
        setIconDrawable(drawable, pos);
        mView.invalidate();
    }

    void setIconResource(@DrawableRes int resourceId, int pos) {
        if (resourceId != 0) {
            Drawable value = AppCompatResources.getDrawable(mView.getContext(), resourceId);
            setIcon(value,pos);
        } else {
            setIcon(null, pos);
        }
    }

    void setIconTint(@ColorInt int color, int pos) {
        setIconTintList(new ColorStateList(new int[][]{}, new int[]{color}), pos);
    }

    void setIconTintList(ColorStateList tintList, int pos) {
        TintInfo tintInfo = icons.iconsTintInfo[pos];
        tintInfo.mTintList=tintList;
        tintInfo.mHasTintMode=tintList!=null;
        applyTintForDrawable(pos);
    }

    void setIconTintMode(PorterDuff.Mode tintMode, int pos) {
        TintInfo tintInfo = icons.iconsTintInfo[pos];
        tintInfo.mTintMode=tintMode;
        tintInfo.mHasTintMode=tintMode!=null;
        applyTintForDrawable(pos);
    }

    void setIconSize(int iconSize, int pos) {
        icons.iconsSizes[pos] = iconSize;
        mView.invalidate();
    }

    void setIconStickingToText(boolean stickingToText, int pos) {
        icons.iconsStickingToText[pos] = stickingToText;
        mView.invalidate();
    }

    //////////////////////////////////////////////////////////////////

    void setIconsTint(@ColorInt int color) {
        ColorStateList tintList = new ColorStateList(new int[][]{}, new int[]{color});
        setIconsTintList(tintList);
    }

    void setIconsTintList(ColorStateList tintList) {
        setIconTintList(tintList, TOP);
        setIconTintList(tintList, BOTTOM);
        setIconTintList(tintList, LEFT);
        setIconTintList(tintList, RIGHT);
    }

    void setIconsTintMode(PorterDuff.Mode tintMode) {
        setIconTintMode(tintMode, TOP);
        setIconTintMode(tintMode, BOTTOM);
        setIconTintMode(tintMode, LEFT);
        setIconTintMode(tintMode, RIGHT);
    }

    void setIconsSize(int iconSize) {
        icons.iconsSizes[TOP] = iconSize;
        icons.iconsSizes[BOTTOM] = iconSize;
        icons.iconsSizes[LEFT] = iconSize;
        icons.iconsSizes[RIGHT] = iconSize;
        mView.invalidate();
    }

    void setIconsStickingToText(boolean stickingToText) {
        icons.iconsStickingToText[TOP] = stickingToText;
        icons.iconsStickingToText[BOTTOM] = stickingToText;
        icons.iconsStickingToText[LEFT] = stickingToText;
        icons.iconsStickingToText[RIGHT] = stickingToText;
        mView.invalidate();
    }

    //////////////////////////////////////////////////////////////////

    static class Icons {
        static final int LEFT = 0;
        static final int TOP = 1;
        static final int RIGHT = 2;
        static final int BOTTOM = 3;

        private final Drawable[] icons = new Drawable[4];
        private final TintInfo[] iconsTintInfo = new TintInfo[4];
        private final int[] iconsSizes = new int[4];
        private final boolean[] iconsStickingToText = new boolean[4];
    }
}
