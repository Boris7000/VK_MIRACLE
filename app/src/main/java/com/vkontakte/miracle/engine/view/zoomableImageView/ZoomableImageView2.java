package com.vkontakte.miracle.engine.view.zoomableImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager2.widget.ViewPager2;

public class ZoomableImageView2 extends AppCompatImageView implements OnGestureListener, View.OnLayoutChangeListener{

    private static final float DEFAULT_MAX_SCALE = 3.0f;
    private static final float DEFAULT_MID_SCALE = 1.5f;
    private static final float DEFAULT_MIN_SCALE = 1.0f;
    private static final int DEFAULT_ZOOM_DURATION = 200;

    private final int touchSlop;
    private float initialX = 0f;
    private float initialY = 0f;
    private boolean verticalDragging = false;
    private FlingRunnable mCurrentFlingRunnable;

    // These are set so we don't keep allocating them on the heap
    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();
    private final RectF mDisplayRect = new RectF();
    private final float[] mMatrixValues = new float[9];

    private Interpolator mInterpolator = new LinearInterpolator();
    private int mZoomDuration = DEFAULT_ZOOM_DURATION;
    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;
    private boolean mZoomEnabled = true;
    private ScaleType mScaleType = ScaleType.FIT_CENTER;
    private ViewPager2 viewPager2;

    // Gesture Detectors
    private final GestureDetector mGestureDetector;
    private final CustomGestureDetector mScaleDragDetector;
    private OnPhotoActionListener onPhotoActionListener;

    public ZoomableImageView2(@NonNull Context context) {
        this(context, null);
    }

    public ZoomableImageView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        // Create Gesture Detectors...
        mScaleDragDetector = new CustomGestureDetector(getContext(), this);
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            // forward long click listener
            @Override
            public void onLongPress(MotionEvent e) { }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                final RectF displayRect = getDisplayRect();
                final float x = e.getX(), y = e.getY();
                if (displayRect != null) {
                    if (onPhotoActionListener != null) {
                        // Check to see if the user tapped on the photo
                        boolean taped = displayRect.contains(x, y);
                        if (taped) {
                            onPhotoActionListener.onSingleTap(ZoomableImageView2.this);
                        }
                        return taped;
                    } else {
                        // Check to see if the user tapped on the photo
                        return displayRect.contains(x, y);
                    }
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent ev) {
                try {
                    if(mZoomEnabled) {
                        float scale = getScale();
                        float x = ev.getX();
                        float y = ev.getY();
                        if (scale < getMediumScale()) {
                            setScale(getMediumScale(), x, y, true);
                        } else if (scale >= getMediumScale() && scale < getMaximumScale()) {
                            setScale(getMaximumScale(), x, y, true);
                        } else {
                            setScale(getMinimumScale(), x, y, true);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // Can sometimes happen when getX() and getY() is called
                }
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                // Wait for the confirmed onDoubleTap() instead
                return false;
            }
        });

