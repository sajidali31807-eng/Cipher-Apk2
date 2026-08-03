package com.cipher.assistant.service

import android.animation.ValueAnimator
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator

class FloatingOrbService : Service() {

    private var windowManager: WindowManager? = null
    private var orbView: OrbCanvasView? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    private val orbStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_SHOW_LISTENING -> showListeningState()
                ACTION_SHOW_PROCESSING -> showProcessingState()
                ACTION_HIDE_ORB -> hideOrbState()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "FloatingOrbService onCreate initializing overlay window...")
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val sizePx = (80 * resources.displayMetrics.density).toInt()
        val marginPx = (24 * resources.displayMetrics.density).toInt()

        layoutParams = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            x = marginPx
            y = marginPx
        }

        orbView = OrbCanvasView(this)

        try {
            windowManager?.addView(orbView, layoutParams)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add overlay orb view. Ensure SYSTEM_ALERT_WINDOW permission granted.", e)
        }

        val filter = IntentFilter().apply {
            addAction(ACTION_SHOW_LISTENING)
            addAction(ACTION_SHOW_PROCESSING)
            addAction(ACTION_HIDE_ORB)
        }
        registerReceiver(orbStateReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW_LISTENING -> showListeningState()
            ACTION_SHOW_PROCESSING -> showProcessingState()
            ACTION_HIDE_ORB -> hideOrbState()
        }
        return START_STICKY
    }

    private fun showListeningState() {
        orbView?.setState(OrbState.LISTENING)
    }

    private fun showProcessingState() {
        orbView?.setState(OrbState.PROCESSING)
    }

    private fun hideOrbState() {
        orbView?.setState(OrbState.STANDBY)
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(orbStateReceiver)
        } catch (e: Exception) {
            Log.w(TAG, "Receiver not registered or already unregistered")
        }
        orbView?.stopAnimations()
        if (orbView != null && windowManager != null) {
            try {
                windowManager?.removeView(orbView)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove orb view from window manager", e)
            }
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // --- CANVAS ORB CUSTOM VIEW ---
    enum class OrbState { STANDBY, LISTENING, PROCESSING }

    private class OrbCanvasView(context: Context) : View(context) {

        private var currentState = OrbState.STANDBY

        private val cyanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E5FF")
            style = Paint.Style.FILL
        }

        private val cyanStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E5FF")
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        private val purpleArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#9D4EDD")
            style = Paint.Style.STROKE
            strokeWidth = 8f
            strokeCap = Paint.Cap.ROUND
        }

        private val auraPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E5FF")
            style = Paint.Style.FILL
        }

        private var pulseValue = 0f
        private var rotationValue = 0f
        private var rippleRadius1 = 0f
        private var rippleRadius2 = 0f

        private val pulseAnimator = ValueAnimator.ofFloat(0.85f, 1.15f).apply {
            duration = 1200
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                pulseValue = it.animatedValue as Float
                invalidate()
            }
        }

        private val rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                rotationValue = it.animatedValue as Float
                invalidate()
            }
        }

        private val rippleAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1800
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                val progress = it.animatedValue as Float
                rippleRadius1 = progress
                rippleRadius2 = (progress + 0.5f) % 1.0f
                invalidate()
            }
        }

        init {
            pulseAnimator.start()
            rotationAnimator.start()
            rippleAnimator.start()
        }

        fun setState(state: OrbState) {
            this.currentState = state
            invalidate()
        }

        fun stopAnimations() {
            pulseAnimator.cancel()
            rotationAnimator.cancel()
            rippleAnimator.cancel()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val centerX = width / 2f
            val centerY = height / 2f
            val baseRadius = (minOf(width, height) / 2f) * 0.45f

            when (currentState) {
                OrbState.STANDBY -> {
                    // Faint pulsing orb at 15% opacity
                    auraPaint.alpha = (38 * pulseValue).toInt().coerceIn(0, 255)
                    cyanPaint.alpha = (38 * pulseValue).toInt().coerceIn(0, 255)

                    canvas.drawCircle(centerX, centerY, baseRadius * pulseValue, cyanPaint)
                }

                OrbState.LISTENING -> {
                    // Fully visible electric blue pulsing orb + ripple rings expanding
                    val maxRipple = baseRadius * 1.8f

                    // Ripple 1
                    cyanStrokePaint.alpha = ((1f - rippleRadius1) * 200).toInt().coerceIn(0, 255)
                    canvas.drawCircle(centerX, centerY, baseRadius + (maxRipple - baseRadius) * rippleRadius1, cyanStrokePaint)

                    // Ripple 2
                    cyanStrokePaint.alpha = ((1f - rippleRadius2) * 200).toInt().coerceIn(0, 255)
                    canvas.drawCircle(centerX, centerY, baseRadius + (maxRipple - baseRadius) * rippleRadius2, cyanStrokePaint)

                    // Core Orb
                    auraPaint.alpha = 60
                    canvas.drawCircle(centerX, centerY, baseRadius * pulseValue * 1.3f, auraPaint)

                    cyanPaint.alpha = 255
                    canvas.drawCircle(centerX, centerY, baseRadius * pulseValue, cyanPaint)
                }

                OrbState.PROCESSING -> {
                    // Rotating purple arc segments around center
                    cyanPaint.alpha = 200
                    canvas.drawCircle(centerX, centerY, baseRadius * 0.7f, cyanPaint)

                    val oval = RectF(
                        centerX - baseRadius * 1.1f,
                        centerY - baseRadius * 1.1f,
                        centerX + baseRadius * 1.1f,
                        centerY + baseRadius * 1.1f
                    )

                    canvas.save()
                    canvas.rotate(rotationValue, centerX, centerY)

                    purpleArcPaint.alpha = 255
                    canvas.drawArc(oval, 0f, 90f, false, purpleArcPaint)
                    canvas.drawArc(oval, 180f, 90f, false, purpleArcPaint)

                    canvas.restore()
                }
            }
        }
    }

    companion object {
        private const val TAG = "FloatingOrbService"

        const val ACTION_SHOW_LISTENING = "com.cipher.assistant.ACTION_SHOW_LISTENING"
        const val ACTION_SHOW_PROCESSING = "com.cipher.assistant.ACTION_SHOW_PROCESSING"
        const val ACTION_HIDE_ORB = "com.cipher.assistant.ACTION_HIDE_ORB"

        fun showListening(context: Context) {
            val intent = Intent(context, FloatingOrbService::class.java).apply {
                action = ACTION_SHOW_LISTENING
            }
            context.startService(intent)
        }

        fun showProcessing(context: Context) {
            val intent = Intent(context, FloatingOrbService::class.java).apply {
                action = ACTION_SHOW_PROCESSING
            }
            context.startService(intent)
        }

        fun hide(context: Context) {
            val intent = Intent(context, FloatingOrbService::class.java).apply {
                action = ACTION_HIDE_ORB
            }
            context.startService(intent)
        }
    }
}
