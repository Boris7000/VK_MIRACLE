package com.vkontakte.miracle.adapter.wall.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.miracle.engine.util.ImageUtil.fastBlur;
import static com.vkontakte.miracle.engine.util.ImageUtil.getOptimalSize;
import static com.vkontakte.miracle.engine.view.PicassoDrawableCopy.setBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.async.AsyncExecutor;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.MainApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.picasso.ATarget;
import com.vkontakte.miracle.engine.util.DeviceUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.util.TimeUtil;
import com.vkontakte.miracle.model.catalog.fields.Image;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.model.users.fields.Career;
import com.vkontakte.miracle.model.users.fields.City;
import com.vkontakte.miracle.model.users.fields.University;
import com.vkontakte.miracle.model.users.fields.LastSeen;
import com.vkontakte.miracle.model.wall.fields.Cover;

public class ProfileLargeViewHolder extends MiracleViewHolder {

    private final ImageView image;
    private final ImageView coverImage;
    private final TextView title;
    private final TextView subtitle;
    private final TextView subtitle2;
    private final ViewStub onlineStatusStub;
    private ImageView onlineStatus;
    private final ViewStub verifiedStub;
    private ImageView verified;
    private final ViewStub profileButtonsStub;
    private LinearLayout profileButtonsHolder;
    private final ViewStub userButtonsStub;
    private LinearLayout userButtonsHolder;
    private final User user;
    private final ViewStub placeStub;
    private LinearLayout place;
    private TextView placeTitle;
    private final ViewStub workStub;
    private LinearLayout work;
    private TextView workTitle;
    private final ViewStub universityStub;
    private LinearLayout university;
    private TextView universityTitle;

