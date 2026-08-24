package com.ourlauncher.app

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable? = null
)

class AppRepository(private val context: Context) {

    fun getInstalledApps(): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, 0)

        return resolveInfos.map { resolveInfo ->
            AppInfo(
                label = resolveInfo.loadLabel(pm).toString(),
                packageName = resolveInfo.activityInfo.packageName,
                icon = resolveInfo.loadIcon(pm)
            )
        }.sortedBy { it.label.lowercase() }
    }

    fun launchApp(app: AppInfo, sourceBounds: Rect? = null) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            ?: return

        if (sourceBounds != null && context is Activity) {
            val rootView = context.window.decorView
            val options = ActivityOptions.makeClipRevealAnimation(
                rootView,
                sourceBounds.left,
                sourceBounds.top,
                sourceBounds.width(),
                sourceBounds.height()
            )
            context.startActivity(launchIntent, options.toBundle())
        } else {
            context.startActivity(launchIntent)
        }
    }

    fun launchApp(packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        }
    }
}
