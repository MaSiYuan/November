package com.msy.vlib.flip;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.View;
import android.widget.Scroller;


/**
 * @author msy
 */
public abstract class BaseAnimationProvider {

    public static enum Direction {
        none(true), next(true), pre(true), up(false), down(false);

        public final boolean IsHorizontal;

        Direction(boolean isHorizontal) {
            IsHorizontal = isHorizontal;
        }
    }

    public static enum Animation {
        none, curl, slide, shift
    }

    protected View mPrePageView, mCurPageView, mNextPageView;
    protected float myStartX;
    protected float myStartY;
    protected int myEndX;
    protected int myEndY;
    protected Direction myDirection;
    protected float mySpeed;

    protected int mScreenWidth;
    protected int mScreenHeight;

    protected PointF mTouch = new PointF(); // 拖拽点
    private Direction direction = Direction.none;
    protected boolean isCancel = false;

    public BaseAnimationProvider(View mPrePageView, View mCurPageView, View mNextPageView, int width, int height) {
        this.mPrePageView = mPrePageView;
        this.mCurPageView = mCurPageView;
        this.mNextPageView = mNextPageView;
        this.mScreenWidth = width;
        this.mScreenHeight = height;
    }

    //绘制滑动页面
    public abstract void drawMove(Canvas canvas);

    //绘制不滑动页面
    public abstract void drawStatic(Canvas canvas);

    //设置开始拖拽点
    public void setStartPoint(float x, float y) {
        myStartX = x;
        myStartY = y;
    }

    //设置拖拽点
    public void setTouchPoint(float x, float y) {
        mTouch.x = x;
        mTouch.y = y;
    }

    //设置方向
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    public abstract void startAnimation(Scroller scroller);

    public boolean getCancel() {
        return isCancel;
    }

}
