package com.miracle.engine.fragment.tabs.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.miracle.engine.fragment.FragmentFabric;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter<T extends FragmentFabric> extends FragmentStateAdapter {

    private final ArrayList<T> fabrics;

    public TabsAdapter(@NonNull Fragment fragment, ArrayList<T> fabrics) {
        super(fragment);
        this.fabrics = fabrics;
    }

    public TabsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,
                             ArrayList<T> fabrics) {
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

    public ArrayList<T> getFabrics() {
        return fabrics;
    }
}