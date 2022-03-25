package edu.temple.audiobookplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.Serializable

class BookDetailsFragment(): Fragment() {
    var book : Book? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            book = it.getSerializable("book") as Book
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_book_details, container, false)
        layout.findViewById<TextView>(R.id.details_title).text = book?.title
        layout.findViewById<TextView>(R.id.details_author).text = book?.author
        return layout
    }

    companion object {


        fun newInstance( _book: Book):BookDetailsFragment{
            val args: Bundle = Bundle()

            args.putSerializable("book", _book as Serializable)

            val fragment = BookDetailsFragment()

            fragment.arguments = args
            return fragment
        }
    }
}