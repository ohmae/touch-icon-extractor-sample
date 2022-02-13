package net.mm2d.webclip.settings

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.mm2d.webclip.BuildConfig

class PackageSettingsRepository(
    context: Context
) {
    private val Context.dataStoreField: DataStore<Preferences> by preferences(
        file = DataStoreFile.PACKAGE,
        migrations = listOf(
            DataStructureMigration(),
            UpdateMigration(),
        )
    )
    private val dataStore: DataStore<Preferences> = context.dataStoreField

    val flow: Flow<PackageSetting> = dataStore.data
        .onErrorResumeEmpty()
        .map {
        PackageSetting(
            versionAtInstall = it[VERSION_AT_INSTALL] ?: 0,
            versionAtLastLaunched = it[VERSION_AT_LAST_LAUNCHED] ?: 0,
            versionBeforeUpdate = it[VERSION_BEFORE_UPDATE] ?: 0
        )
    }

    private class DataStructureMigration : DataMigration<Preferences> {
        override suspend fun shouldMigrate(currentData: Preferences): Boolean =
            currentData[DATA_VERSION] != VERSION

        override suspend fun migrate(currentData: Preferences): Preferences =
            currentData.edit { preferences ->
                preferences[DATA_VERSION] = VERSION
                if (preferences[VERSION_AT_INSTALL] == null) {
                    preferences[VERSION_AT_INSTALL] = BuildConfig.VERSION_CODE
                }
            }

        override suspend fun cleanUp() = Unit
    }

    private class UpdateMigration : DataMigration<Preferences> {
        override suspend fun shouldMigrate(currentData: Preferences): Boolean =
            currentData[VERSION_AT_LAST_LAUNCHED] != BuildConfig.VERSION_CODE

        override suspend fun migrate(currentData: Preferences): Preferences =
            currentData.edit { preferences ->
                preferences[VERSION_AT_LAST_LAUNCHED]?.let {
                    preferences[VERSION_BEFORE_UPDATE] = it
                }
                preferences[VERSION_AT_LAST_LAUNCHED] = BuildConfig.VERSION_CODE
            }

        override suspend fun cleanUp() = Unit
    }

    companion object {
        // 1 : 2022/01/02 : 5.1.0-
        private const val VERSION = 1
        private val DATA_VERSION =
            Key.Package.DATA_VERSION_INT.intKey()
        private val VERSION_AT_INSTALL =
            Key.Package.VERSION_AT_INSTALL_INT.intKey()
        private val VERSION_AT_LAST_LAUNCHED =
            Key.Package.VERSION_AT_LAST_LAUNCHED_INT.intKey()
        private val VERSION_BEFORE_UPDATE =
            Key.Package.VERSION_BEFORE_UPDATE_INT.intKey()
    }
}
