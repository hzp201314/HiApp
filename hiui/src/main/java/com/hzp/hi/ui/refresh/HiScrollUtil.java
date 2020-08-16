package com.hzp.hi.ui.refresh;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hzp.hi.library.log.HiLog;

public class HiScrollUtil {
    /**
     * 判断child列表是否发生了滚动
     *
     * @param child
     * @return true 发生了滚动
     */
    public static boolean childScrolled(@NonNull View child) {
        if (child instanceof AdapterView) {
            AdapterView adapterView = (AdapterView) child;
            if (adapterView.getFirstVisiblePosition() != 0
                    || adapterView.getFirstVisiblePosition() == 0 && adapterView.getChildAt(0) != null
                    && adapterView.getChildAt(0).getTop() < 0) {
                return true;
            }
        } else if (child.getScrollY() > 0) {
            return true;
        }
        if (child instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) child;
            View view = recyclerView.getChildAt(0);
            int firstPosition = recyclerView.getChildAdapterPosition(view);
            HiLog.d("----:top", view.getTop() + "");
            return firstPosition != 0 || view.getTop() != 0;
        }
        return false;
    }

    /**
     * 查找可以滚动的child
     * （尽可能找到可以滚动的视图，并不能一定能找到，找不到的时候返回child index=1的视图）
     *
     * @return 可以滚动的child
     */
    public static View findScrollableChild(@NonNull ViewGroup viewGroup) {
        //索引0 为自定义的header
        View child = viewGroup.getChildAt(1);
        if (child instanceof RecyclerView || child instanceof AdapterView) {
            return child;
        }

        if (child instanceof ViewGroup) {//往下多找一层
            View tempChild = ((ViewGroup) child).getChildAt(0);
            if (tempChild instanceof RecyclerView || tempChild instanceof AdapterView) {
                child = tempChild;
            }
        }
        return child;
    }
}
