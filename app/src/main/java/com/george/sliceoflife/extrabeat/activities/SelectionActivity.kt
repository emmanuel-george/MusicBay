package com.george.sliceoflife.extrabeat.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.MusicAdapter
import com.george.sliceoflife.extrabeat.model.MusicFiles
import kotlinx.android.synthetic.main.activity_selection.*

class SelectionActivity : AppCompatActivity() {
     lateinit var adapter: MusicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)
        supportActionBar?.hide()
        try {
            selectionRV.setItemViewCacheSize(10)
            selectionRV.setHasFixedSize(true)
            selectionRV.layoutManager = LinearLayoutManager(this)


            adapter = MusicAdapter(this, MainActivity.musicFilesMA, selectionActivity = true)
            selectionRV.adapter = adapter
        } catch (e: Exception) {
        }


        searchViewSA.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                val findTask: String = searchViewSA.text.toString().lowercase()
                val resultMusicFiles: ArrayList<MusicFiles> = ArrayList()
                for (music in MainActivity.musicFilesMA) {
                    if (music.title.lowercase().contains(findTask)) {
                        resultMusicFiles.add(music)
                    }
                }
                selectionRV.adapter = MusicAdapter(this@SelectionActivity, resultMusicFiles, selectionActivity = true)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
        backImgSA.setOnClickListener {
            adapter.refreshPlaylist()
            finish()
        }
    }
}