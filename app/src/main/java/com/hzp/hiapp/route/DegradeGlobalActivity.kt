package com.hzp.hiapp.route

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hzp.hi.ui.empty.EmptyView
import com.hzp.hiapp.R

/**
 * 全局统一错误页
 */
@Route(path = "/degrade/global/activity")
class DegradeGlobalActivity : AppCompatActivity() {
    @JvmField//静态字段
    @Autowired//ARouter路由注入
    var degrade_title: String? = null
    @JvmField
    @Autowired
    var degrade_desc: String? = null
    @JvmField
    @Autowired
    var degrade_action: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_degrade_global)
        ARouter.getInstance().inject(this)
//        HiRoute.inject(this)
        val emptyView =findViewById<EmptyView>(R.id.empty_view)
        emptyView.setIcon(R.string.if_empty)

        if (degrade_title != null) {
            emptyView.setTitle(degrade_title!!)
        }

        if (degrade_desc != null) {
            emptyView.setDesc(degrade_desc!!)
        }

        if (degrade_action != null) {
            emptyView.setHelpAction(listener = View.OnClickListener {
//                var intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(degrade_action))
//                startActivity(intent)
                HiRoute.startActivity4Browser(degrade_action!!)
            })
        }
    }
}