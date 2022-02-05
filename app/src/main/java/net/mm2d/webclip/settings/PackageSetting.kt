package net.mm2d.webclip.settings

data class PackageSetting(
    val versionAtInstall: Int,
    val versionAtLastLaunched: Int,
    val versionBeforeUpdate: Int,
)
