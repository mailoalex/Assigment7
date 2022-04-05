package edu.temple.audiobookplayer

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL


class MainActivity : AppCompatActivity() {

    lateinit  var viewmodel : BookViewModel


    private fun isPortraitMode(): Boolean {
        return supportFragmentManager.findFragmentById(R.id.container2) == null
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewmodel = ViewModelProvider(this).get(BookViewModel::class.java)


        // to search for books
        handleIntent(intent)



        if(savedInstanceState == null){

            if(viewmodel.book_list.value!= null) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container1, BookListFragment.newInstance(viewmodel.book_list.value!!))
                    .commit()
            }



        }

        if(isPortraitMode()) {
            if(!viewmodel.is_empty()){
                val fg = BookDetailsFragment()
                fg.book = viewmodel.selected_book.value
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, fg )
                    .commit()
            }

        }else{ // landscape
            if(viewmodel.is_empty()) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container2,BookDetailsFragment())
                    .commit()
            }else{
                val fg = BookDetailsFragment()
                fg.book = viewmodel.selected_book.value
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container2, fg )
                    .addToBackStack(null)
                    .commit()
            }
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            onSearchRequested()
        }





        viewmodel.book_list.observe(this) {
            Log.d("ALEX", "CHANGED BOOKS $it")
            (supportFragmentManager.findFragmentById(R.id.container1) as BookListFragment).adapter?.refresh(it!!)
        }


        viewmodel.selected_book.observe(this) {
            Log.d("ALEX", "SELECTED BOOK ${it.title}")
            if(isPortraitMode()){

                if(!viewmodel.is_empty()){
                    val fg = BookDetailsFragment()
                    fg.book = it
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container1, fg )
                        .addToBackStack(null)
                        .commit()
                }

            }else{
                val fg = BookDetailsFragment()
                fg.book = it
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container2, fg )
                    .addToBackStack(null)
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


        runBlocking {
            val books = BookList()
            withContext(Dispatchers.IO) {

                val url = URL("https://kamorris.com/lab/cis3515/search.php?term=$query")
                val array = JSONArray( url.readText())



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

            }


            viewmodel.book_list.value = books

        }






    }





}