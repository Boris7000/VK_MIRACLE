package com.miracle.button.drawable;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

public class AlwaysStatefulMaterialShapeDrawable extends MaterialShapeDrawable {

    public AlwaysStatefulMaterialShapeDrawable(ShapeAppearanceModel shapeAppearanceModel) {
        super(shapeAppearanceModel);
    }

    @Override
    public boolean isStateful() {
        return true;
    }
}