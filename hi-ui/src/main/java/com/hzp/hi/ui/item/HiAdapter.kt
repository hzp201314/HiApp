package com.hzp.hi.ui.item

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import java.lang.RuntimeException
import java.lang.reflect.ParameterizedType

/**
 * 通用数据适配器
 *
 *
 * bugfix:HiDataItem<*, out RecyclerView.ViewHolder>  都被改成了这样。否则会有类型转换问题
 */
class HiAdapter(context: Context) : Adapter<ViewHolder>() {
    private var mContext: Context = context

    private var mInflater = LayoutInflater.from(context)

    /*item数据集合*/
    private var dataSets = ArrayList<HiDataItem<*, out ViewHolder>>()

    /*item类型集合*/
    private var typeArrays = SparseArray<HiDataItem<*, out ViewHolder>>()

//    init {
//        this.mContext = context
//        this.mInflater = LayoutInflater.from(context)
//    }

    /**
     *在指定为上添加HiDataItem
     */
    fun addItemAt(index: Int, dataItem: HiDataItem<*, out ViewHolder>, notify: Boolean) {
        if (index > 0) {
            dataSets.add(index, dataItem)
        } else {
            dataSets.add(dataItem)
        }
        val notifyPos = if (index > 0) index else dataSets.size - 1
        if (notify) {
            notifyItemInserted(notifyPos)
        }

        dataItem.setAdapter(this)
    }

    /**
     * 往现有集合的尾部一次性添加items集合
     */
    fun addItems(items: List<HiDataItem<*, out ViewHolder>>, notify: Boolean) {
        val start = dataSets.size
        items.forEach { dataItem ->
            dataSets.add(dataItem)
            dataItem.setAdapter(this)
        }

        if (notify) {
            notifyItemRangeInserted(start, items.size)
        }
    }

    /**
     * 从指定位置上移除item
     */
    fun removeItemAt(index: Int): HiDataItem<*, out ViewHolder>? {
        if (index > 0 && index < dataSets.size) {
            val remove: HiDataItem<*, out ViewHolder> = dataSets.removeAt(index)
            notifyItemRemoved(index)
            return remove
        } else {
            return null
        }
    }

    /**
     * 移除指定item
     */
    fun removeItem(dataItem: HiDataItem<*, out ViewHolder>) {
        val index: Int = dataSets.indexOf(dataItem)
        removeItemAt(index)
    }

    /**
     * 指定刷新 某个item的数据
     */
    fun refreshItem(dataItem: HiDataItem<*, out ViewHolder>) {
        val indexOf = dataSets.indexOf(dataItem)
        notifyItemChanged(indexOf)
    }


    /**
     * 以每种item类型的class.hashcode为 该item的viewType
     *
     * 这里把type存储起来，是为了onCreateViewHolder方法能够为不同类型的item创建不同的viewholder
     */
    override fun getItemViewType(position: Int): Int {
        val dataItem = dataSets[position]
        val type = dataItem.javaClass.hashCode()
        //如果还没有包含这种类型的item，则添加进来
        if (typeArrays.indexOfKey(type) > 0) {
            typeArrays.put(type, dataItem)
        }
        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataItem = typeArrays.get(viewType)
        var view:View? = dataItem.getItemView(parent)
        if (view == null) {
            val layoutRes = dataItem.getItemLayoutRes()
            if (layoutRes < 0) {
                throw RuntimeException("dataItem: ${dataItem.javaClass.name} must override getItemView or getItemLayoutRes")
            }
            view = mInflater.inflate(layoutRes, parent, false)
        }
        return createViewHolderInternal(dataItem.javaClass, view!!)
    }

    override fun getItemCount(): Int {
        return dataSets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hiDataItem = dataSets[position]
        hiDataItem.onBindData(holder as Nothing, position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        /**
         * 为列表上的item 适配网格布局
         */
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (position < dataSets.size) {
                        val hiDataItem = dataSets[position]
                        val spanSize = hiDataItem.getSpanSize()
                        return if (spanSize <= 0) spanCount else spanSize
                    }
                    return spanCount
                }
            }
        }

    }

    private fun createViewHolderInternal(
        javaClass: Class<HiDataItem<*, out ViewHolder>>,
        view: View
    ): ViewHolder {
        //得到该Item的父类类型,即为HiDataItem.class。  class 也是type的一个子类。
        //type的子类常见的有 class，类泛型,ParameterizedType参数泛型 ，TypeVariable字段泛型
        //所以进一步判断它是不是参数泛型
        val superclass = javaClass.genericSuperclass
        if (superclass is ParameterizedType) {
            //得到它携带的泛型参数的数组
            val arguments = superclass.actualTypeArguments
            //挨个遍历判断 是不是咱们想要的 RecyclerView.ViewHolder 子类 类型的。
            for (argument in arguments) if (argument is Class<*> && ViewHolder::class.java.isAssignableFrom(
                    argument
                )
            ) {
                try {
                    // 如果是，则使用反射 实例化类上标记的实际的泛型对象
                    // 这里需要  try-catch 一把，
                    // 如果咱们直接在HiDataItem子类上标记 RecyclerView.ViewHolder，
                    // 抽象类是不允许反射的
                    return argument.getConstructor(View::class.java).newInstance(view) as ViewHolder

                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }

        }
        return object : ViewHolder(view) {}
    }


}