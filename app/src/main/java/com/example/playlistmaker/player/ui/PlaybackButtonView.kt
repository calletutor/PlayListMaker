package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null

    private var rect = RectF()

    private var isPlaying = false

    private var onPlaybackToggle: (() -> Unit)? = null
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            0,
            0
        ).apply {
            try {
                val playResId = getResourceId(R.styleable.PlaybackButtonView_playIcon, -1)
                val pauseResId = getResourceId(R.styleable.PlaybackButtonView_pauseIcon, -1)

                if (playResId != -1) {
                    playBitmap = getBitmapFromVector(playResId)
                }
                if (pauseResId != -1) {
                    pauseBitmap = getBitmapFromVector(pauseResId)
                }
            } finally {
                recycle()
            }
        }
    }

    // Приватная функция для конвертации вектора в Bitmap
    private fun getBitmapFromVector(resId: Int): Bitmap? {
        val drawable = AppCompatResources.getDrawable(context, resId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat()) // вместо пересоздания rect
    }

    override fun onDraw(canvas: Canvas) {
        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        bitmap?.let {
            canvas.drawBitmap(it, null, rect, null) // <-- растягиваем в прямоугольник rect
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // сообщаем системе: будем отслеживать дальнейшие события
                return true
            }
            MotionEvent.ACTION_UP -> {
                toggle()
                onPlaybackToggle?.invoke()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /** Меняем состояние кнопки */
    fun toggle() {
        isPlaying = !isPlaying
        invalidate()
    }

    /** Программно задаём состояние (например, из ViewModel) */
    fun setPlaying(isPlaying: Boolean) {
        this.isPlaying = isPlaying
        invalidate()
    }

    /** Подписка на нажатие */
    fun setOnPlaybackToggleListener(listener: () -> Unit) {
        onPlaybackToggle = listener
    }
}
