package com.hzp.hiapp.biz.account

import android.app.Application
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.hzp.common.utils.SPUtil
import com.hzp.hi.library.cache.HiStorage
import com.hzp.hi.library.executor.HiExecutor
import com.hzp.hi.library.restful.HiCallback
import com.hzp.hi.library.restful.HiResponse
import com.hzp.hi.library.util.AppGlobals
import com.hzp.hiapp.http.ApiFactory
import com.hzp.hiapp.http.api.AccountApi
import com.hzp.hiapp.model.UserProfile
import java.lang.IllegalStateException

object AccountManager {
    private val lock = Any()
    private var userProfile: UserProfile? = null
    private var boardingPass: String? = null
    private const val KEY_USER_PROFILE = "user_profile"
    private const val KEY_BOARDING_PASS = "boarding_pass"
    private val loginLiveData = MutableLiveData<Boolean>()
    private val loginForeverObservers = mutableListOf<Observer<Boolean>>()

    private val profileLiveData = MutableLiveData<UserProfile>()
    private val profileForeverObservers = mutableListOf<Observer<UserProfile?>>()

    @Volatile
    private var isFetching = false

    init {
        HiExecutor.execute(runnable = Runnable {
            val local = HiStorage.getCache<UserProfile?>(KEY_USER_PROFILE)
            synchronized(lock) {
                if (userProfile == null && local == null) {
                    userProfile = local
                }
            }
        })
    }

    fun login(context: Context? = AppGlobals.get(), observer: Observer<Boolean>) {
        if (context is LifecycleOwner) {
            loginLiveData.observe(context, observer)
        } else {
            loginLiveData.observeForever(observer)
            loginForeverObservers.add(observer)
        }

        val intent = Intent(context, LoginActivity::class.java)

        if (context is Application) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (context == null) {
            throw IllegalStateException("context must not be null.")
        }

        context.startActivity(intent)
    }


    internal fun loginSuccess(boardingPass: String) {
        SPUtil.putString(KEY_BOARDING_PASS, boardingPass)
        this.boardingPass = boardingPass
        loginLiveData.value=true

        clearLoginForeverObservers()
    }

    private fun clearLoginForeverObservers() {
        for (observer in loginForeverObservers) {
            loginLiveData.removeObserver(observer)
        }
        loginForeverObservers.clear()
    }

    fun getBoardingPass(): String? {
        if (TextUtils.isEmpty(boardingPass)) {
            boardingPass = SPUtil.getString(KEY_BOARDING_PASS)
        }
        return boardingPass
    }

    fun isLogin():Boolean{
        return !TextUtils.isEmpty(getBoardingPass())
    }

    @Synchronized
    fun getUserProfile(
        lifecycleOwner: LifecycleOwner?,
        observer: Observer<UserProfile?>,
        onlyCache: Boolean = true
    ) {
        if (lifecycleOwner == null) {
            profileLiveData.observeForever(observer)
            profileForeverObservers.add(observer)
        } else {
            profileLiveData.observe(lifecycleOwner, observer)
        }
        if (userProfile != null && onlyCache) {
            profileLiveData.postValue(userProfile)
            return
        }
        if (isFetching) return
        isFetching = true
        ApiFactory.create(AccountApi::class.java).profile()
            .enqueue(object : HiCallback<UserProfile> {
                override fun onSuccess(response: HiResponse<UserProfile>) {
                    userProfile = response.data
                    if (response.code == HiResponse.SUCCESS && userProfile != null) {
                        HiExecutor.execute(runnable = Runnable {
                            HiStorage.saveCache(KEY_USER_PROFILE, userProfile)
                            isFetching = false
                        })
                        profileLiveData.value=userProfile
                    } else {
                        profileLiveData.value=null
                    }

                    clearProfileForeverObservers()
                }

                override fun onFailed(throwable: Throwable) {
                    isFetching = false
                    profileLiveData.value=null
                    clearProfileForeverObservers()
                }
            })
    }

    private fun clearProfileForeverObservers() {
        for (observer in profileForeverObservers) {
            profileLiveData.removeObserver(observer)
        }
        profileForeverObservers.clear()
    }

}