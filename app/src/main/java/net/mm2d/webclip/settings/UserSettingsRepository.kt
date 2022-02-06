package net.mm2d.webclip.settings

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.IOException

class UserSettingsRepository(
    private val context: Context
) {
    private val Context.dataStoreField: DataStore<Preferences> by preferences(
        file = DataStoreFile.USER,
        migrations = listOf(DataStructureMigration(context))
    )

    val flow: Flow<UserSettings> = context.dataStoreField.data
        .map {
            UserSettings(
                useExtension = it[USE_EXTENSION] ?: false,
                showTransparentGrid = it[TRANSPARENT_GRID] ?: false,
            )
        }

    suspend fun updateUseExtension(value: Boolean) {
        context.dataStoreField.updateData { preferences ->
            preferences.edit {
                it[USE_EXTENSION] = value
            }
        }
    }

    suspend fun updateTrans(value: Boolean) {
        context.dataStoreField.updateData { preferences ->
            preferences.edit {
                it[TRANSPARENT_GRID] = value
            }
        }
    }

    private class DataStructureMigration(
        private val context: Context
    ) : DataMigration<Preferences> {
        private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        override suspend fun shouldMigrate(currentData: Preferences): Boolean =
            currentData[DATA_VERSION] != VERSION

        override suspend fun migrate(currentData: Preferences): Preferences =
            currentData.edit { preferences ->
                preferences[DATA_VERSION] = VERSION
                if (sharedPreferences.contains("USE_EXTENSION")) {
                    preferences[USE_EXTENSION] =
                        sharedPreferences.getBoolean("USE_EXTENSION", false)
                }
            }

        override suspend fun cleanUp() {
            deleteSharedPreferences(
                context, getDefaultSharedPreferencesName(context)
            )
        }

        private fun getDefaultSharedPreferencesName(context: Context): String {
            return context.packageName + "_preferences"
        }

        private fun deleteSharedPreferences(context: Context, name: String) {
            if (Build.VERSION.SDK_INT >= 24) {
                if (!context.deleteSharedPreferences(name)) {
                    throw IOException("Unable to delete SharedPreferences: $name")
                }
            } else {
                val prefsFile = getSharedPrefsFile(context, name)
                val prefsBackup = getSharedPrefsBackup(prefsFile)

                prefsFile.delete()
                prefsBackup.delete()
            }
        }

        private fun getSharedPrefsFile(context: Context, name: String): File {
            val prefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
            return File(prefsDir, "$name.xml")
        }

        private fun getSharedPrefsBackup(prefsFile: File) = File(prefsFile.path + ".bak")
    }

    companion object {
        // 1 : 2022/01/02 : 5.1.0-
        private const val VERSION = 1
        private val DATA_VERSION =
            Key.User.DATA_VERSION_INT.intKey()
        private val USE_EXTENSION =
            Key.User.USE_EXTENSION_BOOLEAN.booleanKey()
        private val TRANSPARENT_GRID =
            Key.User.TRANSPARENT_GRID_BOOLEAN.booleanKey()
    }
}
