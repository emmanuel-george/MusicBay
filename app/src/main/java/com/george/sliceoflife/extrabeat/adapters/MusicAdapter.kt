package com.george.sliceoflife.extrabeat.adapters

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.FavoriteActivity
import com.george.sliceoflife.extrabeat.activities.PlayListActivity
import com.george.sliceoflife.extrabeat.activities.PlayerActivity
import com.george.sliceoflife.extrabeat.activities.PlaylistDetails
import com.george.sliceoflife.extrabeat.fragments.AllFragment.Companion.favoriteAllAdapter
import com.george.sliceoflife.extrabeat.fragments.AllFragment.Companion.playlistAdapter
import com.george.sliceoflife.extrabeat.model.MusicFiles
import com.google.android.material.snackbar.Snackbar
import java.io.File

class MusicAdapter(
    private val context: Context,
    private var musicList: ArrayList<MusicFiles>,
    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false,
    private val songSearching: Boolean =false
) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>(),  SectionTitleProvider {
//FastScrollRecyclerView.SectionedAdapter,

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView = itemView.findViewById(R.id.music_icon)
        var itemTitle: TextView = itemView.findViewById(R.id.music_name)
        var moreImage: ImageView = itemView.findViewById(R.id.menuMore)
        var likeImage: ImageView = itemView.findViewById(R.id.likeBtnMusicItem)
        var checkImage: ImageView = itemView.findViewById(R.id.checkSongImg)
        var cardView: CardView = itemView.findViewById(R.id.cardView) as CardView

    }

    @SuppressLint("NotifyDataSetChanged")
    public fun deleteItem(i: Int) {
        musicList.removeAt(i)
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.music_items, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MusicAdapter.ViewHolder, position: Int) {
        holder.itemTitle.text = musicList[position].title
        val image = getAlbumArt(musicList[position].path)
        holder.moreImage.isVisible = false


        val isSongfavorite = checkMusicIsFav(musicList[position])

        if (isSongfavorite) {
            holder.likeImage.setImageResource(R.drawable.like)
        } else {
            holder.likeImage.setImageResource(R.drawable.ic_unlike)
        }

        holder.likeImage.setOnClickListener {
            val result = addFavSong(musicList[position])
            if (result) {
                holder.likeImage.setImageResource(R.drawable.like)
                favoriteAllAdapter.notifyDataSetChanged()
            } else {
                holder.likeImage.setImageResource(R.drawable.ic_unlike)
                favoriteAllAdapter.notifyDataSetChanged()
            }


        }

        if (image != null) {
            Glide.with(context).asBitmap().load(image).into(holder.itemImage)
        } else {
            Glide.with(context).asBitmap().load(R.drawable.music).into(holder.itemImage)
        }

        when {
            playlistDetails -> {
                holder.moreImage.isVisible = false
                holder.itemView.setOnClickListener {
                    sendIntent(reference = "PlaylistDetailsAdapter", pos = position)
                }
            }

            songSearching -> {
                holder.itemView.setOnClickListener {
                    sendIntent(reference = "MusicAdapterSongSearch", pos = position)
                }
            }

            selectionActivity -> {

                holder.itemView.setOnClickListener {
                    holder.checkImage.isVisible = addSong(musicList[position])
                    playlistAdapter.notifyDataSetChanged()
                }
            }
            else -> {
                holder.itemView.setOnClickListener {
                    when {

                        MainActivity.search -> {
                            sendIntent(
                                reference = "MusicAdapterSearch",
                                pos = position
                            )
                        }


                        musicList[position].id == PlayerActivity.nowPlayingId -> {
                            sendIntent(reference = "NowPlaying", pos = PlayerActivity.songPosition)
                        }

                        else -> {
                            sendIntent(reference = "MusicAdapter", pos = position)


                        }
                    }


                }
            }
        }

        holder.moreImage.setOnClickListener { v ->
            val popupMenu = PopupMenu(context, v)
            popupMenu.menuInflater.inflate(R.menu.popup, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.delete -> deleteFile(position, v)
                }
                true
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addSong(song: MusicFiles): Boolean {
        PlayListActivity.musicList.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if (song.id == music.id) {
                PlayListActivity.musicList.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(
                    index
                )
                PlaylistDetails.adapter.notifyDataSetChanged()
                return false
            }
        }
        PlayListActivity.musicList.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        PlaylistDetails.adapter.notifyDataSetChanged()
        return true

    }

    private fun checkMusicIsFav(song: MusicFiles): Boolean {
        for (musicFile in FavoriteActivity.favoriteSongs) {
            if (musicFile.title == song.title) {
                return true
            }
        }
        return false
    }

    private fun addFavSong(song: MusicFiles): Boolean {
        FavoriteActivity.favoriteSongs.forEachIndexed { index, musicFiles ->
            if (musicFiles.title == song.title) {
                FavoriteActivity.favoriteSongs.removeAt(index)
                return false
            }
        }
        FavoriteActivity.favoriteSongs.add(0, song)
        return true
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

    private fun deleteFile(position: Int, view: View) {
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            musicList[position].id.toLong()
        )
        val file = File(musicList[position].path)
        val deleted = file.delete()
        if (deleted) {
            context.contentResolver.delete(contentUri, null, null)
            musicList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, musicList.size)
            Snackbar.make(view, "File deleted : ", Snackbar.LENGTH_LONG)
                .show()
        } else {
            Snackbar.make(view, "Can't be deleted : ", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<MusicFiles>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    fun sendIntent(reference: String, pos: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", reference)
        ContextCompat.startActivity(context, intent, null)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist() {
        musicList = ArrayList()
        musicList = PlayListActivity.musicList.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }

//    override fun getSectionName(position: Int): String {
//        return musicList[position].title.substring(0, 1)
//    }

    override fun getSectionTitle(position: Int): String {
        return musicList[position].title.substring(0, 1)

    }
}