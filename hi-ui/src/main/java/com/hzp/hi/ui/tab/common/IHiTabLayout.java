package com.hzp.hi.ui.tab.common;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * 提供对外通用接口方法
 * @param <Tab> 任意tab
 * @param <D> 对应数据
 */
public interface IHiTabLayout<Tab extends ViewGroup,D> {

    /*根据数据查找对应tab*/
    Tab findTab(@NonNull D data);

    /*添加选中监听器*/
    void addTabSelectedChangeListener(OnTabSelectedListener<D> listener);

    /*设置默认选中*/
    void defaultSelected(@NonNull D defaultInfo);

    /*初始化数据*/
    void inflateInfo(@NonNull List<D> infoList);

    /*监听选中回调*/
    interface OnTabSelectedListener<D> {
        /**
         * 选中回调
         * @param index 选中索引
         * @param prevInfo 上一个选中tab的数据
         * @param nextInfo 下一个选中tab的数据(本次?)
         */
        void onTabSelectedChange(int index, @Nullable D prevInfo, @NonNull D nextInfo);
    }
}
