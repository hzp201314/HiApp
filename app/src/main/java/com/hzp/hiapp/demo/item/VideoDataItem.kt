package com.hzp.hiapp.demo.item

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hzp.hi.ui.item.HiDataItem
import com.hzp.hiapp.R

class VideoDataItem(spanCount: Int, data: ItemData) :
    HiDataItem<ItemData, VideoDataItem.MyHolder>(data) {
    private var spanCount:Int?=null
    init {
        this.spanCount=spanCount
    }
    override fun getSpanSize(): Int {
        return this.spanCount!!
    }

    override fun onBindData(holder: MyHolder, position: Int) {
        holder.imageView!!.setImageResource(R.drawable.item_video)
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

}