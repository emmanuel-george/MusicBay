package com.george.sliceoflife.extrabeat.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.PlayerActivity
import com.george.sliceoflife.extrabeat.activities.PlayerActivity.Companion.bgImagePA
import com.george.sliceoflife.extrabeat.activities.PlayerActivity.Companion.favoriteBtnPAA
import com.george.sliceoflife.extrabeat.activities.PlayerActivity.Companion.songImgPAA
import com.george.sliceoflife.extrabeat.fragments.NowPlaying
import com.george.sliceoflife.extrabeat.fragments.NowPlaying.Companion.songImgNPP
import com.george.sliceoflife.extrabeat.fragments.NowPlaying.Companion.songNameNPP
import com.george.sliceoflife.extrabeat.model.exitApplication
import com.george.sliceoflife.extrabeat.model.favoriteChecker
import com.george.sliceoflife.extrabeat.model.setSongPosition




class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS -> prevNextSong(increment = false,context = context!!)
            ApplicationClass.PLAY -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(increment = true,context = context!!)
            ApplicationClass.EXIT -> {
               exitApplication()
            }
        }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pauseicon)
        PlayerActivity.play_pause.setImageResource(R.drawable.ic_pause)
        NowPlaying.playPauseNPP.setIconResource(R.drawable.ic_pauseicon)

    }

    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_playicon)
        PlayerActivity.play_pause.setImageResource(R.drawable.ic_play)
        NowPlaying.playPauseNPP.setIconResource(R.drawable.ic_playicon)
    }

    private fun prevNextSong(increment:Boolean, context:Context){
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()
        val image = getAlbumArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        PlayerActivity.songNamePAA.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        PlayerActivity.artistNamePA.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].artist
        if (image != null) {
            Glide.with(context).asBitmap().load(image).into(songImgPAA)
            Glide.with(context).asBitmap().load(image).into(bgImagePA)
            Glide.with(context).asBitmap().load(image).into(songImgNPP)
        } else {
            Glide.with(context).asBitmap().load(R.drawable.music).into(songImgPAA)
            Glide.with(context).asBitmap().load(R.drawable.music).into(bgImagePA)
            Glide.with(context).asBitmap().load(R.drawable.music).into(songImgNPP)

        }
        val imagee = getAlbumArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        if (imagee != null) {
            Glide.with(context).asBitmap().load(imagee).into(songImgPAA)
            Glide.with(context).asBitmap().load(image).into(songImgNPP)

        } else {
            Glide.with(context).asBitmap().load(R.drawable.music).into(songImgPAA)
            Glide.with(context).asBitmap().load(R.drawable.music).into(songImgNPP)

        }
        songNameNPP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        playMusic()
        PlayerActivity.fIndex = favoriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)
        if(PlayerActivity.isFavorite) favoriteBtnPAA.setImageResource(R.drawable.like)
        else favoriteBtnPAA.setImageResource(R.drawable.ic_unlike)

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
}