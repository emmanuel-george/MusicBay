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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.PlayerActivity
import com.george.sliceoflife.extrabeat.model.MusicFiles

class FavoriteAdapter(
    private val context: Context,
    private var musicList: ArrayList<MusicFiles>
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image: ImageView = itemView.findViewById(R.id.songimageFV)
        var name: TextView = itemView.findViewById(R.id.songNameFV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.favorite_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.ViewHolder, position: Int) {
        holder.name.text = musicList[position].title
        val image = getAlbumArt(musicList[position].path)
        if (image != null) {
            Glide.with(context).asBitmap().load(image).into(holder.image)
        } else {
            Glide.with(context).asBitmap().load(R.drawable.music).into(holder.image)
        }
        holder.itemView.setOnClickListener{
            val intent = Intent(context,PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class", "FavoriteAdapter")
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    private fun getAlbumArt(uri: String): ByteArray? {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art = retriever.embeddedPicture
            retriever.release()
            return art
        } catch (e: Exception) {
            return null
        }
    }


}