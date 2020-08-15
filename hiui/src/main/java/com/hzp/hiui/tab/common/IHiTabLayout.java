package com.hzp.hiui.tab.common;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

public interface IHiTabLayout<Tab extends ViewGroup, D> {
    Tab findTab(@NonNull D data);

    void addTabSelectedChangeListener(OnTabSelectedListener<D> listener);

    void defaultSelected(@NonNull D defaultInfo);

    void inflateInfo(@NonNull List<D> infoList);

    interface  OnTabSelectedListener<D>{
        void onTabSelectedChange(int index, @NonNull D prevInfo, @NonNull D nextInfo);
    }
}
