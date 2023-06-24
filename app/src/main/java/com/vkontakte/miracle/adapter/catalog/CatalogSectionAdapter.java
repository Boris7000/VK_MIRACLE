package com.vkontakte.miracle.adapter.catalog;

import static com.miracle.engine.util.AdapterUtil.getHorizontalGridLayoutManager;
import static com.miracle.engine.util.AdapterUtil.getHorizontalLayoutManager;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_ARTIST_BANNER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_BUTTON_CREATE_PLAYLIST;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_BUTTON_OPEN_SECTION;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_BUTTON_PLAY;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_BUTTON_PLAY_SHUFFLED;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_BANNER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_BANNER_SLIDER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_CATEGORIES_LIST;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_HEADER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_HEADER_EXTENDED;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_LINK;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_SEPARATOR;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_SLIDER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_SUGGESTION;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_TRIPLE_STACKED_SLIDER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_HORIZONTAL_BUTTONS;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_CATALOG_USER;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_GROUP;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_PLAYLIST;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_PLAYLIST_RECOMMENDATION;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.adapter.MiracleAdapter;
import com.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.recycler.MiracleViewRecycler;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.WrappedPlaylistViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.ArtistBannerViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogBannerViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.buttons.CatalogButtonCreatePlaylistViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.buttons.CatalogButtonOpenSectionViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.buttons.CatalogButtonPlayShuffledViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.buttons.CatalogButtonPlayViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogCategoryViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogLinkViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogSuggestionViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.HeaderExtendedViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.HeaderViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.SeparatorViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.WrappedCatalogUserViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.WrappedGroupViewHolder;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.view.RecycleListView;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.ExtendedArrays;
import com.vkontakte.miracle.model.catalog.CatalogSection;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Catalog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class CatalogSectionAdapter extends MiracleAsyncLoadAdapter {

    private String sectionId;
    private Call<JSONObject> call;
    private final ArrayMap<String,CatalogBlock> listCatalogBlocksMap = new ArrayMap<>();

    private CatalogSection catalogSection;

    public CatalogSectionAdapter(String sectionId){
        this.sectionId = sectionId;
    }

    public CatalogSectionAdapter(Call<JSONObject> call){
        this.call = call;
    }

    public CatalogSection getCatalogSection() {
        return catalogSection;
    }

    @Override
    public void restoreState() {
        super.restoreState();
        listCatalogBlocksMap.clear();
    }

    @Override
    public void onLoading() throws Exception {

        User user = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        int previous = holders.size();

        JSONObject jo_response;
        JSONObject section;

        if(sectionId==null){
            Response<JSONObject> response = call.execute();
            jo_response = validateBody(response);
            jo_response = jo_response.getJSONObject("response");
            JSONArray sections = jo_response.getJSONObject("catalog").getJSONArray("sections");
            section = sections.getJSONObject(0);
            sectionId = section.getString("id");
        } else {
            Response<JSONObject> response = Catalog.getSection(sectionId, getNextFrom(), user.getAccessToken()).execute();
            jo_response = validateBody(response);
            jo_response = jo_response.getJSONObject("response");
            section = jo_response.getJSONObject("section");
        }

        Log.d("eofkoefef",jo_response.toString());

        catalogSection = new CatalogSection(section);

        JSONArray blocks = section.getJSONArray("blocks");

        ExtendedArrays extendedArrays = new ExtendedArrays(jo_response);

        for(int i=0; i<blocks.length();i++){

            JSONObject jo_catalogBlock = blocks.getJSONObject(i);

            String catalogId = jo_catalogBlock.getString("id");
            CatalogBlock previousCatalogBlock = listCatalogBlocksMap.get(catalogId);
            if(previousCatalogBlock!=null && previousCatalogBlock.getLayout().isList()){
                ArrayList<ItemDataHolder> catalogBlockItemDataHolders =
                        extendedArrays.extractForBlock(previousCatalogBlock, jo_catalogBlock);
                previousCatalogBlock.getItems().addAll(catalogBlockItemDataHolders);
                holders.addAll(catalogBlockItemDataHolders);
            } else {
                CatalogBlock catalogBlock = new CatalogBlock(jo_catalogBlock);
                ArrayList<ItemDataHolder> catalogBlockItemDataHolders =
                        extendedArrays.extractForBlock(catalogBlock, jo_catalogBlock);
                catalogBlock.getItems().addAll(catalogBlockItemDataHolders);
                if(catalogBlock.getLayout().isList()){
                    listCatalogBlocksMap.put(catalogId,catalogBlock);
                    holders.addAll(catalogBlock.getItems());
                } else {
                    holders.add(catalogBlock);
                }
            }
        }

        setAddedCount(holders.size()-previous);

        if(section.has("next_from")){
            setNextFrom(section.getString("next_from"));
        } else {
            setNextFrom("");
            setFinallyLoaded(true);
        }
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = super.getViewHolderFabricMap();
        arrayMap.put(TYPE_CATALOG_SLIDER, new SliderViewHolderFabric());
        arrayMap.put(TYPE_CATALOG_BANNER_SLIDER, new BannerSliderViewHolderFabric());
        arrayMap.put(TYPE_CATALOG_TRIPLE_STACKED_SLIDER, new TripleStackedSliderViewHolderFabric());
        arrayMap.put(TYPE_HORIZONTAL_BUTTONS, new HorizontalButtonsViewHolderFabric());
        arrayMap.put(TYPE_CATALOG_CATEGORIES_LIST, new CategoriesListViewHolderFabric());
        arrayMap.put(TYPE_CATALOG_HEADER, new HeaderViewHolder.Fabric());
        arrayMap.put(TYPE_CATALOG_HEADER_EXTENDED, new HeaderExtendedViewHolder.Fabric());
        arrayMap.put(TYPE_CATALOG_SEPARATOR, new SeparatorViewHolder.Fabric());
        arrayMap.put(TYPE_ARTIST_BANNER, new ArtistBannerViewHolder.Fabric());
        arrayMap.put(TYPE_CATALOG_LINK, new CatalogLinkViewHolder.Fabric());
        arrayMap.put(TYPE_CATALOG_SUGGESTION, new CatalogSuggestionViewHolder.Fabric());
        arrayMap.put(TYPE_CATALOG_BANNER, new CatalogBannerViewHolder.Fabric());

        arrayMap.put(TYPE_WRAPPED_AUDIO, new WrappedAudioViewHolder.Fabric());
        arrayMap.put(TYPE_WRAPPED_PLAYLIST, new WrappedPlaylistViewHolder.Fabric());
        arrayMap.put(TYPE_WRAPPED_PLAYLIST_RECOMMENDATION, new WrappedPlaylistViewHolder.Fabric());
        arrayMap.put(TYPE_WRAPPED_GROUP, new WrappedGroupViewHolder.Fabric());
        arrayMap.put(TYPE_WRAPPED_CATALOG_USER, new WrappedCatalogUserViewHolder.Fabric());
        return arrayMap;
    }

    public class SliderViewHolder extends MiracleViewHolder {

        private final RecyclerView recyclerView;
        private CatalogBlock catalogBlock;

        public SliderViewHolder(@NonNull View itemView){
            super(itemView);
            recyclerView = ((RecyclerView)itemView);
            recyclerView.setLayoutManager(getHorizontalLayoutManager(itemView.getContext()));
            RecyclerView.RecycledViewPool recycledViewPool =
                    getNestedRecycledViewPool(TYPE_CATALOG_SLIDER);
            recycledViewPool.setMaxRecycledViews(TYPE_WRAPPED_AUDIO, 15);
            recycledViewPool.setMaxRecycledViews(TYPE_WRAPPED_PLAYLIST, 10);
            recycledViewPool.setMaxRecycledViews(TYPE_WRAPPED_GROUP, 7);
            recycledViewPool.setMaxRecycledViews(TYPE_WRAPPED_PLAYLIST_RECOMMENDATION, 7);
            recyclerView.setRecycledViewPool(recycledViewPool);
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {

            CatalogBlock newCatalogBlock = (CatalogBlock) itemDataHolder;
            boolean changedType = true;
            if(catalogBlock!=null){
                changedType = !catalogBlock.getDataType().equals(newCatalogBlock.getDataType());
            }
            catalogBlock = newCatalogBlock;

            if(changedType){
                if(recyclerView.hasFixedSize()) {
                    recyclerView.setHasFixedSize(false);
                }
            }

            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            CatalogSliderAdapter catalogSliderAdapter;
            if(adapter instanceof CatalogSliderAdapter){
                catalogSliderAdapter = ((CatalogSliderAdapter)recyclerView.getAdapter());
                catalogSliderAdapter.iniFromFragment(getFragment());
                catalogSliderAdapter.setNewCatalogBlock(catalogBlock);
            } else {
                if(adapter instanceof MiracleAdapter){
                    MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                    miracleAdapter.setRecyclerView(null);
                }
                catalogSliderAdapter = new CatalogSliderAdapter(catalogBlock);
                catalogSliderAdapter.iniFromFragment(getFragment());
                catalogSliderAdapter.setRecyclerView(recyclerView);
                catalogSliderAdapter.ini();
                recyclerView.setAdapter(catalogSliderAdapter);
            }
            catalogSliderAdapter.instantLoad();

            if(changedType){
                if(!recyclerView.hasFixedSize()) {
                    recyclerView.setHasFixedSize(true);
                }
            }
        }
    }
    public class SliderViewHolderFabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new SliderViewHolder(inflater.inflate(R.layout.catalog_slider,
                    viewGroup, false));
        }
    }

    public class BannerSliderViewHolder extends MiracleViewHolder {

        private final RecyclerView recyclerView;
        private CatalogBlock catalogBlock;

        public BannerSliderViewHolder(@NonNull View itemView){
            super(itemView);
            recyclerView = ((RecyclerView)itemView);
            recyclerView.setLayoutManager(getHorizontalLayoutManager(itemView.getContext()));
            new PagerSnapHelper().attachToRecyclerView(recyclerView);
            RecyclerView.RecycledViewPool recycledViewPool =
                    getNestedRecycledViewPool(TYPE_CATALOG_BANNER_SLIDER);
            recycledViewPool.setMaxRecycledViews(TYPE_CATALOG_BANNER, 3);
            recyclerView.setRecycledViewPool(recycledViewPool);
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {

            CatalogBlock newCatalogBlock = (CatalogBlock) itemDataHolder;
            boolean changedType = true;
            if(catalogBlock!=null){
                changedType = !catalogBlock.getDataType().equals(newCatalogBlock.getDataType());
            }
            catalogBlock = newCatalogBlock;

            if(changedType){
                if(recyclerView.hasFixedSize()) {
                    recyclerView.setHasFixedSize(false);
                }
            }

            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            CatalogBannerSliderAdapter catalogBannerSliderAdapter;
            if(adapter instanceof CatalogBannerSliderAdapter){
                catalogBannerSliderAdapter = ((CatalogBannerSliderAdapter)recyclerView.getAdapter());
                catalogBannerSliderAdapter.iniFromFragment(getFragment());
                catalogBannerSliderAdapter.setNewCatalogBlock(catalogBlock);
            } else {
                if(adapter instanceof MiracleAdapter){
                    MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                    miracleAdapter.setRecyclerView(null);
                }
                catalogBannerSliderAdapter = new CatalogBannerSliderAdapter(catalogBlock);
                catalogBannerSliderAdapter.iniFromFragment(getFragment());
                catalogBannerSliderAdapter.setRecyclerView(recyclerView);
                catalogBannerSliderAdapter.ini();
                recyclerView.setAdapter(catalogBannerSliderAdapter);
            }
            catalogBannerSliderAdapter.load();
            catalogBannerSliderAdapter.setFinallyLoaded(true);

            if(changedType){
                if(!recyclerView.hasFixedSize()) {
                    recyclerView.setHasFixedSize(true);
                }
            }
        }
    }
    public class BannerSliderViewHolderFabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new BannerSliderViewHolder(inflater.inflate(R.layout.catalog_slider,
                    viewGroup, false));
        }
    }

    public class TripleStackedSliderViewHolder extends MiracleViewHolder {

        private final RecyclerView recyclerView;
        private CatalogBlock catalogBlock;

        public TripleStackedSliderViewHolder(@NonNull View itemView){
            super(itemView);
            recyclerView = ((RecyclerView)itemView);
            recyclerView.setLayoutManager(getHorizontalGridLayoutManager(itemView.getContext(), 3));
            RecyclerView.RecycledViewPool recycledViewPool =
                    getNestedRecycledViewPool(TYPE_CATALOG_SLIDER);
            recycledViewPool.setMaxRecycledViews(TYPE_WRAPPED_AUDIO, 15);
            recycledViewPool.setMaxRecycledViews(TYPE_CATALOG_LINK, 15);
            recyclerView.setRecycledViewPool(recycledViewPool);
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {

            CatalogBlock newCatalogBlock = (CatalogBlock) itemDataHolder;
            boolean changedType = true;
            if(catalogBlock!=null){
                changedType = !catalogBlock.getDataType().equals(newCatalogBlock.getDataType());
            }
            catalogBlock = newCatalogBlock;

            if(changedType){
                if(recyclerView.hasFixedSize()) {
                    recyclerView.setHasFixedSize(false);
                }
            }

            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            CatalogTripleStackedSliderAdapter catalogSliderAdapter;
            if(adapter instanceof CatalogTripleStackedSliderAdapter){
                catalogSliderAdapter = ((CatalogTripleStackedSliderAdapter)recyclerView.getAdapter());
                catalogSliderAdapter.iniFromFragment(getFragment());
                catalogSliderAdapter.setNewCatalogBlock(catalogBlock);
            } else {
                if(adapter instanceof MiracleAdapter){
                    MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                    miracleAdapter.setRecyclerView(null);
                }
                catalogSliderAdapter = new CatalogTripleStackedSliderAdapter(catalogBlock);
                catalogSliderAdapter.iniFromFragment(getFragment());
                catalogSliderAdapter.setRecyclerView(recyclerView);
                catalogSliderAdapter.ini();
                recyclerView.setAdapter(catalogSliderAdapter);
            }
            catalogSliderAdapter.load();
            catalogSliderAdapter.setFinallyLoaded(true);

            if(changedType){
                if(!recyclerView.hasFixedSize()) {
                    recyclerView.setHasFixedSize(true);
                }
            }
        }
    }
    public class TripleStackedSliderViewHolderFabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new TripleStackedSliderViewHolder(inflater.inflate(R.layout.catalog_slider,
                    viewGroup, false));
        }
    }

    public class HorizontalButtonsViewHolder extends MiracleViewHolder {

        private final RecycleListView recycleListView;

        public HorizontalButtonsViewHolder(@NonNull View itemView) {
            super(itemView);
            recycleListView = ((RecycleListView)itemView);
            ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap = recycleListView.getViewHolderFabricMap();
            viewHolderFabricMap.put(TYPE_BUTTON_OPEN_SECTION, new CatalogButtonOpenSectionViewHolder.Fabric());
            viewHolderFabricMap.put(TYPE_BUTTON_PLAY_SHUFFLED, new CatalogButtonPlayShuffledViewHolder.Fabric());
            viewHolderFabricMap.put(TYPE_BUTTON_PLAY, new CatalogButtonPlayViewHolder.Fabric());
            viewHolderFabricMap.put(TYPE_BUTTON_CREATE_PLAYLIST, new CatalogButtonCreatePlaylistViewHolder.Fabric());
            MiracleViewRecycler miracleViewRecycler = getMiracleViewRecycler(TYPE_HORIZONTAL_BUTTONS);
            miracleViewRecycler.setMaxRecycledViews(TYPE_BUTTON_OPEN_SECTION, 2);
            miracleViewRecycler.setMaxRecycledViews(TYPE_BUTTON_PLAY_SHUFFLED, 1);
            miracleViewRecycler.setMaxRecycledViews(TYPE_BUTTON_PLAY, 1);
            miracleViewRecycler.setMaxRecycledViews(TYPE_BUTTON_CREATE_PLAYLIST, 1);
            recycleListView.setViewRecycler(miracleViewRecycler);
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {
            CatalogBlock catalogBlock = (CatalogBlock) itemDataHolder;
            ArrayList<ItemDataHolder> itemDataHolders = catalogBlock.getActions();
            recycleListView.setItems(itemDataHolders);
        }
    }
    public class HorizontalButtonsViewHolderFabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new HorizontalButtonsViewHolder(inflater.inflate(R.layout.view_catalog_horizontal_buttons,
                    viewGroup, false));
        }
    }

    public class CategoriesListViewHolder extends MiracleViewHolder {

        private final RecycleListView recycleListView;

        public CategoriesListViewHolder(@NonNull View itemView) {
            super(itemView);
            recycleListView = ((RecycleListView)itemView);
            recycleListView.getViewHolderFabricMap().put(TYPE_CATALOG_LINK,
                    new CatalogCategoryViewHolder.Fabric());
            MiracleViewRecycler miracleViewRecycler = getMiracleViewRecycler(TYPE_CATALOG_CATEGORIES_LIST);
            miracleViewRecycler.setMaxRecycledViews(TYPE_CATALOG_LINK, 6);
            recycleListView.setViewRecycler(miracleViewRecycler);
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {
            CatalogBlock catalogBlock = (CatalogBlock) itemDataHolder;
            ArrayList<ItemDataHolder> itemDataHolders = catalogBlock.getItems();
            recycleListView.setItems(itemDataHolders, false);
        }
    }
    public class CategoriesListViewHolderFabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CategoriesListViewHolder(inflater.inflate(R.layout.catalog_categories_list,
                    viewGroup, false));
        }
    }

}
