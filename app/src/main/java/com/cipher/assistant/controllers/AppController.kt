package com.cipher.assistant.controllers

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log

class AppController(private val context: Context) {

    fun getAllInstalledApps(): List<AppInfo> {
        val packageManager = context.packageManager
        val apps = mutableListOf<AppInfo>()

        try {
            val installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            for (app in installedApplications) {
                // Filter launchable apps
                if (packageManager.getLaunchIntentForPackage(app.packageName) != null) {
                    val appName = packageManager.getApplicationLabel(app).toString()
                    val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    apps.add(AppInfo(name = appName, packageName = app.packageName, isSystemApp = isSystem))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching installed applications", e)
        }

        return apps
    }

    fun findAppByName(name: String): AppInfo? {
        val targetName = name.lowercase().trim()
        val installedApps = getAllInstalledApps()

        // 1. Exact match
        installedApps.find { it.name.lowercase() == targetName }?.let { return it }

        // 2. Starts with / Contains match
        installedApps.find { it.name.lowercase().startsWith(targetName) }?.let { return it }
        installedApps.find { it.name.lowercase().contains(targetName) }?.let { return it }

        // 3. Package name match
        installedApps.find { it.packageName.lowercase().contains(targetName) }?.let { return it }

        return null
    }

    fun openApp(name: String): Boolean {
        val appInfo = findAppByName(name) ?: return false
        val launchIntent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName) ?: return false
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(launchIntent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch app ${appInfo.packageName}", e)
            false
        }
    }

    fun openAppSettings(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open settings for $packageName", e)
        }
    }

    fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getRunningApps(): List<String> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = mutableListOf<String>()

        try {
            val runningProcesses = activityManager.runningAppProcesses ?: emptyList()
            for (process in runningProcesses) {
                runningApps.add(process.processName)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get running app processes", e)
        }

        return runningApps.distinct()
    }

    companion object {
        private const val TAG = "AppController"
    }
}
