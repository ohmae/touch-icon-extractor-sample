package net.mm2d.webclip.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object Toaster {
    fun show(context: Context, @StringRes resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
    }

    fun show(context: Context, text: CharSequence) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}
