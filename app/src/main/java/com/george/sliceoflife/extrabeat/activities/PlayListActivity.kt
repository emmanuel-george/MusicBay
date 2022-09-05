package com.george.sliceoflife.extrabeat.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.PlayListViewAdapter
import com.george.sliceoflife.extrabeat.model.MusicPlaylist
import com.george.sliceoflife.extrabeat.model.Playlist
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_play_list.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PlayListActivity : AppCompatActivity() {



    companion object{
        var musicList:MusicPlaylist = MusicPlaylist()
        @SuppressLint("StaticFieldLeak")
        lateinit var adapter:PlayListViewAdapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)
        supportActionBar!!.hide()

        backBtnPLA.setOnClickListener { finish() }
        try{
            playlistRV.setHasFixedSize(true)
            playlistRV.setItemViewCacheSize(13)
            playlistRV.layoutManager = GridLayoutManager(this,2)
            adapter = PlayListViewAdapter(this, playlistList = musicList.ref)
            playlistRV.adapter = adapter
        } catch (e:Exception){

        }


        addPlayListBtn.setOnClickListener { customAlertdialog() }
    }

    private fun customAlertdialog(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog,
            root,false)

        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog).
        setTitle("Playlist Details")
            .setPositiveButton("ADD"){dialog, _ ->
                val playlistName = customDialog.findViewById<TextInputEditText>(R.id.playListdialogname).text
                val createBy = customDialog.findViewById<TextInputEditText>(R.id.yourName).text

                if(playlistName != null && createBy != null)
                    if(playlistName.isNotEmpty() && createBy.isNotEmpty()){
                        addPlayList(playlistName.toString(), createBy.toString())
                    }
            dialog.dismiss()
            }.show()

    }

    private fun addPlayList(name:String, createdBy: String){
        var playlistExists = false
        for(i in musicList.ref){
            if(name.equals(i.name)){
                playlistExists = true
                break
            }
        }
        if(playlistExists) Toast.makeText(this,"Playlist Exist!!",Toast.LENGTH_SHORT).show()
            else {
                val tempPlaylist = Playlist()
                tempPlaylist.name = name
                tempPlaylist.playlist = ArrayList()
                tempPlaylist.createdBy = createdBy
                val calendar = Calendar.getInstance().time
                val sdf = SimpleDateFormat("dd MMM yyyy",Locale.ENGLISH)
                tempPlaylist.createdOn = sdf.format(calendar)
                musicList.ref.add(tempPlaylist)
                adapter.refreshPlayList()
            }
        }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()

    }

}