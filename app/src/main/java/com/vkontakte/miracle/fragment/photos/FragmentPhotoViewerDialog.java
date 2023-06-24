package com.vkontakte.miracle.fragment.photos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.tabs.adapters.SimpleTabsAdapter;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.view.bottomToolBar.MiracleBottomToolBar;
import com.vkontakte.miracle.engine.view.zoomableImageView.OnPhotoActionListener;
import com.vkontakte.miracle.engine.view.zoomableImageView.ZoomableImageView2;

import java.util.ArrayList;

public class FragmentPhotoViewerDialog extends DialogFragment implements OnPhotoActionListener {

    private SimpleTabsAdapter tabsAdapter;
    private TextView title;
    private WindowInsetsCompat windowInsets;
    private MiracleBottomToolBar bottomToolBar;
    private LinearLayout topToolBar;
    private ViewPager2 viewPager;
    private View rootView;
    private View blocking;
    private PhotoViewerData photoViewerData;
    private boolean showUi = true;
    private boolean dismissed = false;
    private float displayRatio;

    public FragmentPhotoViewerDialog(){
        this(null);
    }

    public FragmentPhotoViewerDialog(PhotoViewerData photoViewerData){
        this.photoViewerData = photoViewerData;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.PhotoViewerTheme);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return rootView = inflater.inflate(R.layout.dialog_photo_viewer, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(rootView, savedInstanceState);

        blocking = rootView.findViewById(R.id.blocking);

        ImageView backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> FragmentPhotoViewerDialog.this.dismiss());

        viewPager = rootView.findViewById(R.id.viewPager);
        View child = viewPager.getChildAt(0);
        if (child instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) child;
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        float pageMargin = getResources().getDimensionPixelOffset(R.dimen.horizontalMargin);

        viewPager.setPageTransformer((page, position) -> {
            float myOffset = position * pageMargin;
            if (position < -1) {
                page.setTranslationX(myOffset);
            } else if (position <= 1) {

                float scaleFactor = 1 - 0.25f*Math.abs(position);
                page.setTranslationX(-myOffset);
                page.setScaleY(scaleFactor);
                page.setScaleX(scaleFactor);
                page.setAlpha(scaleFactor);
            } else {
                page.setAlpha(0);
                page.setTranslationX(-myOffset);
            }
        });

