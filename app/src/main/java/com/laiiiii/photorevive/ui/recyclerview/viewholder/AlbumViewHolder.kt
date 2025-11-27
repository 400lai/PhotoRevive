// ui/album/viewholder/AlbumViewHolder.kt
package com.laiiiii.photorevive.ui.recyclerview.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.laiiiii.photorevive.R

class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imageView: ImageView = view.findViewById(R.id.image_thumbnail)
}