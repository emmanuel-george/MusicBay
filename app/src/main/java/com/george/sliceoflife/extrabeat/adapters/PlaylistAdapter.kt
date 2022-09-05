package com.george.sliceoflife.extrabeat.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.PlayListActivity
import com.george.sliceoflife.extrabeat.activities.PlaylistDetails
import com.george.sliceoflife.extrabeat.model.Playlist
import com.sanojpunchihewa.glowbutton.GlowButton


class PlaylistAdapter(
    private val context: Context,
    private var playlistList: ArrayList<Playlist>
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image: ImageView = itemView.findViewById(R.id.playListImage)
        var name: TextView = itemView.findViewById(R.id.playListName)
        var glow: GlowButton = itemView.findViewById(R.id.glowPlaylistItem)
      //  var songsNumber: TextView = itemView.findViewById(R.id.totalSongs)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        //holder.songsNumber.text = playlistList[position].playlist.size.toString()+" Songs"

        holder.image.setOnClickListener {
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
           // Toast.makeText(context,"HTTTTTTTT",Toast.LENGTH_SHORT).show()
        }

        Glide.with(context).asBitmap().load(R.drawable.music).into(holder.image)
        if(PlayListActivity.musicList.ref[position].playlist.size >0){
            val images: Bitmap? = getAlbumArt(PlayListActivity.musicList.ref[position].playlist[0].path)
            if (images != null) {
                Glide.with(context).asBitmap().load(images).into(holder.image)
               // holder.glow.glowColor = getDominantColor(images)
                Palette.from(images).generate { palette ->
                    val vibrant: Int =
                        palette!!.getVibrantColor(0x000000) // <=== color you want
                    holder.glow.glowColor = vibrant
                }
            } else {
                Glide.with(context).asBitmap().load(R.drawable.music).into(holder.image)
            }
        }

    }


    override fun getItemCount(): Int {
        return playlistList.size
    }

    private fun getAlbumArt(uri: String): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art = retriever.embeddedPicture
            if(art!=null){
                return BitmapFactory.decodeByteArray(art, 0, art.size)
            }
            retriever.release()
            art
        } catch (e: Exception) {
            null
        }
    }

}

