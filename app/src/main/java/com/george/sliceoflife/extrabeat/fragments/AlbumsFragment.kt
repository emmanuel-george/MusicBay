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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.MainActivity.Companion.albums
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.AlbumAdapter
import com.george.sliceoflife.extrabeat.model.MusicFiles
import kotlinx.android.synthetic.main.fragment_albums.*


class AlbumsFragment : Fragment() {
   lateinit var recyclerView:RecyclerView

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var albumAdapter: AlbumAdapter
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
    ): View {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(
            R.layout.fragment_albums,
            container, false)
        try {
             recyclerView = view.findViewById(R.id.albumFragmentRV)
            recyclerView.setHasFixedSize(true)
            albumAdapter = context?.let { AlbumAdapter(it, albums) }!!
            recyclerView.adapter = albumAdapter
            recyclerView.layoutManager = GridLayoutManager(context, 2)

        } catch (e: Exception) {
        }
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchViewAF =  getView()?.findViewById(R.id.searchViewAF) as EditText
        searchViewAF.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val findTask: String = searchViewAF.text.toString().lowercase()
                val resultMusicFiles: HashSet<MusicFiles> = HashSet()
                for (music in albums) {
                    if (music.album.lowercase().contains(findTask) && searchViewAF.text.toString() != "") {
                        resultMusicFiles.add(music)
                    }
                }
                if(resultMusicFiles.size>=1){
                    albumAdapter = context?.let { AlbumAdapter(it, resultMusicFiles) }!!

                    albumFragmentRV.adapter = albumAdapter
                } else{
                    albumAdapter = context?.let { AlbumAdapter(it, albums) }!!
                    albumFragmentRV.adapter = albumAdapter
                }

            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

}