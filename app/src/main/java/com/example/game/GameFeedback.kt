package com.example.game

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class GameFeedback(private val context: Context) {
    private var vibrator: Vibrator? = null
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    fun hitFeedback(enabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (hapticsEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(15)
                }
            } catch (_: Exception) {}
        }
        if (enabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 25)
            } catch (_: Exception) {}
        }
    }

    fun explodeFeedback(enabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (hapticsEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 20, 40), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(40)
                }
            } catch (_: Exception) {}
        }
        if (enabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
            } catch (_: Exception) {}
        }
    }

    fun victoryFeedback(enabled: Boolean = true) {
        if (enabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 120)
            } catch (_: Exception) {}
        }
    }
}
