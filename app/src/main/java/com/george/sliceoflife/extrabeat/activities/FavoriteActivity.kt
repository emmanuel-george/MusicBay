package com.george.sliceoflife.extrabeat.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.FavoriteAdapter
import com.george.sliceoflife.extrabeat.model.MusicFiles
import com.george.sliceoflife.extrabeat.model.checkPlaylist
import kotlinx.android.synthetic.main.activity_favorite.*

class FavoriteActivity : AppCompatActivity() {


    companion object{
        var favoriteSongs: ArrayList<MusicFiles> = ArrayList()
         @SuppressLint("StaticFieldLeak")
         lateinit var adapter:FavoriteAdapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        supportActionBar!!.hide()

        favoriteSongs  = checkPlaylist(favoriteSongs)

        backBtnFA.setOnClickListener { finish() }
        try {
            favoriteRV.setHasFixedSize(true)
            favoriteRV.setItemViewCacheSize(13)
            favoriteRV.layoutManager = GridLayoutManager(this, 4)
            adapter = FavoriteAdapter(this, favoriteSongs)
            favoriteRV.adapter = adapter
        } catch (e:Exception){

        }

    }
}