package com.hzp.hi.ui.banner.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

/**
 * 实现了自动翻页的ViewPager
 */
public class HiViewPager extends ViewPager {
    /*滚动时间间隔*/
    private int mIntervalTime;
    /**
     * 是否开启自动轮播
     */
    private boolean mAutoPlay = true;
    /*是否调用onLayout()方法*/
    private boolean isLayout;
    /*借助handler定时发送消息实现自动播放*/
    private Handler mHandler = new Handler();
    /*handler定时发送消息执行切换下一个轮播图任务*/
    private Runnable mRunnable = new Runnable() {

        public void run() {
            //切换下一个轮播图
            next();
            mHandler.postDelayed(this, mIntervalTime);//延时一定时间执行下一次
        }

    };


    public HiViewPager(@NonNull Context context) {
        super(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            //用户松手或者取消事件，开始自动播放。否则停止自动播放
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                start();
                break;
            default:
                stop();
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        isLayout = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isLayout && getAdapter() != null && getAdapter().getCount() > 0) {
            try {
                //fix 使用RecyclerView + ViewPager bug https://blog.csdn.net/u011002668/article/details/72884893
                //反射将mFirstLayout设置为false，解决滚动bug
                // 现象：RecyclerView滚动上去，直至ViewPager看不见，再滚动下来，ViewPager下一次没有切换动画
                // 原因：ViewPager里有一个私有变量mFirstLayout，表示是不是第一次显示布局，
                //      如果是true，则使用无动画的方式显示当前item，
                //      如果是false，则使用动画的方式显示当前item
                /**
                 void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
                 .                   ...
                 if (mFirstLayout) {
                 //无动画
                 // We don't have any idea how big we are yet and shouldn't have any pages either.
                 // Just set things up and let the pending layout handle things.
                 mCurItem = item;
                 if (dispatchSelected) {
                 dispatchOnPageSelected(item);
                 }
                 requestLayout();
                 } else {
                 //有动画
                 populate(item);
                 scrollToItem(item, smoothScroll, velocity, dispatchSelected);
                 }
                 }
                 */
                // 当ViewPager滚动上去后，因为RecyclerView的回收机制,ViewPager会走onDetachedFromWindow(),
                // 当再次滚动下来时，ViewPager会走onAttachedToWindow(),
                // 问题就出在onAttachedToWindow里面会将mFirstLayout设置为true
                /**
                 @Override protected void onAttachedToWindow() {
                 super.onAttachedToWindow();
                 mFirstLayout = true;
                 }
                 */
                //解决：反射将mFirstLayout设置为false，
                Field mScroller = ViewPager.class.getDeclaredField("mFirstLayout");
                mScroller.setAccessible(true);
                mScroller.set(this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        //fix 使用RecyclerView + ViewPager bug
        // ViewPager滚动到一半的时候RecyclerView发生滚动，
        // 此时如果ViewPager被回收会调用ViewPager.onDetachedFromWindow()方法，
        // 方法内部调用mScroller.abortAnimation();终止动画，动画被卡住。
        if (((Activity) getContext()).isFinishing()) {
            super.onDetachedFromWindow();
        }
        stop();
    }

    /**
     * 设置ViewPager的滚动速度
     *
     * @param duration page切换的时间长度
     */
    public void setScrollDuration(int duration) {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            scrollerField.set(this,new HiBannerScroller(getContext(),duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置页面停留时间
     *
     * @param intervalTime 停留时间单位毫秒
     */
    public void setIntervalTime(int intervalTime) {
        this.mIntervalTime = intervalTime;
    }

    /**
     * 设置自动播放
     *
     * @param autoPlay
     */
    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
        if (!mAutoPlay) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    /*开始自动播放*/
    public void start() {
        //移除handler的消息
        mHandler.removeCallbacksAndMessages(null);
        //判断是否设置自动播放
        if (mAutoPlay) {
            mHandler.postDelayed(mRunnable, mIntervalTime);
        }
    }

    /*停止自动播放*/
    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 设置下一个要显示的item，并返回item的pos
     *
     * @return 下一个要显示item的pos
     */
    private int next() {
        int nextPosition = -1;

        //没有adapter或者adapter的item数量<1停止自动播放
        if (getAdapter() == null || getAdapter().getCount() <= 1) {
            stop();
            return nextPosition;
        }
        //下一个item的位置
        nextPosition = getCurrentItem() + 1;
        //下一个索引大于adapter的view的最大数量时重新开始
        if (nextPosition >= getAdapter().getCount()) {
            //获取第一个item索引
            nextPosition = ((HiBannerAdapter) getAdapter()).getFirstItem();
        }
        setCurrentItem(nextPosition, true);
        return nextPosition;
    }


}
