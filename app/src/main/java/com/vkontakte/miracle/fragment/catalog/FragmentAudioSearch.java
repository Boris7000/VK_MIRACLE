package com.vkontakte.miracle.fragment.catalog;

import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.fragment.search.SearchSideRecyclerFragment;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

public class FragmentAudioSearch extends SearchSideRecyclerFragment {

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(getSearchFragmentController().getContextQuery().isEmpty()){
            return new CatalogSectionAdapter(Catalog.getAudioSearch(getSearchFragmentController().getQuery(), profileItem.getAccessToken()));
        } else {
            return new CatalogSectionAdapter(Catalog.getAudioContextSearch(getSearchFragmentController().getContextQuery(), profileItem.getAccessToken()));
        }

    }

    @Override
    public String requestTitleText() {
        return getMiracleActivity().getString(R.string.search);
    }


}
