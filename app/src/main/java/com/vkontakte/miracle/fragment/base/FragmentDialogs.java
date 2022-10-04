package com.vkontakte.miracle.fragment.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.messages.ConversationsAdapter;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.base.BaseRecyclerFragment;

public class FragmentDialogs extends BaseRecyclerFragment {

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_dialogs, container, false);
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new ConversationsAdapter();
    }

    @Override
    public String requestTitleText() {
        return getMiracleActivity().getString(R.string.dialogs);
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public MiracleFragment createFragment() {
            return new FragmentDialogs();
        }
    }
}
