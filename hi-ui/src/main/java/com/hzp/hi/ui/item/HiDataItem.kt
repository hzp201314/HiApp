package com.hzp.hi.ui.item

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class HiDataItem<DATA, VH : RecyclerView.ViewHolder>(data: DATA?) {
    val TAG: String = "HiDataItem";
    var mData: DATA? = null
    var hiAdapter: HiAdapter? = null

    init {
        this.mData = data
    }

    /**
     * 绑定数据
     */
    abstract fun onBindData(holder: VH, position: Int)

    /**
     * 返回该item的布局资源id
     */
    open fun getItemLayoutRes(): Int {
        return -1;
    }

    /**
     *返回该item的视图view
     */
    open fun getItemView(parent: ViewGroup): View? {
        return null
    }

    fun setAdapter(adapter: HiAdapter) {
        this.hiAdapter = adapter
    }

    /**
     * 刷新列表
     */
    fun refreshItem() {
        if (hiAdapter != null) hiAdapter!!.refreshItem(this)
    }

    /**
     * 从列表上移除
     */
    fun removeItem() {
        if (hiAdapter != null) hiAdapter!!.removeItem(this)
    }

    /**
     * 该item在列表上占几列,代表的宽度是占满屏幕
     */
    open fun getSpanSize(): Int {
        return 0
    }

}