package com.hzp.hiapp.demo.item

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hzp.hi.ui.item.HiDataItem
import com.hzp.hiapp.R

class ActivityDatatItem(data: ItemData): HiDataItem<ItemData, RecyclerView.ViewHolder>(data) {

    override fun onBindData(holder: RecyclerView.ViewHolder, position: Int) {
        val imageView:ImageView = holder.itemView.findViewById<ImageView>(R.id.item_image)
        imageView.setImageResource(R.drawable.item_activity)
    }

    override fun getItemLayoutRes(): Int {
        return R.layout.layout_list_item_banner
    }

}