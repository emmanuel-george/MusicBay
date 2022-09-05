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
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.PlayerActivity
import com.george.sliceoflife.extrabeat.model.MusicFiles

class ArtistDetailAdapter(
    private val context: Context,
    private var artistFiles: ArrayList<MusicFiles>
) : RecyclerView.Adapter<ArtistDetailAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var artist_image: ImageView = itemView.findViewById(R.id.music_icon)
        var likeImage: ImageView = itemView.findViewById(R.id.likeBtnMusicItem)
        var menuImage: ImageView = itemView.findViewById(R.id.menuMore)
        var song_name: TextView = itemView.findViewById(R.id.music_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.song_name.text = artistFiles[position].title
        holder.likeImage.isVisible = false
        holder.menuImage.isVisible = false
        val image = getAlbumArt(artistFiles[position].path)

        if (image != null) {
            Glide.with(context).asBitmap().load(image).into(holder.artist_image)
        } else {
            Glide.with(context).asBitmap().load(R.drawable.music).into(holder.artist_image)
        }

        holder.itemView.setOnClickListener {
            sendIntent(reference = "ArtistDetailAdapter", pos = position)
            MainActivity.artists.clear()
            MainActivity.artists.addAll(artistFiles)
        }


    }

    override fun getItemCount(): Int {
        return artistFiles.size
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