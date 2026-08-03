# Keep Sherpa-ONNX C++ and Kotlin bindings
-keep class com.k2fsa.sherpa.onnx.** { *; }
-keepclassmembers class com.k2fsa.sherpa.onnx.** { *; }

# Keep OkHttp & Gson for Gemini WebSocket API
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class com.google.gson.** { *; }

# Keep Cipher Application and Services
-keep class com.cipher.assistant.** { *; }
-keepclassmembers class com.cipher.assistant.** { *; }
