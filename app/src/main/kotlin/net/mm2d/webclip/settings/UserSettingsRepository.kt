package net.mm2d.webclip.settings

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserSettingsRepository(
    context: Context,
) {
    private val Context.dataStoreField: DataStore<Preferences> by preferences(
        file = DataStoreFile.USER,
        migrations = listOf(DataStructureMigration()),
    )
    private val dataStore: DataStore<Preferences> = context.dataStoreField

    val flow: Flow<UserSettings> = dataStore.data
        .onErrorResumeEmpty()
        .map {
            UserSettings(
                useExtension = it[USE_EXTENSION] ?: false,
                showTransparentGrid = it[TRANSPARENT_GRID] ?: true,
            )
        }

    suspend fun updateUseExtension(
        value: Boolean,
    ) {
        dataStore.updateData { preferences ->
            preferences.edit {
                it[USE_EXTENSION] = value
            }
        }
    }

    suspend fun updateTrans(
        value: Boolean,
    ) {
        dataStore.updateData { preferences ->
            preferences.edit {
                it[TRANSPARENT_GRID] = value
            }
        }
    }

    private class DataStructureMigration : DataMigration<Preferences> {
        override suspend fun shouldMigrate(
            currentData: Preferences,
        ): Boolean = currentData[DATA_VERSION] != VERSION

        override suspend fun migrate(
            currentData: Preferences,
        ): Preferences =
            currentData.edit { preferences ->
                preferences[DATA_VERSION] = VERSION
            }

        override suspend fun cleanUp() = Unit
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
