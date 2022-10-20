package com.vkontakte.miracle.engine.recycler;

import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MiracleViewRecycler {

    static final boolean DEBUG = false;
    private static final int DEFAULT_MAX_SCRAP = 5;

    static class ScrapData {
        final ArrayList<RecyclerView.ViewHolder> mScrapHeap = new ArrayList<>();
        int mMaxScrap = DEFAULT_MAX_SCRAP;
    }

    SparseArray<ScrapData> mScrap = new SparseArray<>();

    public void clear() {
        for (int i = 0; i < mScrap.size(); i++) {
            ScrapData data = mScrap.valueAt(i);
            data.mScrapHeap.clear();
        }
    }

    public void setMaxRecycledViews(int viewType, int max) {
        ScrapData scrapData = getScrapDataForType(viewType);
        scrapData.mMaxScrap = max;
        final ArrayList<RecyclerView.ViewHolder> scrapHeap = scrapData.mScrapHeap;
        while (scrapHeap.size() > max) {
            scrapHeap.remove(scrapHeap.size() - 1);
        }
    }

    public int getRecycledViewCount(int viewType) {
        return getScrapDataForType(viewType).mScrapHeap.size();
    }

    @Nullable
    public RecyclerView.ViewHolder getRecycledView(int viewType) {
        final ScrapData scrapData = mScrap.get(viewType);
        if (scrapData != null && !scrapData.mScrapHeap.isEmpty()) {
            final ArrayList<RecyclerView.ViewHolder> scrapHeap = scrapData.mScrapHeap;
            if(DEBUG) {
                Log.d("foekfoeofek", "type = " + viewType + " count = " + scrapHeap.size());
            }
            return scrapHeap.remove(0);
        }
        return null;
    }

    int size() {
        int count = 0;
        for (int i = 0; i < mScrap.size(); i++) {
            ArrayList<RecyclerView.ViewHolder> viewHolders = mScrap.valueAt(i).mScrapHeap;
            if (viewHolders != null) {
                count += viewHolders.size();
            }
        }
        return count;
    }

    public void putRecycledView(RecyclerView.ViewHolder scrap, int viewType) {
        final ArrayList<RecyclerView.ViewHolder> scrapHeap = getScrapDataForType(viewType).mScrapHeap;
        if (mScrap.get(viewType).mMaxScrap <= scrapHeap.size()) {
            return;
        }
        if (DEBUG && scrapHeap.contains(scrap)) {
            throw new IllegalArgumentException("this scrap item already exists");
        }
        scrapHeap.add(scrap);
        if(DEBUG) {
            Log.d("foekfoeofek", "type = " + viewType + " count = " + scrapHeap.size());
        }
    }

    private ScrapData getScrapDataForType(int viewType) {
        ScrapData scrapData = mScrap.get(viewType);
        if (scrapData == null) {
            scrapData = new ScrapData();
            mScrap.put(viewType, scrapData);
        }
        return scrapData;
    }
}
