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
import com.hzp.hi.ui.refresh.HiOverView.HiRefreshState;

/**
 * 下拉刷新View 刷新视图整个页面最外层视图容器
 * TODO:bug：双指无空隙的下拉会造成下拉到一定位置后卡住不再回弹
 */
public class HiRefreshLayout extends FrameLayout implements HiRefresh {
    private static final String TAG = HiRefreshLayout.class.getSimpleName();
    //状态
    private HiRefreshState mState;
    //手势
    private GestureDetector mGestureDetector;
    //滚动
    private AutoScroller mAutoScroller;
    //刷新监听器
    private HiRefresh.HiRefreshListener mHiRefreshListener;
    //刷新视图
    protected HiOverView mHiOverView;
    //最终下拉Y轴坐标
    private int mLastY;
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
        //没有在最初的位置，恢复
        if (bottom > 0) {
            //先下拉，下over pull 200，height 100
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

    HiGestureDetector hiGestureDetector = new HiGestureDetector() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) > Math.abs(distanceY) || mHiRefreshListener != null && !mHiRefreshListener.enableRefresh()) {
                //横向滑动，或刷新被禁止则不处理
                return false;
            }

            if (disableRefreshScroll && mState == HiRefreshState.STATE_REFRESH) {
                //刷新时是否禁止滑动
                return true;
            }

            View head = getChildAt(0);
            //查找可以滚动的view
            View child = HiScrollUtil.findScrollableChild(HiRefreshLayout.this);
            //判断child是否发生了滚动
            if (HiScrollUtil.childScrolled(child)) {
                //如果列表发生了滚动则不处理
                return false;
            }
            //没有刷新或head没有达到可以刷新的距离，且头部已经划出或下拉
            if ((mState != HiRefreshState.STATE_REFRESH || head.getBottom() <= mHiOverView.mPullRefreshHeight)
                    && (head.getBottom() > 0 || distanceY <= 0.0F)) {
                //还在滑动中
                if (mState != HiRefreshState.STATE_OVER_RELEASE) {
                    //计算滑动速度
                    int speed;
                    //根据阻尼计算速度
                    if (child.getTop() < mHiOverView.mPullRefreshHeight) {
                        speed = (int) (mLastY / mHiOverView.minDamp);
                    } else {
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


    /**
     * 事件分发处理
     * 根据松手下拉位置决定是回弹回去还是触发刷新
     *
     * @param ev 事件
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mAutoScroller.isFinished()) {
            return false;
        }
        //获取headView
        View head = getChildAt(0);
        //松开手
        if (ev.getAction() == MotionEvent.ACTION_UP
                || ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) {
            //head已经被用户拉下来
            if (head.getBottom() > 0) {
                //非正在刷新 恢复回弹或者触发刷新
                if (mState != HiRefreshState.STATE_REFRESH) {
                    //恢复回弹或者触发刷新
                    recover(head.getBottom());
                    return false;
                }
            }
            mLastY = 0;
        }
        boolean consumed = mGestureDetector.onTouchEvent(ev);
        HiLog.i(TAG, "gesture consumed：" + consumed);
        // （手势已经消费 或者 不为初始态不为刷新态） 并且 head被拉下来
        if ((consumed || (mState != HiRefreshState.STATE_INIT && mState != HiRefreshState.STATE_REFRESH)) && head.getBottom() != 0) {
            //让父类接收不到真实的事件
            ev.setAction(MotionEvent.ACTION_CANCEL);
            return super.dispatchTouchEvent(ev);
        }
        if (consumed) {
            //消费事件
            return true;
        } else {
            //父类处理
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
            //正在刷新
            if (mState == HiRefreshState.STATE_REFRESH) {
                //
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
     * 用户手动下拉造成的滚动
     * 根据偏移量移动header与child
     *
     * @param offsetY 偏移量
     * @param nonAuto 是否非自动滚动触发
     * @return true：消费事件  false：没有消费事件
     */
    private boolean moveDown(int offsetY, boolean nonAuto) {
        HiLog.i("111", "changeState:" + nonAuto);
        //获取head与child
        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop() + offsetY;
        HiLog.i("-----", "moveDown head-bottom:" + head.getBottom() + ",child.getTop():" + child.getTop() + ",offsetY:" + offsetY);
        if (childTop <= 0) {//异常情况的补充
            HiLog.i(TAG, "childTop<=0,mState" + mState);
            //重置offsetY
            offsetY = -child.getTop();
            //移动head与child的位置，到原始位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (mState != HiRefreshState.STATE_REFRESH) {
                //状态不为刷新状态就重置为初始态
                mState = HiRefreshState.STATE_INIT;
            }
        } else if (mState == HiRefreshState.STATE_REFRESH && childTop > mHiOverView.mPullRefreshHeight) {
            //如果正在下拉刷新中，禁止继续下拉
            return false;
        } else if (childTop <= mHiOverView.mPullRefreshHeight) {
            //还没超出设定的刷新距离
            if (mHiOverView.getState() != HiRefreshState.STATE_VISIBLE && nonAuto) {
                //头部开始显示
                //设置mHiOverView可见
                mHiOverView.onVisible();
                //状态改为可见STATE_VISIBLE
                mHiOverView.setState(HiRefreshState.STATE_VISIBLE);
                //状态改为可见STATE_VISIBLE
                mState = HiRefreshState.STATE_VISIBLE;
            }
            //移动head与child的位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            //下拉位置=可以刷新的距离并且正好松开手，此时下拉刷新
            if (childTop == mHiOverView.mPullRefreshHeight && mState == HiRefreshState.STATE_OVER_RELEASE) {
                HiLog.i(TAG, "refresh，childTop：" + childTop);
                refresh();
            }
        } else {
            if (mHiOverView.getState() != HiRefreshState.STATE_OVER && nonAuto) {
                //超出刷新位置
                mHiOverView.onOver();
                mHiOverView.setState(HiRefreshState.STATE_OVER);
            }
            //移动head与child的位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
        }
        if (mHiOverView != null) {
            //滚动距离
            mHiOverView.onScroll(head.getBottom(), mHiOverView.mPullRefreshHeight);
        }
        return true;
    }

    /**
     * 刷新
     */
    private void refresh() {
        if (mHiRefreshListener != null) {
            //状态改为刷新状态
            mState = HiRefreshState.STATE_REFRESH;
            //mHiOverView回调刷新
            mHiOverView.onRefresh();
            //mHiOverView状态设置为刷新
            mHiOverView.setState(HiRefreshState.STATE_REFRESH);
            //mHiRefreshListener回调刷新
            mHiRefreshListener.onRefresh();
        }
    }


    /**
     * 恢复
     * dis =200  200-100
     *
     * @param dis
     */
    private void recover(int dis) {
        //head滑动距离大于触发下拉刷新的位置mPullRefreshHeight
        if (mHiRefreshListener != null && dis > mHiOverView.mPullRefreshHeight) {
            //滚动到指定位置的距离 也就是滚动到触发下拉刷新的位置
            mAutoScroller.recover(dis - mHiOverView.mPullRefreshHeight);
            //更新为松手状态
            mState = HiRefreshState.STATE_OVER_RELEASE;
        } else {
            //没有滚动到刷新位置，恢复原状
            mAutoScroller.recover(dis);
        }

    }

    /**
     * 借助Scroller实现视图的自动滚动
     * https://juejin.im/post/5c7f4f0351882562ed516ab6
     */
    private class AutoScroller implements Runnable {
        private Scroller mScroller;
        private int mLastY;
        //是否完成滚动
        private boolean mIsFinished;

        AutoScroller() {
            mScroller = new Scroller(getContext(), new LinearInterpolator());
            mIsFinished = true;
        }

        @Override
        public void run() {

            if (mScroller.computeScrollOffset()) {//还未滚动完成
                //计算滚动距离mLastY - mScroller.getCurrY()
                moveDown(mLastY - mScroller.getCurrY(), false);
                //记录最后一次滚动位置
                mLastY = mScroller.getCurrY();
                //继续下次滚动
                post(this);
            } else {
                //移除滚动
                removeCallbacks(this);
                mIsFinished = true;
            }

        }

        //触发滚动
        void recover(int dis) {
            if (dis <= 0) {
                return;
            }
            //新一轮滚动移除之前的滚动回调
            removeCallbacks(this);
            //初始化最终位置为0，没有完成滚动false
            mLastY = 0;
            mIsFinished = false;
            //开始滚动 垂直方向滚动
            mScroller.startScroll(0, 0, 0, dis, 300);
            post(this);
        }

        //是否滚动完成标识
        boolean isFinished() {
            return mIsFinished;
        }
    }
}
