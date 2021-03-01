package com.hzp.hiapp.route

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.hzp.hi.library.util.AppGlobals

object HiRoute {
    //拉起浏览器
    fun startActivity4Browser(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        //这个目的是为了 防止在部分机型上面 拉不起浏览器，，比说华为
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        // 是为了 使用applicaiton  context 启动activity 不会报错
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        AppGlobals.get()?.startActivity(intent)
    }

    enum class Destination(val desc: String, val path: String) {
        GOODS_LIST("商品列表", "/goods/list"),
        ACCOUNT_REGISTRATION("注册", "/account/registration"),
        ACCOUNT_LOGIN("登陆", "/account/login"),
        DEGRADE_GLOBAL("全局降级页", "/degrade/global/activity")
        //登录
        //注册
    }

    fun startActivity(
        context: Context?,
        bundle: Bundle? = null,
        destination: Destination,
        requestCode: Int = -1
    ) {
        val postcard = ARouter.getInstance().build(destination.path).with(bundle)
        if (requestCode == -1 || context !is Activity) {
            postcard.navigation(context)
        } else {
            postcard.navigation(context, requestCode)
        }
    }

    fun inject(target: Any) {
        ARouter.getInstance().inject(target)
    }
}