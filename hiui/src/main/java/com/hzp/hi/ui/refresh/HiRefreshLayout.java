package com.hzp.hi.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzp.hi.library.log.HiLog;
import com.hzp.hi.ui.refresh.HiOverView.*;

/**
 * 下拉刷新View
 */
public class HiRefreshLayout extends FrameLayout implements HiRefresh {
    private static final String TAG = HiRefreshLayout.class.getSimpleName();
    private HiRefreshState mState;
    private GestureDetector mGestureDetector;//手势
    private AutoScroller mAutoScroller;
    private HiRefreshListener mHiRefreshListener;
    protected HiOverView mHiOverView;//下拉刷新头部
    private int mLastY;//最后下拉位置坐标
    //刷新时是否禁止滚动
    private boolean disableRefreshScroll;

    public HiRefreshLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public HiRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HiRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), hiGestureDetector);
        mAutoScroller = new AutoScroller();
    }

    @Override
    public void setDisableRefreshScroll(boolean disableRefreshScroll) {
        this.disableRefreshScroll = disableRefreshScroll;
    }

    @Override
    public void refreshFinished() {
        final View head = getChildAt(0);
        HiLog.i(this.getClass().getSimpleName(), "refreshFinished head-bottom:" + head.getBottom());
        mHiOverView.onFinish();
        mHiOverView.setState(HiRefreshState.STATE_INIT);
        final int bottom = head.getBottom();
        if (bottom > 0) {
            //下over pull 200，height 100
            //  bottom  =100 ,height 100
            recover(bottom);
        }
        mState = HiRefreshState.STATE_INIT;
    }

    @Override
    public void setRefreshListener(HiRefreshListener hiRefreshListener) {
        this.mHiRefreshListener = hiRefreshListener;
    }

    @Override
    public void setRefreshOverView(HiOverView hiOverView) {
        if (this.mHiOverView != null) {
            removeView(mHiOverView);
        }
        this.mHiOverView = hiOverView;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mHiOverView, 0, params);
    }

    /*手势监听器*/
    HiGestureDetector hiGestureDetector = new HiGestureDetector() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) > Math.abs(distanceY) || mHiRefreshListener != null && !mHiRefreshListener.enableRefresh()) {
                //横向滑动，或刷新被禁止 则不处理
                return false;
            }
            if (disableRefreshScroll && mState == HiRefreshState.STATE_REFRESH) {//刷新时是否禁止滑动，如果禁止返回true
                return true;
            }

            //自己添加的header
            View head = getChildAt(0);
            View child = HiScrollUtil.findScrollableChild(HiRefreshLayout.this);
            if (HiScrollUtil.childScrolled(child)) {
                //如果列表发生了滚动则不处理
                return false;
            }
            //没有刷新或没有达到可以刷新的距离，且头部已经划出或下拉
            if ((mState != HiRefreshState.STATE_REFRESH || head.getBottom() <= mHiOverView.mPullRefreshHeight) && (head.getBottom() > 0 || distanceY < 0.0F)) {
                //还在滑动中
                if (mState != HiRefreshState.STATE_OVER_RELEASE) {
                    int speed;
                    //阻尼计算速度
                    if (child.getTop() < mHiOverView.mPullRefreshHeight) {//还没有滑出设置的高度
                        speed = (int) (mLastY / mHiOverView.minDamp);
                    } else {//滑出设置的高度
                        speed = (int) (mLastY / mHiOverView.maxDamp);
                    }
                    //如果是正在刷新状态，则不允许在滑动的时候改变状态
                    boolean bool = moveDown(speed, true);
                    mLastY = (int) (-distanceY);
                    return bool;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //事件分发处理
        if (!mAutoScroller.isFinished()) {
            return false;
        }

        View head = getChildAt(0);
        if (ev.getAction() == MotionEvent.ACTION_UP
                || ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) {//松开手
            if (head.getBottom() > 0) {//已经拉下来
                if (mState != HiRefreshState.STATE_REFRESH) {//非正在刷新
                    recover(head.getBottom());
                    return false;
                }
            }
            mLastY = 0;
        }
        //手势处理
        boolean consumed = mGestureDetector.onTouchEvent(ev);
        HiLog.i(TAG, "gesture consumed：" + consumed);
        //手势消费事件 或者 不为初始化状态并且不为刷新状态
        //并且 下拉状态
        if ((consumed || (mState != HiRefreshState.STATE_INIT && mState != HiRefreshState.STATE_REFRESH))
                && head.getBottom() != 0) {
            ev.setAction(MotionEvent.ACTION_CANCEL);//让父类接收不到真实的事件
            return super.dispatchTouchEvent(ev);
        }
        if (consumed) {//消费事件
            return true;
        } else {//让父类处理事件
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //定义head和child的排列位置
        View head = getChildAt(0);
        View child = getChildAt(1);
        if (head != null && child != null) {
            HiLog.i(TAG, "onLayout head-height:" + head.getMeasuredHeight());
            int childTop = child.getTop();
            if (mState == HiRefreshState.STATE_REFRESH) {
                head.layout(0, mHiOverView.mPullRefreshHeight - head.getMeasuredHeight(), right, mHiOverView.mPullRefreshHeight);
                child.layout(0, mHiOverView.mPullRefreshHeight, right, mHiOverView.mPullRefreshHeight + child.getMeasuredHeight());
            } else {
                //left,top,right,bottom
                head.layout(0, childTop - head.getMeasuredHeight(), right, childTop);
                child.layout(0, childTop, right, childTop + child.getMeasuredHeight());
            }

            View other;
            for (int i = 2; i < getChildCount(); ++i) {
                other = getChildAt(i);
                other.layout(0, top, right, bottom);
            }
            HiLog.i(TAG, "onLayout head-bottom:" + head.getBottom());
        }
    }

    /**
     * 恢复
     *
     * @param dis 恢复距离
     */
    private void recover(int dis) {//dis =200  200-100
        if (mHiRefreshListener != null && dis > mHiOverView.mPullRefreshHeight) {
            //滚动到指定位置dis-mHiOverView.mPullRefreshHeight
            mAutoScroller.recover(dis - mHiOverView.mPullRefreshHeight);
            mState = HiRefreshState.STATE_OVER_RELEASE;//松开收状态
        } else {
            mAutoScroller.recover(dis);
        }

    }

    /**
     * 根据偏移量移动header与child
     *
     * @param offsetY 偏移量
     * @param nonAuto 是否非自动滚动触发
     * @return
     */
    private boolean moveDown(int offsetY, boolean nonAuto) {
        HiLog.i("111", "changeState:" + nonAuto);
        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop() + offsetY;

        HiLog.i("-----", "moveDown head-bottom:" + head.getBottom() + ",child.getTop():"
                + child.getTop() + ",offsetY:" + offsetY);
        if (childTop <= 0) {//异常情况的补充
            HiLog.i(TAG, "childTop<=0,mState" + mState);
            offsetY = -child.getTop();
            //移动head与child的位置，到原始位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (mState != HiRefreshState.STATE_REFRESH) {
                mState = HiRefreshState.STATE_INIT;
            }
        } else if (mState == HiRefreshState.STATE_REFRESH && childTop > mHiOverView.mPullRefreshHeight) {
            //如果正在下拉刷新中，禁止继续下拉
            return false;
        } else if (childTop <= mHiOverView.mPullRefreshHeight) {//还没超出设定的刷新距离
            if (mHiOverView.getState() != HiRefreshState.STATE_VISIBLE && nonAuto) {//头部开始显示
                mHiOverView.onVisible();
                mHiOverView.setState(HiRefreshState.STATE_VISIBLE);
                mState = HiRefreshState.STATE_VISIBLE;
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (childTop == mHiOverView.mPullRefreshHeight && mState == HiRefreshState.STATE_OVER_RELEASE) {//下拉刷新临界点
                HiLog.i(TAG, "refresh，childTop：" + childTop);
                //刷新
                refresh();
            }
        } else {
            if (mHiOverView.getState() != HiRefreshState.STATE_OVER && nonAuto) {
                //超出刷新位置
                mHiOverView.onOver();
                mHiOverView.setState(HiRefreshState.STATE_OVER);
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
        }
        if (mHiOverView != null) {
            mHiOverView.onScroll(head.getBottom(), mHiOverView.mPullRefreshHeight);
        }
        return true;
    }

    /**
     * 刷新
     */
    private void refresh() {
        if (mHiRefreshListener != null) {
            mState = HiRefreshState.STATE_REFRESH;
            mHiOverView.onRefresh();
            mHiOverView.setState(HiRefreshState.STATE_REFRESH);
            mHiRefreshListener.onRefresh();
        }
    }

    /**
     * 自动滚动
     * <p>
     * 借助Scroller实现视图的自动滚动
     * https://juejin.im/post/5c7f4f0351882562ed516ab6
     */
    private class AutoScroller implements Runnable {
        private Scroller mScroller;//滚动器
        private int mLastY;//记录最后一次滚动的位置
        private boolean mIsFinished;//是否滚动结束

        AutoScroller() {
            mScroller = new Scroller(getContext(), new LinearInterpolator());
            mIsFinished = true;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {//还未滚动完成
                moveDown(mLastY - mScroller.getCurrY(), false);
                mLastY = mScroller.getCurrY();
                post(this);
            } else {
                //移除任务
                removeCallbacks(this);
                mIsFinished = true;
            }
        }

        /**
         * 触发滚动
         *
         * @param dis 滚动距离
         */
        void recover(int dis) {
            if (dis <= 0) {
                return;
            }
            //移除已经存在的滚动任务
            removeCallbacks(this);
            mLastY = 0;
            mIsFinished = false;
            //开始滚动
            mScroller.startScroll(0, 0, 0, dis, 300);
            //执行runnable
            post(this);
        }

        /**
         * 给外界获取是否滚动结束
         *
         * @return true：滚动结束   false：滚动未结束
         */
        boolean isFinished() {
            return mIsFinished;
        }
    }
}
