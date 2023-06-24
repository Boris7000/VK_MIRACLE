package com.vkontakte.miracle.fragment.settings;

import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.miracle.engine.fragment.base.templates.BaseListFragment;
import com.vkontakte.miracle.R;

public class FragmentTest extends BaseListFragment {

    @Override
    public void inflateContent(@NonNull LayoutInflater inflater, @NonNull LinearLayout container) {
        inflater.inflate(R.layout.fragment_content_test, container, true);
    }
}
