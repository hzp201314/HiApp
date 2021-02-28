package com.hzp.hiapp.model

/**
 *"isLogin": false,
"favoriteCount": 0,
"browseCount": 0,
bannerNoticeList[]
 */
data class UserProfile(
    val isLogin: Boolean,
    val favoriteCount: Int,
    val browseCount: Int,
    val learnMinutes: Int,
    val userName: String,
    val avatar: String,
    val bannerNoticeList: List<Notice>
)

/**
 * {
"id": "2",
"sticky": 1,
"type": "recommend",
"title": "好课推荐",
"subtitle": "解锁Flutter",
"url": "https://coding.imooc.com/class/321.html",
"cover": "http://img.mukewang.com/szimg/5d479a8309a496a703750948.jpg",
"createTime": "2020-03-23 11:24:57"
}
 */
data class Notice(
    val id: String,
    val sticky: Int,
    val type: String,
    val title: String,
    val subtitle: String,
    val url: String,
    val cover: String,
    val createTime: String
)

data class CourseNotice(val total: Int, val list: List<Notice>?)