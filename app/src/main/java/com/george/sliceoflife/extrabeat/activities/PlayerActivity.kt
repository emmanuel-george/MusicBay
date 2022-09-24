package com.george.sliceoflife.extrabeat.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.MainActivity.Companion.artists
import com.george.sliceoflife.extrabeat.MainActivity.Companion.folderDetailsFiles
import com.george.sliceoflife.extrabeat.MainActivity.Companion.folders
import com.george.sliceoflife.extrabeat.MainActivity.Companion.isSongShuffled
import com.george.sliceoflife.extrabeat.MainActivity.Companion.mostPlayedMA
import com.george.sliceoflife.extrabeat.MainActivity.Companion.musicFilesMA
import com.george.sliceoflife.extrabeat.MainActivity.Companion.recentlyPlayedMA
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.AlbumDetailAdapter.Companion.albumList
import com.george.sliceoflife.extrabeat.fragments.AllFragment.Companion.favoriteAllAdapter
import com.george.sliceoflife.extrabeat.fragments.AllFragment.Companion.mostPlayedAdapter
import com.george.sliceoflife.extrabeat.fragments.AllFragment.Companion.recentlyPlayedAdapter
import com.george.sliceoflife.extrabeat.fragments.NowPlaying
import com.george.sliceoflife.extrabeat.fragments.NowPlaying.Companion.songNameNPP
import com.george.sliceoflife.extrabeat.fragments.SongsFragment.Companion.musicAdapter
import com.george.sliceoflife.extrabeat.fragments.SongsFragment.Companion.resultMusicFiles
import com.george.sliceoflife.extrabeat.model.MusicFiles
import com.george.sliceoflife.extrabeat.model.exitApplication
import com.george.sliceoflife.extrabeat.model.favoriteChecker
import com.george.sliceoflife.extrabeat.model.setSongPosition
import com.george.sliceoflife.extrabeat.musicservice.MusicService
import com.savantech.seekarc.SeekArc
import kotlinx.android.synthetic.main.activity_player.*
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {
    companion object {
        lateinit var musicListPA: ArrayList<MusicFiles>
        var songPosition: Int = 0
        var isPausedByUser: Boolean = false
        var isPlaying: Boolean = false
        var mostPlayedFlag: Boolean = false
        var recentlyPlayedFlag: Boolean = false

        // var flag:Boolean = false
        var musicService: MusicService? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var play_pause: ImageView

        @SuppressLint("StaticFieldLeak")
        lateinit var songImgPAA: ImageView

        @SuppressLint("StaticFieldLeak")
        lateinit var bgImagePA: ImageView

        @SuppressLint("StaticFieldLeak")
        lateinit var favoriteBtnPAA: ImageView

        @SuppressLint("StaticFieldLeak")
        lateinit var songNamePAA: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var artistNamePA: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var durationPlayedPA: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var totalDuration: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var seekArcPA: SeekArc
        var repeat: Boolean = false
        var nowPlayingId: String = ""
        var isFavorite: Boolean = false
        var fIndex: Int = -1

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        supportActionBar?.hide()
        play_pause = findViewById(R.id.play_pausePA)
        songImgPAA = findViewById(R.id.songImgPA)
        bgImagePA = findViewById(R.id.bgImage)
        songNamePAA = findViewById(R.id.songNamePA)
        favoriteBtnPAA = findViewById(R.id.favoriteBtnPA)
        artistNamePA = findViewById(R.id.song_artist)
        durationPlayedPA = findViewById(R.id.durationPlayed)
        totalDuration = findViewById(R.id.durationTotal)
        seekArcPA = findViewById(R.id.seekArc)

        // for starting service
        floatingActionButtonMore.setOnClickListener { view: View ->
            val popup = PopupMenu(this, view)
            popup.inflate(R.menu.popup_pa)

            popup.setOnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.share -> {
                        val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_SEND
                        shareIntent.type = "audio/*"
                        shareIntent.putExtra(
                            Intent.EXTRA_STREAM,
                            Uri.parse(musicListPA[songPosition].path)
                        )
                        startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))

                    }
                    R.id.equalizer -> {
                        // Toast.makeText(this, "Equalizer", Toast.LENGTH_SHORT).show()

                        val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                        eqIntent.putExtra(
                            AudioEffect.EXTRA_AUDIO_SESSION,
                            musicService!!.mediaPlayer!!.audioSessionId
                        )
                        eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                        eqIntent.putExtra(
                            AudioEffect.EXTRA_CONTENT_TYPE,
                            AudioEffect.CONTENT_TYPE_MUSIC
                        )
                        startActivityForResult(eqIntent, 13)
                    }

                }

                true
            }

            popup.show()
        }
        initialiseLayout()

        Thread {
            play_pausePA.setOnClickListener {
                if (isPlaying) pauseMusic()
                else playMusic()
            }
        }.start()

        Thread {
            previousBtnPA.setOnClickListener {
                prevNextSong(increment = false)
            }
        }.start()

        Thread {
            nextBtnPA.setOnClickListener {
                prevNextSong(increment = true)

            }
        }.start()
        shuffleBtn.setOnClickListener {
            if (isSongShuffled) {
                isSongShuffled = false
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_off)
            } else {
                isSongShuffled = true
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on)

            }
        }

        seekArc.setOnSeekArcChangeListener(object : SeekArc.OnSeekArcChangeListener {
            override fun onStartTrackingTouch(seekArc: SeekArc?) = Unit

            override fun onStopTrackingTouch(seekArc: SeekArc?) = Unit

            override fun onProgressChanged(seekArc: SeekArc?, progress: Float) {
                musicService!!.mediaPlayer!!.seekTo(progress.toInt())
            }
        })

        repeatBtnPA.setOnClickListener {
            if (!repeat) {
                repeat = true
                repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
            } else {
                repeat = false
                repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.white))
            }
        }

        backBtnPA.setOnClickListener { finish() }

