package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bgColor: Int = Color.GRAY
    private var textColor: Int = Color.GRAY

    private var widthSize = 0
    private var heightSize = 0
    private var progress: Double = 0.0

    private var animator: ValueAnimator

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Clicked) { p, old, new ->
    }

    private val listener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()
        invalidate()
        requestLayout()
    }

    fun completedDownload() {
        animator.cancel()
        if (buttonState == ButtonState.Loading)
            buttonState = ButtonState.Clicked
        invalidate()
        requestLayout()
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Clicked && !MainActivity.URL.isEmpty()) {
            buttonState = ButtonState.Loading
            startAnimation()
        }else{
           buttonState=ButtonState.Clicked
        }
        return true
    }
    init {
        isClickable=true
        animator = AnimatorInflater.loadAnimator(
            context, R.animator.animation_loading
        ) as ValueAnimator

        animator.addUpdateListener(listener)

        val attr = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,//default attr
            0
        )
        try {
            bgColor = attr.getColor(
                R.styleable.LoadingButton_bgColor,
                ContextCompat.getColor(context, R.color.colorAccent)
            )

            textColor = attr.getColor(
                R.styleable.LoadingButton_textColor,
                ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )
        } finally {
            attr.recycle()
        }
    }

    fun enableClickable(){
        isClickable = true
        isEnabled=true
    }
    fun disableClickable(){
        isClickable = false
        isEnabled=false
    }

    private val paintRec = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        isDither = true
        isUnderlineText=true
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        typeface = Typeface.create(context.getString(R.string.typefacefamily), Typeface.BOLD)
    }

    private fun startAnimation() {
        animator.start()
    }

    private val rect = RectF(
        700f,
        80f,
        800f,
        100f
    )


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paintRec.strokeWidth = 0f
        paintRec.color = bgColor

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintRec)
        if (buttonState == ButtonState.Loading) {
            paintRec.color = Color.parseColor("#006400")
            canvas.drawRect(
                0f, 0f,
                (width * (progress / 100)).toFloat(), height.toFloat(), paintRec
            )
            paintRec.color = Color.parseColor("#2576f9")
            canvas.drawArc(rect, 0f, (360 * (progress / 100)).toFloat(), true, paintRec)
        }
        val buttonText =
            if (buttonState == ButtonState.Loading)
                resources.getString(R.string.button_loading)
            else resources.getString(R.string.download)

        paintRec.color = textColor
        canvas.drawText(buttonText, (width / 2).toFloat(), ((height + 50) / 2).toFloat(), paintRec)
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