        super.setScaleType(ScaleType.MATRIX);
    }


    int fingers = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        int action = e.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:{

                cancelFling();

                initialX = e.getX();
                initialY = e.getY();

                getParent().requestDisallowInterceptTouchEvent(true);

                fingers = 1;
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:{
                fingers++;
                if(fingers>1){

                    getParent().requestDisallowInterceptTouchEvent(true);

                    if (viewPager2 != null) {
                        viewPager2.setUserInputEnabled(false);
                    }

                    if(verticalDragging) {
                        verticalDragging = false;
                        if (onPhotoActionListener != null) {
                            if(getScale()==1) {
                                onPhotoActionListener.onRelease(this);
                                return true;
                            }
                        }
                    }

                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                fingers--;
                break;
            }
            case  MotionEvent.ACTION_OUTSIDE:
            case  MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:{

                fingers=0;

                if (getScale() < mMinScale) {
                    setScale(mMinScale,true);
                } else if (getScale() > mMaxScale) {
                    setScale(mMaxScale,true);
                }

                getParent().requestDisallowInterceptTouchEvent(false);

                if (viewPager2 != null) {
                    viewPager2.setUserInputEnabled(true);
                }

                if(verticalDragging) {
                    verticalDragging = false;
                    if (onPhotoActionListener != null) {
                        if(getScale()==1) {
                            onPhotoActionListener.onRelease(this);
                            return true;
                        }
                    }
                }

                break;
            }
            case MotionEvent.ACTION_MOVE: {

                float dx = e.getX() - initialX;
                float dy = e.getY() - initialY;

                float absDX = Math.abs(dx);
                float absDY = Math.abs(dy);
                // assuming ViewPager2 touch-slop is 2x touch-slop of child
                float scaledDx = absDX * .5f;


                if(verticalDragging){
                    if (onPhotoActionListener != null && !mScaleDragDetector.isScaling() && getScale() == 1 && fingers==1) {
                        onPhotoActionListener.onDrag(this, e.getRawX(), e.getRawY());
                        return true;
                    }
                } else {
                    if (scaledDx > touchSlop || absDY > touchSlop) {
                        if (absDY > scaledDx) {// Gesture is perpendicular
                            verticalDragging = true;
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } else {// Gesture is parallel
                            getParent().requestDisallowInterceptTouchEvent(getScale() != 1 || verticalDragging);
                        }
                    }
                }

                break;
            }

        }


        // Try the Scale/Drag detector
        mScaleDragDetector.onTouchEvent(e);

        // Check to see if the user double tapped
        mGestureDetector.onTouchEvent(e);

        return true;
    }

    @Override
    public void onDrag(float dx, float dy) {

        if (mScaleDragDetector.isScaling()&&getScale()==1) {
            return;
        }

        float[]values = new float[9];
        mSuppMatrix.getValues(values);
        Log.d("igirjigjrgj", "scale "+ values[0]);
        Log.d("igirjigjrgj", "x "+values[2]);
        Log.d("igirjigjrgj", "y "+values[5]);

        mSuppMatrix.postTranslate(dx, dy);
        checkAndDisplayMatrix();
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        mCurrentFlingRunnable = new FlingRunnable(getContext());
        mCurrentFlingRunnable.fling(getImageViewWidth(this)+1,
                getImageViewHeight(this)+1 , (int) velocityX, (int) velocityY);
        post(mCurrentFlingRunnable);
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        if(mZoomEnabled) {

            float[]values = new float[9];
            mSuppMatrix.getValues(values);
            Log.d("igirjigjrgj", "scale "+ values[0]);
            Log.d("igirjigjrgj", "x "+values[2]);
            Log.d("igirjigjrgj", "y "+values[5]);

            mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkAndDisplayMatrix();
        }
    }

    public void setScale(float scale) {
        setScale(scale, false);
    }

    public void setScale(float scale, boolean animate) {
        setScale(scale, getRight()/2f, getBottom()/2f, animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        // Check to see if the scale is within bounds
        if (scale < mMinScale || scale > mMaxScale) {
            throw new IllegalArgumentException("Scale must be within the range of minScale and maxScale");
        }
        if (animate) {
            post(new AnimatedZoomRunnable(getScale(), scale, focalX, focalY));
        } else {
            mSuppMatrix.setScale(scale, scale, focalX, focalY);
            checkAndDisplayMatrix();
        }
    }

    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2) +
                (float) Math.pow(getValue(mSuppMatrix, Matrix.MSKEW_Y), 2));
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (isSupportedScaleType(scaleType) && scaleType != mScaleType) {
            mScaleType = scaleType;
            if(mSuppMatrix!=null)
                update();
        }
    }

    private boolean isSupportedScaleType(final ImageView.ScaleType scaleType) {
        if (scaleType == null) {
            return false;
        }
        if (scaleType == ScaleType.MATRIX) {
            throw new IllegalStateException("Matrix scale type is not supported");
        }
        return true;
    }

    public void setZoomInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public boolean isZoomable() {
        return mZoomEnabled;
    }

    public void setZoomable(boolean zoomable) {
        mZoomEnabled = zoomable;
        //update();
    }

    public void setZoomDuration(int mZoomDuration) {
        this.mZoomDuration = mZoomDuration;
    }

    public float getMinimumScale() {
        return mMinScale;
    }

    public float getMediumScale() {
        return mMidScale;
    }

    public float getMaximumScale() {
        return mMaxScale;
    }

    public void update() {
        //if (mZoomEnabled) {
            // Update the base matrix using the current drawable
            updateBaseMatrix(getDrawable());
        //} else {
            // Reset the Matrix...
            //resetMatrix();
        //}
    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    @Override
    public Matrix getImageMatrix() {
        return mDrawMatrix;
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     Matrix to unpack
     * @param whichValue Which value from Matrix.M* to return
     * @return returned value
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays its contents
     */
    private void resetMatrix() {
        mSuppMatrix.reset();
        checkAndDisplayMatrix();
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
        setImageMatrix(matrix);
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param drawable - Drawable being displayed
     */
    private void updateBaseMatrix(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        final float viewWidth = getImageViewWidth(this);
        final float viewHeight = getImageViewHeight(this);
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        mBaseMatrix.reset();
        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;
        if (mScaleType == ScaleType.CENTER) {
            mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
                    (viewHeight - drawableHeight) / 2F);

        } else if (mScaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);
            switch (mScaleType) {
                case FIT_CENTER:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER);
                    break;
                case FIT_START:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.START);
                    break;
                case FIT_END:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.END);
                    break;
                case FIT_XY:
                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL);
                    break;
                default:
                    break;
            }
        }
        resetMatrix();
    }

    private boolean checkMatrixBounds() {
        final RectF rect = getDisplayRect(getDrawMatrix());
        if (rect == null) {
            return false;
        }
        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;
        final int viewHeight = getImageViewHeight(this)+1;
        if (height <= viewHeight) {
            switch (mScaleType) {
                case FIT_START:
                    deltaY = -rect.top;
                    break;
                case FIT_END:
                    deltaY = viewHeight - height - rect.top;
                    break;
                default:
                    deltaY = (viewHeight - height) / 2 - rect.top;
                    break;
            }
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }
        final int viewWidth = getImageViewWidth(this)+1;

        if (width <= viewWidth) {
            switch (mScaleType) {
                case FIT_START:
                    deltaX = -rect.left;
                    break;
                case FIT_END:
                    deltaX = viewWidth - width - rect.left;
                    break;
                default:
                    deltaX = (viewWidth - width) / 2 - rect.left;
                    break;
            }
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
        }
        // Finally actually translate the matrix
        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    private int getImageViewWidth(ImageView imageView) {
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    private void cancelFling() {
        if (mCurrentFlingRunnable != null) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap calls through to this method
        update();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        update();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        update();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        update();
        return changed;
    }


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
            updateBaseMatrix(getDrawable());
        }
    }



    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        Drawable d = getDrawable();
        if (d != null) {
            mDisplayRect.set(0, 0, d.getIntrinsicWidth(),
                    d.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    public RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    private class AnimatedZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        }

        @Override
        public void run() {

            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();

            onScale(deltaScale, mFocalX, mFocalY);


            //onScale(deltaScale, mFocalX, mFocalY);
            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                ZoomableImageView2.this.postOnAnimation(this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
            t = Math.min(1f, t);
            t = mInterpolator.getInterpolation(t);
            return t;
        }
    }

    private class FlingRunnable implements Runnable {

        private final OverScroller mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
            mScroller = new OverScroller(context);
        }

        public void cancelFling() {
            mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX,
                          int velocityY) {
            final RectF rect = getDisplayRect();
            if (rect == null) {
                return;
            }
            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;
            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }
            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }
            mCurrentX = startX;
            mCurrentY = startY;
            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX,
                        maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
            if (mScroller.isFinished()) {
                return; // remaining post that should not be handled
            }
            if (mScroller.computeScrollOffset()) {
                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();
                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                checkAndDisplayMatrix();
                mCurrentX = newX;
                mCurrentY = newY;
                // Post On animation
                ZoomableImageView2.this.postOnAnimation(this);
            }
        }
    }

    public void setViewPager2(ViewPager2 viewPager2){
        this.viewPager2 = viewPager2;
    }

    public void setOnPhotoActionListener(OnPhotoActionListener onPhotoActionListener){
        this.onPhotoActionListener = onPhotoActionListener;
    }

}
