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
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;

public class AudioViewHolder extends MiracleViewHolder
        implements View.OnClickListener, View.OnLongClickListener{

    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final TextView duration;
    private final ViewStub explicitStub;
    private ImageView explicit;
    int placeholderDrawableId;

    public AudioViewHolder(@NonNull View itemView, int placeholderDrawableId) {
        super(itemView);
        this.placeholderDrawableId = placeholderDrawableId;
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        explicitStub = itemView.findViewById(R.id.explicitStub);
        duration = itemView.findViewById(R.id.duration);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        AudioItem audioItem = (AudioItem) itemDataHolder;

        title.setText(audioItem.getTitle());
        subtitle.setText(audioItem.getArtist());
        duration.setText(audioItem.getDurationString());

        Picasso.get().cancelRequest(imageView);

        if (audioItem.getAlbum() != null) {
            Album album = audioItem.getAlbum();
            if (album.getThumb() != null) {
                Photo thumb = album.getThumb();
                if (thumb.getPhoto135() != null) {
                    Picasso.get().load(thumb.getPhoto135()).fit().into(imageView);
                } else {
                    imageView.setImageResource(placeholderDrawableId);
                }
            } else {
                imageView.setImageResource(placeholderDrawableId);
            }
        } else {
            imageView.setImageResource(placeholderDrawableId);
        }

        if(audioItem.isLicensed()) {
            if(itemView.getAlpha()<1) {
                itemView.setAlpha(1f);
            }
        } else {
            if(itemView.getAlpha()>0.6f) {
                itemView.setAlpha(0.6f);
            }
        }

        if(audioItem.isExplicit()){
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
