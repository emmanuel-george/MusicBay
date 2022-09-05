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
import com.george.sliceoflife.extrabeat.adapters.ArtistDetailAdapter
import com.george.sliceoflife.extrabeat.model.MusicFiles
import kotlinx.android.synthetic.main.activity_artist_detail.*
import java.util.*

class ArtistDetailActivity : AppCompatActivity() {

    var artistSongs: ArrayList<MusicFiles> = ArrayList<MusicFiles>()
    lateinit var artistDetailAdapter: ArtistDetailAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_detail)
        supportActionBar!!.hide()

        fabBackADA.setOnClickListener { finish() }


        val artistPhoto = findViewById<ImageView>(R.id.artistPhotoADA)

        val artistName = intent.getStringExtra("artistName")
        var j = 0
        for (i in 0 until musicFilesMA.size) {
            if (artistName == musicFilesMA[i].artist) {
                artistSongs.add(j, musicFilesMA[i])
                j++
            }
        }
        var image: ByteArray? = null
        for (i in artistSongs.indices) {
            image = getAlbumArt(artistSongs[i].path)
            if (image != null) {
                Glide.with(this).load(image).into(artistPhoto)
                Glide.with(this).load(image).into(artistPhoto)
                break
            }
        }

        if (image == null) {
            Glide.with(this).load(R.drawable.music).into(artistPhoto)
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
        if (artistSongs.size >= 1) {
            artistDetailAdapter = ArtistDetailAdapter(this, artistSongs)
            artistDetailRV.adapter = artistDetailAdapter
            artistDetailRV.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }

}