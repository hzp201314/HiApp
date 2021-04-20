package com.hzp.common.ext

import android.widget.Toast
import com.hzp.hi.library.util.AppGlobals

fun <T> T.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(AppGlobals.get()!!, message, duration).show()