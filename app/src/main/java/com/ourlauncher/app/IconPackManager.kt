package com.ourlauncher.app

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

data class IconPackInfo(
    val label: String,
    val packageName: String
)

class IconPackManager(private val context: Context) {
    private val pm = context.packageManager
    private val iconMap = HashMap<String, String>()
    private var currentLoadedPack: String? = null

    fun getInstalledIconPacks(): List<IconPackInfo> {
        val list = mutableListOf<IconPackInfo>()
        val intents = arrayOf(
            Intent("org.adw.launcher.THEMES"),
            Intent("com.novalauncher.THEME"),
            Intent("com.gau.go.launcherex.theme"),
            Intent("com.fede.launcher.THEME_ICONPACK"),
            Intent("com.anddoes.launcher.THEME")
        )
        val seen = HashSet<String>()
        for (intent in intents) {
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            for (ri in resolveInfos) {
                val pkg = ri.activityInfo.packageName
                if (seen.add(pkg)) {
                    list.add(IconPackInfo(ri.loadLabel(pm).toString(), pkg))
                }
            }
        }
        return list
    }

    fun loadIconPack(packPackageName: String?) {
        if (packPackageName == null || packPackageName == "default") {
            iconMap.clear()
            currentLoadedPack = null
            return
        }
        if (currentLoadedPack == packPackageName) return

        iconMap.clear()
        currentLoadedPack = packPackageName

        try {
            val appInfo = pm.getApplicationInfo(packPackageName, PackageManager.GET_META_DATA)
            val res = pm.getResourcesForApplication(appInfo)
            val stream = try { res.assets.open("appfilter.xml") } catch (e: Exception) { null }
            if (stream != null) {
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()
                parser.setInput(stream, "UTF-8")
                var eventType = parser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && parser.name == "item") {
                        val component = parser.getAttributeValue(null, "component")
                        val drawableName = parser.getAttributeValue(null, "drawable")
                        if (component != null && drawableName != null) {
                            val clean = component.replace("ComponentInfo{", "").replace("}", "")
                            iconMap[clean] = drawableName
                            val simplePkg = clean.substringBefore("/")
                            if (!iconMap.containsKey(simplePkg)) {
                                iconMap[simplePkg] = drawableName
                            }
                        }
                    }
                    eventType = parser.next()
                }
                stream.close()
            }
        } catch (_: Exception) {}
    }

    fun getCustomIcon(packageName: String): Drawable? {
        val pack = currentLoadedPack ?: return null
        val drawableName = iconMap[packageName] ?: return null
        return try {
            val res = pm.getResourcesForApplication(pack)
            val id = res.getIdentifier(drawableName, "drawable", pack)
            if (id != 0) res.getDrawable(id, null) else null
        } catch (_: Exception) {
            null
        }
    }
}

