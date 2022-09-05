package com.george.sliceoflife.extrabeat.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.MainActivity.Companion.folders
import com.george.sliceoflife.extrabeat.MainActivity.Companion.foldersNames
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.FolderDetailActivity

class FolderAdapter(
    private val context: Context,
    private var folderFiles: HashSet<String>
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    companion object {
        var songCount: Int = 0
        var folderNames: HashSet<String> = MainActivity.foldersNames
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var folder_name: TextView = itemView.findViewById(R.id.folderName)
        var noOfSongs: TextView = itemView.findViewById(R.id.songsCount)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.folder_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: FolderAdapter.ViewHolder, position: Int) {
       if(folderNames.elementAt(position) != ""){
           holder.folder_name.text = foldersNames.elementAt(position)
       }

     //   Log.d("TAG", "onBindViewHolder: folderName = "+foldersNames[position])
//        holder.noOfSongs.text = "0 Song"
        for (i in 0 until folders.size) {
            if (foldersNames.elementAt(position) == folders[i].folderName) {
                songCount++
            }
        }
        Log.d("TAG", "onBindViewHolder: Song count for $foldersNames[position] is $songCount")
        val songCounts: String = if (songCount == 1) {
            songCount.toString() + " Song"
        } else {
            songCount.toString() + " Songs"
        }
        if(songCount != 0){
            holder.noOfSongs.text = songCounts
        }

        Log.d("TAG", "onBindViewHolder: noOfSongs = $songCounts")

        songCount = 0

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FolderDetailActivity::class.java)
            intent.putExtra("folderName", foldersNames.elementAt(position))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return folderFiles.size
    }

}