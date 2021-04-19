package com.hzp.hi.library.util

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * 提供前后台状态监听 以及栈顶activity的服务
 */
class ActivityManager private constructor() {

    /*存储Activity列表*/
    private val activityRefs = ArrayList<WeakReference<Activity>>()

    /*应用前后台切换回调监听列表*/
    private val frontBackCallbacks = ArrayList<FrontBackCallback>()

    /*计数器：当前有多少个Activity处于前台*/
    private var activityStartCount = 0

    /*标记位：当前应用是否正在前台，应用打开默认前台：true*/
    var front = true;

    /*初始化*/
    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(InnerActivityLifecycleCallbacks())
    }

    /*Activity生命周期回调内部类*/
    inner class InnerActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStarted(activity: Activity) {
            activityStartCount++
            //判断应用是不是从后台切换到前台，是就通知回调监听
            //activityStartCount>0  说明应用处在可见状态，也就是前台
            //!front==true 后台切前台
            if (!front && activityStartCount > 0) {
                front = true
                onFrontBackChanged(front);
            }
        }

        override fun onActivityDestroyed(activity: Activity) {
            //Activity.onDestroy()的时候将Activity在activityRefs中移除
            for (activityRef in activityRefs) {
                if (activityRef != null && activityRef.get() == activity) {
                    activityRefs.remove(activityRef);
                    break
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityStopped(activity: Activity) {
            activityStartCount--;
            //判断是不是从前台变后台，是就通知回调监听
            if (activityStartCount <= 0 && front) {
                front = false
                onFrontBackChanged(front)
            }
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            //Activity.onCreate()的时候将Activity添加到activityRefs中
            activityRefs.add(WeakReference(activity))
        }

        override fun onActivityResumed(activity: Activity) {

        }

    }

    /*前后台状态变化回调出去*/
    private fun onFrontBackChanged(front: Boolean) {
        for (callback in frontBackCallbacks) {
            callback.onChange(front)
        }
    }

    /**
     * 获取栈顶Activity
     * 找出栈顶不为空，且没有被销毁的activity
     */
    fun getTopActivity(onlyAlive: Boolean): Activity? {
        if (activityRefs.size <= 0) {
            return null
        } else {
            val activityRef = activityRefs[activityRefs.size - 1]
            val activity = activityRef.get()
            if (onlyAlive) {
                if (activityRef == null || activity!!.isFinishing
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                            && activity!!.isDestroyed)
                ) {
                    activityRefs.remove(activityRef)
                    return getTopActivity(onlyAlive)
                }
            }
            return activity
        }
    }

    /*注册前后台切换回调监听*/
    fun addFrontBackCallback(callback: FrontBackCallback) {
        if (!frontBackCallbacks.contains(callback)) {
            frontBackCallbacks.add(callback)
        }
    }

    /*移除前后台切换回调监听*/
    fun removeFrontBackCallback(callback: FrontBackCallback) {
        frontBackCallbacks.remove(callback);
    }

    interface FrontBackCallback {
        fun onChange(front: Boolean)
    }

    /*单例*/
    companion object {
        @JvmStatic
        val instance: ActivityManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityManager()
        }
    }
}