package net.mm2d.webclip.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.li_web_app_icon.view.*
import net.mm2d.touchicon.Icon
import net.mm2d.touchicon.WebAppIcon
import net.mm2d.webclip.GlideApp
import net.mm2d.webclip.R

@SuppressLint("SetTextI18n")
class WebAppIconViewHolder(itemView: View) : IconViewHolder(itemView) {
    private val iconImage: ImageView = itemView.icon
    private val imageSizes: TextView = itemView.image_size
    private val sizes: TextView = itemView.sizes
    private val type: TextView = itemView.type
    private val density: TextView = itemView.density
    private val url: TextView = itemView.url

    override fun apply(icon: Icon, transparent: Boolean) {
        iconImage.setBackgroundResource(
            selectBackground(
                transparent
            )
        )
        itemView.tag = icon
        sizes.text = icon.sizes
        type.text = icon.mimeType
        url.text = icon.url
        if (icon is WebAppIcon) {
            density.text = icon.density
        }
        val size = icon.inferSize()
        val inferSize = if (size.width > 0 && size.height > 0) {
            "(${size.width}x${size.height})"
        } else {
            "(uncertain)"
        }
        imageSizes.text = inferSize
        GlideApp.with(itemView)
            .load(icon.url)
            .override(Target.SIZE_ORIGINAL)
            .addListener(bitmapHook {
                imageSizes.text = "${it.width}x${it.height} $inferSize"
            })
            .into(iconImage)
    }

    companion object {
        fun create(context: Context, parent: ViewGroup): WebAppIconViewHolder =
            WebAppIconViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.li_web_app_icon, parent, false)
            )
    }
}