    private final Target target = new ATarget() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            new AsyncExecutor<Boolean>() {
                Bitmap blurBitmap;
                @Override
                public Boolean inBackground() {
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 255, 255, false);
                    blurBitmap = fastBlur(scaled, 1, 30);
                    return true;
                }
                @Override
                public void onExecute(Boolean object) {
                    MainApp mainApp = MainApp.getInstance();
                    setBitmap(coverImage, mainApp, blurBitmap);
                }
            }.start();
        }
    };

    public ProfileLargeViewHolder(@NonNull View itemView) {
        super(itemView);
        user = StorageUtil.get().currentUser();
        image = itemView.findViewById(R.id.photo);
        coverImage = itemView.findViewById(R.id.cover);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        subtitle2 = itemView.findViewById(R.id.subtitle2);
        onlineStatusStub = itemView.findViewById(R.id.onlineStatusStub);
        verifiedStub = itemView.findViewById(R.id.verifiedStub);
        profileButtonsStub = itemView.findViewById(R.id.profileButtonsStub);
        userButtonsStub = itemView.findViewById(R.id.userButtonsStub);
        placeStub = itemView.findViewById(R.id.placeStub);
        workStub = itemView.findViewById(R.id.workStub);
        universityStub = itemView.findViewById(R.id.educationStub);
        coverImage.setTag(target);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        Context context = itemView.getContext();

        ProfileItem profileItem = (ProfileItem) itemDataHolder;

        title.setText(profileItem.getFullName());

        if(profileItem.getStatus().isEmpty()){
            if(subtitle.getVisibility()!=GONE){
                subtitle.setVisibility(GONE);
            }
        } else {
            subtitle.setText(profileItem.getStatus());
            if(subtitle.getVisibility()!=VISIBLE){
                subtitle.setVisibility(VISIBLE);
            }
        }


        LastSeen lastSeen = profileItem.getLastSeen();
        if(lastSeen!=null) {
            if (lastSeen.getPlatform() == 7) {
                if (onlineStatus != null && onlineStatus.getVisibility() != GONE) {
                    onlineStatus.setVisibility(GONE);
                    onlineStatus.setImageDrawable(null);
                }
            } else {
                if (onlineStatus == null) {
                    if (onlineStatusStub != null) {
                        onlineStatus = (ImageView) onlineStatusStub.inflate();
                    } else {
                        onlineStatus = itemView.findViewById(R.id.onlineStatus);
                    }
                }
                onlineStatus.setImageResource(R.drawable.ic_online_mobile_16);
            }

            if (profileItem.isOnline()) {
                subtitle2.setText(context.getString(R.string.online));
            } else {
                subtitle2.setText(TimeUtil.getOnlineDateString(context,
                        lastSeen.getTime(), profileItem.getSex()));
            }
        } else {
            if (onlineStatus != null && onlineStatus.getVisibility() != GONE) {
                onlineStatus.setVisibility(GONE);
                onlineStatus.setImageDrawable(null);
            }
            subtitle2.setText(TimeUtil.getOnlineDateString(context,
                    System.currentTimeMillis()/1000L, profileItem.getSex()));
        }

        if(profileItem.isVerified()){
            if(verified==null) {
                if(verifiedStub!=null) {
                    verified = (ImageView) verifiedStub.inflate();
                } else {
                    verified = itemView.findViewById(R.id.verified);
                }
            }
            if(verified.getVisibility()!=VISIBLE) {
                verified.setVisibility(VISIBLE);
            }
        } else {
            if(verified!=null&&verified.getVisibility()!=GONE){
                verified.setVisibility(GONE);
            }
        }

        Picasso.get().cancelRequest(image);

        if(profileItem.getPhotoMax().isEmpty()){
            image.setImageDrawable(null);
        } else {
            Picasso.get().load(profileItem.getPhotoMax()).into(image);
        }


        if(profileItem.getId().equals(user.getId())){
            if(profileButtonsHolder!=null&&profileButtonsHolder.getVisibility()!=GONE) {
                profileButtonsHolder.setVisibility(GONE);
            }
            if(userButtonsHolder==null) {
                if(userButtonsStub!=null) {
                    userButtonsHolder = (LinearLayout) userButtonsStub.inflate();
                } else {
                    userButtonsHolder = itemView.findViewById(R.id.userButtonsHolder);
                }
            }
            if(userButtonsHolder.getVisibility()!=VISIBLE) {
                userButtonsHolder.setVisibility(VISIBLE);
            }
        } else {
            if(userButtonsHolder!=null&&userButtonsHolder.getVisibility()!=GONE) {
                userButtonsHolder.setVisibility(GONE);
            }
            if(profileButtonsHolder==null) {
                if(profileButtonsStub!=null) {
                    profileButtonsHolder = (LinearLayout) profileButtonsStub.inflate();
                } else {
                    profileButtonsHolder = itemView.findViewById(R.id.userButtonsHolder);
                }
            }
            if(profileButtonsHolder.getVisibility()!=VISIBLE) {
                profileButtonsHolder.setVisibility(VISIBLE);
            }
        }

        setCover(profileItem);

        if(profileItem.getCity()!=null){
            if(place==null||placeTitle==null) {
                if(placeStub!=null) {
                    place = (LinearLayout) placeStub.inflate();
                } else {
                    place = itemView.findViewById(R.id.place);
                }
                placeTitle = place.findViewById(R.id.placeTitle);
            }
            if(place.getVisibility()!=VISIBLE) {
                place.setVisibility(VISIBLE);
            }
            City city = profileItem.getCity();
            placeTitle.setText(city.getTitle());
        } else {
            if(place!=null&&place.getVisibility()!=GONE){
                place.setVisibility(GONE);
            }
        }

        if(profileItem.getCareer()!=null&&profileItem.getCareer().getCompany()!=null){
            if(work==null||workTitle==null) {
                if(workStub!=null) {
                    work = (LinearLayout) workStub.inflate();
                } else {
                    work = itemView.findViewById(R.id.work);
                }
                workTitle = work.findViewById(R.id.workTitle);
            }
            if(work.getVisibility()!=VISIBLE) {
                work.setVisibility(VISIBLE);
            }
            Career career = profileItem.getCareer();
            workTitle.setText(career.getCompany());
        } else {
            if(work!=null&&work.getVisibility()!=GONE){
                work.setVisibility(GONE);
            }
        }

        if(profileItem.getEducation()!=null){
            if(university ==null|| universityTitle ==null) {
                if(universityStub !=null) {
                    university = (LinearLayout) universityStub.inflate();
                } else {
                    university = itemView.findViewById(R.id.university);
                }
                universityTitle = university.findViewById(R.id.universityTitle);
            }
            if(university.getVisibility()!=VISIBLE) {
                university.setVisibility(VISIBLE);
            }
            University university = profileItem.getEducation();
            universityTitle.setText(university.getUniversityName());
        } else {
            if(university !=null&& university.getVisibility()!=GONE){
                university.setVisibility(GONE);
            }
        }



    }

    private void setCover(ProfileItem profileItem){
        Picasso.get().cancelRequest(target);
        Picasso.get().cancelRequest(coverImage);
        if(profileItem.getCover()!=null){
            Cover cover = profileItem.getCover();
            if(cover.getEnabled()){
                if(!cover.getImages().isEmpty()) {
                    Image image = getOptimalSize(cover.getImages(),
                            itemView.getWidth() == 0 ? DeviceUtil.getDisplayWidth(itemView.getContext()) : itemView.getWidth(),
                            itemView.getHeight());
                    if (image != null) {
                        Picasso.get().load(image.getUrl()).into(coverImage);
                        return;
                    }
                }
            }
        }
        if(!profileItem.getPhotoMax().isEmpty()){
            Picasso.get().load(profileItem.getPhotoMax()).into(target);
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new ProfileLargeViewHolder(inflater.inflate(R.layout.view_profile_item_large, viewGroup, false));
        }
    }

}
