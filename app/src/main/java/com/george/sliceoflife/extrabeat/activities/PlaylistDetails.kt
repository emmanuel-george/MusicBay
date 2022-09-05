package com.george.sliceoflife.extrabeat.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.PlayListActivity.Companion.musicList
import com.george.sliceoflife.extrabeat.adapters.MusicAdapter
import com.george.sliceoflife.extrabeat.model.checkPlaylist
import com.george.sliceoflife.extrabeat.utility.SwipeGesture
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_play_list.*
import kotlinx.android.synthetic.main.activity_playlist_details.*
import java.util.*

class  PlaylistDetails : AppCompatActivity() {


    companion object{
        var currentPlaylistPos:Int = -1
        @SuppressLint("StaticFieldLeak")
        lateinit var adapter: MusicAdapter
        lateinit var swipeGesture: SwipeGesture
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_details)
        supportActionBar?.hide()

        backBtnPDA.setOnClickListener {
            finish()
        }

        currentPlaylistPos = intent.extras?.get("index") as Int
        musicList.ref[currentPlaylistPos].playlist = checkPlaylist(playlist = musicList.ref[currentPlaylistPos].playlist)
        try{
            playlistDetailsRV.setItemViewCacheSize(10)
            playlistDetailsRV.setHasFixedSize(true)
            playlistDetailsRV.layoutManager = LinearLayoutManager(this)


            adapter = MusicAdapter(this,
                musicList.ref[currentPlaylistPos].playlist,playlistDetails = true)

            swipeGesture = object : SwipeGesture(this) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition

                    Collections.swap(musicList.ref[currentPlaylistPos].playlist,fromPos, toPos)
                    adapter.notifyItemMoved( fromPos, toPos)
                    return false

                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    when (direction) {

                        ItemTouchHelper.LEFT -> {
                            adapter.deleteItem(viewHolder.adapterPosition)
                        }


                    }

                }
            }

            playlistDetailsRV.adapter = adapter
        } catch(e:Exception){

        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(playlistDetailsRV)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navPD)

        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.addBtnPD -> {
                    startActivity(Intent(this, SelectionActivity::class.java))
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.removeAllPD -> {
                    //Toast.makeText(this,"Remove All Clicked",Toast.LENGTH_SHORT).show()
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Remove")
                        .setMessage("Do you want to remove all songs from playlist?")
                        .setPositiveButton("Yes"){ dialog, _ ->
                            musicList.ref[currentPlaylistPos].playlist.clear()
                            adapter.refreshPlaylist()
                            dialog.dismiss()

                        }
                    builder.show()
                    return@setOnNavigationItemSelectedListener true
                }

                else->{
                    Toast.makeText(this,"Nothing Clicked",Toast.LENGTH_SHORT).show()
                    return@setOnNavigationItemSelectedListener false
                }
            }



        }



    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        playlistNamePD.text = musicList.ref[currentPlaylistPos].name
        moreInfoPD.text = "Total ${adapter.itemCount} Songs. \n\n" +
                "Created On:\n${musicList.ref[currentPlaylistPos].createdOn}\n\n" +
                "  -- ${musicList.ref[currentPlaylistPos].createdBy}"
        if(adapter.itemCount > 0){
            val image = getAlbumArt(musicList.ref[currentPlaylistPos].playlist[0].path)
            if (image != null) {
                Glide.with(this).asBitmap().load(image).into(playlistImgPD)
            } else {
                Glide.with(this).asBitmap().load(R.drawable.music).into(playlistImgPD)
            }
        }
        adapter.notifyDataSetChanged()
        adapter.refreshPlaylist()
        // for storing favorites data using shared preferences
        val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(musicList)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()


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