package com.george.sliceoflife.extrabeat.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.futuremind.recyclerviewfastscroll.FastScroller
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.MainActivity.Companion.musicFilesMA
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.MusicAdapter
import com.george.sliceoflife.extrabeat.model.MusicFiles
import kotlinx.android.synthetic.main.fragment_songs.*


class SongsFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var musicAdapter: MusicAdapter
        var resultMusicFiles: ArrayList<MusicFiles> = ArrayList()
    }

    override fun onResume() {
        super.onResume()

        try {
            if (view == null) {
                return
            }

            requireView().isFocusableInTouchMode = true
            requireView().requestFocus()
            requireView().setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    true
                } else false
            }
        } catch (e: Exception) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_songs,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val fastScroller : FastScroller = view.findViewById(R.id.musicRV)
            val recyclerView: RecyclerView = view.findViewById(R.id.song_list)

            //musicAdapter = context?.let { MusicAdapter(it, musicFilesMA) }!!
            musicAdapter = MusicAdapter(requireContext(), musicFilesMA)
            Log.d("TAG", "onCreateViewyyy: ${musicFilesMA.size}")
            recyclerView.adapter = musicAdapter
            val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            fastScroller.setRecyclerView(recyclerView)
            Log.d("TAG", "onCreateView: ")
        } catch (e: Exception) {

        }

        val searchSF = view.findViewById(R.id.searchViewSF) as EditText
        searchSF.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val findTask: String = searchSF.text.toString().lowercase()

                if (findTask.isNotEmpty()) {
                    resultMusicFiles.clear()
                    for (music in musicFilesMA) {
                        if (music.title.lowercase().contains(findTask)) {
                            resultMusicFiles.add(music)
                        }
                    }

                        musicAdapter = context?.let { MusicAdapter(it, resultMusicFiles,songSearching=true) }!!
                        //   musicAdapter = MusicAdapter(this.context, musicFiles)
                    song_list.adapter = musicAdapter

                } else{
                    musicAdapter = context?.let { MusicAdapter(it, musicFilesMA) }!!
                    //   musicAdapter = MusicAdapter(this.context, musicFiles)
                    song_list.adapter = musicAdapter
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

    }


}