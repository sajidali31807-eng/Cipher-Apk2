import { PhaseInfo, ToolFunction, PermissionItem, AccessibilityNode } from '../types';

export const CIPHER_PHASES: PhaseInfo[] = [
  {
    id: 1,
    title: "Project Structure & Manifest Configuration",
    description: "Initialize Kotlin + Jetpack Compose project, declare all 16 Android permissions, set up build.gradle.kts with Sherpa-ONNX, Gemini GenAI, and Coroutines dependencies.",
    status: "ready",
    category: "Core",
    techStack: ["Kotlin 2.0", "Jetpack Compose", "AndroidX Lifecycle", "Gradle Version Catalogs"],
    keyDeliverables: [
      "Clean package architecture (service, accessibility, ai, ui, receiver, util)",
      "AndroidManifest.xml with FOREGROUND_SERVICE, ACCESSIBILITY_SERVICE, etc.",
      "Dependency declarations for Sherpa-ONNX C++ binaries and Gemini Live SDK"
    ],
    codeSnippet: `// AndroidManifest.xml excerpt
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MICROPHONE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />

<service
    android:name=".service.CipherForegroundService"
    android:foregroundServiceType="microphone"
    android:exported="false" />`
  },
  {
    id: 2,
    title: "Permission Onboarding Wizard UI",
    description: "Jetpack Compose setup wizard guiding users through critical permissions (Accessibility, Notification Listener, System Overlay, Audio, Battery Exemption, Vivo Autostart).",
    status: "ready",
    category: "System & Vivo",
    techStack: ["Jetpack Compose", "Android Intents", "Settings API"],
    keyDeliverables: [
      "Interactive permission checklist screen",
      "Direct deep links to Settings.ACTION_ACCESSIBILITY_SETTINGS",
      "Vivo Y15s Funtouch OS proprietary autostart intent shortcuts"
    ],
    codeSnippet: `// Vivo Autostart Deep Link Intent
fun openVivoAutostart(context: Context) {
    try {
        val intent = Intent().apply {
            component = ComponentName(
                "com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
            )
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to standard App Info
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:\${context.packageName}")
        }
        context.startActivity(intent)
    }
}`
  },
  {
    id: 3,
    title: "Foreground Service & Vivo Persistence Engine",
    description: "24/7 background Foreground Service with persistent ongoing notification, wake lock acquisition, and automatic self-healing restart triggers.",
    status: "ready",
    category: "Core",
    techStack: ["Android ForegroundService", "NotificationManager", "WakeLock"],
    keyDeliverables: [
      "Foreground service with low-memory footprint (< 45MB RAM)",
      "Persistent silent/custom notification channel",
      "Auto-restart trigger handling in onDestroy and AlarmManager fallback"
    ],
    codeSnippet: `class CipherForegroundService : Service() {
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildOngoingNotification())
        acquireWakeLock()
    }
    // ...
}`
  },
  {
    id: 4,
    title: "Sherpa-ONNX Offline Wake Word Engine",
    description: "Integrate Sherpa-ONNX C++ / JNI wrapper for zero-latency, 100% offline detection of 'Get Ready Cipher' with zero battery drain during idle state.",
    status: "ready",
    category: "Voice & AI",
    techStack: ["Sherpa-ONNX Java/Kotlin API", "ONNX Runtime", "AudioRecord JNI"],
    keyDeliverables: [
      "Packaged sherpa-onnx-kws model files in assets",
      "Continuous AudioRecord stream piped into Sherpa-ONNX KeywordSpotter",
      "Instant callback trigger 'Get Ready Cipher' -> launches Gemini Live stream"
    ],
    codeSnippet: `val kwsConfig = KeywordSpotterConfig(
    featConfig = FeatureConfig(sampleRate = 16000, featureDim = 80),
    modelConfig = OnlineModelConfig(
        transducer = OnlineTransducerModelConfig(
            encoder = "encoder.onnx",
            decoder = "decoder.onnx",
            joiner = "joiner.onnx"
        ),
        tokens = "tokens.txt",
        numThreads = 1
    ),
    keywordsFile = "keywords.txt" // Contains: GET READY CIPHER
)`
  },
  {
    id: 5,
    title: "Gemini Live API Voice Brain & WebSockets",
    description: "Connect to Gemini Live API over WebSocket audio streaming with bidirectional PCM audio chunking and Coroutine Channels.",
    status: "ready",
    category: "Voice & AI",
    techStack: ["Google GenAI Live API", "OkHttp WebSocket", "Kotlin Coroutines Flow"],
    keyDeliverables: [
      "Bi-directional audio streaming (PCM 16kHz audio input / 24kHz output)",
      "Bilingual Hindi + English conversational natural voice handling",
      "Non-blocking async tool calling event router"
    ]
  },
  {
    id: 6,
    title: "Floating Visual Orb HUD (SYSTEM_ALERT_WINDOW)",
    description: "Pulsating floating orb overlay rendered over any app during active voice session, providing immediate visual feedback and visual state indication.",
    status: "ready",
    category: "Core",
    techStack: ["WindowManager", "Jetpack Compose ComposeView in WindowManager"],
    keyDeliverables: [
      "Custom floating view attached via WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY",
      "Smooth pulsating animations during listening, thinking, and speaking",
      "Touch pass-through / draggable position state"
    ]
  },
  {
    id: 7,
    title: "Accessibility Service — Screen Reader Engine",
    description: "Build CipherAccessibilityService to traverse screen tree nodes, extract text labels, resource IDs, clickable bounds, and perform gestures.",
    status: "ready",
    category: "Accessibility",
    techStack: ["AccessibilityService", "AccessibilityNodeInfo", "Path / GestureDescription"],
    keyDeliverables: [
      "Real-time screen tree hierarchy parser with instant JSON serialization",
      "Multi-stage fuzzy node search (Resource ID -> Text -> Description -> Vision)",
      "Perform Global Actions: BACK, HOME, RECENTS, NOTIFICATIONS"
    ]
  },
  {
    id: 8,
    title: "Function Calling System & Tool Dispatcher",
    description: "Define 21 tool functions in Gemini API schema and route Gemini tool call payloads to specific Android controllers.",
    status: "ready",
    category: "Automation",
    techStack: ["Gemini Function Calling API", "Kotlin Reflection / Sealed Classes"],
    keyDeliverables: [
      "JsonSchema tool definitions for all 21 actions",
      "Asynchronous dispatcher returning immediate function response to Gemini",
      "Safe exception handling prevents voice session disconnects"
    ]
  },
  {
    id: 9,
    title: "Offline Fallback Command Engine",
    description: "Instant offline intent handler for hardware/system controls (Flashlight, Volume, Battery, Mute, WiFi, Bluetooth) without internet.",
    status: "ready",
    category: "System & Vivo",
    techStack: ["Camera2 API (Flashlight)", "AudioManager", "BatteryManager"],
    keyDeliverables: [
      "Regex & keyword offline intent parser",
      "Direct hardware execution in < 50ms without API calls",
      "Graceful audio feedback for offline state"
    ]
  },
  {
    id: 10,
    title: "App Controller & Dynamic Launcher",
    description: "Launch any installed application by name using PackageManager fuzzy matching and Accessibility Service navigation.",
    status: "ready",
    category: "Automation",
    techStack: ["PackageManager", "Intent.ACTION_MAIN"],
    keyDeliverables: ["Query installed apps map", "Launch apps by spoken voice name", "Fallback app search on Google Play if missing"]
  },
  {
    id: 11,
    title: "WhatsApp Complete Automation",
    description: "Hands-free WhatsApp controller: open chat, search contact, read incoming unread messages, type message, and send click.",
    status: "ready",
    category: "Automation",
    techStack: ["AccessibilityService", "WhatsApp UI Node Tree Traverser"],
    keyDeliverables: [
      "Search contact via WhatsApp search bar resource ID",
      "Extract last N chat bubbles text and read aloud",
      "Automated text input into com.whatsapp:id/entry and click com.whatsapp:id/send"
    ]
  },
  {
    id: 12,
    title: "Browser Automation Controller",
    description: "Voice-driven Chrome / Browser navigation: open URL, search Google, read webpage text nodes, click links, and scroll pages.",
    status: "ready",
    category: "Automation",
    techStack: ["AccessibilityService", "Intent.ACTION_VIEW"],
    keyDeliverables: ["Direct search query address bar injection", "DOM node text reader for web pages", "Voice scroll up/down gesture injection"]
  },
  {
    id: 13,
    title: "File Manager & Local Storage Controller",
    description: "Navigate local files, search documents/audio/video by name, and launch media files in default system players.",
    status: "ready",
    category: "Automation",
    techStack: ["Storage Access Framework", "MediaStore API"],
    keyDeliverables: ["Local media file indexer", "Voice search by filename", "Intent view file launcher"]
  },
  {
    id: 14,
    title: "Notification Reader & Voice Reply Engine",
    description: "NotificationListenerService interceptor reading incoming app notifications and providing inline quick voice replies.",
    status: "ready",
    category: "Automation",
    techStack: ["NotificationListenerService", "NotificationCompat.Action.WearableExtender"],
    keyDeliverables: [
      "Intercept WhatsApp, SMS, Email notifications in background",
      "Read sender and text aloud on user prompt",
      "Execute RemoteInput direct notification reply without opening app"
    ]
  },
  {
    id: 15,
    title: "Bilingual Supernatural Voice Tuning",
    description: "Optimize Gemini Live audio prompts for seamless Hindi + English (Hinglish) code-switching with professional personality.",
    status: "ready",
    category: "Voice & AI",
    techStack: ["System Instructions", "Gemini Audio Voice Preset (Puck/Charon/Aoede)"],
    keyDeliverables: ["Hinglish prompt system prompt engineering", "Contextual short response formatting", "Speed and cadence tuning"]
  },
  {
    id: 16,
    title: "Vivo Y15s RAM & Battery Optimization Suite",
    description: "Special Funtouch OS tweaks: memory node recycling, strict garbage collection after accessibility screen scans, wake-lock release timers.",
    status: "ready",
    category: "System & Vivo",
    techStack: ["AccessibilityNodeInfo.recycle()", "Coroutines Dispatchers.Default", "Android PowerManager"],
    keyDeliverables: [
      "Zero node memory leak architecture (under 3.5GB RAM ceiling)",
      "High background power consumption guide for user",
      "OOM protection and low-memory callback handler"
    ]
  },
  {
    id: 17,
    title: "Auto Start on Boot & Update Receiver",
    description: "BroadcastReceiver listening to ACTION_BOOT_COMPLETED and MY_PACKAGE_REPLACED to keep Cipher active after restarts.",
    status: "ready",
    category: "Core",
    techStack: ["BroadcastReceiver", "Intent.ACTION_BOOT_COMPLETED"],
    keyDeliverables: ["BootReceiver class auto-starting ForegroundService", "Wake lock release after service boot confirmation"]
  },
  {
    id: 18,
    title: "End-to-End System Testing & APK Compilation",
    description: "Final release APK build verification, regression test bench, performance validation on Vivo Y15s device.",
    status: "ready",
    category: "Core",
    techStack: ["Android Studio Gradle Release", "ProGuard / R8 Obfuscation"],
    keyDeliverables: ["Standalone optimized APK generator", "Zero internet wake word benchmark (< 150ms trigger)", "Complete functionality smoke test"]
  }
];

