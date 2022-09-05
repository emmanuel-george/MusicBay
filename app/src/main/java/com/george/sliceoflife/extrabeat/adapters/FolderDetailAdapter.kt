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

class FolderDetailAdapter (
    private val context: Context,
    private var folderFiles: ArrayList<MusicFiles>
)  : RecyclerView.Adapter<FolderDetailAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var folder_image: ImageView = itemView.findViewById(R.id.music_icon)
        var like_image: ImageView = itemView.findViewById(R.id.likeBtnMusicItem)
        var menu_image: ImageView = itemView.findViewById(R.id.menuMore)
        var song_name: TextView = itemView.findViewById(R.id.music_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderDetailAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return folderFiles.size
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


    override fun onBindViewHolder(holder: FolderDetailAdapter.ViewHolder, position: Int) {
        holder.song_name.text = folderFiles[position].title
        holder.like_image.isVisible = false
        holder.menu_image.isVisible = false

        val image = getAlbumArt(folderFiles[position].path)
        if (image != null) {
            Glide.with(context).asBitmap().load(image).into(holder.folder_image)
        } else {
            Glide.with(context).asBitmap().load(R.drawable.music).into(holder.folder_image)
        }

        holder.itemView.setOnClickListener {
            sendIntent(reference = "FolderDetailAdapter", pos = position)
            MainActivity.folderDetailsFiles = ArrayList()
            MainActivity.folderDetailsFiles.addAll(folderFiles)
        }


    }

}