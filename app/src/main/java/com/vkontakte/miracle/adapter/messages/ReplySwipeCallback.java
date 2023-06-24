package com.vkontakte.miracle.adapter.messages;

import static com.miracle.engine.util.ColorUtil.getColorByAttributeId;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.util.DimensionsUtil;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.messages.holders.MessageViewHolder;

public class ReplySwipeCallback extends ItemTouchHelper.Callback {

    private final Context context;
    private final ISwipeControllerActions mSwipeControllerActions;

    private Drawable mReplyIcon;
    private final Drawable mReplyIconBackground;

    private RecyclerView.ViewHolder mCurrentViewHolder;
    private View mView;

    private float mDx = 0f;

    private float mReplyButtonProgress = 0f;
    private long  mLastReplyButtonAnimationTime = 0;

    private boolean mSwipeBack = false;
    private boolean mIsVibrating = false;
    private boolean mStartTracking = false;
    private final int maxXDelta = 80;
    private final int maxX = 110;
    private final float itemMargin;

    public ReplySwipeCallback(Context context, ISwipeControllerActions swipeControllerActions){
        this.context = context;
        mSwipeControllerActions = swipeControllerActions;

        mReplyIcon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_share_24,null);
        if(mReplyIcon!=null) {
            mReplyIcon = mReplyIcon.getConstantState().newDrawable().mutate();
            mReplyIcon.setColorFilter(getColorByAttributeId(this.context, R.attr.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
        mReplyIconBackground = ResourcesCompat.getDrawable(this.context.getResources(),
                R.drawable.rounded_card_child_container_secondary, this.context.getTheme());

        itemMargin =  convertToDp(9)/2f;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof MessageViewHolder) {
            mView = viewHolder.itemView;
            return ItemTouchHelper.Callback.makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT);
        } else {
            return 0;
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) { }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection){
        if (mSwipeBack){
            mSwipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            setTouchListener(recyclerView, viewHolder);
        }
        if (mView.getTranslationX() < convertToDp(maxX) || dX < mDx ){
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            mDx = dX;
            mStartTracking = true;
        }
        mCurrentViewHolder = viewHolder;
        drawReplyButton(c);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder){
        recyclerView.setOnTouchListener((v, event) -> {
            mSwipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
            if (mSwipeBack){
                if (Math.abs(mView.getTranslationX()) >= convertToDp(maxXDelta)){
                    mSwipeControllerActions.onSwipePerformed(viewHolder.getBindingAdapterPosition());
                }
            }
            return false;
        });
    }

    private int convertToDp(int pixels){
        return (int) DimensionsUtil.dpToPx(context, pixels);
    }


    private void drawReplyButton(Canvas canvas){

        if (mCurrentViewHolder == null){
            return;
        }

        float translationX = mView.getTranslationX();
        long newTime = System.currentTimeMillis();
        long dt = Math.min(17, newTime - mLastReplyButtonAnimationTime);
        int mReplyBackgroundOffset = 18;
        int mReplyIconXOffset = 12;
        int mReplyIconYOffset = 11;
        int sum = mReplyBackgroundOffset+mReplyIconXOffset;
        int mReplyBackgroundOffsetDouble = mReplyBackgroundOffset*2;

        mLastReplyButtonAnimationTime = newTime;
        boolean showing = translationX >= convertToDp(mReplyBackgroundOffsetDouble);
        if (showing){
            if (mReplyButtonProgress < 1.0f){
                mReplyButtonProgress += dt / (float)maxX;
                if (mReplyButtonProgress > 1.0f){
                    mReplyButtonProgress = 1.0f;
                } else {
                    mView.invalidate();
                }
            }
        } else if (translationX <= 0.0f){
            mReplyButtonProgress = 0f;
            mStartTracking = false;
            mIsVibrating = false;
        } else {
            if (mReplyButtonProgress > 0.0f){
                mReplyButtonProgress -= dt / (float)maxX;
                if (mReplyButtonProgress < 0.1f){
                    mReplyButtonProgress = 0f;
                }
            }
            mView.invalidate();
        }

        float scale;
        if (showing){
            if (mReplyButtonProgress <= 0.8f){
                scale = 1.2f * (mReplyButtonProgress / 0.8f);
            } else{
                scale = 1.2f - 0.2f * ((mReplyButtonProgress - 0.8f) / 0.2f);
            }
        } else{
            scale = mReplyButtonProgress;
        }

        if (mStartTracking){
            if (!mIsVibrating && translationX >= convertToDp(maxXDelta)){
                mIsVibrating = true;
                mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
        }

        int x;
        float y;
        int MaxWithDelta = maxX+sum;

        if (translationX > convertToDp(MaxWithDelta)){
            x = convertToDp(MaxWithDelta) / 2;
        } else {
            x = (int) (translationX+sum) /2;
        }

        y = mView.getTop() + ((float) mView.getMeasuredHeight() /2) - itemMargin;

        mReplyIconBackground.setBounds(new Rect(
                (int)(x - convertToDp(mReplyBackgroundOffset) * scale),
                (int)(y - convertToDp(mReplyBackgroundOffset) * scale),
                (int)(x + convertToDp(mReplyBackgroundOffset) * scale),
                (int)(y + convertToDp(mReplyBackgroundOffset) * scale)
        ));
        mReplyIconBackground.draw(canvas);

        mReplyIcon.setBounds(new Rect(
                (int)(x - convertToDp(mReplyIconXOffset) * scale),
                (int)(y - convertToDp(mReplyIconYOffset) * scale),
                (int)(x + convertToDp(mReplyIconXOffset) * scale),
                (int)(y + convertToDp(mReplyIconYOffset) * scale)
        ));
        mReplyIcon.draw(canvas);

    }

}

