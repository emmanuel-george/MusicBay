package com.george.sliceoflife.extrabeat.activities

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.MainActivity.Companion.folders
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.FolderDetailAdapter
import com.george.sliceoflife.extrabeat.model.MusicFiles
import kotlinx.android.synthetic.main.activity_folder_detail.*
import java.util.*
import android.content.Intent
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.fragments.FoldersFragment
import kotlin.collections.ArrayList


class FolderDetailActivity : AppCompatActivity() {

    lateinit var folderName: String
    var folderSongs: ArrayList<MusicFiles> = ArrayList()
    lateinit var folderDetailAdapter: FolderDetailAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_detail)

        folderName = intent.getStringExtra("folderName")!!
        val folderPhoto = findViewById<ImageView>(R.id.folderPhoto)

        Log.d("TAG", "Folder: $folderName ")

        var j = 0
        for (i in 0 until folders.size) {
            Log.d("TAG", "Folders siizee: "+ folders.size)
            Log.d("TAG", "Folders names "+ folders[i].folderName)
                if (folderName == folders[i].folderName) {
                    Log.d("TAG", "Foldernamee: ${folders[i].folderName}")
                    Log.d("TAG", "Folders siize: "+ folders.size)
                    folderSongs.add(j,folders[i])
                    j++
                }

        }


        var image: ByteArray? = null
        for (i in  folderSongs.indices) {
            image = getAlbumArt(folderSongs[i].path)
            if (image != null) {
                Glide.with(this).load(image).into(folderPhoto)
                break
            }
        }
        if (image == null) {
            Glide.with(this).load(R.drawable.music).into(folderPhoto)
        }
        if (folderSongs.size >= 1) {
            folderDetailAdapter = FolderDetailAdapter(this, folderSongs)
            folderDetailRV.adapter = folderDetailAdapter
            folderDetailRV.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
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
        if (folderSongs.size >= 1) {
            folderDetailAdapter = FolderDetailAdapter(this, folderSongs)
            folderDetailRV.adapter = folderDetailAdapter
            folderDetailRV.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }
//
//    override fun onBackPressed() {
//        val i = Intent(this@FolderDetailActivity, MainActivity::class.java)
//        i.putExtra("fragment","fragment")
//        startActivity(i)
//    }
}