package com.hzp.hi.ui.banner.core;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class HiBannerAdapter extends PagerAdapter {
    private Context mContext;
    //缓存banner view
    private SparseArray<HiBannerViewHolder> mCachedViews = new SparseArray<>();
    /*banner点击监听器*/
    private IHiBanner.OnBannerClickListener mBannerClickListener;
    private IBindAdapter mBindAdapter;
    private List<? extends HiBannerMo> models;
    /**
     * 是否开启自动轮
     */
    private boolean mAutoPlay = true;
    /**
     * 非自动轮播状态下是否可以循环切换
     */
    private boolean mLoop = false;
    private int mLayoutResId = -1;

    public HiBannerAdapter(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 设置数据
     *
     * @param models
     */
    public void setBannerData(@NonNull List<? extends HiBannerMo> models) {
        this.models = models;
        //初始化数据
        initCachedView();
        notifyDataSetChanged();
    }

    /**
     * 设置banner点击监听器
     *
     * @param onBannerClickListener
     */
    public void setOnBannerClickListener(IHiBanner.OnBannerClickListener onBannerClickListener) {
        this.mBannerClickListener = onBannerClickListener;
    }

    /**
     * 设置Adapter
     *
     * @param bindAdapter
     */
    public void setBindAdapter(IBindAdapter bindAdapter) {
        this.mBindAdapter = bindAdapter;
    }

    /**
     * 设置是否自动播放
     *
     * @param autoPlay
     */
    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
    }

    /**
     * 设置无限循环
     *
     * @param loop
     */
    public void setLoop(boolean loop) {
        this.mLoop = loop;
    }

    /**
     * 设置资源布局
     *
     * @param layoutResId
     */
    public void setLayoutResId(@LayoutRes int layoutResId) {
        this.mLayoutResId = layoutResId;
    }

    /**
     * 获取Banner页面数量
     *
     * @return
     */
    public int getRealCount() {
        return models == null ? 0 : models.size();
    }

    @Override
    public int getCount() {
        //无限轮播关键点
        return mAutoPlay ? Integer.MAX_VALUE : (mLoop ? Integer.MAX_VALUE : getRealCount());
    }

    /**
     * 获取初次展示的item位置
     *
     * @return
     */
    public int getFirstItem() {
        return Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % getRealCount();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /*实例化item*/
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int realPosition = position;
        if (getRealCount() > 0) {
            realPosition = position % getRealCount();
        }
        HiBannerViewHolder viewHolder = mCachedViews.get(realPosition);
        //viewHolder.rootView已经被添加进去，需移除
        if (container.equals(viewHolder.rootView.getParent())) {
            container.removeView(viewHolder.rootView);
        }

        //数据绑定
        onBind(viewHolder, models.get(realPosition), realPosition);
        if (viewHolder.rootView.getParent() != null) {
            ((ViewGroup) viewHolder.rootView.getParent()).removeView(viewHolder.rootView);
        }
        container.addView(viewHolder.rootView);

        return viewHolder.rootView;
    }

    /*复写destroyItem，保证mCachedViews不被销毁*/
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        //让item每次都会刷新
        return POSITION_NONE;
    }

    //数据绑定
    protected void onBind(@NonNull final HiBannerViewHolder viewHolder,
                          @NonNull final HiBannerMo bannerMo,
                          final int position) {
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBannerClickListener != null) {
                    mBannerClickListener.onBannerClick(viewHolder, bannerMo, position);
                }
            }
        });
        if (mBindAdapter != null) {
            // 业务层数据绑定
            mBindAdapter.onBind(viewHolder, bannerMo, position);
        }

    }

    /*初始化*/
    private void initCachedView() {
        mCachedViews = new SparseArray<>();
        for (int i = 0; i < models.size(); i++) {
            HiBannerViewHolder viewHolder = new HiBannerViewHolder(createView(LayoutInflater.from(mContext), null));
            mCachedViews.put(i, viewHolder);
        }
    }

    /*创建rootView*/
    private View createView(LayoutInflater layoutInflater, ViewGroup parent) {
        if (mLayoutResId == -1) {
            throw new IllegalArgumentException("you must be set setLayoutResId first");
        }

        return layoutInflater.inflate(mLayoutResId, parent, false);
    }


    public static class HiBannerViewHolder {
        private SparseArray<View> viewHolderSparseArr;
        View rootView;

        public HiBannerViewHolder(View rootView) {
            this.rootView = rootView;
        }

        public View getRootView() {
            return rootView;
        }

        /*工具方法，通过id找到view进行数据数据绑定*/
        public <V extends View> V findViewById(int id) {
            if (!(rootView instanceof ViewGroup)) {
                //单节点无子View直接返回
                return (V) rootView;
            }

            //懒加载
            if (this.viewHolderSparseArr == null) {
                this.viewHolderSparseArr = new SparseArray<>(1);
            }

            //找到就返回，没找到就重新实例化并放入缓存
            V childView = (V) viewHolderSparseArr.get(id);
            if (childView == null) {
                childView = rootView.findViewById(id);
                this.viewHolderSparseArr.put(id, childView);
            }

            return childView;
        }

    }
}