        topToolBar = rootView.findViewById(R.id.topToolBar);
        title = topToolBar.findViewById(R.id.title);
        bottomToolBar = rootView.findViewById(R.id.bottomToolBar);

        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            String key = savedInstanceState.getString("photoViewerData");
            if(key!=null){
                photoViewerData = (PhotoViewerData) LargeDataStorage.get().getLargeData(key);
                savedInstanceState.remove("photoViewerData");
            }
            displayRatio = savedInstanceState.getFloat("displayRatio");
        }

        if(nullSavedAdapter(savedInstanceState)) {
            ArrayList<FragmentFabric> fabrics = new ArrayList<>();
            for (PhotoDialogItem photoDialogItem: photoViewerData.getPhotoDialogItems()) {
                fabrics.add(new FragmentPhotoNested.Fabric(photoDialogItem));
            }
            setAdapter(new SimpleTabsAdapter(getChildFragmentManager(), getLifecycle(),  fabrics));
        }

        viewPager.setCurrentItem(photoViewerData.getItemIndex(),false);

        //////////////////////////////////////////////


        if(savedInstanceState==null) {

            rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener(){
                        @Override
                        public void onGlobalLayout() {
                            animateOpen();
                            rootView.getViewTreeObserver().removeOnGlobalLayoutListener( this );
                        }

                    });
        } else {
            setBackgroundAlpha(255);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                FragmentPhotoViewerDialog.this.dismiss();
            }
        };

        Window window = dialog.getWindow();

        WindowCompat.setDecorFitsSystemWindows(window, false);

        iniWindowInsets(window);

        return dialog;
    }

    public void setAdapter(SimpleTabsAdapter tabsAdapter){
        this.tabsAdapter = tabsAdapter;

        viewPager.setAdapter(tabsAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                title.setText(String.valueOf(position+1));
                photoViewerData.setItemIndex(position);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putFloat("displayRatio", displayRatio);

        if(tabsAdapter!=null) {
            outState.putString("Adapter", LargeDataStorage.get().storeLargeData(tabsAdapter));
        }

        if(photoViewerData!=null){
            outState.putString("photoViewerData", LargeDataStorage.get().storeLargeData(photoViewerData));
        }

        super.onSaveInstanceState(outState);
    }

    public boolean nullSavedAdapter(Bundle savedInstanceState){
        if(savedInstanceState!=null) {
            String key = savedInstanceState.getString("Adapter");
            if (key!= null) {
                SimpleTabsAdapter tabsAdapter = (SimpleTabsAdapter) LargeDataStorage.get().getLargeData(key);
                savedInstanceState.remove("Adapter");

                setAdapter(new SimpleTabsAdapter(getChildFragmentManager(), getLifecycle(), tabsAdapter.getFabrics()));
                return false;
            }
        }
        return true;
    }

    private void iniWindowInsets(Window window){
        ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView(), (v, windowInsets) -> {
            this.windowInsets = windowInsets;
            updateInsets();
            return windowInsets;
        });
    }

    private void updateInsets(){
        if(windowInsets!=null) {
            Insets systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime());

            //height of player bar and bottom navigation menu
            int bottomInsets = Math.max(systemBarsInsets.bottom,imeInsets.bottom);

            bottomToolBar.setPadding(0, 0, 0, bottomInsets);
            topToolBar.setPadding(0, systemBarsInsets.top, 0, 0);
        }
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    private float alpha = 1f;
    private Float yCoordinate = null;

    @Override
    public void onDrag(ZoomableImageView2 image, float rawX, float rawY) {
        if(!dismissed) {
            if (yCoordinate == null) {
                yCoordinate = viewPager.getY() - rawY;
            }

            float yPos = rawY + yCoordinate;


            if (yPos > viewPager.getHeight()) {
                yPos = viewPager.getHeight();
            } else if (yPos < -viewPager.getHeight()) {
                yPos = -viewPager.getHeight();
            }

            viewPager.setY(yPos);
            alpha = 1 - Math.abs(yPos) / ((float) rootView.getHeight());
            setBackgroundAlpha((int) (255 * alpha));
            if(alpha<1) {
                hideUI();
            }
        }
    }

    @Override
    public void onRelease(ZoomableImageView2 image) {
        if(!dismissed) {
            if (alpha < 0.9f) {
                image.setZoomable(false);
                dismiss();
            } else {
                if(alpha<1) {
                    animateBackgroundAlpha(200, (int) (alpha * 255), 255);
                }
                if (viewPager.getAnimation() != null) {
                    viewPager.getAnimation().cancel();
                }
                viewPager.animate().y(0).setDuration(125).start();
                showUI();
                alpha = 1;
                yCoordinate = null;
            }
        }
    }

    @Override
    public void onSingleTap(ZoomableImageView2 image) {
        if(showUi){
            hideUI();
        } else {
            showUI();
        }
    }

    @Override
    public void dismiss(){
        if(!dismissed){
            dismissed =true;
            animateDismiss();
        }
    }

    private void animateBackgroundAlpha(int duration, int alpha1, int alpha2){
        ValueAnimator va = ValueAnimator.ofInt(alpha1, alpha2);
        va.setDuration(duration);
        va.addUpdateListener(animation -> setBackgroundAlpha((int)animation.getAnimatedValue()));
        va.start();
    }

    private void setBackgroundAlpha(int alpha){
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(Color.argb(alpha,0,0,0), PorterDuff.Mode.SRC_IN);
        rootView.getBackground().setColorFilter(colorFilter);
    }

    private void animateUIAlpha(int duration, float alpha1, float alpha2){
        ValueAnimator va = ValueAnimator.ofFloat(alpha1, alpha2);
        va.setDuration(duration);
        va.addUpdateListener(animation -> setUIAlpha((float)animation.getAnimatedValue()));
        va.start();
    }
    private void setUIAlpha(float alpha){
        topToolBar.setAlpha(alpha);
        bottomToolBar.setAlpha(alpha);
    }

    private void animateUITranslationY(int duration, float translation1, float translation2,
                                       float translation3, float translation4){

        ValueAnimator va1 = ValueAnimator.ofFloat(translation1, translation3);
        ValueAnimator va2 = ValueAnimator.ofFloat(translation2, translation4);

        va1.setDuration(duration);
        va2.setDuration(duration);
        va1.addUpdateListener(animation -> topToolBar.setTranslationY((float) animation.getAnimatedValue()));
        va2.addUpdateListener(animation -> bottomToolBar.setTranslationY((float) animation.getAnimatedValue()));

        va1.start();
        va2.start();

    }
    private void showUI(){
        if(!showUi) {
            showUi = true;
            animateUIAlpha(300, topToolBar.getAlpha(), 1f);
            animateUITranslationY(200, topToolBar.getTranslationY(), bottomToolBar.getTranslationY(), 0, 0);
        }
    }

    private void hideUI(){
        if(showUi) {
            showUi = false;
            animateUIAlpha(300, topToolBar.getAlpha(), 0);
            animateUITranslationY(200, topToolBar.getTranslationY(), bottomToolBar.getTranslationY(), -topToolBar.getHeight(),bottomToolBar.getHeight());
        }
    }

    private void animateDismiss(){

        int duration1 = 300;
        int duration2 = 200;

        blocking.setClickable(true);
        Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                FragmentPhotoViewerDialog.super.dismiss();
            }
        };

        animateBackgroundAlpha(duration2, (int) (alpha*255), 0);
        if(showUi) {
            showUi = false;
            animateUIAlpha(duration1, topToolBar.getAlpha(), 0);
            animateUITranslationY(duration2, topToolBar.getTranslationY(), bottomToolBar.getTranslationY(), -topToolBar.getHeight(),bottomToolBar.getHeight());
        }


        ZoomableImageView2 zoomableImageView2 = (((ViewGroup) viewPager.getChildAt(0)).getChildAt(0)).findViewById(R.id.photo);
        zoomableImageView2.setZoomInterpolator(new LinearInterpolator());
        zoomableImageView2.setZoomDuration(duration1);
        zoomableImageView2.setScale(1, true);

        PhotoDialogItem photoDialogItem = photoViewerData.getPhotoDialogItems().get(viewPager.getCurrentItem());
        if(photoDialogItem.getWidth()>0) {
            int[] displaySize = new int[]{rootView.getWidth(), rootView.getHeight()};
            float displayRatio = (float) displaySize[0] / (float) displaySize[1];

            if (displayRatio == this.displayRatio) {
                float defX = (photoDialogItem.getWidth() / 2f + photoDialogItem.getRawX()) - displaySize[0] / 2f;
                float defY = (photoDialogItem.getHeight() / 2f + photoDialogItem.getRawY()) - displaySize[1] / 2f;

                ValueAnimator va = ValueAnimator.ofFloat(viewPager.getX(), defX);
                va.setDuration(duration1);
                va.addUpdateListener(animation -> viewPager.setX((float) animation.getAnimatedValue()));
                va.start();
                va = ValueAnimator.ofFloat(viewPager.getY(), defY);
                va.setDuration(duration1);
                va.addUpdateListener(animation -> viewPager.setY((float) animation.getAnimatedValue()));
                va.start();

                float imageRatio = (float) photoDialogItem.getWidth() / (float) photoDialogItem.getHeight();

                float scale;

                if (imageRatio < displayRatio) {
                    scale = (float) photoDialogItem.getHeight() / (float) displaySize[1];
                } else {
                    scale = (float) photoDialogItem.getWidth() / (float) displaySize[0];
                }

                va = ValueAnimator.ofFloat(viewPager.getScaleX(), scale);
                va.setDuration(duration1);
                va.addUpdateListener(animation -> {
                    viewPager.setScaleY((float) animation.getAnimatedValue());
                    viewPager.setScaleX((float) animation.getAnimatedValue());
                });
                va.addListener(listener);
                va.start();
                return;
            }
        }
        ValueAnimator va = ValueAnimator.ofFloat(1f, 0);
        va.setDuration(duration1);
        va.addUpdateListener(animation -> viewPager.setAlpha((float) animation.getAnimatedValue()));
        va.addListener(listener);
        va.start();
    }

    private void animateOpen(){

        int duration1 = 300;
        int duration2 = 200;

        blocking.setClickable(true);
        Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                blocking.setClickable(false);
            }
        };

        animateBackgroundAlpha(duration2, 0, 255);
        animateUIAlpha(duration1, 0, 1);
        animateUITranslationY(duration2, -topToolBar.getHeight(), bottomToolBar.getHeight(), 0, 0);

        PhotoDialogItem photoDialogItem = photoViewerData.getPhotoDialogItems().get(viewPager.getCurrentItem());

        if(photoDialogItem.getWidth()>0) {
            if (photoDialogItem.getPreview() != null) {
                int[] displaySize = new int[]{rootView.getWidth(), rootView.getHeight()};
                displayRatio = (float) displaySize[0] / (float) displaySize[1];
                float defX = (photoDialogItem.getWidth() / 2f + photoDialogItem.getRawX()) - displaySize[0] / 2f;
                float defY = (photoDialogItem.getHeight() / 2f + photoDialogItem.getRawY()) - displaySize[1] / 2f;

                ValueAnimator va = ValueAnimator.ofFloat(defX, viewPager.getX());
                va.setDuration(duration1);
                va.addUpdateListener(animation -> viewPager.setX((float) animation.getAnimatedValue()));
                va.start();
                va = ValueAnimator.ofFloat(defY, viewPager.getY());
                va.setDuration(duration1);
                va.addUpdateListener(animation -> viewPager.setY((float) animation.getAnimatedValue()));
                va.start();

                float scale;
                float imageRatio = (float) photoDialogItem.getWidth() / (float) photoDialogItem.getHeight();

                if (imageRatio < displayRatio) {
                    scale = (float) photoDialogItem.getHeight() / (float) displaySize[1];
                } else {
                    scale = (float) photoDialogItem.getWidth() / (float) displaySize[0];
                }

                va = ValueAnimator.ofFloat(scale, 1);
                va.setDuration(duration1);
                va.addUpdateListener(animation -> {
                    viewPager.setScaleY((float) animation.getAnimatedValue());
                    viewPager.setScaleX((float) animation.getAnimatedValue());
                });
                va.addListener(listener);
                va.start();
            } else {
                ValueAnimator va = ValueAnimator.ofFloat(0f, 1);
                va.setDuration(duration1);
                va.addUpdateListener(animation -> viewPager.setAlpha((float) animation.getAnimatedValue()));
                va.addListener(listener);
                va.start();
            }
        }

    }

    public static class PhotoViewerData{
        private final ArrayList<PhotoDialogItem> photoDialogItems;
        private int itemIndex;

        public PhotoViewerData(ArrayList<PhotoDialogItem> photoDialogItems, int itemIndex){
            this.photoDialogItems = photoDialogItems;
            this.itemIndex = itemIndex;
        }

        public void setItemIndex(int itemIndex) {
            this.itemIndex = itemIndex;
        }

        public ArrayList<PhotoDialogItem> getPhotoDialogItems() {
            return photoDialogItems;
        }

        public int getItemIndex() {
            return itemIndex;
        }
    }

}
