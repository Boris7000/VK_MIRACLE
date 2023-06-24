package com.vkontakte.miracle.model.catalog.wraps;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_CATALOG_USER;

import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.ItemDataWrapFabric;
import com.vkontakte.miracle.model.catalog.CatalogUser;

public class CatalogUserWF implements ItemDataWrapFabric<CatalogUser, CatalogUserWC> {
    @Override
    public DataItemWrap<CatalogUser, CatalogUserWC> create(CatalogUser item, CatalogUserWC holder) {
        return new DataItemWrap<CatalogUser, CatalogUserWC>(item, holder) {
            @Override
            public int getViewHolderType() {
                return TYPE_WRAPPED_CATALOG_USER;
            }
        };
    }
}
