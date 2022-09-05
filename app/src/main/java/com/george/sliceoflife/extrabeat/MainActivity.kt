package com.george.sliceoflife.extrabeat


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.george.sliceoflife.extrabeat.activities.FavoriteActivity
import com.george.sliceoflife.extrabeat.activities.PlayListActivity
import com.george.sliceoflife.extrabeat.activities.PlayerActivity
import com.george.sliceoflife.extrabeat.activities.SettingsActivity
import com.george.sliceoflife.extrabeat.adapters.FolderAdapter
import com.george.sliceoflife.extrabeat.adapters.ViewPagerAdapter
import com.george.sliceoflife.extrabeat.fragments.*
import com.george.sliceoflife.extrabeat.model.MusicFiles
import com.george.sliceoflife.extrabeat.model.MusicPlaylist
import com.george.sliceoflife.extrabeat.model.exitApplication
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var drawerLayout: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView


    companion object {
        var musicFilesMA: ArrayList<MusicFiles> = ArrayList()
        var mostPlayedMA: ArrayList<MusicFiles> = ArrayList()
        var recentlyPlayedMA: ArrayList<MusicFiles> = ArrayList()
        var albums: HashSet<MusicFiles> = HashSet()
        var artists: ArrayList<MusicFiles> = ArrayList()
        var folders: ArrayList<MusicFiles> = ArrayList()
        var folderDetailsFiles: ArrayList<MusicFiles> = ArrayList()
        var foldersNames = HashSet<String>()
        var artistNames = HashSet<String>()
        var isSongShuffled: Boolean = false
        var MY_SORT_PREF: String = "SortOrder"
        lateinit var musicListSearch: ArrayList<MusicFiles>
        var search: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //for nav drawer


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer)


        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        navigationView = findViewById(R.id.nav_view)
        navigationView.bringToFront()
        navigationView.setNavigationItemSelectedListener(this)


        if (requestPermission()) {
            FavoriteActivity.favoriteSongs = ArrayList()
            // for retreiving favorite songs data using shared preferences
            val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavoriteSongs", null)
            val typeToken = object : TypeToken<ArrayList<MusicFiles>>() {}.type

            if (jsonString != null) {
                val data: ArrayList<MusicFiles> =
                    GsonBuilder().create().fromJson(jsonString, typeToken)
                FavoriteActivity.favoriteSongs.addAll(data)

            }

            mostPlayedMA = ArrayList()
            // for retreiving most played songs data using shared preferences
            val jsonStringMostPlayed = editor.getString("MostPlayedSongs", null)


            if (jsonStringMostPlayed != null) {
                val mostPlayedata: ArrayList<MusicFiles> =
                    GsonBuilder().create().fromJson(jsonStringMostPlayed, typeToken)
                mostPlayedMA.addAll(mostPlayedata)
                mostPlayedMA.sort()
                Log.i("Count", "Size = " + mostPlayedMA.size)
                if (mostPlayedMA.size > 80) {
                    val list = ArrayList<MusicFiles>()
                    list.addAll(mostPlayedMA)
                    mostPlayedMA = ArrayList()
                    mostPlayedMA.addAll(list.subList(0, 78))
                }
            }

            recentlyPlayedMA = ArrayList()
            // for retreiving Recently played songs data using shared preferences
            val jsonStringRecentlyPlayed = editor.getString("RecentlyPlayedSongs", null)

            if (jsonStringRecentlyPlayed != null) {
                val recentlyPlayedata: ArrayList<MusicFiles> =
                    GsonBuilder().create().fromJson(jsonStringRecentlyPlayed, typeToken)
                if (recentlyPlayedata.size >= 50) {
                    recentlyPlayedMA.addAll(recentlyPlayedata.subList(0, 49))
                } else {
                    recentlyPlayedMA.addAll(recentlyPlayedata)
                }

            }

            PlayListActivity.musicList = MusicPlaylist()
            // for retreiving Playlsit songs data using shared preference
            val jsonStringPlaylist = editor.getString("MusicPlaylist", null)

            if (jsonStringPlaylist != null) {
                val dataPlaylist: MusicPlaylist =
                    GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
                PlayListActivity.musicList = dataPlaylist
            }
        }
