package com.vkontakte.miracle.adapter.catalog;

import static com.vkontakte.miracle.engine.adapter.MiracleViewRecycler.resolveMultiTypeItems;
import static com.vkontakte.miracle.engine.adapter.MiracleViewRecycler.resolveSingleTypeItems;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ARTIST_BANNER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_BUTTON_CREATE_PLAYLIST;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_BUTTON_OPEN_SECTION;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_BUTTON_PLAY;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_BUTTON_PLAY_SHUFFLED;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_BANNER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_CATEGORIES_LIST;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_HEADER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_HEADER_EXTENDED;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_LINK;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_SEPARATOR;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_SLIDER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_SUGGESTION;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_TRIPLE_STACKED_SLIDER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_HORIZONTAL_BUTTONS;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_TRIPLE_STACKED;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_CATALOG_USER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_GROUP;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_PLAYLIST;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_PLAYLIST_RECOMMENDATION;
import static com.vkontakte.miracle.engine.util.AdapterUtil.getHorizontalLayoutManager;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.WrappedPlaylistViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.ArtistBannerViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogBannerViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogButtonCreatePlaylistViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogButtonOpenSectionViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogButtonPlayShuffledViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogButtonPlayViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogCategoryViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogLinkViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogSuggestionViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.HeaderExtendedViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.HeaderViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.SeparatorViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.WrappedCatalogUserViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.WrappedGroupViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleViewRecycler;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.catalog.CatalogSection;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

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

        ProfileItem profileItem = StorageUtil.get().currentUser();
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
            Response<JSONObject> response = Catalog.getSection(sectionId, getNextFrom(), profileItem.getAccessToken()).execute();
            jo_response = validateBody(response);
            jo_response = jo_response.getJSONObject("response");
            section = jo_response.getJSONObject("section");
        }

        Log.d("suwdwokdowkdchushcdus",jo_response.toString());

        catalogSection = new CatalogSection(section);

        JSONArray blocks = section.getJSONArray("blocks");

        CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);

        for(int i=0; i<blocks.length();i++){

            JSONObject jo_catalogBlock = blocks.getJSONObject(i);

            Log.d("suwdwokdowkdchushcdus",jo_catalogBlock.toString());

            String catalogId = jo_catalogBlock.getString("id");
            CatalogBlock previousCatalogBlock = listCatalogBlocksMap.get(catalogId);
            if(previousCatalogBlock!=null){
                if(previousCatalogBlock.getLayout().isList()){
                    ArrayList<ItemDataHolder> catalogBlockItemDataHolders =
                            previousCatalogBlock.findItems(jo_catalogBlock, catalogExtendedArrays);
                    previousCatalogBlock.getItems().addAll(catalogBlockItemDataHolders);
                    holders.addAll(catalogBlockItemDataHolders);
                } else {
                    CatalogBlock catalogBlock = new CatalogBlock(jo_catalogBlock, catalogExtendedArrays);
                    if(catalogBlock.getLayout().isList()){
                        listCatalogBlocksMap.put(catalogId,catalogBlock);
                        holders.addAll(catalogBlock.getItems());
                    } else {
                        holders.add(catalogBlock);
                    }
                }
            } else {
                CatalogBlock catalogBlock = new CatalogBlock(jo_catalogBlock, catalogExtendedArrays);
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
        arrayMap.put(TYPE_CATALOG_TRIPLE_STACKED_SLIDER, new TripleStackedSliderViewHolderFabric());
        arrayMap.put(TYPE_CATALOG_SLIDER, new SliderViewHolderFabric());
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

    public class TripleStackedSliderViewHolder extends MiracleViewHolder {

        private final RecyclerView recyclerView;
        private CatalogBlock catalogBlock;

        public TripleStackedSliderViewHolder(@NonNull View itemView){
            super(itemView);
            recyclerView = ((RecyclerView)itemView);
            recyclerView.setLayoutManager(getHorizontalLayoutManager(itemView.getContext()));
            RecyclerView.RecycledViewPool recycledViewPool =
                    getNestedRecycledViewPool(TYPE_CATALOG_TRIPLE_STACKED_SLIDER);
            recycledViewPool.setMaxRecycledViews(TYPE_TRIPLE_STACKED, 7);
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
                catalogSliderAdapter =
                        ((CatalogTripleStackedSliderAdapter)recyclerView.getAdapter());
                catalogSliderAdapter.setNewCatalogBlock(catalogBlock);
            } else {
                if(adapter instanceof MiracleAdapter){
                    MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                    miracleAdapter.setRecyclerView(null);
                }
                catalogSliderAdapter = new CatalogTripleStackedSliderAdapter(catalogBlock);
                catalogSliderAdapter.setRecyclerView(recyclerView);
                catalogSliderAdapter.ini();
                recyclerView.setAdapter(catalogSliderAdapter);
            }
            catalogSliderAdapter.iniFromFragment(getMiracleFragment());
            catalogSliderAdapter.load();

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

    public class SliderViewHolder extends MiracleViewHolder {

        private final RecyclerView recyclerView;
        private CatalogBlock catalogBlock;

        public SliderViewHolder(@NonNull View itemView){
            super(itemView);
            recyclerView = ((RecyclerView)itemView);
            recyclerView.setLayoutManager(getHorizontalLayoutManager(itemView.getContext()));
            RecyclerView.RecycledViewPool recycledViewPool =
                    getNestedRecycledViewPool(TYPE_CATALOG_SLIDER);
            recycledViewPool.setMaxRecycledViews(TYPE_WRAPPED_PLAYLIST, 10);
            recycledViewPool.setMaxRecycledViews(TYPE_WRAPPED_GROUP, 7);
            recycledViewPool.setMaxRecycledViews(TYPE_WRAPPED_PLAYLIST_RECOMMENDATION, 7);
            recyclerView.setRecycledViewPool(recycledViewPool);
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {
            super.bind(itemDataHolder);
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
                catalogSliderAdapter.iniFromFragment(getMiracleFragment());
                catalogSliderAdapter.setNewCatalogBlock(catalogBlock);
            } else {
                if(adapter instanceof MiracleAdapter){
                    MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                    miracleAdapter.setRecyclerView(null);
                }
                catalogSliderAdapter = new CatalogSliderAdapter(catalogBlock);
                catalogSliderAdapter.iniFromFragment(getMiracleFragment());
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

    public class HorizontalButtonsViewHolder extends MiracleViewHolder {

        private final ArrayList<RecyclerView.ViewHolder> cache = new ArrayList<>();
        private final ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap = new ArrayMap<>();

        private ArrayList<ItemDataHolder> actions = new ArrayList<>();

        public HorizontalButtonsViewHolder(@NonNull View itemView) {
            super(itemView);
            viewHolderFabricMap.put(TYPE_BUTTON_OPEN_SECTION, new CatalogButtonOpenSectionViewHolder.Fabric());
            viewHolderFabricMap.put(TYPE_BUTTON_PLAY_SHUFFLED, new CatalogButtonPlayShuffledViewHolder.Fabric());
            viewHolderFabricMap.put(TYPE_BUTTON_PLAY, new CatalogButtonPlayViewHolder.Fabric());
            viewHolderFabricMap.put(TYPE_BUTTON_CREATE_PLAYLIST, new CatalogButtonCreatePlaylistViewHolder.Fabric());

        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {

            super.bind(itemDataHolder);
            CatalogBlock catalogBlock = (CatalogBlock) itemDataHolder;
            ArrayList<ItemDataHolder> actions = catalogBlock.getActions();

            MiracleViewRecycler miracleViewRecycler =
                    getMiracleAdapter().getMiracleViewRecycler(itemDataHolder.getViewHolderType());

            resolveMultiTypeItems((ViewGroup) itemView, actions, this.actions, cache,
                    miracleViewRecycler, viewHolderFabricMap, getInflater());

            for(int i=0; i<actions.size(); i++){
                MiracleViewHolder miracleViewHolder = (MiracleViewHolder) cache.get(i);
                miracleViewHolder.bind(actions.get(i));
            }

            this.actions = actions;

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

        private final ArrayList<RecyclerView.ViewHolder> cache = new ArrayList<>();
        private final ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap = new ArrayMap<>();

        private ArrayList<ItemDataHolder> itemDataHolders = new ArrayList<>();

        public CategoriesListViewHolder(@NonNull View itemView) {
            super(itemView);
            viewHolderFabricMap.put(TYPE_CATALOG_LINK, new CatalogCategoryViewHolder.Fabric());

        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {

            super.bind(itemDataHolder);
            CatalogBlock catalogBlock = (CatalogBlock) itemDataHolder;
            ArrayList<ItemDataHolder> itemDataHolders = catalogBlock.getItems();

            MiracleViewRecycler miracleViewRecycler =
                    getMiracleAdapter().getMiracleViewRecycler(itemDataHolder.getViewHolderType());

            resolveSingleTypeItems((ViewGroup) itemView, itemDataHolders, this.itemDataHolders, cache,
                    miracleViewRecycler, viewHolderFabricMap, getInflater());

            for(int i=0; i<itemDataHolders.size(); i++){
                MiracleViewHolder miracleViewHolder = (MiracleViewHolder) cache.get(i);
                miracleViewHolder.bind(itemDataHolders.get(i));
            }

            this.itemDataHolders = itemDataHolders;

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
