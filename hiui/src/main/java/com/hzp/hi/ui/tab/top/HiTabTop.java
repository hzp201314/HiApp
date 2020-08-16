package com.hzp.hi.ui.tab.top;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzp.hi.ui.R;
import com.hzp.hi.ui.tab.common.IHiTab;

public class HiTabTop extends RelativeLayout implements IHiTab<HiTabTopInfo<?>> {

    private HiTabTopInfo<?> tabInfo;
    private ImageView tabImageView;
    private TextView tabNameView;
    private View indicator;

    public HiTabTop(Context context) {
        this(context, null);
    }

    public HiTabTop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiTabTop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.hi_tab_top, this);
        tabImageView = findViewById(R.id.iv_image);
        tabNameView = findViewById(R.id.tv_name);
        indicator=findViewById(R.id.tab_top_indicator);
    }


    @Override
    public void setHiTabInfo(@NonNull HiTabTopInfo<?> hiTabTopInfo) {
        this.tabInfo = hiTabTopInfo;
        //重新设置默认不选中，需要初始化
        inflateInfo(false, true);
    }

    public HiTabTopInfo<?> getHiTabInfo() {
        return tabInfo;
    }

    public ImageView getTabImageView() {
        return tabImageView;
    }

    public TextView getTabNameView() {
        return tabNameView;
    }

    /**
     * 改变某个tab的高度
     *
     * @param height
     */
    @Override
    public void resetHeight(int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        //隐藏底部name
        getTabNameView().setVisibility(GONE);
    }

    private void inflateInfo(boolean selected, boolean init) {
        if (tabInfo.tabType == HiTabTopInfo.TabType.TEXT) {//文本类型
            if (init) {//是否初始化
                tabNameView.setVisibility(VISIBLE);
                //隐藏图片
                tabImageView.setVisibility(GONE);
                if (!TextUtils.isEmpty(tabInfo.name)) {
                    tabNameView.setText(tabInfo.name);
                }
            }

            if (selected) {//选中
                indicator.setVisibility(VISIBLE);
                tabNameView.setTextColor(getTextColor(tabInfo.tintColor));
            } else {
                indicator.setVisibility(GONE);
                tabNameView.setTextColor(getTextColor(tabInfo.defaultColor));
            }
        } else if (tabInfo.tabType == HiTabTopInfo.TabType.BITMAP) {//BitMap类型
            if (init) {
                tabImageView.setVisibility(VISIBLE);
                tabNameView.setVisibility(GONE);
            }
            if (selected) {//选中
                indicator.setVisibility(VISIBLE);
                tabImageView.setImageBitmap(tabInfo.selectedBitmap);
            } else {
                indicator.setVisibility(GONE);
                tabImageView.setImageBitmap(tabInfo.defaultBitmap);
            }
        }
    }


    @Override
    public void onTabSelectedChange(int index, @Nullable HiTabTopInfo<?> prevInfo, @NonNull HiTabTopInfo<?> nextInfo) {
        //没有选择到的图标(选择的其他图标，直接return)，重复选中自己
        if (prevInfo != tabInfo && nextInfo != tabInfo || prevInfo == nextInfo) {
            return;
        }
        //选中被反选
        if (prevInfo == tabInfo) {//已选中的改为未选中状态
            inflateInfo(false, false);
        } else {//未选中的变为选中状态
            inflateInfo(true, false);
        }
    }

    /**
     * 获取颜色
     * @param color
     * @return
     */
    @ColorInt
    private int getTextColor(Object color) {
        if (color instanceof String) {
            return Color.parseColor((String) color);
        } else {
            return (int) color;
        }
    }
}
