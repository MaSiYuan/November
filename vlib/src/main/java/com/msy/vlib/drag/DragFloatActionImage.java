package com.msy.vlib.drag;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

/**
 * 功能描述：可拖拽的悬浮按钮，必须注册点击事件拖拽才生效
 *
 * @author msy
 */
public class DragFloatActionImage extends android.support.v7.widget.AppCompatImageView {

    private int screenWidth;
    private int screenHeight;
    private int screenWidthHalf;
    private int statusHeight;

    public DragFloatActionImage(Context context) {
        super(context);
        init();
    }

    public DragFloatActionImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragFloatActionImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenWidth = getScreenWidth();
        screenWidthHalf = screenWidth / 2;
        screenHeight = getScreenHeight() - getHeight();
        statusHeight = 50;
    }

    private int originalX;
    private int originalY;

    private int lastX;
    private int lastY;

    private boolean isDrag;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        screenHeight = (int) (getScreenHeight() - 1.1 * getHeight());
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                getParent().requestDisallowInterceptTouchEvent(true);
                originalX = rawX;
                originalY = rawY;

                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx1 = rawX - lastX;
                int dy1 = rawY - lastY;
                float x = getX() + dx1;
                float y = getY() + dy1;
                x = x < 0 ? 0 : x > screenWidth - getWidth() ? screenWidth - getWidth() : x;
                y = y < statusHeight ? statusHeight : y + getHeight() > screenHeight ? screenHeight - getHeight() : y;
                setX(x);
                setY(y);
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_UP:
                int dx = rawX - originalX;
                int dy = rawY - originalY;
                if (Math.abs(dx) <= 10 && Math.abs(dy) <= 10) {
                    Log.e("DragFloatActionButton", "dy=" + dx + ":dy=" + dy + ":onTouchEvent: 按压" + getWidth() + ":" + getHeight());
                    isDrag = false;
                } else {
                    Log.e("DragFloatActionButton", "dy=" + dx + ":dy=" + dy + ":onTouchEvent: 拖拽" + getWidth() + ":" + getHeight());
                    isDrag = true;
                    setPressed(false);
                    if (rawX >= screenWidthHalf) {
                        animate().setInterpolator(new DecelerateInterpolator())
                                .setDuration(500)
                                .xBy(screenWidth - getWidth() - getX())
                                .start();
                    } else {
                        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                        oa.setInterpolator(new DecelerateInterpolator());
                        oa.setDuration(500);
                        oa.start();
                    }
                }
                break;
            default:
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);
    }

    /**
     * 获取屏幕的宽度（单位：px）
     *
     * @return 屏幕宽px
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度（单位：px）
     *
     * @return 屏幕高px
     */
    private int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
        return dm.heightPixels;
    }
}
