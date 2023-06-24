package com.miracle.engine.fragment.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.miracle.engine.R;
import com.miracle.engine.fragment.MiracleFragmentController;

public abstract class ListFragmentController<T extends Fragment & IListFragment> extends MiracleFragmentController<T> {

    private NestedScrollView scrollView;

    public ListFragmentController(T fragment) {
        super(fragment);
    }

    @Override
    public void onCreateView(@NonNull LayoutInflater inflater, @NonNull View rootView, Bundle savedInstanceState){
        scrollView = rootView.findViewById(R.id.scrollView);
        LinearLayout linearLayout = scrollView.findViewById(R.id.linearLayout);
        getFragment().inflateContent(inflater, linearLayout);
    }

    @CallSuper
    public boolean notTop(){
        if (scrollView != null) {
            return scrollView.canScrollVertically(-1);
        }
        return false;
    }

    @CallSuper
    public void scrollToTop() {
        if (scrollView != null) {
            if(scrollView.canScrollVertically(-1)) {
                scrollView.scrollTo(0, 0);
            }
        }
    }

    /////////////////////////////////////////////////////////////////

    public final NestedScrollView getScrollView() {
        return scrollView;
    }

}
