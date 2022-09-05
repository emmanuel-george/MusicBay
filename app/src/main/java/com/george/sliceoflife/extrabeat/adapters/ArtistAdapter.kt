package com.george.sliceoflife.extrabeat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.MainActivity.Companion.musicFilesMA
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.ArtistDetailActivity
import com.george.sliceoflife.extrabeat.model.MusicFiles

class ArtistAdapter(
    private val context: Context,
    private var artistFiles: ArrayList<MusicFiles>
) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    companion object {
        var songCount: Int = 0
        var artistNames: HashSet<String> = MainActivity.artistNames
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var album_image: ImageView = itemView.findViewById(R.id.artist_image)
        var artist_name: TextView = itemView.findViewById(R.id.artist_name)
        var noOfSongs: TextView = itemView.findViewById(R.id.noOfSongs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.artist_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (artistNames.elementAt(position) != "") {
            holder.artist_name.text = artistNames.elementAt(position)
        }
        var noCover = true
        for (i in 0 until musicFilesMA.size) {
            if (musicFilesMA[i].artist != null && artistNames.elementAt(position) != null) {
                if (artistNames.elementAt(position) == musicFilesMA[i].artist) {
                    songCount++
                }
                if (artistNames.elementAt(position) == musicFilesMA[i].artist &&
                    noCover &&artistNames.elementAt(position) != null
                ) {
                    val image = getAlbumArt(musicFilesMA[i].path)
                    if (image != null) {
                        Glide.with(context).asBitmap().load(image).into(holder.album_image)
                    } else {
                        Glide.with(context).asBitmap().load(R.drawable.music)
                            .into(holder.album_image)
                    }
                    noCover = false
                }
            }

        }

        val songCounts: String = if (songCount == 1) {
            songCount.toString() + " Song"
        } else {
            songCount.toString() + " Songs"
        }

        if (songCount != 0) {
            holder.noOfSongs.text = songCounts
        }
        songCount = 0

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ArtistDetailActivity::class.java)
            intent.putExtra("artistName", artistNames.elementAt(position))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return artistNames.size
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
    fun updateMusicList(searchListArtistNames: HashSet<String>,searchArtistSongs: ArrayList<MusicFiles>) {
        if (searchListArtistNames.size >= 1 && searchArtistSongs.size >= 1) {
            artistNames = HashSet()
            artistNames.addAll(searchListArtistNames)
            artistFiles = ArrayList()
            artistFiles.addAll(searchArtistSongs)
            notifyDataSetChanged()
        }
    }


}