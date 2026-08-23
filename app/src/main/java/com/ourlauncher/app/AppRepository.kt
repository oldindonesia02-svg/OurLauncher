package com.ourlauncher.app

import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.os.Process
import android.os.UserHandle

/**
 * One installed, launchable app.
 */
data class AppInfo(
    val label: String,
    val packageName: String,
    val componentClassName: String,
    val user: UserHandle,
    val icon: Drawable
)

/**
 * Wraps LauncherApps — the correct API for a *launcher* to enumerate apps
 * (unlike a normal app, which should use PackageManager.queryIntentActivities).
 * LauncherApps also correctly handles work-profile / multi-user apps.
 */
class AppRepository(private val context: Context) {

    private val launcherApps =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    fun getAllApps(): List<AppInfo> {
        val result = mutableListOf<AppInfo>()
        val userManager = launcherApps
        val myUser = Process.myUserHandle()

        // Phase 1: current user only. Work-profile support is a later-phase add-on.
        val activities = userManager.getActivityList(null, myUser)
        for (activity in activities) {
            result.add(
                AppInfo(
                    label = activity.label.toString(),
                    packageName = activity.applicationInfo.packageName,
                    componentClassName = activity.componentName.className,
                    user = myUser,
                    icon = activity.getIcon(0)
                )
            )
        }
        return result.sortedBy { it.label.lowercase() }
    }

    fun launchApp(app: AppInfo) {
        val component = android.content.ComponentName(app.packageName, app.componentClassName)
        launcherApps.startMainActivity(component, app.user, null, null)
    }
}
