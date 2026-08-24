package com.ourlauncher.app

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

        // Zero-duration transition to let our custom Compose animation handle 100% of the visuals seamlessly
        val options = ActivityOptions.makeCustomAnimation(context, 0, 0)
        context.startActivity(launchIntent, options.toBundle())
    }

    fun launchApp(packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            val options = ActivityOptions.makeCustomAnimation(context, 0, 0)
            context.startActivity(launchIntent, options.toBundle())
        }
    }
}
