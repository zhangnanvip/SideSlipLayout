package com.zn.sidesliplayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * @author zhangnan
 * @date 2018/8/13
 */
class SideSlipMenu : ViewGroup {

    private var isExpand = false

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        (0 until childCount).forEach {
            val child = getChildAt(it)
            child.left = 0
            child.top = 0
            child.right = width
            child.bottom = height
        }
    }

    override fun setX(x: Float) {
        isExpand = false
        (0 until childCount).forEach {
            val child = getChildAt(it)
            val futureX = child.x - x * (childCount - it) / childCount
            if (Math.abs(futureX) < width * (childCount - it)) {
                child.x = futureX
            } else {
                child.x = -(childCount - it) * width.toFloat()
                isExpand = true
            }
        }
    }

    fun resetX() {
        (0 until childCount).forEach { getChildAt(it).x = 0F }
    }

    fun isExpand() = isExpand

    fun expandWidth() = width * childCount
}