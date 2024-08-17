package com.vscodeners.ui_study_android.feature

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CaptureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var rectPaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 5f
    }
    private var captureRect: Rect? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 빨간 테두리 그리기
        captureRect?.let {
            canvas.drawRect(it, rectPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                endX = event.x
                endY = event.y
                captureRect = Rect(
                    startX.toInt(),
                    startY.toInt(),
                    endX.toInt(),
                    endY.toInt()
                )
                invalidate() // View 갱신
            }
        }
        return true
    }

    fun captureArea(targetView: View): Bitmap? {
        captureRect?.let {
            // 테두리 두께를 양쪽 모두에서 제외
            val strokeWidth = rectPaint.strokeWidth.toInt()
            val adjustedRect = Rect(
                it.left + strokeWidth,
                it.top + strokeWidth,
                it.right - strokeWidth,
                it.bottom - strokeWidth
            )

            val width = adjustedRect.width()
            val height = adjustedRect.height()

            // 비트맵 생성 및 캡처
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.translate(-adjustedRect.left.toFloat(), -adjustedRect.top.toFloat())
            targetView.draw(canvas)

            return bitmap
        }
        return null
    }
}