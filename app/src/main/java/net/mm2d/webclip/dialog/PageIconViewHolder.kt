package net.mm2d.webclip.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.request.target.Target
import net.mm2d.touchicon.Icon
import net.mm2d.webclip.GlideApp
import net.mm2d.webclip.databinding.ItemPageIconBinding

@SuppressLint("SetTextI18n")
class PageIconViewHolder(
    private val binding: ItemPageIconBinding
) : IconViewHolder(binding.root) {
    override fun apply(icon: Icon, transparent: Boolean) {
        binding.icon.setBackgroundResource(
            selectBackground(transparent)
        )
        itemView.tag = icon
        binding.sizes.text = icon.sizes
        binding.rel.text = icon.rel.value
        binding.type.text = icon.mimeType
        binding.url.text = icon.url
        val size = icon.inferSize()
        val inferSize = if (size.isValid()) {
            "(${size.width}x${size.height})"
        } else {
            "(uncertain)"
        }
        binding.imageSize.text = inferSize
        GlideApp.with(itemView)
            .load(icon.url)
            .override(Target.SIZE_ORIGINAL)
            .addListener(bitmapHook {
                binding.imageSize.text = "${it.width}x${it.height} $inferSize"
            })
            .into(binding.icon)
    }

    companion object {
        fun create(context: Context, parent: ViewGroup): PageIconViewHolder =
            PageIconViewHolder(
                ItemPageIconBinding.inflate(LayoutInflater.from(context), parent, false)
            )
    }
}