//        equalizerBtnPA.setOnClickListener {
//
//        }

//        shareBtnPA.setOnClickListener {
//            val shareIntent = Intent()
//            shareIntent.action = Intent.ACTION_SEND
//            shareIntent.type = "audio/*"
//            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
//            startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))
//        }

        favoriteBtnPA.setOnClickListener {
            if (isFavorite) {
                favoriteBtnPA.setImageResource(R.drawable.ic_unlike)
                isFavorite = false
                FavoriteActivity.favoriteSongs.removeAt(fIndex)
                favoriteAllAdapter.updateFavoriteData()
                musicAdapter.notifyDataSetChanged()
            } else {
                favoriteBtnPA.setImageResource(R.drawable.like)
                isFavorite = true
                FavoriteActivity.favoriteSongs.add(0,musicListPA[songPosition])
                favoriteAllAdapter.notifyItemInserted(0)
                musicAdapter.notifyDataSetChanged()
            }

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

    private fun createMediaPlayer() {

        try {
            if (musicService!!.mediaPlayer == null) {musicService!!.mediaPlayer = MediaPlayer()}

            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            durationPlayed.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            durationTotal.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            seekArc.progress = 0F
            seekArc.setMaxProgress(musicService!!.mediaPlayer!!.duration.toFloat())
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPA[songPosition].id
            playMusic()
//                play_pausePA.setImageResource(R.drawable.ic_pause)
//                musicService!!.showNotification(R.drawable.ic_pauseicon)
            //  musicService!!.mediaPlayer!!.start()
//                songNamePA.text = musicListPA[songPosition].title
//                song_artist.text = musicListPA[songPosition].artist


//            else {
//                musicService!!.mediaPlayer!!.stop()
//                musicService!!.mediaPlayer = MediaPlayer()
//                musicService!!.mediaPlayer!!.reset()
//                musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
//                musicService!!.mediaPlayer!!.prepare()
//
//                play_pausePA.setImageResource(R.drawable.ic_pause)
//                musicService!!.showNotification(R.drawable.ic_pauseicon)
//                musicService!!.mediaPlayer!!.start()
//
//                songNamePA.text = musicListPA[songPosition].title
//                song_artist.text = musicListPA[songPosition].artist
//                durationPlayed.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
//
//                durationTotal.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
//                seekArc.progress = 0F
//                seekArc.setMaxProgress(musicService!!.mediaPlayer!!.duration.toFloat())
//                musicService!!.mediaPlayer!!.setOnCompletionListener(this)
//            }
//            nowPlayingId = musicListPA[songPosition].id
//            Log.d("Playmusic", "createMediaPlayer: ")
//            playMusic()
        } catch (e: Exception) {
            // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    private fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%2d:%2d", minutes, seconds)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initialiseLayout() {
        songPosition = intent.getIntExtra("index", 0)

        when (intent.getStringExtra("class")) {

            "FolderDetailAdapter" -> {
                //Toast.makeText(this,"FolderDetailAdapter",Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(folderDetailsFiles)
                setLayout()
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
            }
            "ArtistDetailAdapter" -> {
                // Toast.makeText(this, "ArtistAdapter", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(artists)
                setLayout()
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
            }
            "AlbumDetailAdapter" -> {
                //  Toast.makeText(this, "AlbumDetailAdapter", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(albumList)
                setLayout()
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
            }
            "FavoriteAdapter" -> {
                // Toast.makeText(this, "FavoriteAdapter", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavoriteActivity.favoriteSongs)
                setLayout()
                Log.d("TAG", "initialiseLayout:11111 ")
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
            }

            "MostPlayedAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(mostPlayedMA)
                setLayout()
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
            }
            "RecentlyPlayedAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(recentlyPlayedMA)
                setLayout()
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
            }
            "NowPlaying" -> {
                //    Toast.makeText(this, "NowPlaying", Toast.LENGTH_SHORT).show()
                setLayout()
                artistNamePA.text = musicListPA[songPosition].artist
                durationPlayed.text =
                    formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                durationTotal.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                seekArcPA.progress = musicService!!.mediaPlayer!!.currentPosition.toFloat()
                seekArcPA.setMaxProgress(musicService!!.mediaPlayer!!.duration.toFloat())
                if (isPlaying) play_pausePA.setImageResource(R.drawable.ic_pause)
                else play_pausePA.setImageResource(R.drawable.ic_play)
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
                try {
                    val audioSessionId = musicService!!.getSessionId()
                    if (audioSessionId != -1) {
                        blast.setAudioSessionId(audioSessionId)
                    }
                } catch (e: Exception) {
                }

            }

            "MusicAdapterSearch" -> {
                // Toast.makeText(this,"MusicAdapterSearch",Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                setLayout()
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
            }
            "MusicAdapter" -> {
                //   Toast.makeText(this, "MusicAdapter", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)

                musicListPA = ArrayList()
                musicListPA.addAll(musicFilesMA)

                setLayout()
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
            }
            "MusicAdapterSongSearch" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(resultMusicFiles)

                setLayout()
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
            }
            "PlaylistDetailsAdapter" -> {
                //  Toast.makeText(this, "PlaylistDetailsAdapter", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlayListActivity.musicList.ref[PlaylistDetails.currentPlaylistPos].playlist)
                setLayout()
                setMostPlayedSongs()
                setRecentlyPlayedSongs()
            }


        }
        artistNamePA.text = musicListPA[songPosition].artist
        // setGlowColor()

    }

