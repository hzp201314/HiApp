package com.hzp.hi.common.tab;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 1.将Fragment的操作内聚
 * 2.提供通用的一些API
 */
public class HiFragmentTabView extends FrameLayout {
    private HiTabViewAdapter mAdapter;
    private int currentPosition;

    public HiFragmentTabView(@NonNull Context context) {
        super(context);
    }

    public HiFragmentTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HiFragmentTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HiTabViewAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(HiTabViewAdapter adapter) {
        if (this.mAdapter != null || adapter == null) {
            return;
        }
        this.mAdapter = adapter;
        currentPosition = -1;
    }

    public void setCurrentItem(int position) {
        if (position < 0 || position >= mAdapter.getCount()) {
            return;
        }
        if (currentPosition != position) {
            currentPosition = position;
            mAdapter.instantiateItem(this, position);
        }
    }

    public int getCurrentItem(){
        return currentPosition;
    }

    public Fragment getCurrentFragment(){
        if(this.mAdapter==null){
            throw new IllegalArgumentException("please call setAdapter first.");
        }
        return mAdapter.getCurrentFragment();
    }
}
