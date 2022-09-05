package com.george.sliceoflife.extrabeat.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.MainActivity.Companion.folders
import com.george.sliceoflife.extrabeat.MainActivity.Companion.foldersNames
import com.george.sliceoflife.extrabeat.R
import com.george.sliceoflife.extrabeat.adapters.FolderAdapter

class FoldersFragment : Fragment() {


    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var folderAdapter: FolderAdapter
        lateinit var recyclerView: RecyclerView
    }


    override fun onResume() {
        super.onResume()
        // (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
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
        val view: View = inflater.inflate(R.layout.fragment_folders, container, false)
        recyclerView = view.findViewById(R.id.folderFragmentRV)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        folderAdapter = context?.let { FolderAdapter(it, foldersNames) }!!
        recyclerView.adapter = folderAdapter
        return view
    }

}