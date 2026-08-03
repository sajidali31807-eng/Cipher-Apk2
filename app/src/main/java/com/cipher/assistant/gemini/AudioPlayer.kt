package com.cipher.assistant.gemini

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.util.Log

class AudioPlayer(private val context: Context? = null) {

    private var audioTrack: AudioTrack? = null
    private var audioManager: AudioManager? = null
    private var focusRequest: AudioFocusRequest? = null

    var isPlaying: Boolean = false
        private set

    private var currentVolume: Float = 1.0f

    init {
        context?.let {
            audioManager = it.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        }
    }

    fun init() {
        requestAudioFocus()

        val bufferSize = AudioTrack.getMinBufferSize(
            GeminiConfig.AUDIO_SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(GeminiConfig.AUDIO_SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        setVolume(currentVolume)
        audioTrack?.play()
        isPlaying = true
        Log.d(TAG, "AudioTrack initialized for 24kHz voice speech output")
    }

    fun playChunk(pcmBytes: ByteArray) {
        if (!isPlaying || audioTrack == null) {
            init()
        }
        audioTrack?.write(pcmBytes, 0, pcmBytes.size)
    }

    fun setVolume(level: Float) {
        currentVolume = level.coerceIn(0.0f, 1.0f)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                audioTrack?.setVolume(currentVolume)
            } else {
                @Suppress("DEPRECATION")
                audioTrack?.setStereoVolume(currentVolume, currentVolume)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting AudioTrack volume", e)
        }
    }

    fun stopPlayback() {
        stop()
    }

    fun stop() {
        try {
            isPlaying = false
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioTrack", e)
        } finally {
            audioTrack = null
            abandonAudioFocus()
        }
    }

    private fun requestAudioFocus() {
        try {
            audioManager?.let { am ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                        )
                        .build()
                    focusRequest = req
                    am.requestAudioFocus(req)
                } else {
                    @Suppress("DEPRECATION")
                    am.requestAudioFocus(
                        null,
                        AudioManager.STREAM_VOICE_CALL,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request audio focus", e)
        }
    }

    private fun abandonAudioFocus() {
        try {
            audioManager?.let { am ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    focusRequest?.let { am.abandonAudioFocusRequest(it) }
                } else {
                    @Suppress("DEPRECATION")
                    am.abandonAudioFocus(null)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to abandon audio focus", e)
        }
    }

    companion object {
        private const val TAG = "AudioPlayer"
    }
}
