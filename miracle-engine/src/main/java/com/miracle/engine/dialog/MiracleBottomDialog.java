package com.miracle.engine.dialog;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.miracle.engine.R;

public abstract class MiracleBottomDialog extends BottomSheetDialog {

   public MiracleBottomDialog(@NonNull Context context) {
      this(context, R.style.BottomSheetDialog);
   }

   public MiracleBottomDialog(@NonNull Context context, int theme) {
      super(context, theme);
   }

   public abstract void show(Context context);

   public void expand(){
      FrameLayout bottomSheet = findViewById(R.id.design_bottom_sheet);
      if(bottomSheet!=null) {
         BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
         behavior.setSkipCollapsed(true);
         behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      }
   }
}
