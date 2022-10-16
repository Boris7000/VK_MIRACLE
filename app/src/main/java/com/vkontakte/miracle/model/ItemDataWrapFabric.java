package com.vkontakte.miracle.model;

public interface ItemDataWrapFabric<T,H> {
    DataItemWrap<T,H> create(T item, H holder);
}
