/*
 * Copyright (c) 2021 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.webclip.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.request.target.Target
import net.mm2d.touchicon.Icon
import net.mm2d.webclip.GlideApp
import net.mm2d.webclip.databinding.ItemDomainIconBinding

@SuppressLint("SetTextI18n")
class DomainIconViewHolder(
    private val binding: ItemDomainIconBinding
) : IconViewHolder(binding.root) {
    override fun apply(icon: Icon, transparent: Boolean) {
        binding.icon.setBackgroundResource(
            selectBackground(transparent)
        )
        binding.root.tag = icon
        binding.sizes.text = icon.sizes
        binding.length.text = icon.length.toString()
        binding.type.text = icon.mimeType
        binding.url.text = icon.url
        GlideApp.with(itemView)
            .load(icon.url)
            .override(Target.SIZE_ORIGINAL)
            .addListener(bitmapHook {
                binding.imageSize.text = "${it.width}x${it.height}"
            })
            .into(binding.icon)
    }

    companion object {
        fun create(context: Context, parent: ViewGroup): DomainIconViewHolder =
            DomainIconViewHolder(
                ItemDomainIconBinding.inflate(LayoutInflater.from(context), parent, false)
            )
    }
}
