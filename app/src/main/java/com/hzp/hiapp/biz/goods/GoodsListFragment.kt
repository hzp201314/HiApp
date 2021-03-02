package com.hzp.hiapp.biz.goods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.hzp.common.ui.component.HiAbsListFragment
import com.hzp.hi.library.restful.HiCallback
import com.hzp.hi.library.restful.HiResponse
import com.hzp.hiapp.fragment.home.GoodsItem
import com.hzp.hiapp.http.ApiFactory
import com.hzp.hiapp.http.api.GoodsApi
import com.hzp.hiapp.model.GoodsList
import com.hzp.hiapp.route.HiRoute

class GoodsListFragment : HiAbsListFragment() {

    @JvmField
    @Autowired
    var categoryId: String = ""

    @JvmField
    @Autowired
    var sucategoryId: String = ""

    companion object {
        fun newInstance(categoryId: String, subcategoryId: String): Fragment {
            val args = Bundle()
            args.putString("categoryId", categoryId)
            args.putString("subcategoryId", subcategoryId)
            val fragment = GoodsListFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        ARouter.getInstance().inject(this)
        HiRoute.inject(this)

        enableLoadMore { loadData() }

        loadData()
    }

    override fun onRefresh() {
        super.onRefresh()
        loadData()
    }

    private fun loadData() {
        ApiFactory.create(GoodsApi::class.java)
            .queryCategoryGoodsList(categoryId, sucategoryId, 10, pageIndex)
            .enqueue(object : HiCallback<GoodsList> {
                override fun onSuccess(response: HiResponse<GoodsList>) {
                    if (response.successful() && response.data != null) {
                        onQueryCategoryGoodsList(response.data!!)
                    } else {
                        finishRefresh(null)
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    finishRefresh(null)
                }

            })

    }

    private fun onQueryCategoryGoodsList(data: GoodsList) {
        val dataItems = mutableListOf<GoodsItem>()
        for (goodsModel in data.list) {
            val goodsItem = GoodsItem(goodsModel, false)
            dataItems.add(goodsItem)
        }
        finishRefresh(dataItems)
    }


    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(context, 2)
    }
}