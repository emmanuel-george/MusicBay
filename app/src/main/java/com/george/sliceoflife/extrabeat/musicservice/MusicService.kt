package com.george.sliceoflife.extrabeat.musicservice

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.PlayerActivity
import com.george.sliceoflife.extrabeat.activities.PlayerActivity.Companion.play_pause
import com.george.sliceoflife.extrabeat.fragments.NowPlaying
import com.george.sliceoflife.extrabeat.model.getImageArt
import com.george.sliceoflife.extrabeat.utility.ApplicationClass
import com.george.sliceoflife.extrabeat.utility.NotificationReceiver
import kotlinx.android.synthetic.main.activity_player.*
import java.util.concurrent.TimeUnit

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager



    override fun onBind(p0: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn: Int) {
        val intent = Intent(baseContext, MainActivity::class.java)

        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)


        val prevIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val imgArt = getImageArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.music)
        }

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_music)
            .setLargeIcon(image)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_previousicon, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.ic_nexticon, "Next", nextPendingIntent)
            .addAction(R.drawable.ic_exit, "Exit", exitPendingIntent)
            .build()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val playbackSpeed = if(PlayerActivity.isPlaying) 1F else 0F
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong())
                .build())
            val playBackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
            mediaSession.setPlaybackState(playBackState)
            mediaSession.setCallback(object: MediaSessionCompat.Callback(){
                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer!!.seekTo(pos.toInt())
                    val playBackStateNew = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
                    mediaSession.setPlaybackState(playBackStateNew)
                }
            })
        }

        startForeground(13, notification)
    }


    fun createMediaPlayer() {

        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            mediaPlayer!!.prepare()
            play_pause.setImageResource(R.drawable.ic_pause)
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_pauseicon)
            PlayerActivity.durationPlayedPA.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.totalDuration.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.seekArcPA.progress = 0F
            PlayerActivity.seekArcPA.setMaxProgress(mediaPlayer!!.duration.toFloat())
            Log.d("TAG", "createMediaPlayer: MusicService ")
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id

        }catch (e: Exception){return}

    }

    fun seekBarSetUp() {
        runnable = Runnable {
            PlayerActivity.durationPlayedPA.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.seekArcPA.progress = mediaPlayer!!.currentPosition.toFloat()
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)


    }

    private fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%2d:%2d", minutes, seconds)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0 && PlayerActivity.isPausedByUser) { //  ) {
            // pause music
            play_pause.setImageResource(R.drawable.ic_play)
            NowPlaying.playPauseNPP.setIconResource(R.drawable.ic_playicon)
            //   NowPlaying.playPauseNPP.setBackgroundResource(R.drawable.ic_playicon)
            showNotification(R.drawable.ic_playicon)
            PlayerActivity.isPlaying = false
            mediaPlayer!!.pause()
            Log.d("TAG", "mediaPlayer!!.pause() ")
        }


            //&& ! PlayerActivity.isPausedByUser
//        } else if(focusChange>0 && ! PlayerActivity.isPausedByUser)  {
//            //play music
//            play_pause.setImageResource(R.drawable.ic_pause)
//            NowPlaying.playPauseNPP.setIconResource(R.drawable.ic_pauseicon)
//            showNotification(R.drawable.ic_pauseicon)
//            PlayerActivity.isPlaying = true
//            mediaPlayer!!.start()
//        }

        else{
            play_pause.setImageResource(R.drawable.ic_pause)
            NowPlaying.playPauseNPP.setIconResource(R.drawable.ic_pauseicon)
            showNotification(R.drawable.ic_pauseicon)
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
        }
    }

    fun getSessionId(): Int {
        return mediaPlayer?.audioSessionId!!
    }
}