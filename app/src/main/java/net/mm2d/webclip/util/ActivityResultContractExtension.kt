package net.mm2d.webclip.util

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment

fun <I, O> Fragment.registerForActivityResultWrapper(
    contract: ActivityResultContract<I, O>,
    input: I,
    callback: ActivityResultCallback<O>,
): ActivityResultLauncherWrapper<I> =
    ActivityResultLauncherWrapper(registerForActivityResult(contract, callback), input)

class ActivityResultLauncherWrapper<I>(
    private val launcher: ActivityResultLauncher<I>,
    private val input: I,
) {
    fun launch() = launcher.launch(input)
}
