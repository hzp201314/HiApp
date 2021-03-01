package com.hzp.hiapp.fragment.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzp.common.ui.view.loadUrl
import com.hzp.hi.library.util.HiDisplayUtil
import com.hzp.hi.ui.item.HiDataItem
import com.hzp.hi.ui.item.HiViewHolder
import com.hzp.hiapp.R
import com.hzp.hiapp.model.Subcategory
import com.hzp.hiapp.route.HiRoute
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_home_op_grid_item.view.*

class GridItem(val list: List<Subcategory>) : HiDataItem<List<Subcategory>, HiViewHolder>(list) {
    override fun onBindData(holder: HiViewHolder, position: Int) {
        val context = holder.itemView.context
        val gridView = holder.itemView as RecyclerView
        gridView.adapter = GridAdapter(context, list)
    }

    override fun getItemView(parent: ViewGroup): View? {
        val gridView = RecyclerView(parent.context)
        val params = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

        params.bottomMargin = HiDisplayUtil.dp2px(10f)
        gridView.layoutManager = GridLayoutManager(parent.context, 5)
        gridView.layoutParams = params
        gridView.setBackgroundColor(Color.WHITE)
        return gridView
    }

    inner class GridAdapter(val context: Context, val list: List<Subcategory>) :
        RecyclerView.Adapter<HMyViewHolder>() {
        private var inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HMyViewHolder {
            val view = inflater.inflate(R.layout.layout_home_op_grid_item, parent, false)
            return HMyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: HMyViewHolder, position: Int) {
            val subcategory = list[position]
//            holder.item_image.loadUrl(subcategory.subcategoryIcon)
//            holder.item_title.text = subcategory.subcategoryName
            holder.itemView.item_image.loadUrl(subcategory.subcategoryIcon)
            holder.itemView.item_title.text = subcategory.subcategoryName

            holder.itemView.setOnClickListener {
                //会跳转到子分类列表上面去，，是一个单独的页面
                //Toast.makeText(context, "you touch me:" + position, Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putString("categoryId", subcategory.categoryId)
                bundle.putString("subcategoryId", subcategory.subcategoryId)
                bundle.putString("categoryTitle", subcategory.subcategoryName)
                HiRoute.startActivity(context, bundle, HiRoute.Destination.GOODS_LIST)
            }
        }
    }


    class HMyViewHolder(val view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
        override val containerView: View?
            get() = view
    }
}