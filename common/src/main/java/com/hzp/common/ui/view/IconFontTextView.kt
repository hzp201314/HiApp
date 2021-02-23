package com.hzp.common.ui.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 用以支持全局iconfont资源的引用，可以在布局中直接设置text
 */
class IconFontTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    init {
        val typeface = Typeface.createFromAsset(context.assets, "fonts/iconfont.ttf")
        setTypeface(typeface)
    }
}