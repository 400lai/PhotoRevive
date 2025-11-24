package com.laiiiii.photorevive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

data class MediaItem(val id: Long, val uri: String, val isVideo: Boolean = false)

class MediaItemAdapter(private var items: List<MediaItem>) :
    RecyclerView.Adapter<MediaItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        Glide.with(holder.imageView)
            .load(item.uri)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(android.R.color.darker_gray)
            .error(android.R.drawable.ic_menu_gallery)
            .into(holder.imageView)
    }

    fun updateItems(newItems: List<MediaItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
}