package com.msy.vlib.flip;

import android.view.View;

import java.util.ArrayList;

/**
 * @author msy
 */

public abstract class BasePageFactory<T> {

    private int mLayoutId;
    private ArrayList<T> mData;

    private int mCancelPageIndex;
    private int mCurrentPageIndex = 0;

    public BasePageFactory(int layoutId) {
        this.mLayoutId = layoutId;
    }

    public int getLayoutId() {
        return mLayoutId;
    }

    public void setData(ArrayList<T> data) {
        this.mData = data;
    }

    protected abstract void showData(View view, T item);

    public void initShow(FlipWidget flipWidget) {
        View preView = flipWidget.getPrePage();
        View curView = flipWidget.getCurPage();
        View nextView = flipWidget.getNextPage();

        //前一页
        if (mCurrentPageIndex - 1 >= 0) {
            showData(preView, mData.get(mCurrentPageIndex - 1));
        } else {
            showData(preView, mData.get(mCurrentPageIndex));
        }

        //当前页
        if (mData.size() > mCurrentPageIndex) {
            showData(curView, mData.get(mCurrentPageIndex));
        }

        //下一页
        if (mData.size() > mCurrentPageIndex + 1) {
            showData(nextView, mData.get(mCurrentPageIndex + 1));
        } else {
            showData(nextView, mData.get(mCurrentPageIndex));
        }

    }

    public boolean prePage(FlipWidget flipWidget) {
        if (mCurrentPageIndex - 1 >= 0 && mCurrentPageIndex - 1 < mData.size()) {

            mCancelPageIndex = mCurrentPageIndex;
            mCurrentPageIndex -= 1;

            return true;
        } else {
            return false;
        }
    }

    public boolean nextPage(FlipWidget flipWidget) {
        if (mCurrentPageIndex + 1 >= 0 && mCurrentPageIndex + 1 < mData.size()) {

            mCancelPageIndex = mCurrentPageIndex;
            mCurrentPageIndex += 1;

            return true;
        } else {
            return false;
        }
    }

    public void cancelPage(FlipWidget flipWidget) {
        mCurrentPageIndex = mCancelPageIndex;
    }
}
