package com.vkontakte.miracle.engine.fragment.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;

public class FragmentError extends NestedMiracleFragment {

    private String error;

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_error, container, false);

        setTabsMiracleFragment((TabsMiracleFragment) getParentFragment());

        TextView errorText = rootView.findViewById(R.id.errorText);

        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            error = savedInstanceState.getString("error");
        }

        errorText.setText(error);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(error !=null){
            outState.putString("error", error);
        }
        super.onSaveInstanceState(outState);
    }

    public static class Fabric extends NestedMiracleFragmentFabric {
        private final String error;
        public Fabric(String error,String title, int icon) {
            super(title, icon);
            this.error = error;
        }

        @NonNull
        @Override
        public NestedMiracleFragment createFragment() {
            FragmentError fragmentError = new FragmentError();
            fragmentError.setError(error);
            return fragmentError;
        }
    }
}
