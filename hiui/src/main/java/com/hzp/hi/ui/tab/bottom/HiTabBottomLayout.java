package com.hzp.hi.ui.tab.bottom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hzp.hi.library.util.HiDisplayUtil;
import com.hzp.hi.library.util.HiViewUtil;
import com.hzp.hi.ui.R;
import com.hzp.hi.ui.tab.common.IHiTabLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 底部tab布局
 */
public class HiTabBottomLayout extends FrameLayout implements IHiTabLayout<HiTabBottom, HiTabBottomInfo<?>> {
    //Tab选中监听器集合
    private List<OnTabSelectedListener<HiTabBottomInfo<?>>> tabSelectedChangeListeners = new ArrayList<>();
    //保存当前选中的数据
    private HiTabBottomInfo<?> selectedInfo;
    //TabBottom透明度
    private float bottomAlpha = 1f;
    //TabBottom高度
    private static float tabBottomHeight = 50;
    //TabBottom的头部线条高度
    private float bottomLineHeight = 0.5f;
    //TabBottom的头部线条颜色
    private String bottomLineColor = "#dfe0e1";
    //TabBottom数据
    private List<HiTabBottomInfo<?>> infoList;

    public HiTabBottomLayout(@NonNull Context context) {
        this(context, null);
    }

    public HiTabBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiTabBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*查找TabBottom*/
    @Nullable
    @Override
    public HiTabBottom findTab(@NonNull HiTabBottomInfo<?> info) {
        ViewGroup ll = findViewWithTag(TAG_TAB_BOTTOM);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof HiTabBottom) {
                HiTabBottom tab = (HiTabBottom) child;
                if (tab.getHiTabInfo() == info) {
                    return tab;
                }
            }
        }
        return null;
    }

    /*添加监听器*/
    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<HiTabBottomInfo<?>> listener) {
        tabSelectedChangeListeners.add(listener);
    }

    /*默认选中*/
    @Override
    public void defaultSelected(@NonNull HiTabBottomInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    private static final String TAG_TAB_BOTTOM = "TAG_TAB_BOTTOM";

    /**
     * 填充数据
     *
     * @param infoList
     */
    @Override
    public void inflateInfo(@NonNull List<HiTabBottomInfo<?>> infoList) {
        if (infoList.isEmpty()) {
            return;
        }

        this.infoList = infoList;
        //移除之前已经添加的View
        for (int i = getChildCount() - 1; i > 0; i--) {
            removeViewAt(i);
        }
        selectedInfo = null;
        //添加背景
        addBackground();
        //清除之前添加的HiTabBottom listener，Tips：Java foreach remove问题
        Iterator<OnTabSelectedListener<HiTabBottomInfo<?>>> iterator = tabSelectedChangeListeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof HiTabBottom) {
                iterator.remove();
            }
        }
        FrameLayout ll = new FrameLayout(getContext());
        ll.setTag(TAG_TAB_BOTTOM);
        //高度
        int height = HiDisplayUtil.dp2px(tabBottomHeight, getResources());
        //每个item宽度
        int width = HiDisplayUtil.getDisplayWidthInPx(getContext()) / infoList.size();
        for (int i = 0; i < infoList.size(); i++) {
            final HiTabBottomInfo<?> info = infoList.get(i);
            //Tips：为何不用LinearLayout：当动态改变child大小后Gravity.BOTTOM会失效
            LayoutParams params = new LayoutParams(width, height);
            params.gravity = Gravity.BOTTOM;
            params.leftMargin = i * width;

            //实例化HiTabBottom
            HiTabBottom tabBottom = new HiTabBottom(getContext());
            tabSelectedChangeListeners.add(tabBottom);
            tabBottom.setHiTabInfo(info);
            ll.addView(tabBottom, params);
            tabBottom.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelected(info);
                }
            });
        }
        LayoutParams flParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        flParams.gravity = Gravity.BOTTOM;
        //添加底部线
        addBottomLine();
        addView(ll, flParams);

        fixContentView();
    }


    /*设置tab透明度*/
    public void setTabAlpha(float alpha) {
        this.bottomAlpha = alpha;
    }

    /*设置tab高度*/
    public static void setTabHeight(float tabHeight) {
        HiTabBottomLayout.tabBottomHeight = tabHeight;
    }

    /*设置底部线高度*/
    public void setBottomLineHeight(float bottomLineHeight) {
        this.bottomLineHeight = bottomLineHeight;
    }

    /*设置底部线颜色*/
    public void setBottomLineColor(String bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
    }

    /*构造添加底部线*/
    private void addBottomLine() {
        View bottomLine = new View(getContext());
        bottomLine.setBackgroundColor(Color.parseColor(bottomLineColor));

        LayoutParams bottomLineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                HiDisplayUtil.dp2px(bottomLineHeight, getResources()));
        bottomLineParams.gravity = Gravity.BOTTOM;
        bottomLineParams.bottomMargin = HiDisplayUtil.dp2px(tabBottomHeight - bottomLineHeight, getResources());
        addView(bottomLine, bottomLineParams);
        bottomLine.setAlpha(bottomAlpha);
    }

    /*选中*/
    private void onSelected(@NonNull HiTabBottomInfo<?> nextInfo) {
        for (OnTabSelectedListener<HiTabBottomInfo<?>> listener : tabSelectedChangeListeners) {
            listener.onTabSelectedChange(infoList.indexOf(nextInfo), selectedInfo, nextInfo);
        }
        this.selectedInfo = nextInfo;
    }

    /**
     * 添加背景
     */
    private void addBackground() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.hi_bottom_layout_bg, null);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HiDisplayUtil.dp2px(tabBottomHeight, getResources()));
        params.gravity = Gravity.BOTTOM;
        addView(view, params);
        view.setAlpha(bottomAlpha);
    }

    /**
     * 修复内容区域的底部Padding
     */
    private void fixContentView() {
        if (!(getChildAt(0) instanceof ViewGroup)) {
            //单节点
            return;
        }

        ViewGroup rootView = (ViewGroup) getChildAt(0);
        ViewGroup targetView = HiViewUtil.findTypeView(rootView, RecyclerView.class);
        if (targetView == null) {
            targetView = HiViewUtil.findTypeView(rootView, ScrollView.class);
        }
        if (targetView == null) {
            targetView = HiViewUtil.findTypeView(rootView, AbsListView.class);
        }
        if (targetView != null) {
            targetView.setPadding(0, 0, 0, HiDisplayUtil.dp2px(tabBottomHeight, getResources()));
            targetView.setClipToPadding(false);
        }
    }

//    public static void clipBottomPadding(ViewGroup targetView) {
//        if (targetView != null) {
//            targetView.setPadding(0, 0, 0, HiDisplayUtil.dp2px(tabBottomHeight));
//            targetView.setClipToPadding(false);
//        }
//    }
}