export const CIPHER_TOOLS: ToolFunction[] = [
  {
    name: "open_app",
    description: "Opens any installed application on the user's Android device by name.",
    parameters: {
      app_name: { type: "string", description: "The name of the app to launch (e.g. 'WhatsApp', 'Chrome', 'Settings', 'YouTube')", required: true }
    },
    category: "App Control",
    isOfflineSupported: true
  },
  {
    name: "send_whatsapp_message",
    description: "Navigates WhatsApp, finds the specified contact, types the message and sends it.",
    parameters: {
      contact: { type: "string", description: "Name or phone number of the WhatsApp contact", required: true },
      message: { type: "string", description: "The exact text message to send", required: true }
    },
    category: "Messaging"
  },
  {
    name: "read_whatsapp_messages",
    description: "Opens a WhatsApp conversation with a contact and reads the latest messages from the screen.",
    parameters: {
      contact: { type: "string", description: "Contact name to read messages from", required: true }
    },
    category: "Messaging"
  },
  {
    name: "read_current_screen",
    description: "Scans the entire current screen via Accessibility Tree and returns text, buttons, and elements visible.",
    parameters: {},
    category: "Screen",
    isOfflineSupported: true
  },
  {
    name: "click_element",
    description: "Clicks on a specific button or element on the current screen described by text, content description, or label.",
    parameters: {
      description: { type: "string", description: "Visual description or text label of the target button to click (e.g. 'Send button', 'Settings icon', 'Search')", required: true }
    },
    category: "Screen"
  },
  {
    name: "type_text",
    description: "Types specified text into the currently focused text field on screen.",
    parameters: {
      text: { type: "string", description: "Text content to insert into field", required: true }
    },
    category: "Screen"
  },
  {
    name: "scroll",
    description: "Performs a touch scroll gesture on the current screen.",
    parameters: {
      direction: { type: "string", description: "Direction to scroll: 'up', 'down', 'left', 'right'", required: true }
    },
    category: "Screen"
  },
  {
    name: "navigate_browser",
    description: "Opens web browser and navigates directly to a given URL.",
    parameters: {
      url: { type: "string", description: "The website URL to open", required: true }
    },
    category: "Browser"
  },
  {
    name: "search_browser",
    description: "Performs a web search in browser for the query.",
    parameters: {
      query: { type: "string", description: "Search query string", required: true }
    },
    category: "Browser"
  },
  {
    name: "open_file",
    description: "Finds and opens a document or media file on local storage by filename.",
    parameters: {
      filename: { type: "string", description: "Name or keywords of file to open", required: true }
    },
    category: "App Control",
    isOfflineSupported: true
  },
  {
    name: "make_call",
    description: "Initiates a direct phone call to a contact or phone number.",
    parameters: {
      contact: { type: "string", description: "Contact name or phone number", required: true }
    },
    category: "System",
    isOfflineSupported: true
  },
  {
    name: "send_sms",
    description: "Sends a direct cellular SMS text message.",
    parameters: {
      contact: { type: "string", description: "Contact name or phone number", required: true },
      message: { type: "string", description: "SMS text content", required: true }
    },
    category: "Messaging",
    isOfflineSupported: true
  },
  {
    name: "take_screenshot",
    description: "Captures a screenshot of the current screen for Gemini Vision visual analysis.",
    parameters: {},
    category: "Screen",
    isOfflineSupported: true
  },
  {
    name: "adjust_volume",
    description: "Adjusts system volume level (media, ring, alarm).",
    parameters: {
      level: { type: "number", description: "Volume level percentage from 0 to 100", required: true }
    },
    category: "System",
    isOfflineSupported: true
  },
  {
    name: "set_alarm",
    description: "Sets a system clock alarm.",
    parameters: {
      time: { type: "string", description: "Alarm time in 24h format (e.g. '07:30')", required: true },
      label: { type: "string", description: "Optional alarm title or label" }
    },
    category: "System",
    isOfflineSupported: true
  },
  {
    name: "reply_notification",
    description: "Replies directly to an active notification from WhatsApp, SMS, or app without opening the full application.",
    parameters: {
      app: { type: "string", description: "Target app name (e.g. 'WhatsApp', 'Messages')", required: true },
      message: { type: "string", description: "Reply text content", required: true }
    },
    category: "Messaging",
    isOfflineSupported: true
  },
  {
    name: "read_notifications",
    description: "Reads aloud recent unread notifications captured by NotificationListenerService.",
    parameters: {},
    category: "System",
    isOfflineSupported: true
  },
  {
    name: "toggle_flashlight",
    description: "Turns the camera LED flashlight hardware ON or OFF.",
    parameters: {
      state: { type: "boolean", description: "true to turn ON, false for OFF", required: true }
    },
    category: "System",
    isOfflineSupported: true
  },
  {
    name: "toggle_wifi",
    description: "Toggles WiFi device radio ON or OFF.",
    parameters: {
      state: { type: "boolean", description: "true for ON, false for OFF", required: true }
    },
    category: "System",
    isOfflineSupported: true
  },
  {
    name: "toggle_bluetooth",
    description: "Toggles Bluetooth hardware adapter state.",
    parameters: {
      state: { type: "boolean", description: "true for ON, false for OFF", required: true }
    },
    category: "System",
    isOfflineSupported: true
  },
  {
    name: "get_battery_level",
    description: "Queries current battery percentage and charging state.",
    parameters: {},
    category: "System",
    isOfflineSupported: true
  }
];

