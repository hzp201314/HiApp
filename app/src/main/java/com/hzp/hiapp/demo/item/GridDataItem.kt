package com.hzp.hiapp.demo.item

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzp.hi.library.util.HiDisplayUtil
import com.hzp.hi.library.util.HiRes
import com.hzp.hi.ui.item.HiDataItem
import com.hzp.hiapp.R

/*上部网格Item*/
class GridDataItem(data: ItemData) : HiDataItem<ItemData,GridDataItem.MyHolder >(data) {

    override fun onBindData(holder: MyHolder, position: Int) {
        holder.imageView!!.setImageResource(R.drawable.item_grid)
    }

    override fun getItemLayoutRes(): Int {
        return R.layout.layout_list_item_grid
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null

        init {
            imageView = itemView.findViewById<ImageView>(R.id.item_image)
        }
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
        gridView.setBackgroundColor(HiRes.getColor(R.color.color_white))
        return gridView
    }

}