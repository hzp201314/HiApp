package com.hzp.hi.ui.tab.top;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.hzp.hi.library.log.HiLog;
import com.hzp.hi.library.util.HiDisplayUtil;
import com.hzp.hi.ui.tab.common.IHiTabLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tips:
 * 自动滚动，实现点击的位置能够自动滚动以展示前后2个
 */
public class HiTabTopLayout extends HorizontalScrollView implements IHiTabLayout<HiTabTop, HiTabTopInfo<?>> {
    private List<OnTabSelectedListener<HiTabTopInfo<?>>> tabSelectedChangeListeners = new ArrayList<>();
    private HiTabTopInfo<?> selectedInfo;
    private List<HiTabTopInfo<?>> infoList;


    public HiTabTopLayout(Context context) {
        this(context, null);
    }

    public HiTabTopLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiTabTopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 隐藏水平滚动条
        setVerticalScrollBarEnabled(false);
    }

    @Override
    public HiTabTop findTab(@NonNull HiTabTopInfo<?> info) {
        ViewGroup ll = getRootLayout(false);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof HiTabTop) {
                HiTabTop tab = (HiTabTop) child;
                if (tab.getHiTabInfo() == info) {
                    return tab;
                }
            }
        }
        return null;
    }

    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<HiTabTopInfo<?>> listener) {
        tabSelectedChangeListeners.add(listener);
    }

    @Override
    public void defaultSelected(@NonNull HiTabTopInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    @Override
    public void inflateInfo(@NonNull List<HiTabTopInfo<?>> infoList) {
        if (infoList.isEmpty()) {
            return;
        }
        this.infoList = infoList;
        LinearLayout linearLayout = getRootLayout(true);
        selectedInfo = null;
        //清除之前添加的HiTabTop listener，Tips：Java foreach remove问题
        Iterator<OnTabSelectedListener<HiTabTopInfo<?>>> iterator = tabSelectedChangeListeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof HiTabTop) {
                iterator.remove();
            }
        }
        for (int i = 0; i < infoList.size(); i++) {
            final HiTabTopInfo<?> info = infoList.get(i);
            HiTabTop tab = new HiTabTop(getContext());
            tabSelectedChangeListeners.add(tab);
            tab.setHiTabInfo(info);
            linearLayout.addView(tab);
            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelected(info);
                }
            });
        }
    }

    private LinearLayout getRootLayout(boolean clear) {
        LinearLayout rootView = (LinearLayout) getChildAt(0);
        if (rootView == null) {
            rootView = new LinearLayout(getContext());
            rootView.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            addView(rootView, layoutParams);
        } else if (clear) {
            rootView.removeAllViews();
        }
        return rootView;
    }

    private void onSelected(@NonNull HiTabTopInfo<?> nextInfo) {
        for (OnTabSelectedListener<HiTabTopInfo<?>> listener : tabSelectedChangeListeners) {
            listener.onTabSelectedChange(infoList.indexOf(nextInfo), selectedInfo, nextInfo);
        }
        this.selectedInfo = nextInfo;
        autoScroll(nextInfo);
    }

    int tabWith;

    /**
     * 自动滚动，实现点击的位置能够自动滚动以展示前后2个
     *
     * @param nextInfo 点击tab的info
     */
    private void autoScroll(HiTabTopInfo<?> nextInfo) {
        HiTabTop tabTop = findTab(nextInfo);
        if (tabTop == null) return;
        //索引位置
        int index = infoList.indexOf(nextInfo);
        //控件在屏幕坐标
        int[] loc = new int[2];
        //获取点击的控件在屏幕的位置
        tabTop.getLocationInWindow(loc);
        int scrollWidth;
        if (tabWith == 0) {
            //控件宽度
            tabWith = tabTop.getWidth();
        }
        //判断点击了屏幕左侧还是右侧
        if ((loc[0] + tabWith / 2) > HiDisplayUtil.getDisplayWidthInPx(getContext()) / 2) {
            //点击了右侧 判断tab右侧2个tab是否完全展示，并返回需要滚动的距离
            scrollWidth = rangeScrollWidth(index, 2);
        } else {
            //点击了左侧 判断tab左侧2个tab是否完全展示，并返回需要滚动的距离
            scrollWidth = rangeScrollWidth(index, -2);
        }
        //x轴滚动
        scrollTo(getScrollX() + scrollWidth, 0);
    }

    /**
     * 获取可滚动的范围
     * 判断tab左侧/右侧range个tab是否完全展示，并返回需要滚动的距离
     * @param index 从第几个tab开始
     * @param range 向前向后几个tab的范围
     * @return 可滚动的范围，需要滚动的距离
     */
    private int rangeScrollWidth(int index, int range) {
        //需要滚动的距离
        int scrollWidth = 0;
        for (int i = 0; i <= Math.abs(range); i++) {
            //下一个位置
            int next;
            if (range < 0) {
                //向左滑动下一个位置索引
                next = range + i + index;
            } else {
                //向右滑动下一个位置索引
                next = range - i + index;
            }
            if (next >= 0 && next < infoList.size()) {
                if (range < 0) {
                    //计算向左滑动可滚动的距离
                    scrollWidth -= scrollWidth(next, false);
                } else {
                    //计算向右滑动可滚动的距离
                    scrollWidth += scrollWidth(next, true);
                }
                HiLog.i("scrollWidth:"+scrollWidth);
            }
        }
        return scrollWidth;

    }

    /**
     * 指定位置的控件可滚动的距离
     *
     * @param index   指定位置的控件
     * @param toRight 是否是点击了屏幕右侧
     * @return 可滚动的距离
     */
    private int scrollWidth(int index, boolean toRight) {
        HiTabTop target = findTab(infoList.get(index));
        if (target == null) return 0;
        Rect rect = new Rect();
        //判断视图是否可见
        target.getLocalVisibleRect(rect);
        //TODO 不是很明白为什么这样计算可滚动距离
        HiLog.i("tabWith:"+tabWith,"rect.left:"+rect.left,"rect.right:"+rect.right);
        if (toRight) {//点击屏幕右侧
            if (rect.right > tabWith) {//right坐标大于控件的宽度时，说明完全没有显示
                return tabWith;
            } else {//显示部分，减去已显示的宽度
                return tabWith - rect.right;
            }
        } else {
            if (rect.left <= -tabWith) {//left坐标小于等于-控件的宽度，说明完全没有显示
                return tabWith;
            } else if (rect.left > 0) {//显示部分
                return rect.left;
            }
            return 0;
        }
    }

}
