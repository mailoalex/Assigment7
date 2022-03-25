package edu.temple.audiobookplayer

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val list_books = BookList().generate_books()


        var viewmodel = ViewModelProvider(this).get(BookViewModel::class.java)



        val listFragment = BookListFragment.newInstance(list_books)
        val frag_to_load = if (viewmodel.is_empty()) listFragment else BookDetailsFragment.newInstance(viewmodel.selected_book.value!!)

        // PORTRAIT MODE
        // check if there was a book clicked if not load list if load details
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

            supportFragmentManager.beginTransaction()
                .replace(R.id.port_frag, frag_to_load)
                .addToBackStack(null)
                .commit()
        }
        // LANDSCAPE MODE
        else {
            // in landscape mode we always have a list. this is just loading that list
            supportFragmentManager.beginTransaction()
                .replace(R.id.land_list, listFragment)
                .addToBackStack(null)
                .commit()

            // check if there was a book clicked if not ignore if yes change details to that book
            if (!viewmodel.is_empty()) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.land_detail, BookDetailsFragment.newInstance(viewmodel.selected_book.value!!))
                    .addToBackStack(null)
                    .commit()
            }
        }

    }


}