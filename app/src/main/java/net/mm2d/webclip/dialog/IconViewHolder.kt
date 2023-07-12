/*
 * Copyright (c) 2021 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip.dialog

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.mm2d.touchicon.Icon
import net.mm2d.webclip.R

abstract class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun apply(icon: Icon, transparent: Boolean)

    companion object {
        internal fun selectBackground(transparent: Boolean): Int =
            if (transparent) R.drawable.bg_icon else 0
    }
}
