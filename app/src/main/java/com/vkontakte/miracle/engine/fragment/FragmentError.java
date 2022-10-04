package com.vkontakte.miracle.engine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.nested.NestedFragment;

public class FragmentError extends NestedFragment {

    private String error;

    public void setError(String error) {
        this.error = error;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        TextView errorText = rootView.findViewById(R.id.errorText);

        errorText.setText(error);

        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_error, container, false);
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        error = savedInstanceState.getString("error");
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
        public MiracleFragment createFragment() {
            FragmentError fragmentError = new FragmentError();
            fragmentError.setError(error);
            return fragmentError;
        }
    }
}
