package com.cipher.assistant.gemini

object GeminiConfig {

    const val GEMINI_LIVE_WEBSOCKET_URL =
        "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent"

    const val MODEL_NAME = "models/gemini-3.1-flash-live-preview"
    const val AUDIO_SAMPLE_RATE = 24000

    val SYSTEM_PROMPT: String = """
        You are Cipher, a brilliant, highly capable, and trusted personal AI phone assistant. You possess complete operational control of this smartphone through system tools, accessibility controls, and background services.

        Core Personality & Language Guidelines:
        1. Bilingual Mastery: Speak fluently in both Hindi and English (and Hinglish). Automatically adapt to whatever language or dialect the user speaks to you.
        2. Conversational & Warm: Be natural, polite, respectful, and professional. Always address the user with respect (use 'aap' / 'aapka' in Hindi contexts). Never sound like a cold or robotic voice engine.
        3. Concise Action Confirmations: When executing tool calls or user tasks, give extremely brief, clear verbal confirmations (e.g., "Opening WhatsApp for you," or "Haan ji, call connect kar raha hoon."). Do NOT speak long multi-sentence paragraphs while performing actions.
        4. Detailed Explanations On Request: Provide detailed, well-structured, and helpful answers only when the user explicitly asks an informational question or requests an explanation.
        5. Confident & Resourceful: Never say "I am an AI model," "I am just a voice program," or "I cannot do that." If a primary tool fails, explain clearly what happened in natural language and suggest a helpful alternative or workaround.
        6. Phone Knowledge: You know everything about this device — apps, settings, contacts, notifications, files, volume, Wi-Fi, battery, and screen content.
        7. Fresh Greetings: Respond promptly and naturally to wake word triggers with warm greetings like "Haan, batao?", "Yes, how can I help?", or "Cipher ready."
    """.trimIndent()

