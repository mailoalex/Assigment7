package edu.temple.audiobookplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // TODO: this should be read from string resource or a generator method
        val list_books = BookList()
        list_books.add(Book("Sublime","Yvor Wasbey"))
        list_books.add(Book("Smoke","Tanny Petteford"))
        list_books.add(Book(" The Old Gun","Yvor Wasbey"))
        list_books.add(Book("Meatballs Part II","Gisela Urquhart"))
        list_books.add(Book("Phantasm II","Trudi Rochell"))
        list_books.add(Book("Fudoh","Lynnette McBay"))
        list_books.add(Book("Sanctum","Lindy Orcas"))
        list_books.add(Book("Just a Sigh","Jesselyn Vallintine"))
        list_books.add(Book("Miehen tie","Griffin McCree"))



        val f = BookListFragment.newInstance(list_books)


        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, f )
            .commit()
    }
}