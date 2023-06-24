package com.vkontakte.miracle.adapter.audio.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Photo;

public class PlaylistViewHolderHorizontal extends MiracleViewHolder
        implements View.OnClickListener, View.OnLongClickListener{

    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final ViewStub explicitStub;
    private ImageView explicit;

    public PlaylistViewHolderHorizontal(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        explicitStub = itemView.findViewById(R.id.explicitStub);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        PlaylistItem playlistItem = (PlaylistItem) itemDataHolder;
        title.setText(playlistItem.getTitle());
        subtitle.setText(playlistItem.getSubtitle());

        Picasso.get().cancelRequest(imageView);

        if(playlistItem.getPhoto()!=null){
            Photo photo = playlistItem.getPhoto();
            if(photo.getPhoto300()!=null){
                Picasso.get().load(photo.getPhoto300()).fit().into(imageView);
            }
        } else {
            imageView.setImageDrawable(null);
        }

        if(playlistItem.isExplicit()){
            if(explicit==null) {
                if(explicitStub!=null) {
                    explicit = (ImageView) explicitStub.inflate();
                } else {
                    explicit = itemView.findViewById(R.id.explicit);
                }
            }
            if(explicit.getVisibility()!=VISIBLE) {
                explicit.setVisibility(VISIBLE);
            }
        } else {
            if(explicit!=null&&explicit.getVisibility()!=GONE){
                explicit.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onClick(View view){}

    @Override
    public boolean onLongClick(View view){
        return false;
    }
}
