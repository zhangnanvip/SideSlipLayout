package com.zn.sidesliplayout

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * @author zhangnan
 * @date 2018/8/13
 */
class SideSlipLayout : ViewGroup {

    var onLayoutClickListener: OnLayoutClickListener? = null
    var menuSizePercent: Float = 1 / 12F

    private val layout: FrameLayout by lazy { FrameLayout(context) }
    private val menus: SideSlipMenu by lazy { SideSlipMenu(context) }
    private var slipStatus: Int = SLIP_END

    private var oldX: Float = 0F

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        clipChildren = false
        addView(menus)
        addView(layout)
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
                if (slipStatus == SLIP_END) {
                    slipStatus = SLIP_START
                    oldX = event.x
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val slipX = oldX - event.x
                if (menus.isExpand() && slipX >= 0) {
                    return false
                }
                if (layout.x == 0F && slipX <= 0) {
                    return false
                }
                when (slipStatus) {
                    SLIP_START -> slipStatus = SLIPPING
                    SLIPPING -> {
                        val futureX = layout.x - slipX
                        when (futureX) {
                            in -10000 until -menus.expandWidth() -> {
                                layout.x = -menus.expandWidth().toFloat()
                                menus.x = slipX
                            }
                            in -menus.expandWidth() until 0 -> {
                                layout.x -= slipX
                                menus.x = slipX
                            }
                            in 0..10000 -> {
                                layout.x -= (slipX + futureX)
                                menus.x = (slipX + futureX)
                            }
                        }
//                        if (futureX < -menus.expandWidth()) {
//                            layout.x = -menus.expandWidth().toFloat()
//                            menus.x = slipX
//                        } else {
//                            if (futureX < 0) {
//                                layout.x -= slipX
//                                menus.x = slipX
//                            } else {
//                                layout.x -= (slipX + futureX)
//                                menus.x = (slipX + futureX)
//                            }
//                        }
                        oldX = event.x
                    }
                }
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
//                            layout.x = 0F
//                            menus.resetX()
                            slipStatus = SLIP_END
//                    val layoutAnimator = ObjectAnimator.ofFloat(layout, "translationX", layout.x)
//                    layoutAnimator.interpolator = DecelerateInterpolator()
//                    layoutAnimator.duration = 1000
//                    layoutAnimator.addListener(object : Animator.AnimatorListener {
//                        override fun onAnimationRepeat(animation: Animator?) {
//                        }
//
//                        override fun onAnimationEnd(animation: Animator?) {
//                            slipStatus = SLIP_END
//                        }
//
//                        override fun onAnimationCancel(animation: Animator?) {
//                        }
//
//                        override fun onAnimationStart(animation: Animator?) {
//                        }
//                    })
//                    layoutAnimator.start()
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
        private const val SLIP_START = 0
        private const val SLIPPING = 1
        private const val SLIP_END = 2
    }

    interface OnLayoutClickListener {

        fun onClick()

    }
}