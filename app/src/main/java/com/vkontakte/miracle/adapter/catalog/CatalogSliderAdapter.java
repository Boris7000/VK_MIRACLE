package com.vkontakte.miracle.adapter.catalog;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST_RECOMMENDATION;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_GROUP;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_PLAYLIST;
import static com.vkontakte.miracle.engine.util.AdapterUtil.getVerticalLayoutManager;
import static com.vkontakte.miracle.engine.util.FragmentUtil.goToPlaylist;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.WrappedPlaylistViewHolderHorizontal;
import com.vkontakte.miracle.adapter.catalog.holders.WrappedGroupViewHolderHorizontal;
import com.vkontakte.miracle.dialog.audio.PlaylistDialog;
import com.vkontakte.miracle.dialog.audio.PlaylistDialogActionListener;
import com.vkontakte.miracle.engine.adapter.MiracleNestedLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.FragmentUtil;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.catalog.CatalogRecommendedPlaylist;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;
import com.vkontakte.miracle.player.PlayerServiceController;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class CatalogSliderAdapter extends MiracleNestedLoadableAdapter {

    private CatalogBlock catalogBlock;

    public CatalogSliderAdapter(CatalogBlock catalogBlock){
        this.catalogBlock = catalogBlock;

        if(catalogBlock.getItems().size()>0) {
            ArrayList<ItemDataHolder> holders = getItemDataHolders();

            holders.addAll(catalogBlock.getItems());

            setAddedCount(holders.size());

            if(catalogBlock.getNextFrom().isEmpty()){
                setFinallyLoaded(true);
            } else {
                setNextFrom(catalogBlock.getNextFrom());
            }
        } else {
            setHasData(false);
            setFinallyLoaded(true);
        }
    }

    public void setItemDataHolders(CatalogBlock catalogBlock){
        this.catalogBlock = catalogBlock;
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        ArrayList<ItemDataHolder> newHolders = catalogBlock.getItems();
        if(newHolders.size()>0) {
            final int oldSize = holders.size();
            final int newSize = newHolders.size();
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return oldSize;
                }

                @Override
                public int getNewListSize() {
                    return newSize;
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    ItemDataHolder oldItem = holders.get(oldItemPosition);
                    ItemDataHolder newItem = newHolders.get(newItemPosition);
                    if (oldItem instanceof DataItemWrap && newItem instanceof DataItemWrap) {
                        oldItem = (ItemDataHolder) ((DataItemWrap<?, ?>) oldItem).getItem();
                        newItem = (ItemDataHolder) ((DataItemWrap<?, ?>) newItem).getItem();
                    }
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ItemDataHolder oldItem = holders.get(oldItemPosition);
                    ItemDataHolder newItem = newHolders.get(newItemPosition);
                    if (oldItem instanceof DataItemWrap && newItem instanceof DataItemWrap) {
                        oldItem = (ItemDataHolder) ((DataItemWrap<?, ?>) oldItem).getItem();
                        newItem = (ItemDataHolder) ((DataItemWrap<?, ?>) newItem).getItem();
                    }

                    if (oldItem instanceof PlaylistItem && newItem instanceof PlaylistItem) {
                        PlaylistItem oldPlaylistItem = (PlaylistItem) oldItem;
                        PlaylistItem newPlaylistItem = (PlaylistItem) newItem;
                        return oldPlaylistItem.equalsContent(newPlaylistItem);
                    } else {
                        if (oldItem instanceof CatalogRecommendedPlaylist && newItem instanceof CatalogRecommendedPlaylist) {
                            CatalogRecommendedPlaylist oldPlaylistItem = (CatalogRecommendedPlaylist) oldItem;
                            CatalogRecommendedPlaylist newPlaylistItem = (CatalogRecommendedPlaylist) newItem;
                            return oldPlaylistItem.equalsContent(newPlaylistItem);
                        }
                    }
                    return false;
                }
            });
            holders.clear();
            holders.addAll(newHolders);
            if (catalogBlock.getNextFrom().isEmpty()) {
                setFinallyLoaded(true);
            } else {
                setFinallyLoaded(false);
                setNextFrom(catalogBlock.getNextFrom());
            }
            if (!hasData()) {
                setHasData(true);
            }
            setItemDataHolders(holders, result);
            getRecyclerView().scrollToPosition(0);
        } else {
            final int oldSize = holders.size();
            holders.clear();
            setHasData(false);
            setFinallyLoaded(true);
            notifyItemRangeRemoved(0, oldSize);
        }

    }

    @Override
    public void onLoading() throws Exception {
        ProfileItem profileItem = getUserItem();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        Response<JSONObject> response = Catalog.getBlockItems(catalogBlock.getId(),
                getNextFrom(), profileItem.getAccessToken()).execute();

        JSONObject jo_response;
        JSONObject block;

        jo_response = validateBody(response).getJSONObject("response");
        block = jo_response.getJSONObject("block");

        CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);

        ArrayList<ItemDataHolder> itemDataHolders = catalogBlock.findItems(block,catalogExtendedArrays);

        int previous = holders.size();

        holders.addAll(itemDataHolders);

        setAddedCount(holders.size()-previous);

        if(block.has("next_from")){
            setNextFrom(block.getString("next_from"));
        } else {
            setFinallyLoaded(true);
            setNextFrom("");
        }

    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = super.getViewHolderFabricMap();
        arrayMap.put(TYPE_WRAPPED_PLAYLIST, new WrappedPlaylistViewHolderHorizontal.Fabric());
        arrayMap.put(TYPE_WRAPPED_GROUP, new WrappedGroupViewHolderHorizontal.Fabric());
        arrayMap.put(TYPE_PLAYLIST_RECOMMENDATION, new RecommendedPlaylistViewHolderFabric());
        return arrayMap;
    }

    public class RecommendedPlaylistViewHolder extends MiracleViewHolder {

        private final RecyclerView recyclerView;
        private CatalogRecommendedPlaylist catalogRecommendedPlaylist;
        private final TextView title;
        private final TextView subtitle;

        public RecommendedPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(getVerticalLayoutManager(itemView.getContext()));
            RecyclerView.RecycledViewPool recycledViewPool =
                    getNestedRecycledViewPool(TYPE_PLAYLIST_RECOMMENDATION);
            recycledViewPool.setMaxRecycledViews(TYPE_WRAPPED_AUDIO, 15);
            recyclerView.setRecycledViewPool(recycledViewPool);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            itemView.setOnClickListener(view ->
                    goToPlaylist(catalogRecommendedPlaylist.getPlaylistItem(), getMiracleActivity()));
            itemView.setOnLongClickListener(view -> {
                MiracleActivity miracleActivity = getMiracleActivity();
                final ProfileItem userItem = miracleActivity.getUserItem();
                final PlaylistItem playlistItem = catalogRecommendedPlaylist.getPlaylistItem();
                PlaylistDialog playlistDialog = new PlaylistDialog(miracleActivity, playlistItem, userItem);
                playlistDialog.setDialogActionListener(new PlaylistDialogActionListener() {
                    @Override
                    public void follow() {
                        playlistItem.setFollowing(true);
                        new AsyncExecutor<Integer>(){
                            @Override
                            public Integer inBackground() {
                                try {
                                    playlistItem.follow(userItem);
                                    return 1;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return -1;
                            }
                            @Override
                            public void onExecute(Integer object) { }
                        }.start();
                    }

                    @Override
                    public void delete() {
                        playlistItem.setFollowing(false);
                        new AsyncExecutor<Integer>(){
                            @Override
                            public Integer inBackground() {
                                try {
                                    playlistItem.delete(userItem);
                                    return 1;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return -1;
                            }
                            @Override
                            public void onExecute(Integer object) { }
                        }.start();
                    }

                    @Override
                    public void playNext() {
                        PlayerServiceController.get().loadAndSetPlayNext(playlistItem);
                    }

                    @Override
                    public void goToArtist() {
                        FragmentUtil.goToArtist(playlistItem, getMiracleActivity());
                    }

                    @Override
                    public void goToOwner() {
                        FragmentUtil.goToOwner(playlistItem, getMiracleActivity());
                    }
                });
                playlistDialog.show(view.getContext());
                itemView.getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            });
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {
            catalogRecommendedPlaylist = (CatalogRecommendedPlaylist) itemDataHolder;
            PlaylistItem playlistItem = catalogRecommendedPlaylist.getPlaylistItem();
            title.setText(playlistItem.getTitle());
            subtitle.setText(playlistItem.getSubtitle());

            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if(adapter instanceof CatalogTripleStackedAdapter){
                ((CatalogTripleStackedAdapter)recyclerView.getAdapter())
                        .setNewItemDataHolders(catalogRecommendedPlaylist.getAudios());
            } else {
                CatalogTripleStackedAdapter catalogTripleStackedAdapter =
                        new CatalogTripleStackedAdapter(catalogRecommendedPlaylist.getAudios());
                catalogTripleStackedAdapter.iniFromActivity(getMiracleActivity());
                catalogTripleStackedAdapter.setRecyclerView(recyclerView);
                catalogTripleStackedAdapter.ini();
                recyclerView.setAdapter(catalogTripleStackedAdapter);
                catalogTripleStackedAdapter.load();
                recyclerView.setHasFixedSize(true);
            }

        }

    }

    public class RecommendedPlaylistViewHolderFabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new RecommendedPlaylistViewHolder(
                    inflater.inflate(R.layout.view_playlist_recommended_horizontal, viewGroup, false));
        }
    }

}
