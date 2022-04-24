package edu.temple.audiobookplayer

import android.app.DownloadManager
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.net.Uri
import android.os.*
import android.util.Log

import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity


import androidx.lifecycle.ViewModelProvider
import edu.temple.audlibplayer.PlayerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binder: PlayerService.MediaControlBinder
    private var isBounded: Boolean = false
    private lateinit var viewmodel: BookViewModel
    private   var latestProgress : Int? = null

     private var handler : Handler= Handler(Looper.getMainLooper()){ it ->
         if(isBounded && binder.isPlaying){
            it.obj?.let {
                val bp = it as PlayerService.BookProgress

                findViewById<SeekBar>(R.id.seekbar).setProgress(
                    bp.progress, true
                )
                 latestProgress = bp.progress
            }

         }


         true
     }



    private val connection = object : ServiceConnection {


        override fun onServiceConnected(className: ComponentName, service: IBinder) {

            binder = service as PlayerService.MediaControlBinder


            isBounded = true

           binder.setProgressHandler(handler)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBounded = false

        }
    }


    override fun onStart() {
        super.onStart()



        Intent(this, PlayerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }






    private fun isSingleMode(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }





    override fun onBackPressed() {
        super.onBackPressed()
        viewmodel.updateBook(null)

//        // stop if playing
//        if(binder.isPlaying){
//            binder.stop()
//        }
//
//        findViewById<SeekBar>(R.id.seekbar).setProgress(0, true)



    }







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        viewmodel = ViewModelProvider(this).get(BookViewModel::class.java)




        findViewById<Button>(R.id.search).setOnClickListener {
            onSearchRequested()
        }



        if(savedInstanceState ==null) {
            val bl = BookList()
            val possibleQuery = getPreferences(Context.MODE_PRIVATE).getString("QUERY", "")
            if ( possibleQuery != "") {
                bl.generateBooks(""){
                    searchBooks(possibleQuery!!)
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.container1, BookListFragment.newInstance(bl))
                .commit()
        }else{
            if(isSingleMode() && viewmodel.selectedBook.value != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()
            }
        }



        if(!isSingleMode()  ){
            supportFragmentManager.beginTransaction()
                .add(R.id.container2, BookDetailsFragment())
                .commit()
        }



        viewmodel.selectedBook.observe(this){

            if(it != null){

                if(isBounded){
                    if(binder.isPlaying ){
                        binder.stop()
                    }
                }

                val book = searchBooksWithId(it.id)
                val p = loadBookProgress(book.id)
                Log.d("ALEX", "Loaded BOOK PROGRESS WITH ${book.id} progress $p ")
                val startPosition = if (p != -1) {p }else {0}


                findViewById<SeekBar>(R.id.seekbar).setProgress(startPosition, true)




                findViewById<Button>(R.id.play).setOnClickListener {




                    if(hasBookBeenDownloaded(book.id)){
                        val downloaded = applicationContext.getExternalFilesDir(
                            Environment.DIRECTORY_AUDIOBOOKS

                        )

                        val f = File("${downloaded?.path}/${book.id}.mp3")


                        binder.play(
                            f, startPosition
                        )
                    }else{
                        if(isBounded){
                            binder.play(book.id)
                        }
                        downloadBookWithId(book.id)
                    }






                }


                findViewById<SeekBar>(R.id.seekbar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, changedByUser: Boolean) {
                        if(changedByUser){
                            binder.seekTo(p1)

                        }

                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {

                    }

                })
                findViewById<Button>(R.id.pause).setOnClickListener {
                        //TODO: save progress with book id

                        saveBookProgress(book.id)

                        if(isBounded){
                            binder.pause()
                        }



                }
                findViewById<Button>(R.id.cancel).setOnClickListener {

                    if(isBounded){
                        binder.stop()
                    }

                }

                val fg = BookDetailsFragment()
                fg.book = it



                if(isSingleMode() ) {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container1,fg)
                        .addToBackStack(null)
                        .commit()
                }else{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container2, fg)
                        .addToBackStack(null)
                        .commit()
                }
            }

        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent!!)
    }

    private fun hasBookBeenDownloaded(id:Int): Boolean {
        val downloaded = applicationContext.getExternalFilesDir(
            Environment.DIRECTORY_AUDIOBOOKS

        )

        val f = File("${downloaded?.path}/$id.mp3")

        return f.exists()

    }



    private fun saveBookProgress(id: Int) {
        val shared = getPreferences(Context.MODE_PRIVATE)




        if(latestProgress != null){
            Log.d("ALEX", "SAVED BOOK PROGRESS WITH $id progress $latestProgress ")
            with(shared.edit()){
                putInt("BOOK/$id",latestProgress!!)
                apply()
            }
        }


    }

    private fun loadBookProgress(id: Int) : Int {
        val shared = getPreferences(Context.MODE_PRIVATE)!!
        return shared.getInt("BOOK/$id",-1)

    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->



                val shared = getPreferences(Context.MODE_PRIVATE)?: return

                with(shared.edit()){
                    putString("QUERY",query)

                    apply()
                }
                searchBooks(query)

            }

        }
    }



    private fun downloadBookWithId(id: Int) {

        runBlocking {

            withContext(Dispatchers.IO){
                val req = DownloadManager.Request(
                    Uri.parse("https://kamorris.com/lab/audlib/download.php?id=$id")
                )
                req.setTitle("downloading book $id")
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                req.setDestinationInExternalFilesDir(applicationContext, Environment.DIRECTORY_AUDIOBOOKS,
                    "$id.mp3"
                )

                val manager = applicationContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                manager.enqueue(req)
            }

        }




    }



    private fun searchBooks(query: String): BookList {
        supportFragmentManager.popBackStack()
        val books = BookList()
        runBlocking {

            withContext(Dispatchers.IO) {
                val url = URL("https://kamorris.com/lab/cis3515/search.php?term=$query")
                val array = JSONArray(url.readText())



                for (i in 0 until array.length()) {
                    val book = array.getJSONObject(i)
                    books.add(
                        Book(
                            book.getString("title"),
                            book.getString("author"),
                            book.getInt("id"),
                            book.getString("cover_url"),
                            book.getInt("duration")


                            )
                    )

                }

            }


            viewmodel.updateBooks(
                books
            )


        }

        return books


    }


    private fun searchBooksWithId(id: Int) : Book{
        supportFragmentManager.popBackStack()
        val book : Book
        runBlocking {

            withContext(Dispatchers.IO) {
                val url = URL("https://kamorris.com/lab/cis3515/book.php?id=$id")

                 val obj = JSONObject(url.readText())

                book = Book(
                    obj.getString("title"),
                    obj.getString("author"),
                    obj.getInt("id"),
                    obj.getString("cover_url"),
                    obj.getInt("duration")
                )

            }





        }
        return book


    }




}