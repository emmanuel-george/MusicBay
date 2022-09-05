package com.george.sliceoflife.extrabeat.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.MainActivity.Companion.artistNames
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.ArtistAdapter
import kotlinx.android.synthetic.main.fragment_artists.*


class ArtistsFragment : Fragment() {
    lateinit var recyclerView: RecyclerView

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var artistAdapter: ArtistAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchViewArF =  getView()?.findViewById(R.id.searchViewArF) as EditText
        searchViewArF.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val findTask: String = searchViewArF.text.toString().lowercase()
                val resultMusicFiles: HashSet<String> = HashSet()

                for (music in MainActivity.artists) {
                    if (music.artist.lowercase().contains(findTask) && findTask != "") {
                        resultMusicFiles.add(music.artist)
                    }
                }
                if(resultMusicFiles.size>=1){
                    ArtistAdapter.artistNames = resultMusicFiles
                    artistAdapter = context?.let { ArtistAdapter(it, MainActivity.artists) }!!
                    artistFragmentRV.adapter = artistAdapter
                }else{
                    ArtistAdapter.artistNames = artistNames
                    artistAdapter = context?.let { ArtistAdapter(it, MainActivity.musicFilesMA) }!!
                    recyclerView.adapter = artistAdapter
                }

            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_artists, container, false)
        recyclerView = view.findViewById(R.id.artistFragmentRV)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        artistAdapter = context?.let { ArtistAdapter(it, MainActivity.musicFilesMA) }!!
        recyclerView.adapter = artistAdapter

        return view
    }


}