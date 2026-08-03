package com.cipher.assistant.controllers

data class FileInfo(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Long,
    val mimeType: String
)
