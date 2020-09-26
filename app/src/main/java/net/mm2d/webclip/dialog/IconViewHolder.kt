package net.mm2d.webclip.dialog

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import net.mm2d.touchicon.Icon
import net.mm2d.webclip.R

abstract class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun apply(icon: Icon, transparent: Boolean)

    companion object {
        internal fun bitmapHook(callback: ((bitmap: Bitmap) -> Unit)): RequestListener<Drawable?> {
            return object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean = false

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource is BitmapDrawable) {
                        callback(resource.bitmap)
                    }
                    return false
                }
            }
        }

        internal fun selectBackground(transparent: Boolean): Int =
            if (transparent) R.drawable.bg_icon else 0
    }
}
