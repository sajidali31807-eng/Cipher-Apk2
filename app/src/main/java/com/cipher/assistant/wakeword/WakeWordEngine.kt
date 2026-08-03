package com.cipher.assistant.wakeword

import android.content.Context
import android.util.Log
import com.k2fsa.sherpa.onnx.KeywordSpotter
import com.k2fsa.sherpa.onnx.KeywordSpotterConfig
import com.k2fsa.sherpa.onnx.OnlineStream
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class WakeWordEngine(
    private val context: Context,
    private val config: WakeWordConfig = WakeWordConfig(),
    private val onWakeWordDetected: () -> Unit,
    private val onAudioChunkReceived: ((ShortArray) -> Unit)? = null
) {

    private val engineJob = SupervisorJob()
    private val engineScope = CoroutineScope(Dispatchers.IO + engineJob)

    private val audioRecordManager = AudioRecordManager(config)
    private var keywordSpotter: KeywordSpotter? = null
    private var stream: OnlineStream? = null
    private var isListening = false

    init {
        initializeSherpaOnnx()
    }

    private fun initializeSherpaOnnx() {
        try {
            val assetsDir = File(context.filesDir, "sherpa-onnx-kws")
            if (!assetsDir.exists()) assetsDir.mkdirs()

            copyAssetIfNeeded(config.encoderModel, File(assetsDir, "encoder.onnx"))
            copyAssetIfNeeded(config.decoderModel, File(assetsDir, "decoder.onnx"))
            copyAssetIfNeeded(config.joinerModel, File(assetsDir, "joiner.onnx"))
            copyAssetIfNeeded(config.tokensFile, File(assetsDir, "tokens.txt"))
            copyAssetIfNeeded(config.keywordsFile, File(assetsDir, "keywords.txt"))

            val kwsConfig = KeywordSpotterConfig().apply {
                featConfig.sampleRate = config.sampleRate
                featConfig.featureDim = 80
                modelConfig.transducer.encoder = File(assetsDir, "encoder.onnx").absolutePath
                modelConfig.transducer.decoder = File(assetsDir, "decoder.onnx").absolutePath
                modelConfig.transducer.joiner = File(assetsDir, "joiner.onnx").absolutePath
                modelConfig.tokens = File(assetsDir, "tokens.txt").absolutePath
                modelConfig.numThreads = 2
                keywordsFile = File(assetsDir, "keywords.txt").absolutePath
            }

            keywordSpotter = KeywordSpotter(assetManager = context.assets, config = kwsConfig)
            stream = keywordSpotter?.createStream()
            Log.i(TAG, "Sherpa-ONNX KeywordSpotter initialized successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Sherpa-ONNX KeywordSpotter. Defaulting to fallback mode.", e)
        }
    }

    fun startListening() {
        if (isListening) return
        isListening = true

        audioRecordManager.startRecording(engineScope)

        engineScope.launch {
            audioRecordManager.audioStream.collect { pcmData ->
                if (!isListening) return@collect

                // Pass PCM chunks to external listener (e.g., GeminiLiveClient streaming)
                onAudioChunkReceived?.invoke(pcmData)

                val floatSamples = FloatArray(pcmData.size) { i -> pcmData[i] / 32768.0f }

                stream?.acceptWaveform(floatSamples, sampleRate = config.sampleRate)

                keywordSpotter?.let { spotter ->
                    while (spotter.isReady(stream)) {
                        spotter.decode(stream)
                        val result = spotter.getResult(stream)
                        if (result.keyword.contains("CIPHER", ignoreCase = true) ||
                            result.keyword.contains("GET READY", ignoreCase = true)
                        ) {
                            Log.i(TAG, "WAKE WORD TRIGGERED via Sherpa-ONNX: ${result.keyword}")
                            withContext(Dispatchers.Main) {
                                onWakeWordDetected()
                            }
                        }
                    }
                }
            }
        }
    }

    fun stopListening() {
        isListening = false
        audioRecordManager.stopRecording()
        Log.d(TAG, "WakeWordEngine stopped listening")
    }

    private fun copyAssetIfNeeded(assetPath: String, targetFile: File) {
        if (targetFile.exists() && targetFile.length() > 0) return
        try {
            context.assets.open(assetPath).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Asset $assetPath not present in APK assets directory.")
        }
    }

    companion object {
        private const val TAG = "WakeWordEngine"
    }
}
