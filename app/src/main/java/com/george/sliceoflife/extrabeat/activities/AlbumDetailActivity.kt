package com.george.sliceoflife.extrabeat.activities

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.MainActivity.Companion.musicFilesMA
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.AlbumDetailAdapter
import com.george.sliceoflife.extrabeat.model.MusicFiles
import kotlinx.android.synthetic.main.activity_album_detail.*
import java.util.*

class AlbumDetailActivity : AppCompatActivity() {
    var albumSongs: ArrayList<MusicFiles> = ArrayList<MusicFiles>()
    var albumDetailsAdapter: AlbumDetailAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_detail)

        var recyclerView = findViewById<RecyclerView>(R.id.albumDetailRV)
        val album_photo = findViewById<ImageView>(R.id.albumPhoto)

        val albumName = intent.getStringExtra("albumName")
        for (i in 0 until musicFilesMA.size) {
            if (albumName == musicFilesMA[i].album) {
                albumSongs.add(musicFilesMA[i])
            }
        }
        var image: ByteArray? = null
        for (i in albumSongs.indices) {
            image = getAlbumArt(albumSongs[i].path)
            if (image != null) {
                Glide.with(this).load(image).into(album_photo)
                break
            }
        }
        if (image == null) {
            Glide.with(this).load(R.drawable.music).into(album_photo)
        }
        if (albumSongs.size >= 1) {
            albumDetailRV.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            albumDetailsAdapter = AlbumDetailAdapter(this, albumSongs)
            albumDetailRV.adapter = albumDetailsAdapter
        }

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

    override fun onResume() {
        super.onResume()

    }
}