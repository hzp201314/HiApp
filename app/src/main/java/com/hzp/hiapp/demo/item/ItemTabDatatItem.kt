package com.hzp.hiapp.demo.item

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hzp.hi.ui.item.HiDataItem
import com.hzp.hiapp.R

class ItemTabDatatItem(data: ItemData): HiDataItem<ItemData, RecyclerView.ViewHolder>(data) {

    override fun onBindData(holder: RecyclerView.ViewHolder, position: Int) {
        val imageView:ImageView = holder.itemView.findViewById<ImageView>(R.id.item_image)
        imageView.setImageResource(R.drawable.item_goods_tab)
    }

    override fun getItemLayoutRes(): Int {
        return R.layout.layout_list_item_banner
    }

}