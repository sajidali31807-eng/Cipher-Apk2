package com.cipher.assistant.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.ArrayDeque

class CipherAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        isRunning = true
        Log.i(TAG, "CipherAccessibilityService connected and ready.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Event listener for screen updates or window changes
    }

    override fun onInterrupt() {
        Log.w(TAG, "CipherAccessibilityService interrupted.")
    }

    override fun onDestroy() {
        isRunning = false
        if (instance == this) {
            instance = null
        }
        super.onDestroy()
    }

    // --- SCREEN READING ---

    fun readCurrentScreen(): ScreenContent {
        val rootNode = rootInActiveWindow ?: return ScreenContent()
        val allTexts = mutableListOf<String>()
        val clickableElements = mutableListOf<String>()
        val inputFields = mutableListOf<String>()
        val scrollableElements = mutableListOf<String>()
        val rawNodeDescriptions = mutableListOf<String>()

        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(rootNode)

        try {
            while (!queue.isEmpty()) {
                val node = queue.poll() ?: continue

                val text = node.text?.toString()?.trim()
                val description = node.contentDescription?.toString()?.trim()
                val className = node.className?.toString() ?: ""

                val label = text ?: description ?: ""

                if (label.isNotEmpty()) {
                    allTexts.add(label)
                    rawNodeDescriptions.add("[$className] $label")
                }

                if (node.isClickable && label.isNotEmpty()) {
                    clickableElements.add(label)
                }

                if (node.isEditable || className.contains("EditText", ignoreCase = true)) {
                    inputFields.add(label.ifEmpty { "Input Field (${node.viewIdResourceName ?: "unnamed"})" })
                }

                if (node.isScrollable) {
                    scrollableElements.add(label.ifEmpty { className })
                }

                for (i in 0 until node.childCount) {
                    val child = node.getChild(i)
                    if (child != null) {
                        queue.add(child)
                    }
                }

                // Recycle intermediate node if not rootNode
                if (node != rootNode) {
                    node.recycle()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error traversing accessibility tree", e)
        } finally {
            rootNode.recycle()
        }

        return ScreenContent(
            currentApp = getCurrentAppPackage(),
            screenTitle = getCurrentActivityTitle(),
            allTexts = allTexts.distinct(),
            clickableElements = clickableElements.distinct(),
            inputFields = inputFields.distinct(),
            scrollableElements = scrollableElements.distinct(),
            rawNodeDescriptions = rawNodeDescriptions
        )
    }

    fun findElementByText(text: String): AccessibilityNodeInfo? {
        val rootNode = rootInActiveWindow ?: return null
        val targetLower = text.lowercase().trim()

        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(rootNode)

        try {
            while (!queue.isEmpty()) {
                val node = queue.poll() ?: continue

                val nodeText = node.text?.toString()?.lowercase() ?: ""
                val nodeDesc = node.contentDescription?.toString()?.lowercase() ?: ""

                if (nodeText.contains(targetLower) || nodeDesc.contains(targetLower)) {
                    // Drain and recycle remaining queued nodes
                    while (!queue.isEmpty()) {
                        val remaining = queue.poll()
                        if (remaining != rootNode) remaining?.recycle()
                    }
                    if (rootNode != node) rootNode.recycle()
                    return node
                }

                for (i in 0 until node.childCount) {
                    val child = node.getChild(i)
                    if (child != null) queue.add(child)
                }

                if (node != rootNode) {
                    node.recycle()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in findElementByText", e)
        } finally {
            rootNode.recycle()
        }
        return null
    }

    fun findElementByDescription(description: String): AccessibilityNodeInfo? {
        return findElementByText(description)
    }

    fun findClickableElements(): List<AccessibilityNodeInfo> {
        val rootNode = rootInActiveWindow ?: return emptyList()
        val result = mutableListOf<AccessibilityNodeInfo>()
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(rootNode)

        try {
            while (!queue.isEmpty()) {
                val node = queue.poll() ?: continue

                if (node.isClickable) {
                    result.add(AccessibilityNodeInfo.obtain(node))
                }

                for (i in 0 until node.childCount) {
                    val child = node.getChild(i)
                    if (child != null) queue.add(child)
                }

                if (node != rootNode) {
                    node.recycle()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding clickable elements", e)
        } finally {
            rootNode.recycle()
        }
        return result
    }

    fun getCurrentAppPackage(): String {
        val rootNode = rootInActiveWindow ?: return ""
        return try {
            rootNode.packageName?.toString() ?: ""
        } finally {
            rootNode.recycle()
        }
    }

    fun getCurrentActivityTitle(): String {
        val rootNode = rootInActiveWindow ?: return ""
        return try {
            rootNode.text?.toString() ?: rootNode.contentDescription?.toString() ?: getCurrentAppPackage()
        } finally {
            rootNode.recycle()
        }
    }

    // --- ACCESSIBILITY ACTIONS ---

    fun clickElement(nodeInfo: AccessibilityNodeInfo): Boolean {
        return try {
            var targetNode: AccessibilityNodeInfo? = nodeInfo
            while (targetNode != null && !targetNode.isClickable) {
                targetNode = targetNode.parent
            }

            if (targetNode != null) {
                val success = targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                if (targetNode != nodeInfo) {
                    targetNode.recycle()
                }
                success
            } else {
                // Fallback: Gesture Click at element center bounds
                val bounds = Rect()
                nodeInfo.getBoundsInScreen(bounds)
                clickAtPoint(bounds.exactCenterX(), bounds.exactCenterY())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error performing click element", e)
            false
        } finally {
            nodeInfo.recycle()
        }
    }

    fun clickElementByText(text: String): Boolean {
        val node = findElementByText(text) ?: return false
        return clickElement(node)
    }

    fun typeText(nodeInfo: AccessibilityNodeInfo, text: String): Boolean {
        return try {
            val arguments = android.os.Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            }
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting text on node", e)
            false
        } finally {
            nodeInfo.recycle()
        }
    }

    fun typeTextInFocused(text: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        try {
            val focusedNode = rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
            if (focusedNode != null) {
                return typeText(focusedNode, text)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error typing in focused node", e)
        } finally {
            rootNode.recycle()
        }
        return false
    }

    fun scrollDown(): Boolean {
        val displayMetrics = resources.displayMetrics
        val centerX = displayMetrics.widthPixels / 2f
        val startY = displayMetrics.heightPixels * 0.7f
        val endY = displayMetrics.heightPixels * 0.3f
        return performSwipeGesture(centerX, startY, centerX, endY)
    }

    fun scrollUp(): Boolean {
        val displayMetrics = resources.displayMetrics
        val centerX = displayMetrics.widthPixels / 2f
        val startY = displayMetrics.heightPixels * 0.3f
        val endY = displayMetrics.heightPixels * 0.7f
        return performSwipeGesture(centerX, startY, centerX, endY)
    }

    fun pressBack(): Boolean = performGlobalAction(GLOBAL_ACTION_BACK)

    fun pressHome(): Boolean = performGlobalAction(GLOBAL_ACTION_HOME)

    fun openRecents(): Boolean = performGlobalAction(GLOBAL_ACTION_RECENTS)

    fun swipeLeft(): Boolean {
        val displayMetrics = resources.displayMetrics
        val centerY = displayMetrics.heightPixels / 2f
        val startX = displayMetrics.widthPixels * 0.8f
        val endX = displayMetrics.widthPixels * 0.2f
        return performSwipeGesture(startX, centerY, endX, centerY)
    }

    fun swipeRight(): Boolean {
        val displayMetrics = resources.displayMetrics
        val centerY = displayMetrics.heightPixels / 2f
        val startX = displayMetrics.widthPixels * 0.2f
        val endX = displayMetrics.widthPixels * 0.8f
        return performSwipeGesture(startX, centerY, endX, centerY)
    }

    private fun clickAtPoint(x: Float, y: Float): Boolean {
        val path = Path().apply {
            moveTo(x, y)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()
        return dispatchGesture(gesture, null, null)
    }

    private fun performSwipeGesture(startX: Float, startY: Float, endX: Float, endY: Float): Boolean {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()
        return dispatchGesture(gesture, null, null)
    }

    companion object {
        private const val TAG = "CipherAccessibilityService"

        @Volatile
        var instance: CipherAccessibilityService? = null
            private set

        @Volatile
        var isRunning: Boolean = false
            private set
    }
}
