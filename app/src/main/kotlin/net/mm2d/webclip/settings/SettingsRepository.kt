package net.mm2d.webclip.settings

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class SettingsRepository(
    context: Context,
) {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val packageSettingsRepository = PackageSettingsRepository(context)
    val userSettingsRepository = UserSettingsRepository(context)

    init {
        scope.launch {
            packageSettingsRepository.flow.take(1).collect()
        }
    }
}
