package edu.temple.audiobookplayer

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.json.JSONArray
import java.net.URL


class MainActivity : AppCompatActivity() {

    lateinit  var viewmodel : BookViewModel

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (intent != null) {
            handleIntent(intent)
        }


    }

    private fun isPortraitMode(): Boolean {
        return supportFragmentManager.findFragmentById(R.id.container2) == null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //        // get a handle on viewmodel
        viewmodel = ViewModelProvider(this).get(BookViewModel::class.java)
        //        viewmodel.isLand.value = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        // to search for books
        handleIntent(intent)

       val list_books = BookList().generate_books()





        if(viewmodel.book_list.value!= null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookListFragment.newInstance(viewmodel.book_list.value!!))
                .commit()
        }else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookListFragment.newInstance(list_books))
                .commit()
        }



        if(!isPortraitMode()){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container2,BookDetailsFragment())
                .commit()
        }







        val button = findViewById<Button>(R.id.button)








        button.setOnClickListener {
            onSearchRequested()
        }


        //container 1 is list . container2 is detail


        viewmodel.book_list.observe(this) {
            (supportFragmentManager.findFragmentById(R.id.container1) as BookListFragment).adapter?.refresh(it!!)
        }


        viewmodel.selected_book.observe(this) {
            if(isPortraitMode()){
                val fg = BookDetailsFragment()
                fg.book = it
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, fg )
                    .addToBackStack(null)
                    .setReorderingAllowed(true)
                    .commit()
            }

        }







    }

    private  fun handleIntent( intent : Intent) {
        if(Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchBooks(query)
            }

        }
    }

    private fun searchBooks( query: String)
    {


        Thread{

            val url = URL("https://kamorris.com/lab/cis3515/search.php?term=$query")
            val array = JSONArray( url.readText())

            val books = BookList()

            for (i in 0 until array.length()) {
                val book = array.getJSONObject(i)
                books.add(
                    Book(
                        book.getString("title") ,
                        book.getString("author") ,
                        book.getInt("id") ,
                        book.getString("cover_url"),

                    )
                )

            }

           viewmodel.book_list.postValue(books)



        }.start()



    }





}