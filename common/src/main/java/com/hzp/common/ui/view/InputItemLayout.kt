package com.hzp.common.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hzp.common.R

/**
 * 输入框组件
 */
class InputItemLayout : LinearLayout {
    private lateinit var titleView: TextView
    private lateinit var editText: EditText
    private var bottomLine: Line
    private var topLine: Line
    private var topPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bottomPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
        dividerDrawable = ColorDrawable()
        showDividers = SHOW_DIVIDER_BEGINNING

        //去加载 去读取 自定义sytle属性
        orientation = HORIZONTAL
        
        //解析属性
        val array = context.obtainStyledAttributes(attributeSet, R.styleable.InputItemLayout)

        //解析title 属性
        array.getString(R.styleable.InputItemLayout_title)
        val title = array.getString(R.styleable.InputItemLayout_title)
        val titleResId = array.getResourceId(R.styleable.InputItemLayout_titleTextAppearance, 0)
        parseTitleStyle(title, titleResId)

        //解析右侧的输入框属性
        val hint = array.getString(R.styleable.InputItemLayout_hint)
        val inputResId = array.getResourceId(R.styleable.InputItemLayout_inputTextAppearance, 0)
        val inputType = array.getInteger(R.styleable.InputItemLayout_inputType, 0)
        parseInputStyle(hint, inputResId, inputType)

        //上下分割线属性
        val topResId = array.getResourceId(R.styleable.InputItemLayout_topLineAppearance, 0)
        val bottomResId = array.getResourceId(R.styleable.InputItemLayout_bottomLineAppearance, 0)
        topLine = parseLineStyle(topResId)
        bottomLine = parseLineStyle(bottomResId)


        if (topLine.enable) {
            topPaint.color = topLine.color
            topPaint.style = Paint.Style.FILL_AND_STROKE
            topPaint.strokeWidth = topLine.height
        }

        if (bottomLine.enable) {
            bottomPaint.color = bottomLine.color
            bottomPaint.style = Paint.Style.FILL_AND_STROKE
            bottomPaint.strokeWidth = bottomLine.height
        }

        array.recycle()
    }

    fun getTitleView(): TextView {
        return titleView
    }

    fun getEditText(): EditText {
        return editText
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //巨坑
        if (topLine.enable) {
            canvas!!.drawLine(
                topLine.leftMargin,
                0f,
                measuredWidth - topLine.rightMargin,
                0f,
                topPaint
            )
        }

        if (bottomLine.enable) {
            canvas!!.drawLine(
                bottomLine.leftMargin,
                height - bottomLine.height,
                measuredWidth - bottomLine.rightMargin,
                height - bottomLine.height,
                bottomPaint
            )
        }
    }


    @SuppressLint("CustomViewStyleable")
    private fun parseInputStyle(hint: String?, resId: Int, inputType: Int) {

        val array = context.obtainStyledAttributes(resId, R.styleable.inputTextAppearance)

        val hintColor = array.getColor(
            R.styleable.inputTextAppearance_hintColor,
            ContextCompat.getColor(context, R.color.color_d1d2)

        )
        val inputColor = array.getColor(
            R.styleable.inputTextAppearance_inputColor,
            ContextCompat.getColor(context, R.color.color_565)
        )
        //px
        val textSize = array.getDimensionPixelSize(
            R.styleable.inputTextAppearance_textSize,
            applyUnit(TypedValue.COMPLEX_UNIT_SP, 15f)
        )

        val maxInputLength = array.getInteger(R.styleable.InputItemLayout_maxInputLength, 20)

        //右侧输入框EditText
        editText = EditText(context)
        editText.filters = arrayOf(InputFilter.LengthFilter(maxInputLength))//最多可输入的字符数
        editText.setPadding(0, 0, 0, 0)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        params.weight = 1f
        editText.layoutParams = params


        editText.hint = hint
        editText.setTextColor(inputColor)
        editText.setHintTextColor(hintColor)
        editText.gravity = Gravity.LEFT or (Gravity.CENTER)
        editText.setBackgroundColor(Color.TRANSPARENT)
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())

        /**
         * <enum name="text" value="0"></enum>
         * <enum name="password" value="1"></enum>
         * <enum name="number" value="2"></enum>
         */
        if (inputType == 0) {
            editText.inputType = InputType.TYPE_CLASS_TEXT
        } else if (inputType == 1) {
            editText.inputType =
                InputType.TYPE_TEXT_VARIATION_PASSWORD or (InputType.TYPE_CLASS_TEXT)
        } else if (inputType == 2) {
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        addView(editText)
        array.recycle()
    }

    @SuppressLint("CustomViewStyleable")
    private fun parseTitleStyle(title: String?, resId: Int) {
        val array = context.obtainStyledAttributes(resId, R.styleable.titleTextAppearance)
        val titleColor = array.getColor(
            R.styleable.titleTextAppearance_titleColor,
            resources.getColor(R.color.color_565)
        )

        //px
        val titleSize = array.getDimensionPixelSize(
            R.styleable.titleTextAppearance_titleSize,
            applyUnit(TypedValue.COMPLEX_UNIT_SP, 15f)
        )

        val minWidth = array.getDimensionPixelOffset(R.styleable.titleTextAppearance_minWidth, 0)

        titleView = TextView(context)
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize.toFloat())  //sp---当做sp在转换一次
        titleView.setTextColor(titleColor)
        titleView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        titleView.minWidth = minWidth
        titleView.gravity = Gravity.LEFT or (Gravity.CENTER)
        titleView.text = title


        addView(titleView)

        array.recycle()
    }

    @SuppressLint("CustomViewStyleable")
    private fun parseLineStyle(resId: Int): Line {
        val line = Line()
        val array = context.obtainStyledAttributes(resId, R.styleable.lineAppearance)
        line.color =
            array.getColor(
                R.styleable.lineAppearance_color,
                ContextCompat.getColor(context, R.color.color_d1d2)
            )
        line.height = array.getDimensionPixelOffset(R.styleable.lineAppearance_height, 0).toFloat()
        line.leftMargin =
            array.getDimensionPixelOffset(R.styleable.lineAppearance_leftMargin, 0).toFloat()
        line.rightMargin =
            array.getDimensionPixelOffset(R.styleable.lineAppearance_rightMargin, 0).toFloat()
        line.enable = array.getBoolean(R.styleable.lineAppearance_enable, false)

        array.recycle()
        return line
    }

    private fun applyUnit(applyUnit: Int, value: Float): Int {
        return TypedValue.applyDimension(applyUnit, value, resources.displayMetrics).toInt()
    }


    inner class Line{
        var color = 0
        var height = 0f
        var leftMargin = 0f
        var rightMargin = 0f;
        var enable: Boolean = false
    }
}