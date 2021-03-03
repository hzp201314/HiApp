package com.hzp.hiapp.model

import java.io.Serializable


data class HomeModel(
    val bannerList: List<HomeBanner>?,
    val subcategoryList: List<Subcategory>?,
    val goodsList: List<GoodsModel>?
) : Serializable

/**
 * {
"categoryId": "1",
"categoryName": "热门",
"goodsCount": "1"
}
 */
data class TabCategory(val categoryId: String, val categoryName: String, val goodsCount: String) :
    Serializable


/**
 *  {
"id": "5",
"sticky": 1,
"type": "goods",
"title": "商品推荐",
"subtitle": "2019新款X27水滴屏6.5英寸全网通4G指纹人脸美颜游戏一体智能手机",
"url": "1574920889775",
"cover": "https://o.devio.org/images/as/goods/images/2019-11-14/8ad2ad11-afa3-4ae8-a816-860ec82f7b37.jpeg",
"createTime": "2020-03-23 11:24:57"
}
 */
data class HomeBanner(
    val cover: String,
    val createTime: String,
    val id: String,
    val sticky: Int,
    val subtitle: String,
    val title: String,
    val type: String,
    val url: String
) : Serializable {
    companion object {
        const val TYPE_GOODS = "goods"
        const val TYPE_RECOMMEND = "recommend"
    }
}


/**
 * {
"subcategoryId": "1",
"groupName": null,
"categoryId": "1",
"subcategoryName": "限时秒杀",
"subcategoryIcon": "https://o.devio.org/images/as/images/2018-05-16/26c916947489c6b2ddd188ecdb54fd8d.png",
"showType": "1"
}
 */
data class Subcategory(
    val categoryId: String,
    val groupName: String,
    val showType: String,
    val subcategoryIcon: String,
    val subcategoryId: String,
    val subcategoryName: String
) : Serializable

/**
 * "goodsId": "1580374361011",
"categoryId": "16",
"hot": true,
"sliderImages": [
{
"url": "https://o.devio.org/images/as/goods/images/2018-12-21/5c3672e33377b65d5f1bef488686462b.jpeg",
"type": 1
},
{
"url": "https://o.devio.org/images/as/goods/images/2018-12-21/117a40a6d63c5bac590080733512b89d.jpeg",
"type": 1
},
{
"url": "https://o.devio.org/images/as/goods/images/2018-12-21/7d4449179b509531414365460d80a87d.jpeg",
"type": 1
}
],
"marketPrice": "¥100",
"groupPrice": "14",
"completedNumText": "已拼1348件",
"goodsName": "吉祥鱼房间新年装饰客厅餐厅卧室玄背景墙亚克力3d立体自粘墙贴画",
"tags": "极速退款 全场包邮 7天无理由退货",
"joinedAvatars": null,
"createTime": "2020-01-30 16:52:41",
"sliderImage": "https://o.devio.org/images/as/goods/images/2018-12-21/5c3672e33377b65d5f1bef488686462b.jpeg"
 */
data class GoodsModel(
    val categoryId: String,
    val completedNumText: String,
    val createTime: String,
    val goodsId: String,
    val goodsName: String,
    val groupPrice: String,
    val hot: Boolean,
    val joinedAvatars: List<SliderImage>,
    val marketPrice: String,
    val sliderImage: String,
    val sliderImages: List<SliderImage>,
    val tags: String
) : Serializable

data class SliderImage(
    val type: Int,
    val url: String
) : Serializable

data class GoodsList(val total: Int, val list: List<GoodsModel>) {

}