export const CIPHER_PERMISSIONS: PermissionItem[] = [
  {
    id: "accessibility",
    name: "Accessibility Service",
    androidPermission: "android.permission.BIND_ACCESSIBILITY_SERVICE",
    description: "Essential for screen reading (eyes) and clicking/typing in apps (hands). Allows Cipher to interact with WhatsApp, Chrome, and system UI.",
    isCritical: true,
    granted: false,
    intentAction: "android.settings.ACCESSIBILITY_SETTINGS"
  },
  {
    id: "overlay",
    name: "System Alert Window (Draw Over Apps)",
    androidPermission: "android.permission.SYSTEM_ALERT_WINDOW",
    description: "Allows rendering the Floating Visual Orb HUD on top of any active screen when Cipher wakes up.",
    isCritical: true,
    granted: false,
    intentAction: "android.settings.action.MANAGE_OVERLAY_PERMISSION"
  },
  {
    id: "audio",
    name: "Record Audio",
    androidPermission: "android.permission.RECORD_AUDIO",
    description: "Required for offline Sherpa-ONNX wake word detection ('Get Ready Cipher') and Gemini Live streaming.",
    isCritical: true,
    granted: false
  },
  {
    id: "notification_listener",
    name: "Notification Listener Service",
    androidPermission: "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE",
    description: "Allows Cipher to intercept incoming WhatsApp, SMS, and app notifications and send quick voice replies.",
    isCritical: true,
    granted: false,
    intentAction: "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
  },
  {
    id: "foreground_service",
    name: "Foreground Service & Microphones",
    androidPermission: "android.permission.FOREGROUND_SERVICE",
    description: "Keeps Cipher running 24/7 in the background with persistent ongoing status notification.",
    isCritical: true,
    granted: false
  },
  {
    id: "battery_optimization",
    name: "Ignore Battery Optimizations",
    androidPermission: "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS",
    description: "Prevents Android OS from sleeping or suspending Cipher's background voice listener.",
    isCritical: true,
    granted: false,
    intentAction: "android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
  },
  {
    id: "vivo_autostart",
    name: "Vivo Funtouch OS Autostart",
    androidPermission: "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager",
    description: "Special Vivo permission allowing Cipher to launch automatically in background on Vivo Y15s without Funtouch OS killing it.",
    isCritical: true,
    granted: false,
    vivoSpecific: true
  },
  {
    id: "boot_completed",
    name: "Receive Boot Completed",
    androidPermission: "android.permission.RECEIVE_BOOT_COMPLETED",
    description: "Automatically restarts Cipher background service when the Vivo phone reboots.",
    isCritical: true,
    granted: false
  },
  {
    id: "contacts",
    name: "Read Contacts",
    androidPermission: "android.permission.READ_CONTACTS",
    description: "Allows matching spoken contact names (e.g. 'Call Rahul', 'WhatsApp Priya') with phone numbers.",
    isCritical: false,
    granted: false
  },
  {
    id: "call_phone",
    name: "Make Phone Calls",
    androidPermission: "android.permission.CALL_PHONE",
    description: "Allows making direct voice phone calls via voice command.",
    isCritical: false,
    granted: false
  },
  {
    id: "sms",
    name: "Send SMS Messages",
    androidPermission: "android.permission.SEND_SMS",
    description: "Allows sending direct text messages via cellular carrier.",
    isCritical: false,
    granted: false
  },
  {
    id: "camera",
    name: "Camera (Flashlight Control)",
    androidPermission: "android.permission.CAMERA",
    description: "Required to control camera torch LED hardware for offline flashlight toggle.",
    isCritical: false,
    granted: false
  }
];

