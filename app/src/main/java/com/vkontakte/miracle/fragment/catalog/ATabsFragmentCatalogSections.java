package com.vkontakte.miracle.fragment.catalog;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.miracle.engine.fragment.FragmentError;
import com.miracle.engine.fragment.tabs.nested.NestedMiracleFragmentFabric;
import com.miracle.engine.fragment.searchable.templates.SearchableBaseTabsFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.catalog.CatalogSection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public abstract class ATabsFragmentCatalogSections extends SearchableBaseTabsFragment {

    private String exString="";
    private int defaultSectionIndex = 0;

    @Override
    public ArrayList<NestedMiracleFragmentFabric> loadTabs() {
        try {

            ArrayList<NestedMiracleFragmentFabric> fabrics = new ArrayList<>();

            Response<JSONObject> response = requestCall().execute();

            JSONObject jsonObject = validateBody(response).getJSONObject("response");

            JSONObject jo_catalog = jsonObject.getJSONObject("catalog");

            String defaultSection = "";

            if(jo_catalog.has("default_section")){
                defaultSection = jo_catalog.getString("default_section");
            }

            JSONArray sections = jo_catalog.getJSONArray("sections");

            for (int i=0; i<sections.length();i++){
                CatalogSection catalogSection = new CatalogSection(sections.getJSONObject(i));
                if(catalogSection.getId().equals(defaultSection)){
                    defaultSectionIndex = i;
                }
                fabrics.add(new NestedFragmentCatalogSection.Fabric(
                        catalogSection.getId(), catalogSection.getTitle()));
            }

            return fabrics;

        }catch (Exception e){
            if(e.getMessage()==null){
                exString = e.toString();
            } else {
                exString = e.getMessage();
            }
            return null;
        }
    }

    @Override
    public int defaultTab() {
        return defaultSectionIndex;
    }

    @Override
    public boolean asyncLoadTabs() {
        return true;
    }

    @Override
    public ArrayList<NestedMiracleFragmentFabric> getErrorTabs() {
        ArrayList<NestedMiracleFragmentFabric> fabrics = new ArrayList<>();
        String title="";
        Context context = getContext();
        if(context!=null){
            title = context.getString(R.string.error);
        }
        fabrics.add(new FragmentError.Fabric(exString, title,-1));
        return fabrics;
    }

    public abstract Call<JSONObject> requestCall();

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        super.readSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("exString");
        if (key!=null) {
            exString = key;
            savedInstanceState.remove("exString");
        }

        int key1 = savedInstanceState.getInt("defaultSectionIndex");
        if (key1!=0) {
            defaultSectionIndex = key1;
            savedInstanceState.remove("defaultSectionIndex");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("exString");
        savedInstanceState.remove("defaultSectionIndex");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(!exString.isEmpty()) {
            outState.putString("exString", exString);
        }
        if(defaultSectionIndex!=0) {
            outState.putInt("defaultSectionIndex", defaultSectionIndex);
        }
        super.onSaveInstanceState(outState);
    }

}