    val TOOLS_DECLARATIONS_JSON: String = """
    [
      {
        "function_declarations": [
          {
            "name": "open_app",
            "description": "Launches an application installed on the device by name",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "appName": { "type": "STRING", "description": "The name or fuzzy match of the application to launch" }
              },
              "required": ["appName"]
            }
          },
          {
            "name": "read_current_screen",
            "description": "Reads and analyzes all text, clickable items, and input fields currently visible on screen",
            "parameters": {
              "type": "OBJECT",
              "properties": {}
            }
          },
          {
            "name": "click_element",
            "description": "Clicks an element on the screen matching the given text or description",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "description": { "type": "STRING", "description": "Text or description of the button/element to click" }
              },
              "required": ["description"]
            }
          },
          {
            "name": "type_text",
            "description": "Types the provided text into the currently focused input field on screen",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "text": { "type": "STRING", "description": "Text content to type into the focused input field" }
              },
              "required": ["text"]
            }
          },
          {
            "name": "scroll",
            "description": "Scrolls the active screen up or down",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "direction": { "type": "STRING", "description": "Direction to scroll: 'up' or 'down'" }
              },
              "required": ["direction"]
            }
          },
          {
            "name": "press_back",
            "description": "Performs system back navigation button press",
            "parameters": {
              "type": "OBJECT",
              "properties": {}
            }
          },
          {
            "name": "press_home",
            "description": "Navigates to the system home screen",
            "parameters": {
              "type": "OBJECT",
              "properties": {}
            }
          },
          {
            "name": "make_call",
            "description": "Initiates a phone call to a specified contact name",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "contactName": { "type": "STRING", "description": "Name of the contact to call" }
              },
              "required": ["contactName"]
            }
          },
          {
            "name": "send_sms",
            "description": "Sends an SMS text message to a specified contact name",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "contactName": { "type": "STRING", "description": "Name of the recipient contact" },
                "message": { "type": "STRING", "description": "Message text body to send" }
              },
              "required": ["contactName", "message"]
            }
          },
          {
            "name": "take_screenshot",
            "description": "Takes a screenshot of the current screen and saves it",
            "parameters": {
              "type": "OBJECT",
              "properties": {}
            }
          },
          {
            "name": "adjust_volume",
            "description": "Sets the media volume level on the device",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "level": { "type": "INTEGER", "description": "Target volume level percentage between 0 and 100" }
              },
              "required": ["level"]
            }
          },
          {
            "name": "set_alarm",
            "description": "Sets a new alarm for a specified time and optional label",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "time": { "type": "STRING", "description": "Time string formatted as HH:mm (24-hour style)" },
                "label": { "type": "STRING", "description": "Alarm description label" }
              },
              "required": ["time"]
            }
          },
          {
            "name": "get_battery_level",
            "description": "Reads current battery percentage level from the device",
            "parameters": {
              "type": "OBJECT",
              "properties": {}
            }
          },
          {
            "name": "toggle_flashlight",
            "description": "Turns the camera flashlight on or off",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "state": { "type": "BOOLEAN", "description": "True to turn flashlight on, false for off" }
              },
              "required": ["state"]
            }
          },
          {
            "name": "toggle_wifi",
            "description": "Opens Wi-Fi quick panel or toggles Wi-Fi state",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "state": { "type": "BOOLEAN", "description": "True to enable Wi-Fi, false to disable" }
              },
              "required": ["state"]
            }
          },
          {
            "name": "toggle_bluetooth",
            "description": "Toggles device Bluetooth adapter state",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "state": { "type": "BOOLEAN", "description": "True to enable Bluetooth, false to disable" }
              },
              "required": ["state"]
            }
          },
          {
            "name": "read_notifications",
            "description": "Reads active notifications currently present in status bar",
            "parameters": {
              "type": "OBJECT",
              "properties": {}
            }
          },
          {
            "name": "navigate_browser",
            "description": "Navigates default web browser to specified web URL",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "url": { "type": "STRING", "description": "Destination web URL (e.g. https://example.com)" }
              },
              "required": ["url"]
            }
          },
          {
            "name": "search_browser",
            "description": "Executes a web search query in default web browser",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "query": { "type": "STRING", "description": "Search query keywords" }
              },
              "required": ["query"]
            }
          },
          {
            "name": "get_all_installed_apps",
            "description": "Retrieves list of all launchable user applications installed on device",
            "parameters": {
              "type": "OBJECT",
              "properties": {}
            }
          },
          {
            "name": "send_whatsapp_message",
            "description": "Opens WhatsApp and sends a message to a contact",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "contactName": { "type": "STRING", "description": "Name of contact or group" },
                "message": { "type": "STRING", "description": "Message text to send" }
              },
              "required": ["contactName", "message"]
            }
          },
          {
            "name": "read_whatsapp_messages",
            "description": "Reads recent messages from a WhatsApp chat",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "contactName": { "type": "STRING", "description": "Name of contact or group" },
                "count": { "type": "INTEGER", "description": "Number of recent messages to read" }
              },
              "required": ["contactName"]
            }
          },
          {
            "name": "open_whatsapp_chat",
            "description": "Opens a specific chat in WhatsApp",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "contactName": { "type": "STRING", "description": "Name of contact or group" }
              },
              "required": ["contactName"]
            }
          },
          {
            "name": "browse_url",
            "description": "Opens a URL in web browser",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "url": { "type": "STRING", "description": "URL to browse" }
              },
              "required": ["url"]
            }
          },
          {
            "name": "search_google",
            "description": "Performs Google web search",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "query": { "type": "STRING", "description": "Search query" }
              },
              "required": ["query"]
            }
          },
          {
            "name": "read_page",
            "description": "Reads current browser page text content",
            "parameters": {
              "type": "OBJECT",
              "properties": {}
            }
          },
          {
            "name": "click_link",
            "description": "Clicks link or button with text on current webpage",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "linkText": { "type": "STRING", "description": "Link or button text" }
              },
              "required": ["linkText"]
            }
          },
          {
            "name": "search_file",
            "description": "Searches storage for file by name",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "fileName": { "type": "STRING", "description": "Name or partial name of file" }
              },
              "required": ["fileName"]
            }
          },
          {
            "name": "open_file",
            "description": "Opens file at given file path",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "filePath": { "type": "STRING", "description": "Absolute file path" }
              },
              "required": ["filePath"]
            }
          },
          {
            "name": "list_directory",
            "description": "Lists all files in directory path",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "path": { "type": "STRING", "description": "Directory path" }
              },
              "required": ["path"]
            }
          },
          {
            "name": "reply_notification",
            "description": "Replies to notification from app",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "appName": { "type": "STRING", "description": "Package or app name" },
                "message": { "type": "STRING", "description": "Reply text" }
              },
              "required": ["appName", "message"]
            }
          },
          {
            "name": "dismiss_notification",
            "description": "Dismisses notification from app",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "appName": { "type": "STRING", "description": "Package or app name" }
              },
              "required": ["appName"]
            }
          },
          {
            "name": "get_notifications_from_app",
            "description": "Reads active notifications from specific app",
            "parameters": {
              "type": "OBJECT",
              "properties": {
                "appName": { "type": "STRING", "description": "Package or app name" }
              },
              "required": ["appName"]
            }
          }
        ]
      }
    ]
    """.trimIndent()
}