//    private fun setGlowColor() {
//        try {
//            Palette.from(getAlbumArts(musicListPA[songPosition].path)!!).generate { palette ->
//                val vibrant: Int =
//                    palette!!.getVibrantColor(0x000000) // <=== color you want
////                glowBtn.glowColor = vibrant
////                glowButton.glowColor =vibrant
////                glowButton6.glowColor =vibrant
////                glowButton8.glowColor =vibrant
////                glowBtn3.glowColor =vibrant
////                glowButton4.glowColor =vibrant
////                glowBtn1.glowColor =vibrant
////                glowButton2.glowColor =vibrant
////                glowBtn2.glowColor =vibrant
//            }
//        } catch (e:Exception){
//
//        }
//    }


    @SuppressLint("NotifyDataSetChanged")
    fun setRecentlyPlayedSongs() {
        recentlyPlayedFlag = false
        if (recentlyPlayedMA.size == 0) {
            val musicFile = musicListPA[songPosition]
            musicFile.recentPlayedCount = 1
            recentlyPlayedMA.add(musicFile)
            recentlyPlayedAdapter.updateRecentLyPlayedData()
        } else {
            for (i in 0 until recentlyPlayedMA.size) {
                if (recentlyPlayedMA[i].title == musicListPA[songPosition].title) {
                    val song = recentlyPlayedMA[i]
                    song.recentPlayedCount = song.recentPlayedCount + 1
                    recentlyPlayedMA.removeAt(i)
                    recentlyPlayedMA.add(0,song)
                    recentlyPlayedFlag = true
                    recentlyPlayedAdapter.updateRecentLyPlayedData()
                    break
                }
            }

            if (!recentlyPlayedFlag) {
                val song = musicListPA[songPosition]
                song.recentPlayedCount = 1
                recentlyPlayedMA.add(0,song)
                recentlyPlayedAdapter.updateRecentLyPlayedData()
            }
        }
//        if(recentlyPlayedMA.size>50){
//            val list = recentlyPlayedMA.subList(0,49)
//            recentlyPlayedMA.clear()
//            recentlyPlayedMA.addAll(list)
//        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setMostPlayedSongs() {
        mostPlayedFlag = false
        if (mostPlayedMA.size == 0) {
            val musicFile = musicListPA[songPosition]
            musicFile.mostPlayedCount = 1
            mostPlayedMA.add(musicFile)
            mostPlayedAdapter.updateMostPlayedData()
            mostPlayedMA.sort()
            mostPlayedAdapter.notifyDataSetChanged()
            return
        }
        else{
            for(i in 0 until mostPlayedMA.size){
                if(mostPlayedMA[i].title == musicListPA[songPosition].title){
                    val song = mostPlayedMA[i]
                    mostPlayedMA.removeAt(i)
                    song.mostPlayedCount = song.mostPlayedCount+1
                    mostPlayedMA.add(i,song)
                    mostPlayedAdapter.notifyItemInserted(i)
                    mostPlayedFlag = true
                    mostPlayedAdapter.updateMostPlayedData()
                    mostPlayedMA.sort()
                    mostPlayedAdapter.notifyDataSetChanged()
                    return

                }
            }
        }

        val song = musicListPA[songPosition]
        song.mostPlayedCount = 1
        //Toast.makeText(this,"count = ${song.mostPlayedCount} ",Toast.LENGTH_SHORT).show()
        mostPlayedMA.add(song)
        mostPlayedAdapter.updateMostPlayedData()
        mostPlayedMA.sort()
        mostPlayedAdapter.notifyDataSetChanged()

    }

    private fun playMusic() {
        isPlaying = true
        isPausedByUser = false
        musicService!!.mediaPlayer!!.start()
        Log.d("Playmusic() called", "playMusic: ")
        play_pausePA.setImageResource(R.drawable.ic_pause)
        musicService!!.showNotification(R.drawable.ic_pauseicon)
    }

    private fun pauseMusic() {
        Log.d("pauseMusic() called", "pauseMusic: ")
        isPausedByUser = true
        play_pausePA.setImageResource(R.drawable.ic_play)
        musicService!!.showNotification(R.drawable.ic_playicon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun setLayout() {
        if (songPosition < musicListPA.size) {
            fIndex = favoriteChecker(musicListPA[songPosition].id)
        }
        if (isSongShuffled) {
            musicListPA.shuffle()
            shuffleBtn.setImageResource(R.drawable.ic_shuffle_on)
        }
        val image = getAlbumArt(musicListPA[songPosition].path)
        if (image != null) {
            Glide.with(applicationContext).asBitmap().load(image).into(songImgPA)
            Glide.with(applicationContext).asBitmap().load(image).into(bgImage)
        } else {
            Glide.with(applicationContext).asBitmap().load(R.drawable.music).into(songImgPA)
            Glide.with(applicationContext).asBitmap().load(R.drawable.music).into(bgImage)

        }
        songNamePA.text = musicListPA[songPosition].title
        if (repeat) repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
        if (isFavorite) favoriteBtnPA.setImageResource(R.drawable.like)
        else favoriteBtnPA.setImageResource(R.drawable.ic_unlike)

    }

    private fun prevNextSong(increment: Boolean) {
        play_pausePA.setImageResource(R.drawable.ic_pause)
        if (increment) {
            setSongPosition(increment = true)
            if (isSongShuffled) {
                // musicListPA = ArrayList()
                //  musicListPA.addAll(musicFilesMA)
                musicListPA.shuffle()
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on)
            }
            setLayout()


        } else {
            setSongPosition(increment = false)
            setLayout()
            if (isSongShuffled) {
                // musicListPA = ArrayList()
                //  musicListPA.addAll(musicFilesMA)
                musicListPA.shuffle()
            }

        }
        song_artist.text = musicListPA[songPosition].artist
        createMediaPlayer()
        setRecentlyPlayedSongs()

//        setGlowColor()
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("TAG", "createMediaPlayer: MusicService ")
        if (musicService == null) {
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
        }
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(
            musicService,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )

        createMediaPlayer()
        val audioSessionId = musicService!!.getSessionId()
        try {
            if (audioSessionId != -1) {
                blast.setAudioSessionId(audioSessionId)
            }
            musicService!!.seekBarSetUp()
        } catch (e: Exception) {
           e.printStackTrace()
        }

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(true)
        setLayout()

        try {
            song_artist.text = musicListPA[songPosition].artist
            val image = getAlbumArt(musicListPA[songPosition].path)
            songNameNPP.text = musicListPA[songPosition].title
            if(image!=null){
                Glide.with(applicationContext).asBitmap().load(image).into(NowPlaying.songImgNPP)
                Glide.with(applicationContext).asBitmap().load(image).into(songImgPA)
            } else{
                Glide.with(applicationContext).asBitmap().load(R.drawable.music).into(songImgPA)
                Glide.with(applicationContext).asBitmap().load(R.drawable.music).into(NowPlaying.songImgNPP)
            }
            createMediaPlayer()

        } catch (e: Exception) {
           e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK) {
            return

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (musicListPA[songPosition].id == "Unknown" && !isPlaying) exitApplication()
        if (blast != null) {
            blast.release()
        }
    }


}