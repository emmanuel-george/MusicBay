package com.george.sliceoflife.extrabeat.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.activities.PlayerActivity
import com.george.sliceoflife.extrabeat.model.setSongPosition
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.android.synthetic.main.fragment_now_playing.*

class NowPlaying : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var root: RelativeLayout

        @SuppressLint("StaticFieldLeak")
        lateinit var playPauseNPP: ExtendedFloatingActionButton

        @SuppressLint("StaticFieldLeak")
        lateinit var nextBtnNPP: ExtendedFloatingActionButton

        @SuppressLint("StaticFieldLeak")
        lateinit var songNameNPP: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var songImgNPP: ImageView
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        root = view.findViewById(R.id.rootNP)
        playPauseNPP = view.findViewById(R.id.playPauseBtnNP)
        nextBtnNPP = view.findViewById(R.id.nextBtnNP)
        songNameNPP = view.findViewById(R.id.songNameNP)
        songImgNPP = view.findViewById(R.id.songImgNP)
        root.visibility = View.INVISIBLE
        Thread{
            playPauseNPP.setOnClickListener {
                if (PlayerActivity.isPlaying) {
                    pauseMusic()
                } else {
                    playMusic()
                }
            }
        }.start()

        Thread{
            nextBtnNPP.setOnClickListener {
                setSongPosition(increment = true)
                PlayerActivity.musicService!!.createMediaPlayer()

                val imagee = getAlbumArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
                if (imagee != null) {
                    Glide.with(this).asBitmap().load(imagee).into(PlayerActivity.songImgPAA)
                    Glide.with(this).asBitmap().load(imagee).into(songImgNPP)

                } else {
                    Glide.with(this).asBitmap().load(R.drawable.music).into(PlayerActivity.songImgPAA)
                    Glide.with(this).asBitmap().load(R.drawable.music).into(songImgNPP)
                }
                songNameNPP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
                PlayerActivity.musicService!!.showNotification(R.drawable.ic_pauseicon)
                playMusic()
            }
        }.start()

        root.setOnClickListener {

            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)

        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {
            root.visibility = View.VISIBLE
            songNameNPP.isSelected = true
            val image = getAlbumArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            if (image != null) {
                Glide.with(this).asBitmap().load(image).into(songImgNP)
            }
//            } else {
//                Glide.with(this).asBitmap().load(R.drawable.music).into(songImgPA)
//
//            }
            songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            if (PlayerActivity.isPlaying) playPauseBtnNP.setIconResource(R.drawable.ic_pauseicon)
            else playPauseBtnNP.setIconResource(R.drawable.ic_playicon)
        }
    }

    private fun playMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        playPauseBtnNP.setIconResource(R.drawable.ic_pauseicon)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pauseicon)
        PlayerActivity.play_pause.setImageResource(R.drawable.ic_pause)
        PlayerActivity.isPlaying = true
    }

    private fun pauseMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        playPauseBtnNP.setIconResource(R.drawable.ic_playicon)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_playicon)
        PlayerActivity.play_pause.setImageResource(R.drawable.ic_play)
        PlayerActivity.isPlaying = false
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