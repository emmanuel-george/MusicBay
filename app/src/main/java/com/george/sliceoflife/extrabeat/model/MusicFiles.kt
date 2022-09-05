package com.george.sliceoflife.extrabeat.model

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.FavoriteActivity
import com.george.sliceoflife.extrabeat.activities.PlayerActivity
import com.george.sliceoflife.extrabeat.activities.PlayerActivity.Companion.play_pause
import java.io.File
import kotlin.system.exitProcess

data class MusicFiles(
    val id: String, val path: String, val title: String, val artist: String, val album: String,
    val duration: Long = 0, val folderName: String = "Unknown",var mostPlayedCount: Int = 0, var recentPlayedCount:Int =0
) :Comparable<MusicFiles>{

    var folder: String = ""

    override fun toString(): String {
        return "MusicFiles(id='$id', path='$path', title='$title', artist='$artist', album='$album', duration=$duration, folder='$folder')"
    }

    override fun compareTo(other: MusicFiles): Int {
        return other.mostPlayedCount - this.mostPlayedCount
    }
}

class Playlist{
    lateinit var name:String
    lateinit var playlist: ArrayList<MusicFiles>
    lateinit var createdBy:String
    lateinit var createdOn:String
}

class MusicPlaylist{
    var ref:ArrayList<Playlist> = ArrayList()
}


fun getImageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setSongPosition(increment: Boolean) {
 if(!PlayerActivity.repeat){
     if (increment) {
         if (PlayerActivity.musicListPA.size - 1 == PlayerActivity.songPosition) {
             PlayerActivity.songPosition = 0
         } else ++PlayerActivity.songPosition
     } else {
         if (0 == PlayerActivity.songPosition) {
             PlayerActivity.songPosition = PlayerActivity.musicListPA.size - 1
         } else --PlayerActivity.songPosition
     }
 }


}

 fun createMediaPlayer() {
     try {
         if (PlayerActivity.musicService!!.mediaPlayer == null) {
             PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
             PlayerActivity.musicService!!.mediaPlayer!!.reset()
             PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
             PlayerActivity.musicService!!.mediaPlayer!!.prepare()


             play_pause.setImageResource(R.drawable.ic_pause)
             PlayerActivity.musicService!!.showNotification(R.drawable.ic_pauseicon)
         } else {
             PlayerActivity.musicService!!.mediaPlayer!!.stop()
             PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
             PlayerActivity.musicService!!.mediaPlayer!!.reset()
             PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
             PlayerActivity.musicService!!.mediaPlayer!!.prepare()

             PlayerActivity.musicService!!.showNotification(R.drawable.ic_pauseicon)
         }
     } catch (e: Exception) {
         return
     }
 }


    fun favoriteChecker(id:String): Int{
         PlayerActivity.isFavorite = false
         FavoriteActivity.favoriteSongs.forEachIndexed{
             index, music ->
             if(id== music.id){
                 PlayerActivity.isFavorite = true
                 return index
             }
         }
         return -1
     }

fun checkPlaylist(playlist: ArrayList<MusicFiles>): ArrayList<MusicFiles>{
    playlist.forEachIndexed{index,music ->
        val file = File(music.path)
        if(!file.exists())
            playlist.removeAt(index)

    }
    return playlist
}

fun exitApplication(){
    if(PlayerActivity.musicService != null){
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null}
    exitProcess(1)
}





