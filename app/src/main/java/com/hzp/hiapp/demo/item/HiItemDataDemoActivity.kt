package com.hzp.hiapp.demo.item

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.hzp.hi.ui.item.HiAdapter
import com.hzp.hi.ui.item.HiDataItem
import com.hzp.hiapp.R
import kotlinx.android.synthetic.main.activity_hi_item_data_demo.*

class HiItemDataDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_item_data_demo)

        var hiAdapter =HiAdapter(this)
        recycler_view.adapter=hiAdapter
        recycler_view.layoutManager=GridLayoutManager(this,2)


        var dataSets = ArrayList<HiDataItem<*,*>>()
        //顶部tab
        dataSets.add(TopTabDataItem(ItemData()))
        //顶部Banner
        dataSets.add(TopBanner(ItemData()))
        //商品分类
        dataSets.add(GridDataItem(ItemData()))

        //活动区域
        dataSets.add(ActivityDatatItem(ItemData()))
        //商品tab栏
        dataSets.add(ItemTabDatatItem(ItemData()))
        for (i in 0..9){
            if(i%2==0){
                //feeds流的视频类型
                dataSets.add(VideoDataItem(2,ItemData()))
            }else{
                //feeds流的图片类型
                dataSets.add(ImageDataItem(2,ItemData()))
            }
        }

        hiAdapter.addItems(dataSets,false)
    }
}