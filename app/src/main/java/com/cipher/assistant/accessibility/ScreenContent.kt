package com.cipher.assistant.accessibility

data class ScreenContent(
    val currentApp: String = "",
    val screenTitle: String = "",
    val allTexts: List<String> = emptyList(),
    val clickableElements: List<String> = emptyList(),
    val inputFields: List<String> = emptyList(),
    val scrollableElements: List<String> = emptyList(),
    val rawNodeDescriptions: List<String> = emptyList()
)
