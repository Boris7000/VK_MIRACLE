package com.vkontakte.miracle.engine.view.bottomNavigation;

public interface OnTabSelectListener {
    void onSelect(int pos, int previous);
    void onReselect(int pos);
    void onLongClick(int pos);
}
