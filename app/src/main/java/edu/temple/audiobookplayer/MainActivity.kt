package edu.temple.audiobookplayer

import android.app.SearchManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class MainActivity : AppCompatActivity() {


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


}