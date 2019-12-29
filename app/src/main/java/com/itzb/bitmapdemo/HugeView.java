package com.itzb.bitmapdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class HugeView extends View implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener
        , ScaleGestureDetector.OnScaleGestureListener {

    private BitmapFactory.Options mOptions;
    private Scroller mScroller;
    private Matrix mMatrix;
    private BitmapRegionDecoder mRegionDecoder;
    private int mImageWidth;//图片的真实宽
    private int mImageHeight;//图片的真实高
    private int mViewWidth;//View的宽
    private int mViewHeight;//View的高
    private Rect mRect;
    private float mMultiple = 1.5f;
    private float mScale;//原始的缩放比例
    private float mCurrentScale;//当前缩放比例
    private Bitmap mBitmap;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private ValueAnimator mDoubleScaleAnimator;

    public HugeView(Context context) {
        super(context);
        init();
    }

    public HugeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HugeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mOptions = new BitmapFactory.Options();
        mScroller = new Scroller(getContext());
        mMatrix = new Matrix();//用于缩放的矩阵
        mRect = new Rect();

        //手势识别
        mGestureDetector = new GestureDetector(getContext(), this);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
    }

    //设置大图
    public void setImage(InputStream inputStream) {
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, mOptions);
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;//RGB_565比ARGB_8888节省一半内存开销
        mOptions.inJustDecodeBounds = false;
        try {
            mRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mRect.set(0, 0, mViewWidth, mViewHeight);
        mScale = mImageWidth * 1.0f / mViewWidth;
        mCurrentScale = mScale;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mRegionDecoder == null) return;

        //复用内存
        mOptions.inBitmap = mBitmap;
        mBitmap = mRegionDecoder.decodeRegion(mRect, mOptions);
        mMatrix.setScale(mCurrentScale, mCurrentScale);
        canvas.drawBitmap(mBitmap, mMatrix, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //当手指按下的时候，如果图片正在飞速滑动，那么停止
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     * onScroll中处理滑，根据手指移动的参数，来移动矩形绘制区域，这里需要处理各个边界点，
     * 比如左边最小就为0，右边最大为图片的宽度，不能超出边界否则就报错了。
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //滑动的时候，改变mRect显示区域的位置
        mRect.offset((int) distanceX, (int) distanceY);
        //处理上下左右的边界
        if (mRect.left < 0) {
            mRect.left = 0;
            mRect.right = (int) (mViewWidth / mCurrentScale);
        }
        if (mRect.right > mImageWidth) {
            mRect.right = mImageWidth;
            mRect.left = (int) (mImageWidth - mViewWidth / mCurrentScale);
        }
        if (mRect.top < 0) {
            mRect.top = 0;
            mRect.bottom = (int) (mViewHeight / mCurrentScale);
        }
        if (mRect.bottom > mImageHeight) {
            mRect.bottom = (int) mImageHeight;
            mRect.top = (int) (mImageHeight - mViewHeight / mCurrentScale);
        }
        invalidate();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    /*
    在onFling方法中调用滑动器Scroller的fling方法来处理手指离开之后惯性滑动。
    惯性移动的距离在View的computeScroll()方法中计算，也需要注意边界问题，不要滑出边界。
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mScroller.fling(mRect.left, mRect.top, -(int) velocityX, -(int) velocityY, 0,
                mImageWidth, 0, (int) mImageHeight);
        return false;
    }


    @Override
    public void computeScroll() {
        if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
            if (mRect.top + mViewHeight / mCurrentScale < mImageHeight) {
                mRect.top = mScroller.getCurrY();
                mRect.bottom = (int) (mRect.top + mViewHeight / mCurrentScale);
            }
            if (mRect.bottom > mImageHeight) {
                mRect.top = (int) (mImageHeight - mViewHeight / mCurrentScale);
                mRect.bottom = (int) mImageHeight;
            }
            invalidate();
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        //处理手指缩放事件
        //获取与上次事件相比，得到的比例因子
        float scaleFactor = detector.getScaleFactor();
        mCurrentScale += scaleFactor - 1;
//        mCurrentScale *= scaleFactor;
        if (mCurrentScale > mScale * mMultiple) {
            mCurrentScale = mScale * mMultiple;
        } else if (mCurrentScale <= mScale) {
            mCurrentScale = mScale;
        }
        mRect.right = mRect.left + (int) (mViewWidth / mCurrentScale);
        mRect.bottom = mRect.top + (int) (mViewHeight / mCurrentScale);
        invalidate();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        //当 >= 2 个手指碰触屏幕时调用，若返回 false 则忽略改事件调用
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        //处理双击事件
        if (mCurrentScale > mScale) {
            mDoubleScaleAnimator = ValueAnimator.ofFloat(mCurrentScale, mScale);
        } else {
            mDoubleScaleAnimator = ValueAnimator.ofFloat(mCurrentScale, mScale * mMultiple);
        }
        mDoubleScaleAnimator.setDuration(300).setInterpolator(new LinearInterpolator());
        mDoubleScaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentScale = (float) animation.getAnimatedValue();
                mRect.right = mRect.left + (int) (mViewWidth / mCurrentScale);
                mRect.bottom = mRect.top + (int) (mViewHeight / mCurrentScale);
                //处理上下左右的边界
                handleBorder();
                invalidate();
            }
        });
        mDoubleScaleAnimator.start();

        return true;
    }

    private void handleBorder() {
        if (mRect.left < 0) {
            mRect.left = 0;
            mRect.right = (int) (mViewWidth / mCurrentScale);
        }
        if (mRect.right > mImageWidth) {
            mRect.right = mImageWidth;
            mRect.left = (int) (mImageWidth - mViewWidth / mCurrentScale);
        }
        if (mRect.top < 0) {
            mRect.top = 0;
            mRect.bottom = (int) (mViewHeight / mCurrentScale);
        }
        if (mRect.bottom > mImageHeight) {
            mRect.bottom = (int) mImageHeight;
            mRect.top = (int) (mImageHeight - mViewHeight / mCurrentScale);
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}
