package com.laiiiii.photorevive.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.cardview.widget.CardView
import com.laiiiii.photorevive.R

class ShimmerCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val shimmerPaint = Paint()
    private val shimmerMatrix = Matrix()
    private val shimmerGradient: LinearGradient
    private var translateAnimation = 0f
    private val ANIMATION_SPEED = 5f

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        repeatCount = ValueAnimator.INFINITE
        duration = 1000L
        interpolator = LinearInterpolator()
        addUpdateListener {
            translateAnimation += ANIMATION_SPEED
            if (translateAnimation > width + 200) {
                translateAnimation = -200f
            }
            invalidate()
        }
    }

    init {
        val colors = intArrayOf(
            Color.TRANSPARENT,
            Color.parseColor("#40FFFFFF"),
            Color.TRANSPARENT
        )

        val positions = floatArrayOf(0f, 0.5f, 1f)
        shimmerGradient = LinearGradient(
            0f, 0f, 200f, 0f,
            colors,
            positions,
            Shader.TileMode.CLAMP
        )

        shimmerPaint.shader = shimmerGradient
        shimmerPaint.isAntiAlias = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!animator.isRunning) {
            animator.start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (animator.isRunning) {
            animator.cancel()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        shimmerMatrix.setTranslate(translateAnimation, 0f)
        shimmerGradient.setLocalMatrix(shimmerMatrix)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), shimmerPaint)
    }
}
