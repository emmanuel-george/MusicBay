package com.george.sliceoflife.extrabeat.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.RecentlyPlayedAdapter
import com.george.sliceoflife.extrabeat.model.MusicFiles
import kotlinx.android.synthetic.main.activity_recent_played_all.*

class RecentPlayedAllActivity : AppCompatActivity() {
    companion object{
        var recentPlayedSongs: ArrayList<MusicFiles> = ArrayList()
        @SuppressLint("StaticFieldLeak")
        lateinit var adapter: RecentlyPlayedAdapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_played_all)
        supportActionBar?.hide()

        backBtnRPA.setOnClickListener { finish() }
        recentPlayedSongs = MainActivity.recentlyPlayedMA
        try {
            recentPlayedAllRV.setHasFixedSize(true)
            recentPlayedAllRV.setItemViewCacheSize(13)
            recentPlayedAllRV.layoutManager = GridLayoutManager(this, 3)
            adapter = RecentlyPlayedAdapter(this,recentPlayedSongs)
            recentPlayedAllRV.adapter = adapter
        } catch (e:Exception){
            
        }
    }
}