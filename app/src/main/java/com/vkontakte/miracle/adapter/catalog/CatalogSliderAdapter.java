package com.vkontakte.miracle.adapter.catalog;

import static com.vkontakte.miracle.adapter.audio.holders.WrappedPlaylistViewHolderHorizontal.resolveDelete;
import static com.vkontakte.miracle.adapter.audio.holders.WrappedPlaylistViewHolderHorizontal.resolveFollow;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_BANNER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_LINK;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_SUGGESTION;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST_RECOMMENDATION;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_GROUP;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_PLAYLIST;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_PLAYLIST_RECOMMENDATION;
import static com.vkontakte.miracle.engine.util.AdapterUtil.getVerticalLayoutManager;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToPlaylist;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.graphics.Color;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.WrappedPlaylistViewHolderHorizontal;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogBannerViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogLinkViewHolderHorizontal;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogSuggestionViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.WrappedGroupViewHolderHorizontal;
import com.vkontakte.miracle.dialog.audio.PlaylistDialog;
import com.vkontakte.miracle.dialog.audio.PlaylistDialogActionListener;
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleNestedLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.catalog.RecommendedPlaylist;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;
import com.vkontakte.miracle.service.player.PlayerServiceController;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class CatalogSliderAdapter extends MiracleNestedLoadableAdapter {

    private CatalogBlock catalogBlock;

    public CatalogSliderAdapter(CatalogBlock catalogBlock){
        this.catalogBlock = catalogBlock;
    }

    public void setNewCatalogBlock(CatalogBlock catalogBlock){
        this.catalogBlock = catalogBlock;
    }

    @Override
    public void onLoading() throws Exception {

        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        if(instantLoading()){
            ArrayList<ItemDataHolder> newHolders = catalogBlock.getItems();
            if(holders.isEmpty()){
                holders.addAll(newHolders);
                setAddedCount(newHolders.size());
            } else {
                if(newHolders.isEmpty()){
                    int oldSize = holders.size();
                    holders.clear();
                    setAddedCount(-oldSize);
                    setFinallyLoaded(true);
                } else {
                    DiffUtil.DiffResult diffResult = calculateDifference(holders, newHolders);
                    holders.clear();
                    holders.addAll(newHolders);
                    setDiffResult(diffResult);
                }
            }
            if(catalogBlock.getNextFrom().isEmpty()){
                setFinallyLoaded(true);
            } else {
                setNextFrom(catalogBlock.getNextFrom());
            }
        } else {
            ProfileItem profileItem = StorageUtil.get().currentUser();
            Response<JSONObject> response = Catalog.getBlockItems(catalogBlock.getId(),
                    getNextFrom(), profileItem.getAccessToken()).execute();
            JSONObject jo_response = validateBody(response).getJSONObject("response");
            JSONObject block = jo_response.getJSONObject("block");
            CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);
            ArrayList<ItemDataHolder> itemDataHolders = catalogBlock.findItems(block,catalogExtendedArrays);
            int previous = holders.size();
            holders.addAll(itemDataHolders);
            setAddedCount(holders.size()-previous);
            if(block.has("next_from")){
                setNextFrom(block.getString("next_from"));
            } else {
                setFinallyLoaded(true);
            }
        }
    }

    private DiffUtil.DiffResult calculateDifference(ArrayList<ItemDataHolder> holders,
                                                    ArrayList<ItemDataHolder> newHolders){
        final int oldSize = holders.size();
        final int newSize = newHolders.size();
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
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
                }
                return false;
            }
        });
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = super.getViewHolderFabricMap();
        arrayMap.put(TYPE_WRAPPED_PLAYLIST, new WrappedPlaylistViewHolderHorizontal.Fabric());
        arrayMap.put(TYPE_WRAPPED_GROUP, new WrappedGroupViewHolderHorizontal.Fabric());
        arrayMap.put(TYPE_WRAPPED_PLAYLIST_RECOMMENDATION, new RecommendedPlaylistViewHolderFabric());
        arrayMap.put(TYPE_CATALOG_BANNER, new CatalogBannerViewHolder.FabricHorizontal());
        arrayMap.put(TYPE_CATALOG_LINK, new CatalogLinkViewHolderHorizontal.Fabric());
        arrayMap.put(TYPE_CATALOG_SUGGESTION, new CatalogSuggestionViewHolder.FabricHorizontal());
        return arrayMap;
    }

    public class RecommendedPlaylistViewHolder extends MiracleViewHolder {

        private final RecyclerView recyclerView;
        private DataItemWrap<?,?> itemWrap;
        private RecommendedPlaylist recommendedPlaylist;
        private final TextView title;
        private final TextView subtitle;
        private final TextView percentage;
        private final TextView percentageTitle;

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
            percentage = itemView.findViewById(R.id.percentage);
            percentageTitle = itemView.findViewById(R.id.percentageTitle);
            itemView.setOnClickListener(view ->
                    goToPlaylist(recommendedPlaylist, getContext()));
            itemView.setOnLongClickListener(view -> {
                final PlaylistItem playlistItem = recommendedPlaylist;
                PlaylistDialog playlistDialog = new PlaylistDialog(getContext(), playlistItem);
                playlistDialog.setDialogActionListener(new PlaylistDialogActionListener() {
                    @Override
                    public void follow() {
                        resolveFollow(itemWrap);
                    }

                    @Override
                    public void delete() {
                        resolveDelete(itemWrap);
                    }

                    @Override
                    public void playNext() {
                        PlayerServiceController.get().loadAndSetPlayNext(playlistItem);
                    }

                    @Override
                    public void goToArtist() {
                        NavigationUtil.goToArtist(playlistItem, getContext());
                    }

                    @Override
                    public void goToOwner() {
                        NavigationUtil.goToOwner(playlistItem, getContext());
                    }
                });
                playlistDialog.show(view.getContext());
                itemView.getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            });
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {
            itemWrap = (DataItemWrap<?,?>) itemDataHolder;
            Object item = itemWrap.getItem();
            if(item instanceof RecommendedPlaylist){
                recommendedPlaylist = (RecommendedPlaylist) item;
            }

            title.setText(recommendedPlaylist.getTitle());
            subtitle.setText(recommendedPlaylist.getSubtitle());

            percentage.setText(recommendedPlaylist.getPercentage());
            percentageTitle.setText(recommendedPlaylist.getPercentageTitle());

            title.setTextColor(Color.parseColor(recommendedPlaylist.getColor()));

            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if(adapter instanceof CatalogTripleStackedAdapter){
                CatalogTripleStackedAdapter catalogTripleStackedAdapter =
                        ((CatalogTripleStackedAdapter)recyclerView.getAdapter());
                catalogTripleStackedAdapter.iniFromFragment(getMiracleFragment());
                catalogTripleStackedAdapter.setNewItemDataHolders(recommendedPlaylist.getAudioItems());
                catalogTripleStackedAdapter.load();
            } else {
                if(adapter instanceof MiracleAdapter){
                    MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                    miracleAdapter.setRecyclerView(null);
                }
                CatalogTripleStackedAdapter catalogTripleStackedAdapter =
                        new CatalogTripleStackedAdapter(recommendedPlaylist.getAudioItems());
                catalogTripleStackedAdapter.iniFromFragment(getMiracleFragment());
                catalogTripleStackedAdapter.setRecyclerView(recyclerView);
                catalogTripleStackedAdapter.ini();
                recyclerView.setHasFixedSize(false);
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
