package com.hzp.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hzp.common.R
import com.hzp.hi.library.log.HiLog
import com.hzp.hi.ui.item.HiAdapter

class HiRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
):RecyclerView(context, attrs, defStyleAttr) {
    private var loadMoreScrollListener: OnScrollListener? = null
    private var footerView: View? = null
    private var isLoadingMore: Boolean = false

    inner class LoadMoreScrollListener(val prefetchSize:Int,val callback:()->Unit):OnScrollListener(){
        //咱们这里的强转，因为前面 会有前置检查
        val hiAdapter = adapter as HiAdapter
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            //需要根据当前的滑动状态  已决定要不要添加footer view ，要不要执行上拉加载分页的动作

            //正在执行加载更多
            if(isLoadingMore){
                return
            }
            //咱们需要判断当前类表上 已经显示的 item的个数 ，如果列表上已显示的item的数量小于0
            val totalItemCount = hiAdapter.itemCount
            if(totalItemCount<=0){
                return
            }

            //此时，咱们需要在滑动状态为 拖动状态时，就要判断要不要添加footer
            //目的就是为了防止列表滑动到底部了但是 footerview 还没显示出来，
            //1. 依旧需要判断列表是否能够滑动,那么问题来了，如何判断RecyclerView ，是否可以继续向下滑动
            val canScrollVertical = recyclerView.canScrollVertically(1)

            //还有一种情况,canScrollVertical 咱们是检查他能不能继续向下滑动，
            //特殊情况，咱们的列表已经滑动到底部了，但是分页失败了。
            val lastVisibleItem = findLastVisibleItem(recyclerView)
            if (lastVisibleItem <= 0)
                return
            val arriveBottom = lastVisibleItem >= totalItemCount - 1 //totalItemCount 15,14.
            //可以向下滑动，或者当前已经滑动到最底下了，此时在拖动列表，那也是允许分页的
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING && (canScrollVertical || arriveBottom)) {
                addFooterView()
            }
            //不能在 滑动停止了，才去添加footer view
            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                return
            }

            //预加载,就是 不需要等待 滑动到最后一个item的时候，就出发下一页的加载动作
            val arrivePrefetchPosition = totalItemCount - lastVisibleItem <= prefetchSize
            if (!arrivePrefetchPosition) {
                return
            }

            isLoadingMore = true
            callback()
        }

        private fun addFooterView() {
            val footerView = getFooterView()
            //但是，这里有个坑。。。在一些边界场景下。会出现多次添加的情况， 添加之前先 remove --》hiAdapter。.removeFooterView()

            //主要是为了避免 removeFooterView 不及时，在边界场景下可能会出现，footerView还没从recyclervIEW上移除掉，但我们又调用了addFooterView，
            //造成的重复添加的情况，此时会抛出 add view must call removeview form it parent first exception
            if (footerView.parent != null) {
                footerView.post {
                    addFooterView()
                }
            } else {
                hiAdapter.addFooterView(footerView)
            }
        }

        private fun getFooterView(): View {
            if (footerView == null) {
                footerView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_footer_loading, this@HiRecyclerView, false)

            }
            return footerView!!
        }

        private fun findLastVisibleItem(recyclerView: RecyclerView): Int {
            when (val layoutManager = recyclerView.layoutManager) {
                //layoutManager is GridLayoutManager
                is LinearLayoutManager -> {
                    return layoutManager.findLastVisibleItemPosition()
                }
                is StaggeredGridLayoutManager -> {
                    return layoutManager.findLastVisibleItemPositions(null)[0]
                }
            }
            return -1
        }

    }

    /*开启加载更多*/
    fun enableLoadMore(callback: () -> Unit, prefetchSize: Int) {
        if (adapter !is HiAdapter) {
            HiLog.e("enableLoadMore must use hiadapter")
            return
        }

        loadMoreScrollListener = LoadMoreScrollListener(prefetchSize, callback)
        addOnScrollListener(loadMoreScrollListener!!)
    }

    /*关闭加载更多*/
    fun disableLoadMore() {
        if (adapter !is HiAdapter) {
            HiLog.e("disableLoadMore must use hiadapter")
            return
        }

        val hiAdapter = adapter as HiAdapter
        footerView?.let {
            if (footerView!!.parent != null) {
                hiAdapter.removeFooterView(footerView!!)
            }
        }

        loadMoreScrollListener?.let {
            removeOnScrollListener(loadMoreScrollListener!!)
            loadMoreScrollListener = null
            footerView = null
            isLoadingMore = false
        }
    }

    /*是否正在加载*/
    fun isLoading(): Boolean {
        return isLoadingMore
    }

    /*加载结束*/
    fun loadFinished(success: Boolean) {
        if (adapter !is HiAdapter) {
            HiLog.e("loadFinished must use hiadapter")
            return
        }

        isLoadingMore = false
        val hiAdapter = adapter as HiAdapter
        if (!success) {
            //加载失败移除footerView
            footerView?.let {
                if (footerView!!.parent != null) {
                    hiAdapter.removeFooterView(footerView!!)
                }
            }
        } else {
            //nothing to do .
        }
    }
}