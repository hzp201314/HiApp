package com.hzp.hiapp.fragment.category

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseIntArray
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.hzp.common.ui.component.HiBaseFragment
import com.hzp.common.ui.view.loadUrl
import com.hzp.hi.library.restful.HiCallback
import com.hzp.hi.library.restful.HiResponse
import com.hzp.hi.ui.item.HiViewHolder
import com.hzp.hi.ui.tab.bottom.HiTabBottomLayout
import com.hzp.hiapp.R
import com.hzp.hiapp.http.ApiFactory
import com.hzp.hiapp.http.api.CategoryApi
import com.hzp.hiapp.model.Subcategory
import com.hzp.hiapp.model.TabCategory
import com.hzp.hiapp.route.HiRoute
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : HiBaseFragment() {
    private var emptyView: EmptyView? = null
    private val SPAN_COUNT = 3
    private val subcategoryListCache = mutableMapOf<String, List<Subcategory>>()
    private val subcategoryList = mutableListOf<Subcategory>()
    private val decoration = CategoryItemDecoration({ position ->
        subcategoryList[position].groupName
    }, SPAN_COUNT)
    private val layoutManager = GridLayoutManager(context, SPAN_COUNT)
    private val groupSpanSizeOffset = SparseIntArray()
    private val spanSizeLookUp = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            var spanSize = 1
            val groupName: String = subcategoryList[position].groupName
            val nextGroupName: String? =
                if (position + 1 < subcategoryList.size) subcategoryList[position + 1].groupName else null

            if (TextUtils.equals(groupName, nextGroupName)) {
                //同一组
                spanSize = 1
            } else {
                //当前位置和 下一个位置 不在同一个分组
                //1 .要拿到当前组 position （所在组）在 groupSpanSizeOffset 的索引下标
                //2 .拿到 当前组前面一组 存储的 spansizeoffset 偏移量
                //3 .给当前组最后一个item 分配 spansize count

                val indexOfKey = groupSpanSizeOffset.indexOfKey(position)
                val size = groupSpanSizeOffset.size()
                val lastGroupOffset = if (size <= 0) 0
                else if (indexOfKey >= 0) {
                    //说明当前组的偏移量记录，已经存在了 groupSpanSizeOffset ，这个情况发生在上下滑动，
                    if (indexOfKey == 0) 0 else groupSpanSizeOffset.valueAt(indexOfKey - 1)
                } else {
                    //说明当前组的偏移量记录，还没有存在于 groupSpanSizeOffset ，这个情况发生在 第一次布局的时候
                    //得到前面所有组的偏移量之和
                    groupSpanSizeOffset.valueAt(size - 1)
                }
                //          3       -     (6     +    5               % 3  )第几列=0  ，1 ，2
                spanSize = SPAN_COUNT - (position + lastGroupOffset) % SPAN_COUNT
                if (indexOfKey < 0) {
                    //得到当前组 和前面所有组的spansize 偏移量之和
                    val groupOffset = lastGroupOffset + spanSize - 1
                    groupSpanSizeOffset.put(position, groupOffset)
                }

            }
            return spanSize

        }

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_category
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HiTabBottomLayout.clipBottomPadding(root_container)

        content_loading.visibility = View.VISIBLE
        queryCategoryList()
    }

    private fun queryCategoryList() {
        ApiFactory.create(CategoryApi::class.java)
            .queryCategoryList()
            .enqueue(object : HiCallback<List<TabCategory>> {
                override fun onSuccess(response: HiResponse<List<TabCategory>>) {
                    if (response.successful() && response.data != null) {
                        onQueryCategoryListSuccess(response.data!!)
                    } else {
                        showEmptyView()
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    showEmptyView()
                }

            })

    }

    private fun onQueryCategoryListSuccess(data: List<TabCategory>) {
        if (!isAlive) return
        emptyView?.visibility = View.GONE
        content_loading.visibility = View.VISIBLE
        slider_view.visibility = View.VISIBLE

        slider_view.bindMenuView(
            itemCount = data.size,
            onBindView = { holder: HiViewHolder, position: Int ->
                val category = data[position]

                // holder.menu_item_tilte    无法直接访问，不支持跨模块对象的资源文件引用
                //  holder.itemView.menu_item_title.  每次都需要findViewById
                // 拓展HiViewHolder，增加findViewById方法，支持缓存
                holder.findViewById<TextView>(R.id.menu_item_title)?.text = category.categoryName
            },
            onItemClick = { holder, position ->
                val category = data[position]
                val categoryId = category.categoryId
                if (subcategoryListCache.containsKey(categoryId)) {
                    onQuerySubcategoryListSuccess(subcategoryList)
                } else {
                    querySubcategoryList(categoryId)
                }
            }
        )
    }

    private fun querySubcategoryList(categoryId: String) {
        ApiFactory.create(CategoryApi::class.java)
            .querySubcategoryList(categoryId)
            .enqueue(object : HiCallback<List<Subcategory>> {
                override fun onSuccess(response: HiResponse<List<Subcategory>>) {
                    if (response.successful() && response.data != null) {
                        onQuerySubcategoryListSuccess(response.data!!)
                        if (!subcategoryListCache.containsKey(categoryId)) {
                            subcategoryListCache[categoryId] = response.data!!
                        }
                    }
                }

                override fun onFailed(throwable: Throwable) {

                }

            })
    }

    private fun onQuerySubcategoryListSuccess(data: List<Subcategory>) {
        if (!isAlive) return
        decoration.clear()
        groupSpanSizeOffset.clear()
        subcategoryList.clear()
        subcategoryList.addAll(data)

        if (layoutManager.spanSizeLookup != spanSizeLookUp) {
            layoutManager.spanSizeLookup = spanSizeLookUp
        }

        slider_view.bindContentView(
            itemCount = data.size,
            itemDecoration = decoration,
            layoutManager = layoutManager,
            onBindView = { holder, position ->
                val subCategory = data[position]
                holder.findViewById<ImageView>(R.id.content_item_image)
                    ?.loadUrl(subCategory.subcategoryIcon)
                holder.findViewById<TextView>(R.id.content_item_title)
                    ?.text = subCategory.subcategoryName
            },
            onItemClick = { holder, position ->
                //是应该跳转到类目的商品列表页的
                val subcategory = data[position]
                val bundle = Bundle()
                bundle.putString("categoryId", subcategory.categoryId)
                bundle.putString("subcategoryId", subcategory.subcategoryId)
                bundle.putString("categoryTitle", subcategory.subcategoryName)
                HiRoute.startActivity(requireContext(), bundle, HiRoute.Destination.GOODS_LIST)
            }
        )

    }

    private fun showEmptyView() {
        if (!isAlive) return

        if (emptyView == null) {
            emptyView = EmptyView(requireContext())
            emptyView?.setIcon(R.string.if_empty3)
            emptyView?.setDesc(getString(R.string.list_empty_desc))
            emptyView?.setButton(getString(R.string.list_empty_action), View.OnClickListener {
                queryCategoryList()
            })
            emptyView?.setBackgroundColor(Color.WHITE)
            emptyView?.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            root_container.addView(emptyView)
        }

        content_loading.visibility = View.GONE
        slider_view.visibility = View.GONE
        emptyView?.visibility = View.VISIBLE
    }
}