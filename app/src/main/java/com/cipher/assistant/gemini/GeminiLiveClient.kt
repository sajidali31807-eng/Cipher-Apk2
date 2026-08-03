package com.cipher.assistant.gemini

import android.content.Context
import android.util.Base64
import android.util.Log
import com.cipher.assistant.data.PreferencesManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiLiveClient(private val context: Context) {

    private val clientJob = SupervisorJob()
    private val clientScope = CoroutineScope(Dispatchers.IO + clientJob)

    private val preferencesManager = PreferencesManager(context)
    private val functionCallHandler = FunctionCallHandler(context)

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    fun connect() {
        if (_connectionState.value == ConnectionState.Connecting || _connectionState.value == ConnectionState.Connected) {
            return
        }

        val apiKey = preferencesManager.getApiKey()
        if (apiKey.isBlank()) {
            Log.e(TAG, "Cannot connect: Gemini API Key is missing in preferences!")
            _connectionState.value = ConnectionState.Error("API Key missing")
            return
        }

        _connectionState.value = ConnectionState.Connecting

        val url = "${GeminiConfig.GEMINI_LIVE_WEBSOCKET_URL}?key=$apiKey"
        val request = Request.Builder().url(url).build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.i(TAG, "Gemini Live WebSocket opened successfully.")
                _connectionState.value = ConnectionState.Connected
                sendSetupMessage(webSocket)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleIncomingMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                handleIncomingMessage(bytes.utf8())
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "Gemini Live WebSocket connection failure", t)
                _connectionState.value = ConnectionState.Error(t.localizedMessage ?: "WebSocket connection failed")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.w(TAG, "Gemini Live WebSocket closed: $reason ($code)")
                _connectionState.value = ConnectionState.Disconnected
            }
        })
    }

    private fun sendSetupMessage(ws: WebSocket) {
        try {
            val setupJson = JSONObject().apply {
                put("setup", JSONObject().apply {
                    put("model", GeminiConfig.MODEL_NAME)
                    put("generationConfig", JSONObject().apply {
                        put("responseModalities", JSONArray().apply {
                            put("AUDIO")
                        })
                        put("speechConfig", JSONObject().apply {
                            put("voiceConfig", JSONObject().apply {
                                put("prebuiltVoiceConfig", JSONObject().apply {
                                    put("voiceName", "Puck")
                                })
                            })
                        })
                    })
                    put("tools", JSONArray(GeminiConfig.TOOLS_DECLARATIONS_JSON))
                })
            }

            ws.send(setupJson.toString())
            Log.i(TAG, "Sent setup configuration with tools to Gemini Live API.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send setup message", e)
        }
    }

    fun sendAudioChunk(pcmBytes: ByteArray) {
        if (_connectionState.value != ConnectionState.Connected) return

        try {
            val base64Audio = Base64.encodeToString(pcmBytes, Base64.NO_WRAP)
            val audioPayload = JSONObject().apply {
                put("realtimeInput", JSONObject().apply {
                    put("mediaChunks", JSONArray().apply {
                        put(JSONObject().apply {
                            put("mimeType", "audio/pcm;rate=16000")
                            put("data", base64Audio)
                        })
                    })
                })
            }
            webSocket?.send(audioPayload.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error sending audio chunk to Gemini", e)
        }
    }

    private fun handleIncomingMessage(text: String) {
        try {
            val json = JSONObject(text)

            // Handle serverContent (audio response or text)
            if (json.has("serverContent")) {
                val serverContent = json.getJSONObject("serverContent")
                if (serverContent.has("modelTurn")) {
                    val parts = serverContent.getJSONObject("modelTurn").optJSONArray("parts")
                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            if (part.has("inlineData")) {
                                val inlineData = part.getJSONObject("inlineData")
                                val mimeType = inlineData.optString("mimeType")
                                if (mimeType.startsWith("audio/")) {
                                    val audioBase64 = inlineData.getString("data")
                                    val pcmData = Base64.decode(audioBase64, Base64.DEFAULT)
                                    // Audio play back logic can receive pcmData
                                }
                            }
                        }
                    }
                }
            }

            // Handle toolCall / functionCalls from Gemini
            if (json.has("toolCall")) {
                val toolCall = json.getJSONObject("toolCall")
                val functionCalls = toolCall.optJSONArray("functionCalls")
                if (functionCalls != null) {
                    for (i in 0 until functionCalls.length()) {
                        val call = functionCalls.getJSONObject(i)
                        val name = call.getString("name")
                        val id = call.optString("id", "call_$i")
                        val args = call.optJSONObject("args") ?: JSONObject()

                        clientScope.launch {
                            val functionResult = functionCallHandler.executeFunction(name, args)
                            sendToolResponse(id, name, functionResult)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing incoming Gemini WebSocket message", e)
        }
    }

    private fun sendToolResponse(callId: String, name: String, responseData: JSONObject) {
        try {
            val toolResponse = JSONObject().apply {
                put("toolResponse", JSONObject().apply {
                    put("functionResponses", JSONArray().apply {
                        put(JSONObject().apply {
                            put("response", responseData)
                            put("id", callId)
                            put("name", name)
                        })
                    })
                })
            }
            webSocket?.send(toolResponse.toString())
            Log.i(TAG, "Sent toolResponse back to Gemini for call $callId ($name)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send tool response to Gemini", e)
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnect")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
        Log.d(TAG, "GeminiLiveClient disconnected")
    }

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connecting : ConnectionState()
        object Connected : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    companion object {
        private const val TAG = "GeminiLiveClient"
    }
}
