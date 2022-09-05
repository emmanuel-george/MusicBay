package com.george.sliceoflife.extrabeat.adapters

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.PlayerActivity
import com.george.sliceoflife.extrabeat.model.MusicFiles

class AlbumDetailAdapter (
    private val context: Context,
    private var albumFiles: ArrayList<MusicFiles>
)  : RecyclerView.Adapter<AlbumDetailAdapter.ViewHolder>() {

    companion object{
        var albumList = ArrayList<MusicFiles>()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var album_image: ImageView = itemView.findViewById(R.id.music_icon)
        var album_name: TextView = itemView.findViewById(R.id.music_name)
        var like_image: ImageView = itemView.findViewById(R.id.likeBtnMusicItem)
        var menu_image: ImageView = itemView.findViewById(R.id.menuMore)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.album_name.text = albumFiles[position].title
        holder.like_image.isVisible = false
        holder.menu_image.isVisible = false
        val image= getAlbumArt(albumFiles[position].path)

        if (image != null) {
            Glide.with(context).asBitmap().load(image).into(holder.album_image)
        } else {
            Glide.with(context).asBitmap().load(R.drawable.music).into(holder.album_image)
        }

        holder.itemView.setOnClickListener {
            sendIntent(reference = "AlbumDetailAdapter", pos = position)
            albumList.clear()
            albumList.addAll(albumFiles)
        }


    }

    override fun getItemCount(): Int {
        return albumFiles.size
    }
    private fun getAlbumArt(uri: String): ByteArray? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art = retriever.embeddedPicture
            retriever.release()
            art
        } catch (e: Exception) {
            null
        }
    }

    private fun sendIntent(reference: String, pos: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", reference)
        ContextCompat.startActivity(context, intent, null)
    }

}