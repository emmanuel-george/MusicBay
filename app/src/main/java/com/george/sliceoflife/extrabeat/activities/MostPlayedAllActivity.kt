package com.george.sliceoflife.extrabeat.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.MostPlayedAdapter
import com.george.sliceoflife.extrabeat.model.MusicFiles
import kotlinx.android.synthetic.main.activity_most_played_all.*

class MostPlayedAllActivity : AppCompatActivity() {

    companion object{
        var mostPlayedSongs: ArrayList<MusicFiles> = ArrayList()
        @SuppressLint("StaticFieldLeak")
        lateinit var adapter: MostPlayedAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_most_played_all)
        supportActionBar?.hide()

        backBtnMPA.setOnClickListener { finish() }
        mostPlayedSongs = MainActivity.mostPlayedMA

        try {
            mostPlayedAllRV.setHasFixedSize(true)
            mostPlayedAllRV.setItemViewCacheSize(13)
            mostPlayedAllRV.layoutManager = GridLayoutManager(this, 3)
            adapter = MostPlayedAdapter(this, mostPlayedSongs)
            mostPlayedAllRV.adapter = adapter
        } catch (e:Exception){

        }
    }
}