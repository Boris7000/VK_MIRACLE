package com.miracle.engine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miracle.engine.R;
import com.miracle.engine.fragment.tabs.nested.NestedMiracleFragmentFabric;
import com.miracle.engine.fragment.tabs.nested.templates.NestedFragment;

public class FragmentError extends NestedFragment {

    private String error;

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);
        TextView errorText = rootView.findViewById(R.id.errorText);
        errorText.setText(error);
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
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        savedInstanceState.remove("error");
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
        public Fabric(String error, String title, int icon) {
            super(title, icon);
            this.error = error;
        }

        @NonNull
        @Override
        public Fragment createFragment() {
            FragmentError fragmentError = new FragmentError();
            fragmentError.setError(error);
            return fragmentError;
        }
    }
}
