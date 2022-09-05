package com.george.sliceoflife.extrabeat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.AlbumDetailActivity
import com.george.sliceoflife.extrabeat.model.MusicFiles


class AlbumAdapter(
    private val context: Context,
    private var albumFiles: HashSet<MusicFiles>
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>()  {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var album_image: ImageView = itemView.findViewById(R.id.album_image)
        var album_name: TextView = itemView.findViewById(R.id.album_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.album_name.text = albumFiles.elementAt(position).album
        val image = getAlbumArt(albumFiles.elementAt(position).path)
        if(image != null){
            Glide.with(context).asBitmap().load(image).into(holder.album_image)
        } else{
            Glide.with(context).asBitmap().load(R.drawable.music).into(holder.album_image);
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, AlbumDetailActivity::class.java)
            Log.d("TAG", "labums size ${albumFiles.size} ")
            Log.d("TAG", "labums name ${ albumFiles.elementAt(position).album} ")
            intent.putExtra("albumName", albumFiles.elementAt(position).album)
            context.startActivity(intent)
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: HashSet<MusicFiles>) {
        albumFiles = HashSet()
        albumFiles.addAll(searchList)
        notifyDataSetChanged()
    }
}