package com.george.sliceoflife.extrabeat.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.PlayListActivity
import com.george.sliceoflife.extrabeat.activities.PlaylistDetails
import com.george.sliceoflife.extrabeat.fragments.AllFragment.Companion.playlistAdapter
import com.george.sliceoflife.extrabeat.model.Playlist
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayListViewAdapter(
    private val context: Context,
    private var playlistList: ArrayList<Playlist>
) : RecyclerView.Adapter<PlayListViewAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image: ImageView = itemView.findViewById(R.id.playListImg)
        var name: TextView = itemView.findViewById(R.id.playListdialogname)
        val delete: ImageButton = itemView.findViewById(R.id.playListDeleteBtn)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayListViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.playlist_view, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PlayListViewAdapter.ViewHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Do you want to delete the playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlayListActivity.musicList.ref.removeAt(position)
                    refreshPlayList()
                    playlistAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)

        }
        if(PlayListActivity.musicList.ref[position].playlist.size >0){
            val images = getAlbumArt(PlayListActivity.musicList.ref[position].playlist[0].path)
            if (images != null) {
                Glide.with(context).asBitmap().load(images).into(holder.image)
            } else {
                Glide.with(context).asBitmap().load(R.drawable.music).into(holder.image)
            }
        }

    }

    override fun getItemCount(): Int {
        return playlistList.size
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

    fun refreshPlayList() {
        playlistList = ArrayList()
        playlistList.addAll(PlayListActivity.musicList.ref)
        notifyDataSetChanged()
    }


}