//        val name = intent.getStringExtra("fragment")
//        if(name == "fragment"){
//            viewpager.setCurrentItem(4,true);
//        } else{
//            viewpager.setCurrentItem(0,true);
//        }


    }

    private fun requestPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.RECORD_AUDIO
                ), 13
            )
            return false
        } else {
            musicFilesMA = getAllAudios()
            setUpTabs()
            return true
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                musicFilesMA = getAllAudios()
                setUpTabs()

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.RECORD_AUDIO
                    ), 13
                )
            }
        }
    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(AllFragment(), "ALL")
        adapter.addFragment(SongsFragment(), "SONG")
        adapter.addFragment(AlbumsFragment(), "ALBUM")
        adapter.addFragment(ArtistsFragment(), "ARTIST")
        adapter.addFragment(FoldersFragment(), "FOLDER")
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
        search = false
    }


    @SuppressLint("Range")
    private fun getAllAudios(): ArrayList<MusicFiles> {
        val editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE)
        val sortingOrder = editor.getString("sorting", "sortByName")
        var order: String? = null
        var num = 0
        when (sortingOrder) {
            "sortByName" -> {
                order = MediaStore.Audio.Media.TITLE + " ASC"
            }
            "sortByDate" -> {
                order = MediaStore.Audio.Media.DATE_ADDED + " DESC"
            }
            "sortBySize" -> {
                order = MediaStore.Audio.Media.SIZE + " ASC"
            }
        }
        artists.clear()
        val tempList = ArrayList<MusicFiles>()
        val duplicateFolders = ArrayList<String>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            order, null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    var artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    var albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    if (artistC == null) {
                        artistC = "UnKnown"
                        num += 1
                        Log.d("TAG", "Number: $num")
                    }
                    if (albumC == null) {
                        albumC = "UnKnown"
                    }
                    val musicFiles = MusicFiles(
                        id = idC, path = pathC, title = titleC,
                        artist = artistC, album = albumC, duration = durationC
                    )
                    //  Log.d("TAG", "getAllAudios:  " + musicFiles)
                    val file = File(musicFiles.path)
                    if (file.exists()) {
                        tempList.add(musicFiles)
                        if (albumC != null && albumC != "") {
                            albums.add(musicFiles)
                        }
                        if (artistC != null && artistC != "") {
                            artistNames.add(artistC)
                            Log.d("TAG", "Artist: $artistC")
                        }

                        val pathD: String = musicFiles.path
                        val folder = StringBuilder()

                        for (i in pathD.length - 1 downTo 1) {
                            if (pathC[i] == '/') {
                                for (j in i - 1 downTo 1) {
                                    if (pathC[j] == '/') {
                                        break
                                    } else {
                                        folder.append(pathC[j])
                                    }
                                }
                                break
                            }
                        }
                        folder.reverse()
                        //     Log.d("FolderName", "getAllAudiosQQQ: " + folder.toString())
                        if (!duplicateFolders.contains(folder.toString())) {
                            foldersNames.add(folder.toString())
                            duplicateFolders.add(folder.toString())
                            //  Log.d("TAG", "getAllAudios: folderName = " + folder.toString())
                        }
                        val folderFiles =
                            MusicFiles(
                                idC,
                                pathC,
                                titleC,
                                artistC,
                                albumC,
                                durationC,
                                folder.toString()
                            )
                        folders.add(folderFiles)
                        //  Log.d("TAG", "MYYYYYYYY: $folderFiles")
                        //  Log.d("TAG", "folders.size() " + folders.size)
                        artists.add(musicFiles)
                    }

                } while (cursor.moveToNext())
                cursor.close()
                return tempList
            }
        }

        return tempList

    }

    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null) {
            exitApplication()
        }

    }

    override fun onResume() {
        super.onResume()
        // for storing favorites data using shared preferences
        val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavoriteActivity.favoriteSongs)
        editor.putString("FavoriteSongs", jsonString)

        // for storing Playlist data using shared preferences
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlayListActivity.musicList)
        editor.putString("MusicPlaylist", jsonStringPlaylist)

        // for storing MostPlayed data using shared preferences
        val jsonStringMostPlayed = GsonBuilder().create().toJson(mostPlayedMA)
        editor.putString("MostPlayedSongs", jsonStringMostPlayed)

        // for storing RecentlyPlayed data using shared preferences
        val jsonStringRecentPlayed = GsonBuilder().create().toJson(recentlyPlayedMA)
        editor.putString("RecentlyPlayedSongs", jsonStringRecentPlayed)
        editor.apply()

    }

    /*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit()

        return when (item.itemId) {
            R.id.by_name -> {
                editor.putString("sorting", "sortByName")
                editor.apply()
                this.recreate()
                true
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.by_date -> {
                editor.putString("sorting", "sortByDate")
                editor.apply()
                this.recreate()
                true
            }
            R.id.by_size -> {
                editor.putString("sorting", "sortBySize")
                editor.apply()
                this.recreate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit()


        if (item.itemId == R.id.name) {
            editor.putString("sorting", "sortByName")
            editor.apply()
            this.recreate()
        }
        if (item.itemId == R.id.date) {
            editor.putString("sorting", "sortByDate")
            editor.apply()
            this.recreate()
        }
        if (item.itemId == R.id.size) {
            editor.putString("sorting", "sortBySize")
            editor.apply()
            this.recreate()
        }
        if (item.itemId == R.id.set) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return false

    }

    override fun onBackPressed() {
        finish()
    }
}






