package com.vkontakte.miracle.engine.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import java.util.ArrayList;
import java.util.List;

public class NestedTabsAdapter extends FragmentStateAdapter {

    private final ArrayList<NestedMiracleFragmentFabric> fabrics;

    public NestedTabsAdapter(@NonNull Fragment fragment, ArrayList<NestedMiracleFragmentFabric> fabrics) {
        super(fragment);
        this.fabrics = fabrics;
    }

    public NestedTabsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,
                             ArrayList<NestedMiracleFragmentFabric> fabrics) {
        super(fragmentManager, lifecycle);
        this.fabrics = fabrics;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fabrics.get(position).createFragment();
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return fabrics.size();
    }

    public ArrayList<NestedMiracleFragmentFabric> getFabrics() {
        return fabrics;
    }
}
