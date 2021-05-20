package com.hzp.hiapp.biz.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hzp.common.route.HiRoute
import com.hzp.common.ui.component.HiBaseActivity
import com.hzp.common.utils.SPUtil
import com.hzp.hi.library.restful.HiCallback
import com.hzp.hi.library.restful.HiResponse
import com.hzp.hi.library.util.HiStatusBar
import com.hzp.hiapp.R
import com.hzp.hiapp.http.ApiFactory
import com.hzp.hiapp.http.api.AccountApi
import kotlinx.android.synthetic.main.activity_login.*

//tom1 astom1123
@Route(path = "/account/login")
class LoginActivity : HiBaseActivity() {
    private val REQUEST_CODE_REGISTRATION = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HiStatusBar.setStatusBar(this,true, translucent = false)
        setContentView(R.layout.activity_login)

        action_back.setOnClickListener {
            onBackPressed()
        }

        action_register.setOnClickListener {
            goRegistration()
        }

        action_login.setOnClickListener {
            goLogin()
        }

    }

    private fun goLogin() {
        val name: Editable = input_item_username.getEditText().text
        val password = input_item_password.getEditText().text

        if (TextUtils.isEmpty(name) or TextUtils.isEmpty(password)) {
            return
        }

        //viewmodel+respostory+livedata
        ApiFactory.create(AccountApi::class.java).login(name.toString(), password.toString())
            .enqueue(object : HiCallback<String> {
                override fun onSuccess(response: HiResponse<String>) {
                    if (response.code == HiResponse.SUCCESS) {
                        showToast(getString(R.string.login_success))
                        //usermanager
                        val data = response.data
                        AccountManager.loginSuccess(data!!)
                        SPUtil.putString("boarding-pass", data)
                        setResult(Activity.RESULT_OK, Intent())
                        finish()
                    } else {
                        showToast(getString(R.string.login_failed) + response.msg)
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    showToast(getString(R.string.login_failed) + throwable.message)
                }

            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((resultCode == Activity.RESULT_OK)and(data!=null)and (requestCode==REQUEST_CODE_REGISTRATION)){
            val username = data!!.getStringExtra("username")
            if(!TextUtils.isEmpty(username)){
                input_item_username.getEditText().setText(username)
            }
        }
    }


    private fun goRegistration() {
        startActivityForResult(
            Intent(this, RegistrationActivity::class.java),
            REQUEST_CODE_REGISTRATION
        )
//        ARouter.getInstance().build("/account/registration").navigation(this,REQUEST_CODE_REGISTRATION)
//        HiRoute.startActivity(this,null,"/account/registration",REQUEST_CODE_REGISTRATION)
    }

    override fun onDestroy() {
//        AccountManager.clearObservers()
        super.onDestroy()
    }


}