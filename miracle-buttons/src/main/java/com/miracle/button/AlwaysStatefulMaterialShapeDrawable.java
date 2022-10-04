package com.miracle.button;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

public class AlwaysStatefulMaterialShapeDrawable extends MaterialShapeDrawable {

    AlwaysStatefulMaterialShapeDrawable(ShapeAppearanceModel shapeAppearanceModel) {
        super(shapeAppearanceModel);
    }

    @Override
    public boolean isStateful() {
        return true;
    }
}