package com.cipher.assistant.wakeword

data class WakeWordConfig(
    val sampleRate: Int = 16000,
    val channelConfig: Int = android.media.AudioFormat.CHANNEL_IN_MONO,
    val audioFormat: Int = android.media.AudioFormat.ENCODING_PCM_16BIT,
    val keywordsFile: String = "sherpa-onnx-kws/keywords.txt",
    val encoderModel: String = "sherpa-onnx-kws/encoder.onnx",
    val decoderModel: String = "sherpa-onnx-kws/decoder.onnx",
    val joinerModel: String = "sherpa-onnx-kws/joiner.onnx",
    val tokensFile: String = "sherpa-onnx-kws/tokens.txt",
    val threshold: Float = 0.25f,
    val targetWakeWord: String = "Get Ready Cipher"
)
