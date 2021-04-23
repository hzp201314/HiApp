package com.hzp.hi.library.util

import android.os.Build
import android.text.TextUtils

/**
 * 折叠屏检测
 */
object FoldableDeviceUtil {
    //1. 官方没有给我们提供api的
    // 2.只能去检测 针对的机型
    val application = AppGlobals.get()!!

    /**
     * 是否折叠
     * true：折叠状态
     * false：展开状态
     */
    fun isFold(): Boolean {
        return if (TextUtils.equals(Build.BRAND, "samsung") && TextUtils.equals(
                Build.DEVICE,
                "Galaxy Z Fold2"
            )
        ) {
            return HiDisplayUtil.getDisplayWidthInPx(application) != 1768
        } else if (TextUtils.equals(Build.BRAND, "huawei") && TextUtils.equals(
                Build.DEVICE,
                "MateX"
            )
        ) {
            return HiDisplayUtil.getDisplayWidthInPx(application) != 2200
        } else if (TextUtils.equals(Build.BRAND, "google") && TextUtils.equals(
                Build.DEVICE,
                "generic_x86"
            )
        ) {
            return HiDisplayUtil.getDisplayWidthInPx(application) != 2200
        } else {
            true
        }
    }
}