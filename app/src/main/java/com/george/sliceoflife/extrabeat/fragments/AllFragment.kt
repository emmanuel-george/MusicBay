package com.george.sliceoflife.extrabeat.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.george.sliceoflife.extrabeat.MainActivity
import com.george.sliceoflife.extrabeat.activities.FavoriteActivity
import com.george.sliceoflife.extrabeat.activities.MostPlayedAllActivity
import com.george.sliceoflife.extrabeat.activities.PlayListActivity
import com.george.sliceoflife.extrabeat.activities.RecentPlayedAllActivity
import com.george.sliceoflife.extrabeat.adapters.FavoriteAllAdapter
import com.george.sliceoflife.extrabeat.adapters.MostPlayedAdapter
import com.george.sliceoflife.extrabeat.adapters.PlaylistAdapter
import com.george.sliceoflife.extrabeat.adapters.RecentlyPlayedAdapter
import kotlinx.android.synthetic.main.fragment_all.*


class AllFragment : Fragment() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var mostPlayedAdapter: MostPlayedAdapter
        @SuppressLint("StaticFieldLeak")
        lateinit var recentlyPlayedAdapter: RecentlyPlayedAdapter
        @SuppressLint("StaticFieldLeak")
        lateinit var favoriteAllAdapter: FavoriteAllAdapter
        @SuppressLint("StaticFieldLeak")
        lateinit var playlistAdapter: PlaylistAdapter
    }

    override fun onResume() {
        super.onResume()
        fragmentAllFavoriteRV.smoothScrollToPosition(0)
        fragmentAllRecentlyPlayedRV.smoothScrollToPosition(0)
        fragmentAllmostPlayedRV.smoothScrollToPosition(0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.george.sliceoflife.extrabeat.R.layout.fragment_all, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val seeAllFA: TextView = requireView().findViewById<View>(com.george.sliceoflife.extrabeat.R.id.seeAll_fav) as TextView
        val seeAllPL: TextView = requireView().findViewById<View>(com.george.sliceoflife.extrabeat.R.id.seeAll_playlist) as TextView
        val seeAllMP: TextView = requireView().findViewById<View>(com.george.sliceoflife.extrabeat.R.id.seeAll_mostPlayed) as TextView
        val seeAllRP: TextView = requireView().findViewById<View>(com.george.sliceoflife.extrabeat.R.id.seeAll_RecentlyPlayed) as TextView
        seeAllFA.setOnClickListener {
            val intent = Intent(activity, FavoriteActivity::class.java)
            startActivity(intent)
        }
        seeAllPL.setOnClickListener {
            val intent = Intent(activity, PlayListActivity::class.java)
            startActivity(intent)
        }
        seeAllMP.setOnClickListener {
            val intent = Intent(activity, MostPlayedAllActivity::class.java)
            startActivity(intent)
        }
        seeAllRP.setOnClickListener {
            val intent = Intent(activity, RecentPlayedAllActivity::class.java)
            startActivity(intent)
        }
        try {

            fragmentAllPlaylistRV.setHasFixedSize(true)
            fragmentAllPlaylistRV.setItemViewCacheSize(13)
            fragmentAllPlaylistRV.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            playlistAdapter = context?.let { PlaylistAdapter(it, PlayListActivity.musicList.ref) }!!
            fragmentAllPlaylistRV.adapter = playlistAdapter

            fragmentAllFavoriteRV.setHasFixedSize(true)
            val favLL =  LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            fragmentAllFavoriteRV.layoutManager = favLL
            favoriteAllAdapter = context?.let { FavoriteAllAdapter(it, FavoriteActivity.favoriteSongs) }!!
            fragmentAllFavoriteRV.adapter = favoriteAllAdapter

            fragmentAllmostPlayedRV.setHasFixedSize(true)
            fragmentAllmostPlayedRV.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            mostPlayedAdapter = context?.let { MostPlayedAdapter(it, MainActivity.mostPlayedMA) }!!
            fragmentAllmostPlayedRV.adapter = mostPlayedAdapter

            fragmentAllRecentlyPlayedRV.setHasFixedSize(true)
            val linearLayoutManager =  LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            fragmentAllRecentlyPlayedRV.layoutManager = linearLayoutManager
            recentlyPlayedAdapter = context?.let { RecentlyPlayedAdapter(it, MainActivity.recentlyPlayedMA) }!!
            fragmentAllRecentlyPlayedRV.adapter = recentlyPlayedAdapter

        } catch (e: Exception) {

        }
    }


}