package com.miracle.widget;

import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class Utils {


    public static TintInfo extractTintInfo(@NonNull TypedArray a, int tintAttr, int tintModeAttr, @Nullable TintInfo defValue){
        TintInfo tintInfo = new TintInfo();
        if (a.hasValue(tintAttr)) {
            tintInfo.mTintList = a.getColorStateList(tintAttr);
            tintInfo.mHasTintList = true;
        } else {
            if(defValue!=null) {
                tintInfo.mTintList = defValue.mTintList;
                tintInfo.mHasTintList = defValue.mHasTintList;
            } else {
                tintInfo.mTintList = null;
                tintInfo.mHasTintList = false;
            }
        }

        if (a.hasValue(tintModeAttr)) {
            tintInfo.mTintMode = intToMode(a.getInt(tintAttr, 0));
            tintInfo.mHasTintMode = true;
        } else {
            if(defValue!=null) {
                tintInfo.mTintMode = defValue.mTintMode;
                tintInfo.mHasTintMode = defValue.mHasTintMode;
            } else {
                tintInfo.mTintMode = null;
                tintInfo.mHasTintMode = false;
            }
        }
        return tintInfo;
    }

    public static PorterDuff.Mode intToMode(int val) {
        switch (val) {
            default:
            case  0: return PorterDuff.Mode.CLEAR;
            case  1: return PorterDuff.Mode.SRC;
            case  2: return PorterDuff.Mode.DST;
            case  3: return PorterDuff.Mode.SRC_OVER;
            case  4: return PorterDuff.Mode.DST_OVER;
            case  5: return PorterDuff.Mode.SRC_IN;
            case  6: return PorterDuff.Mode.DST_IN;
            case  7: return PorterDuff.Mode.SRC_OUT;
            case  8: return PorterDuff.Mode.DST_OUT;
            case  9: return PorterDuff.Mode.SRC_ATOP;
            case 10: return PorterDuff.Mode.DST_ATOP;
            case 11: return PorterDuff.Mode.XOR;
            case 16: return PorterDuff.Mode.DARKEN;
            case 17: return PorterDuff.Mode.LIGHTEN;
            case 13: return PorterDuff.Mode.MULTIPLY;
            case 14: return PorterDuff.Mode.SCREEN;
            case 12: return PorterDuff.Mode.ADD;
            case 15: return PorterDuff.Mode.OVERLAY;
        }
    }

    public static boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }
}
