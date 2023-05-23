/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip.settings

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import net.mm2d.webclip.BuildConfig
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

interface Key {
    enum class Package : Key {
        DATA_VERSION_INT,
        VERSION_AT_INSTALL_INT,
        VERSION_AT_LAST_LAUNCHED_INT,
        VERSION_BEFORE_UPDATE_INT,
    }

    enum class User : Key {
        DATA_VERSION_INT,
        USE_EXTENSION_BOOLEAN,
        TRANSPARENT_GRID_BOOLEAN,
    }
}

enum class DataStoreFile {
    PACKAGE,
    USER,
    ;

    fun fileName(): String =
        BuildConfig.APPLICATION_ID + "." + name.lowercase()
}

fun preferences(
    file: DataStoreFile,
    migrations: List<DataMigration<Preferences>> = listOf(),
): ReadOnlyProperty<Context, DataStore<Preferences>> =
    preferencesDataStore(
        name = file.fileName(),
        produceMigrations = { migrations },
    )

fun Preferences.edit(editor: (preferences: MutablePreferences) -> Unit): Preferences =
    toMutablePreferences().also(editor).toPreferences()

fun <K> K.intKey(): Preferences.Key<Int>
    where K : Enum<*>,
          K : Key {
    if (BuildConfig.DEBUG) {
        checkSuffix(Int::class)
    }
    return intPreferencesKey(name)
}

fun <K> K.booleanKey(): Preferences.Key<Boolean>
    where K : Enum<*>,
          K : Key {
    if (BuildConfig.DEBUG) {
        checkSuffix(Boolean::class)
    }
    return booleanPreferencesKey(name)
}

private const val SUFFIX_BOOLEAN = "_BOOLEAN"
private const val SUFFIX_INT = "_INT"
private const val SUFFIX_LONG = "_LONG"
private const val SUFFIX_FLOAT = "_FLOAT"
private const val SUFFIX_STRING = "_STRING"

internal fun Enum<*>.checkSuffix(value: KClass<*>) {
    if (!BuildConfig.DEBUG) return
    when (value) {
        Boolean::class -> require(name.endsWith(SUFFIX_BOOLEAN)) {
            "$this is used for Boolean, suffix \"$SUFFIX_BOOLEAN\" is required."
        }

        Int::class -> require(name.endsWith(SUFFIX_INT)) {
            "$this is used for Int, suffix \"$SUFFIX_INT\" is required."
        }

        Long::class -> require(name.endsWith(SUFFIX_LONG)) {
            "$this is used for Long, suffix \"$SUFFIX_LONG\" is required."
        }

        Float::class -> require(name.endsWith(SUFFIX_FLOAT)) {
            "$this is used for Float, suffix \"$SUFFIX_FLOAT\" is required."
        }

        String::class -> require(name.endsWith(SUFFIX_STRING)) {
            "$this is used for String, suffix \"$SUFFIX_STRING\" is required."
        }
    }
}