export const MOCK_WHATSAPP_NODE_TREE: AccessibilityNode = {
  id: "root_1",
  className: "android.widget.FrameLayout",
  clickable: false,
  bounds: { x: 0, y: 0, width: 360, height: 720 },
  children: [
    {
      id: "header_1",
      className: "android.widget.LinearLayout",
      clickable: false,
      bounds: { x: 0, y: 0, width: 360, height: 60 },
      children: [
        {
          id: "back_button",
          className: "android.widget.ImageView",
          contentDescription: "Navigate up",
          clickable: true,
          bounds: { x: 10, y: 15, width: 30, height: 30 }
        },
        {
          id: "contact_title",
          className: "android.widget.TextView",
          text: "Rahul Sharma",
          clickable: true,
          bounds: { x: 50, y: 15, width: 200, height: 30 }
        }
      ]
    },
    {
      id: "chat_list",
      className: "android.widget.ListView",
      clickable: true,
      bounds: { x: 0, y: 60, width: 360, height: 580 },
      children: [
        {
          id: "msg_1",
          className: "android.widget.TextView",
          text: "Hey, are you free for the meeting at 4 PM?",
          resourceId: "com.whatsapp:id/message_text",
          clickable: false,
          bounds: { x: 10, y: 80, width: 280, height: 40 }
        },
        {
          id: "msg_2",
          className: "android.widget.TextView",
          text: "Yes, I will send the project files shortly.",
          resourceId: "com.whatsapp:id/message_text",
          clickable: false,
          bounds: { x: 70, y: 130, width: 280, height: 40 }
        }
      ]
    },
    {
      id: "input_bar",
      className: "android.widget.LinearLayout",
      clickable: false,
      bounds: { x: 0, y: 640, width: 360, height: 80 },
      children: [
        {
          id: "entry_field",
          className: "android.widget.EditText",
          text: "Type a message...",
          resourceId: "com.whatsapp:id/entry",
          clickable: true,
          bounds: { x: 10, y: 650, width: 280, height: 50 }
        },
        {
          id: "send_btn",
          className: "android.widget.ImageButton",
          contentDescription: "Send",
          resourceId: "com.whatsapp:id/send",
          clickable: true,
          bounds: { x: 300, y: 650, width: 50, height: 50 }
        }
      ]
    }
  ]
};
