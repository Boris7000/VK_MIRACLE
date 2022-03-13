package com.vkontakte.miracle.adapter.catalog;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import androidx.collection.ArrayMap;

import com.vkontakte.miracle.engine.adapter.MiracleLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.catalog.CatalogSection;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class CatalogSectionAdapter extends MiracleLoadableAdapter {

    private String sectionId;
    private Call<JSONObject> call;
    private final ArrayMap<String,CatalogBlock> listCatalogBlocksMap = new ArrayMap<>();

    private CatalogSection catalogSection;
    private OnSectionLoadedListener onSectionLoadedListener;

    public CatalogSectionAdapter(String sectionId){
        this.sectionId = sectionId;
    }

    public CatalogSectionAdapter(Call<JSONObject> call){
        this.call = call;
    }

    @Override
    public void onLoading() throws Exception {

        ProfileItem profileItem = getUserItem();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        int previous = holders.size();

        JSONObject jo_response;
        JSONObject section;

        if(sectionId==null){
            Response<JSONObject> response = call.execute();
            jo_response = validateBody(response).getJSONObject("response");
            JSONArray sections = jo_response.getJSONObject("catalog").getJSONArray("sections");
            section = sections.getJSONObject(0);
            sectionId = section.getString("id");
        } else {
            Response<JSONObject> response = Catalog.getSection(sectionId, getNextFrom(), profileItem.getAccessToken()).execute();
            jo_response = validateBody(response).getJSONObject("response");
            section = jo_response.getJSONObject("section");
        }

        catalogSection = new CatalogSection(section);

        JSONArray blocks = section.getJSONArray("blocks");

        CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);

        for(int i=0; i<blocks.length();i++){

            JSONObject jo_catalogBlock = blocks.getJSONObject(i);

            String catalogId = jo_catalogBlock.getString("id");
            CatalogBlock previousCatalogBlock = listCatalogBlocksMap.get(catalogId);
            if(previousCatalogBlock!=null){
                if(previousCatalogBlock.getLayout().getName().equals("list")){
                    ArrayList<ItemDataHolder> catalogBlockItemDataHolders =
                            previousCatalogBlock.findItems(jo_catalogBlock, catalogExtendedArrays);
                    previousCatalogBlock.getItems().addAll(catalogBlockItemDataHolders);
                    holders.addAll(catalogBlockItemDataHolders);
                } else {
                    CatalogBlock catalogBlock = new CatalogBlock(jo_catalogBlock, catalogExtendedArrays);
                    if(catalogBlock.getLayout().getName().equals("list")){
                        listCatalogBlocksMap.put(catalogId,catalogBlock);
                        holders.addAll(catalogBlock.getItems());
                    } else {
                        holders.add(catalogBlock);
                    }
                }
            } else {
                CatalogBlock catalogBlock = new CatalogBlock(jo_catalogBlock, catalogExtendedArrays);
                if(catalogBlock.getLayout().getName().equals("list")){
                    listCatalogBlocksMap.put(catalogId,catalogBlock);
                    holders.addAll(catalogBlock.getItems());
                } else {
                    holders.add(catalogBlock);
                }
            }
        }

        setAddedCount(holders.size()-previous);

        if(section.has("next_from")){
            setNextFrom(section.getString("next_from"));
        } else {
            setNextFrom("");
            setFinallyLoaded(true);
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        if(!hasData()){
            if(onSectionLoadedListener!=null){
                onSectionLoadedListener.onSectionLoaded(catalogSection);
            }
        }

    }

    public void setOnSectionLoadedListener(OnSectionLoadedListener onSectionLoadedListener) {
        this.onSectionLoadedListener = onSectionLoadedListener;
    }
}
