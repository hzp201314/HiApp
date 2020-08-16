package com.hzp.hi.ui.tab.bottom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzp.hi.ui.R;
import com.hzp.hi.ui.tab.common.IHiTab;

public class HiTabBottom extends RelativeLayout implements IHiTab<HiTabBottomInfo<?>> {

    private HiTabBottomInfo<?> tabInfo;
    private ImageView tabImageView;
    private TextView tabIconView;
    private TextView tabNameView;

    public HiTabBottom(Context context) {
        this(context, null);
    }

    public HiTabBottom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiTabBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.hi_tab_bottom, this);
        tabImageView = findViewById(R.id.iv_image);
        tabIconView = findViewById(R.id.tv_icon);
        tabNameView = findViewById(R.id.tv_name);
    }


    @Override
    public void setHiTabInfo(@NonNull HiTabBottomInfo<?> hiTabBottomInfo) {
        this.tabInfo = hiTabBottomInfo;
        //重新设置默认不选中，需要初始化
        inflateInfo(false, true);
    }

    public HiTabBottomInfo<?> getHiTabInfo() {
        return tabInfo;
    }

    public ImageView getTabImageView() {
        return tabImageView;
    }

    public TextView getTabIconView() {
        return tabIconView;
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
        if (tabInfo.tabType == HiTabBottomInfo.TabType.ICON) {//图标Icon类型
            if (init) {//是否初始化
                //隐藏图片
                tabImageView.setVisibility(GONE);
                tabIconView.setVisibility(VISIBLE);
                //创建加载字体
                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), tabInfo.iconFont);
                //设置字体
                tabIconView.setTypeface(typeface);
                if (!TextUtils.isEmpty(tabInfo.name)) {
                    tabNameView.setText(tabInfo.name);
                }
            }

            if (selected) {//选中
                tabIconView.setText(TextUtils.isEmpty(tabInfo.selectIconName) ? tabInfo.defaultIconName : tabInfo.selectIconName);
                tabIconView.setTextColor(getTextColor(tabInfo.tintColor));
                tabNameView.setTextColor(getTextColor(tabInfo.tintColor));
            } else {
                tabIconView.setText(tabInfo.defaultIconName);
                tabIconView.setTextColor(getTextColor(tabInfo.defaultColor));
                tabNameView.setTextColor(getTextColor(tabInfo.defaultColor));
            }
        } else if (tabInfo.tabType == HiTabBottomInfo.TabType.BITMAP) {//BitMap类型
            if (init) {
                tabImageView.setVisibility(VISIBLE);
                //隐藏Icon
                tabIconView.setVisibility(GONE);
                if (!TextUtils.isEmpty(tabInfo.name)) {
                    tabNameView.setText(tabInfo.name);
                }
            }
            if (selected) {//选中
                tabImageView.setImageBitmap(tabInfo.selectedBitmap);
            } else {
                tabImageView.setImageBitmap(tabInfo.defaultBitmap);
            }
        }
    }


    @Override
    public void onTabSelectedChange(int index, @Nullable HiTabBottomInfo<?> prevInfo, @NonNull HiTabBottomInfo<?> nextInfo) {
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
