/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip.settings

import kotlin.reflect.KClass

enum class Key(
    private val type: KClass<*>? = null,
    private val defaultValue: Any? = null
) {
    SETTINGS_VERSION(
        Int::class, -1
    ),
    APP_VERSION(
        Int::class, -1
    ),
    USE_EXTENSION(
        Boolean::class, false
    ),
    ;

    init {
        if (defaultValue != null) {
            requireNotNull(type)
            require(type.isInstance(defaultValue))
        } else require(type == null)
    }

    internal val isBooleanKey: Boolean
        get() = type == Boolean::class

    internal val isIntKey: Boolean
        get() = type == Int::class

    internal val defaultBoolean: Boolean
        get() = defaultValue as Boolean

    internal val defaultInt: Int
        get() = defaultValue as Int
}
