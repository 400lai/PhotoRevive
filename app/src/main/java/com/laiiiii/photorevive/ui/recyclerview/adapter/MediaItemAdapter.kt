package com.laiiiii.photorevive.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.laiiiii.photorevive.R
import com.laiiiii.photorevive.ui.recyclerview.model.MediaItem
import com.laiiiii.photorevive.ui.recyclerview.viewholder.AlbumViewHolder

// ui/recyclerview/adapter/MediaItemAdapter.kt
class MediaItemAdapter(private var items: List<MediaItem>, private val onItemClick: (MediaItem) -> Unit) :
    RecyclerView.Adapter<AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.imageView)
            .load(item.uri)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(android.R.color.darker_gray)
            .error(android.R.drawable.ic_menu_gallery)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    fun updateItems(newItems: List<MediaItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
