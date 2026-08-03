package com.cipher.assistant.controllers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File

class FileController(private val context: Context) {

    fun openFileManager(): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage("com.android.documentsui")
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse("content://com.android.externalstorage.documents/root/primary"), "resource/folder")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open File Manager", e)
            false
        }
    }

    fun searchFile(fileName: String): List<FileInfo> {
        val results = mutableListOf<FileInfo>()
        val queryLower = fileName.lowercase().trim()

        val rootDirs = listOfNotNull(
            Environment.getExternalStorageDirectory(),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            context.filesDir
        )

        for (rootDir in rootDirs) {
            searchRecursive(rootDir, queryLower, results, depth = 0, maxDepth = 5)
            if (results.size >= 20) break
        }

        return results.take(20)
    }

    private fun searchRecursive(
        directory: File,
        query: String,
        results: MutableList<FileInfo>,
        depth: Int,
        maxDepth: Int
    ) {
        if (depth > maxDepth || !directory.exists() || !directory.isDirectory) return

        val files = directory.listFiles() ?: return
        for (file in files) {
            if (results.size >= 20) return

            if (file.isDirectory) {
                searchRecursive(file, query, results, depth + 1, maxDepth)
            } else if (file.name.lowercase().contains(query)) {
                results.add(file.toFileInfo())
            }
        }
    }

    fun openFile(filePath: String): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false

        return try {
            val mimeType = getMimeType(file)
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open file $filePath", e)
            false
        }
    }

    fun listFilesInDirectory(path: String): List<FileInfo> {
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) return emptyList()

        val files = dir.listFiles() ?: return emptyList()
        return files.map { it.toFileInfo() }
    }

    fun deleteFile(filePath: String): Boolean {
        val file = File(filePath)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    private fun getMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
    }

    private fun File.toFileInfo(): FileInfo {
        return FileInfo(
            name = this.name,
            path = this.absolutePath,
            size = this.length(),
            lastModified = this.lastModified(),
            mimeType = getMimeType(this)
        )
    }

    companion object {
        private const val TAG = "FileController"
    }
}
