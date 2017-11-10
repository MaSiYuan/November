package com.msy.vlib.flip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;


/**
 * @author msy
 */
public class FlipWidget extends FrameLayout {

    private final static String TAG = "BookPageWidget";
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    /**
     * 是否移动了
     */
    private Boolean isMove = false;
    /**
     * 是否翻到下一页
     */
    private Boolean isNext = false;
    /**
     * 是否取消翻页
     */
    private Boolean cancelPage = false;
    /**
     * 是否没下一页或者上一页
     */
    private Boolean noNext = false;
    private int downX = 0;
    private int downY = 0;

    private int moveX = 0;
    private int moveY = 0;
    /**
     * 翻页动画是否在执行
     */
    private Boolean isRuning = false;

    /**
     * 前一页
     */
    private View mPrePageView = null;

    /**
     * 当前页
     */
    private View mCurPageView = null;

    /**
     * 下一页
     */
    private View mNextPageView = null;
    private BaseAnimationProvider mAnimationProvider;

    private Scroller mScroller;
    private int mBgColor = Color.LTGRAY;

    private BasePageFactory mPageFactory;

    public FlipWidget(Context context) {
        this(context, null);
    }

    public FlipWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mAnimationProvider != null) {
            if (isRuning) {
                mAnimationProvider.drawMove(canvas);
            } else {
                mPageFactory.initShow(this);
                mAnimationProvider.drawStatic(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        mAnimationProvider.setTouchPoint(x, y);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) event.getX();
            downY = (int) event.getY();
            moveX = 0;
            moveY = 0;
            isMove = false;
            cancelPage = false;
            noNext = false;
            isNext = false;
            isRuning = false;
            mAnimationProvider.setStartPoint(downX, downY);
            abortAnimation();
            Log.e(TAG, "ACTION_DOWN");
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            //判断是否移动了
            if (!isMove) {
                isMove = Math.abs(downX - x) > slop || Math.abs(downY - y) > slop;
            }

            if (isMove) {
                isMove = true;
                if (moveX == 0 && moveY == 0) {
                    Log.e(TAG, "isMove");
                    //判断翻得是上一页还是下一页
                    if (x - downX > 0) {
                        isNext = false;
                    } else {
                        isNext = true;
                    }
                    cancelPage = false;
                    if (isNext) {
                        Boolean isNext = mPageFactory.nextPage(this);
                        mAnimationProvider.setDirection(BaseAnimationProvider.Direction.next);

                        if (!isNext) {
                            noNext = true;
                            return true;
                        }
                    } else {
                        Boolean isPre = mPageFactory.prePage(this);
                        mAnimationProvider.setDirection(BaseAnimationProvider.Direction.pre);

                        if (!isPre) {
                            noNext = true;
                            return true;
                        }
                    }
                    Log.e(TAG, "isNext:" + isNext);
                } else {
                    //判断是否取消翻页
                    if (isNext) {
                        if (x - moveX > 0) {
                            cancelPage = true;
                            mAnimationProvider.setCancel(true);
                        } else {
                            cancelPage = false;
                            mAnimationProvider.setCancel(false);
                        }
                    } else {
                        if (x - moveX < 0) {
                            mAnimationProvider.setCancel(true);
                            cancelPage = true;
                        } else {
                            mAnimationProvider.setCancel(false);
                            cancelPage = false;
                        }
                    }
                    Log.e(TAG, "cancelPage:" + cancelPage);
                }

                moveX = x;
                moveY = y;
                isRuning = true;
                this.postInvalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.e(TAG, "ACTION_UP");
            if (!isMove) {
                cancelPage = false;
                //是否点击了中间
                if (downX > mScreenWidth / 5 && downX < mScreenWidth * 4 / 5 && downY > mScreenHeight / 3 && downY < mScreenHeight * 2 / 3) {
                    return true;
                } else if (x < mScreenWidth / 2) {
                    isNext = false;
                } else {
                    isNext = true;
                }

                if (isNext) {
                    Boolean isNext = mPageFactory.nextPage(this);
                    mAnimationProvider.setDirection(BaseAnimationProvider.Direction.next);
                    if (!isNext) {
                        return true;
                    }
                } else {
                    Boolean isPre = mPageFactory.prePage(this);
                    mAnimationProvider.setDirection(BaseAnimationProvider.Direction.pre);
                    if (!isPre) {
                        return true;
                    }
                }
            }

            if (cancelPage) {
                mPageFactory.cancelPage(this);
            }

            Log.e(TAG, "isNext:" + isNext);
            if (!noNext) {
                isRuning = true;
                mAnimationProvider.startAnimation(mScroller);
                this.postInvalidate();
            }
        }

        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller != null && mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            mAnimationProvider.setTouchPoint(x, y);
            if (mScroller.getFinalX() == x && mScroller.getFinalY() == y) {
                isRuning = false;
            }
            postInvalidate();
        }
        super.computeScroll();
    }

    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            mAnimationProvider.setTouchPoint(mScroller.getFinalX(), mScroller.getFinalY());
            postInvalidate();
        }
    }

    public void setPageFactory(BasePageFactory pageFactory) {
        this.mPageFactory = pageFactory;

        mPrePageView = LayoutInflater.from(getContext()).inflate(mPageFactory.getLayoutId(), null);
        mCurPageView = LayoutInflater.from(getContext()).inflate(mPageFactory.getLayoutId(), null);
        mNextPageView = LayoutInflater.from(getContext()).inflate(mPageFactory.getLayoutId(), null);

        addView(mNextPageView);
        addView(mCurPageView);
        addView(mPrePageView);

        mNextPageView.setVisibility(View.INVISIBLE);
        mCurPageView.setVisibility(View.INVISIBLE);
        mPrePageView.setVisibility(View.INVISIBLE);

        mScreenWidth = getWidth();
        mScreenHeight = getHeight();

        mScroller = new Scroller(getContext(), new LinearInterpolator());
        mAnimationProvider = new SimulationAnimation(mPrePageView, mCurPageView, mNextPageView, mScreenWidth, mScreenHeight);

        mPageFactory.initShow(this);
    }

    public View getPrePage() {
        return mPrePageView;
    }

    public View getNextPage() {
        return mNextPageView;
    }

    public View getCurPage() {
        return mCurPageView;
    }
}
