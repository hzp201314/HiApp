package com.hzp.hiui.tab.common;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

public interface IHiTab<D> extends IHiTabLayout.OnTabSelectedListener<D> {
    void setHiTabInfo(@NonNull D data);

    /**
     * 动态修改某个item的大小
     * @param height
     */
    void resetHeight(@Px int height);
}
