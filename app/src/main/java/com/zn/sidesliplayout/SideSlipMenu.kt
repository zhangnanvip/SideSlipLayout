package com.zn.sidesliplayout

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation

/**
 * @author zhangnan
 * @date 2018/8/13
 */
class SideSlipMenu : ViewGroup {

    var onMenuItemClickListener: OnMenuItemClickListener? = null
    var durationTime: Long = 0
    private var isExpand = false

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        (0 until childCount).forEach { position ->
            val child = getChildAt(position)
            child.left = 0
            child.top = 0
            child.right = width
            child.bottom = height
            child.setOnClickListener { onMenuItemClickListener?.onMenuClick(position) }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // todo add click listener
        return super.onTouchEvent(event)
    }

    fun setChildX(x: Float) {
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

    fun close() {
        (0 until childCount).forEach { getChildAt(it).x = 0F }
        isExpand = false
    }

    fun expand() {
        (0 until childCount).forEach {
            getChildAt(it).x = -(childCount - it) * width.toFloat()
        }
        isExpand = true
    }

    fun translationForAnimation(toExpand: Boolean) {
        (0 until childCount).forEach {
            val toXDelta: Float = if (toExpand) {
                if (it == 0) 0F else (width + getChildAt(childCount - 1).x) * it
            } else {
                if (it == 0) 0F else getChildAt(childCount - 1).x * it
            }
            val backAnimation = TranslateAnimation(0F, toXDelta, 0F, 0F)
            backAnimation.interpolator = DecelerateInterpolator()
            backAnimation.duration = durationTime
            getChildAt(it).startAnimation(backAnimation)
        }
    }

    fun isExpand() = isExpand

    fun expandWidth() = width * childCount

    interface OnMenuItemClickListener {

        fun onMenuClick(position: Int)

    }
}