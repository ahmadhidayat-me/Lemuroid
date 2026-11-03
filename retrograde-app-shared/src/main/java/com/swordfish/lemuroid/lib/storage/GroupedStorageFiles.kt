package com.swordfish.lemuroid.lib.storage

data class GroupedStorageFiles(
    val primaryFile: BaseStorageFile,
    val dataFiles: List<BaseStorageFile>,
    val coverFile: BaseStorageFile? = null,
) {
    fun allFiles() = listOf(primaryFile) + dataFiles
}
