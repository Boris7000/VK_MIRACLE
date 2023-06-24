package com.vkontakte.miracle.fragment.catalog;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.search.templates.SearchBaseRecyclerFragment;
import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Catalog;

public class FragmentAudioSearch extends SearchBaseRecyclerFragment {

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        User user = StorageUtil.get().currentUser();
        return new CatalogSectionAdapter(Catalog.getAudioSearch(getSearchFragmentController().getSearchQuery(),
                user.getAccessToken()));
    }

}
