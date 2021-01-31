package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

@Suppress("UNREACHABLE_CODE")
class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()
    private var progress: Double = 0.0

    private var bgColor: Int = Color.BLUE
    private var textColor: Int = Color.BLACK
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        isDither = true
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimension(R.dimen.textsize).toFloat()
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val animatorUpdateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()
        invalidate()
        requestLayout()
    }

    init {
        valueAnimator = AnimatorInflater.loadAnimator(
            context, R.animator.animation_loading
        ) as ValueAnimator

        valueAnimator.addUpdateListener(animatorUpdateListener)
        isClickable = true

        val attr = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            context.resources.getDimension(R.dimen.default_style_size).toInt(),
            context.resources.getDimension(R.dimen.default_style_size).toInt()
        )
        try {
            bgColor = attr.getColor(
                R.styleable.LoadingButton_bgColor,
                ContextCompat.getColor(context, R.color.colorAccent)
            )
            textColor = attr.getColor(
                R.styleable.LoadingButton_textColor,
                ContextCompat.getColor(context, R.color.colorPrimary)
            )

        } finally {
            attr.recycle()
        }

    }

    public fun startAnimation() {
        valueAnimator.start()
    }

    override fun performClick(): Boolean {
        return super.performClick()
        if (buttonState == ButtonState.Completed) buttonState = ButtonState.Loading
        startAnimation()
        return true
    }

    private val rect = RectF(
        500f,
        50f,
        510f,
        200f
    )

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.strokeWidth =0f
        paint.color = bgColor

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        if (buttonState == ButtonState.Loading) {
            paint.color = Color.parseColor("#004349")
            canvas?.drawRect(
                0f, 0f,
                (width * (progress / 100)).toFloat(), height.toFloat(), paint
            )
            paint.color = Color.parseColor("#F9A825")
            canvas?.drawArc(rect, 0f, (360 * (progress / 100)).toFloat(), true, paint)
        }
        val buttonText =
            if (buttonState == ButtonState.Loading)
                resources.getString(R.string.button_loading)
            else resources.getString(R.string.download)

        paint.color = textColor
        canvas?.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}