package com.hzp.hiapp.fragment.home

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzp.common.ui.component.HiAbsListFragment
import com.hzp.hi.library.restful.HiCallback
import com.hzp.hi.library.restful.HiResponse
import com.hzp.hi.ui.item.HiDataItem
import com.hzp.hiapp.http.ApiFactory
import com.hzp.hiapp.http.api.HomeApi
import com.hzp.hiapp.model.HomeModel

class HomeTabFragment : HiAbsListFragment() {
    private var categoryId: String? = null
    val DEFAULT_HOT_TAB_CATEGORY_ID = "1"

    companion object {
        fun newInstance(categoryId: String): HomeTabFragment {
            val args = Bundle()
            args.putString("categoryId", categoryId)
            val fragment = HomeTabFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        categoryId=arguments?.getString("categoryId",DEFAULT_HOT_TAB_CATEGORY_ID)
        super.onViewCreated(view, savedInstanceState)

        queryTabCategoryList()

        enableLoadMore { queryTabCategoryList() }
    }

    override fun onRefresh() {
        super.onRefresh()
        queryTabCategoryList()
    }

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        val isHotTab = TextUtils.equals(categoryId,DEFAULT_HOT_TAB_CATEGORY_ID)
        return if(isHotTab) super.createLayoutManager() else GridLayoutManager(context,2)
    }

    private fun queryTabCategoryList() {
        ApiFactory.create(HomeApi::class.java)
            .queryTabCategoryList(categoryId!!,pageIndex,10)
            .enqueue(object :HiCallback<HomeModel>{
                override fun onSuccess(response: HiResponse<HomeModel>) {
                    if(response.successful()&&response.data!=null){
                        updateUI(response.data!!)
                    }else{
                        finishRefresh(null)
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    //空数据页面
                    finishRefresh(null)
                }
            })
    }

    private fun updateUI(data: HomeModel) {
        if(!isAlive)return

        val dataItems = mutableListOf<HiDataItem<*,*>>()
        data.bannerList?.let { dataItems.add(BannerItem(data.bannerList)) }

        data.subcategoryList?.let {
            dataItems.add(GridItem(data.subcategoryList))
        }

        data.goodsList?.forEachIndexed { index, goodsModel ->
            dataItems.add(
                GoodsItem(
                    goodsModel,
                    TextUtils.equals(categoryId, DEFAULT_HOT_TAB_CATEGORY_ID)
                )
            )
        }

        finishRefresh(dataItems)
    }
}