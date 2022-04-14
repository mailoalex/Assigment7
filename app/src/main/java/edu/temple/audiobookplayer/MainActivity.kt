package edu.temple.audiobookplayer

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
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
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binder: PlayerService.MediaControlBinder
    private var isBounded: Boolean = false

    private var isPlaying: Boolean = false
    private var isPaused: Boolean = false
    private var isStopped: Boolean = false
    lateinit private var handler : Handler


    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            binder = service as PlayerService.MediaControlBinder

            isBounded = true

//            binder.setProgressHandler(handler)
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



    lateinit var viewmodel: BookViewModel


    private fun isSingleMode(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }





//    override fun onBackPressed() {
//        super.onBackPressed()
//        viewmodel.updateBook(null)
//    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        viewmodel = ViewModelProvider(this).get(BookViewModel::class.java)




        findViewById<Button>(R.id.search).setOnClickListener {
            onSearchRequested()
        }



        if(savedInstanceState ==null) {
            val bl = BookList()
            bl.generateBooks("a"){
                searchBooks(it)
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

                val book = searchBooksWithId(it.id)


                findViewById<Button>(R.id.play).setOnClickListener {

                    if(isBounded){
                        binder.play(book.id)
                        isPlaying = true
                        isPaused = false
                        isStopped = false
                    }



                }






                findViewById<SeekBar>(R.id.seekbar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                        TODO("Not yet implemented")
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                        TODO("Not yet implemented")
                    }

                })
                findViewById<Button>(R.id.pause).setOnClickListener {


                        binder.pause()



                }
                findViewById<Button>(R.id.cancel).setOnClickListener {

                    if(isBounded){
                        binder.stop()
                    }

                }

                val fg = BookDetailsFragment()
                fg.book = it!!



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

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchBooks(query)
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