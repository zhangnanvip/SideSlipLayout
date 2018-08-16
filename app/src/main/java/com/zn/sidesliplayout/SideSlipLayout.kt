package com.zn.sidesliplayout

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout

/**
 * @author zhangnan
 * @date 2018/8/13
 */
class SideSlipLayout : ViewGroup {

    var onMenuItemClickListener: SideSlipMenu.OnMenuItemClickListener? = null
    var onLayoutClickListener: OnLayoutClickListener? = null
    var menuSizePercent: Float = 1 / 12F
    var duration: Long = 1000

    /**
     * 是否展开或者合上
     */
    private var isBack = true

    private val layout: FrameLayout by lazy { FrameLayout(context) }
    private val menus: SideSlipMenu by lazy { SideSlipMenu(context) }
    private var slipStatus: Int = SLIP_END
    private var durationTime: Long = 0
        set(value) {
            field = value
            menus.durationTime = value
        }
    private var oldX: Float = 0F

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        clipChildren = false
        addView(menus)
        addView(layout)
        menus.onMenuItemClickListener = onMenuItemClickListener
    }

    fun inflaterLayout(layout: View) {
        this.layout.removeAllViews()
        this.layout.addView(layout)
    }

    fun inflaterMenus(menuItems: List<View>) {
        this.menus.removeAllViews()
        menuItems.forEach { this.menus.addView(it) }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed) {
            layout.layout(0, 0, width, height)
            layout.getChildAt(0).layout(0, 0, width, height)
            menus.layout(width, 0, width + (width * menuSizePercent * menus.childCount).toInt(), height)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isBack) {
                    return false
                }
                if (slipStatus == SLIP_END) {
                    slipStatus = SLIP_START
                    oldX = event.x
                }
            }
            MotionEvent.ACTION_MOVE -> {
                slipStatus = SLIPPING
                val slipX = oldX - event.x
                if (menus.isExpand() && slipX >= 0) {
                    return false
                }
                if (layout.x == 0F && slipX <= 0) {
                    return false
                }
                val futureX = layout.x - slipX
                when (futureX) {
                    in -10000 until -menus.expandWidth() -> {
                        layout.x = -menus.expandWidth().toFloat()
                        menus.setChildX(slipX)
                    }
                    in -menus.expandWidth() until 0 -> {
                        layout.x -= slipX
                        menus.setChildX(slipX)
                    }
                    in 0..10000 -> {
                        layout.x -= (slipX + futureX)
                        menus.setChildX(slipX + futureX)
                    }
                }
                oldX = event.x
            }
            MotionEvent.ACTION_UP -> {
                when (slipStatus) {
                    SLIP_START -> {
                        if ((layout.width + layout.x) > event.x) {
                            onLayoutClickListener?.onClick()
                        }
                        slipStatus = SLIP_END
                    }
                    SLIPPING -> {
                        if (layout.x != 0F) {
                            isBack = false
                            val toExpand: Boolean = -layout.x >= menus.expandWidth() / 2
                            val toXDelta = if (toExpand) -(menus.expandWidth() + menus.getChildAt(menus.childCount - 1).x * menus.childCount) else -layout.x
                            val backAnimation = TranslateAnimation(0F, toXDelta, 0F, 0F)
                            backAnimation.interpolator = DecelerateInterpolator()
                            durationTime = if (toExpand) (duration.toFloat() * (Math.abs((width + layout.x) / width))).toLong() else (duration.toFloat() * (Math.abs(layout.x / width))).toLong()
                            backAnimation.duration = durationTime
                            backAnimation.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationRepeat(animation: Animation?) {
                                }

                                override fun onAnimationEnd(animation: Animation?) {
                                    if (toExpand) {
                                        layout.x = -menus.expandWidth().toFloat()
                                        menus.expand()
                                        // todo fix splash screen
                                    } else {
                                        layout.x = 0F
                                        menus.close()
                                    }
                                    slipStatus = SLIP_END
                                    isBack = true
                                }

                                override fun onAnimationStart(animation: Animation?) {
                                }
                            })
                            layout.startAnimation(backAnimation)
                            menus.startAnimation(backAnimation)
                            menus.translationForAnimation(toExpand)
                        } else {
                            slipStatus = SLIP_END
                        }
                    }
                }
            }
        }
        return true
    }

    companion object {
        const val SLIP_START = 0
        const val SLIPPING = 1
        const val SLIP_END = 2
    }

    interface OnLayoutClickListener {

        fun onClick()

    }
}