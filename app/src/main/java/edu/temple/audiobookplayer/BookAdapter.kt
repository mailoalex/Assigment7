package edu.temple.audiobookplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter ( _book_list: BookList, _callback: (Book) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var books = _book_list
    val callback = _callback

    inner class BookViewHolder(_view: View) : RecyclerView.ViewHolder(_view) {

        var title: TextView = _view.findViewById(R.id.recycler_title)
        var author: TextView = _view.findViewById(R.id.recycler_author)

        lateinit var book: Book

        init {
            // when clicked call the callback with the book we just got from onBindViewHolder
            _view.setOnClickListener{callback(book)}
        }
    }

    fun refresh(newBookList: BookList){
        books = newBookList
        notifyDataSetChanged()
    }


    // when a view gets "binded" or focused
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var holder = holder as BookViewHolder
        holder.book = books.get( position)
        holder.title.text = books[position].title
        holder.author.text = books[position].author


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Inflate layout file instead of creating views in code
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item ,parent, false)


        return BookViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return books.size()
    }

}