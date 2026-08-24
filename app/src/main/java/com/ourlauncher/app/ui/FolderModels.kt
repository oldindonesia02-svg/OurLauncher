package com.ourlauncher.app.ui

import com.ourlauncher.app.AppInfo

data class FolderInfo(
    val id: String,
    var name: String,
    val apps: MutableList<AppInfo> = mutableListOf()
)

sealed class GridItem {
    data class SingleApp(val app: AppInfo) : GridItem()
    data class Folder(val folder: FolderInfo) : GridItem()

    val id: String
        get() = when (this) {
            is SingleApp -> app.packageName
            is Folder -> folder.id
        }
}
