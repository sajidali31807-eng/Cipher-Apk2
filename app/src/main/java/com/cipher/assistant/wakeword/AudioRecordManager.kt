package com.cipher.assistant.wakeword

import android.annotation.SuppressLint
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AudioRecordManager(private val config: WakeWordConfig = WakeWordConfig()) {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingJob: Job? = null

    private val audioChannel = Channel<ShortArray>(Channel.UNLIMITED)
    val audioStream: Flow<ShortArray> = audioChannel.receiveAsFlow()

    @SuppressLint("MissingPermission")
    fun startRecording(scope: CoroutineScope) {
        if (isRecording) return

        val minBufferSize = AudioRecord.getMinBufferSize(
            config.sampleRate,
            config.channelConfig,
            config.audioFormat
        )

        val bufferSize = (minBufferSize * 2).coerceAtLeast(config.sampleRate / 10 * 2)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            config.sampleRate,
            config.channelConfig,
            config.audioFormat,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "AudioRecord failed to initialize")
            return
        }

        audioRecord?.startRecording()
        isRecording = true
        Log.d(TAG, "AudioRecord started streaming at ${config.sampleRate}Hz")

        recordingJob = scope.launch(Dispatchers.IO) {
            val chunkBuffer = ShortArray(config.sampleRate / 10) // 100ms chunks
            while (isRecording) {
                val readSize = audioRecord?.read(chunkBuffer, 0, chunkBuffer.size) ?: 0
                if (readSize > 0) {
                    val actualData = chunkBuffer.copyOf(readSize)
                    audioChannel.trySend(actualData)
                }
            }
        }
    }

    fun stopRecording() {
        isRecording = false
        recordingJob?.cancel()
        recordingJob = null

        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioRecord", e)
        } finally {
            audioRecord = null
        }
        Log.d(TAG, "AudioRecord stopped")
    }

    companion object {
        private const val TAG = "AudioRecordManager"
    }
}
