package edu.temple.audiobookplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import org.w3c.dom.Text
import java.io.InputStream
import java.io.Serializable
import java.net.URL

class BookDetailsFragment(): Fragment() {

    private lateinit var titleView: TextView
    private lateinit var authorView: TextView
    private lateinit var imageView: ImageView

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
        titleView = layout.findViewById(R.id.details_title)
        authorView = layout.findViewById(R.id.details_author)
        imageView = layout.findViewById(R.id.details_image)
        return layout
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(book!=null){
            updateBook(book!!)
        }


    }

    fun setImageFromUrl(url: String) {

        var vm : Bitmap? =null
        Thread{
            val u = URL(url)

            vm = BitmapFactory.decodeStream(u.openConnection().getInputStream())

        }.start()
        imageView.setImageBitmap(vm!!)


    }

    public fun updateBook(_book: Book): BookDetailsFragment{
        _book.run {
            titleView.text = this.title
            authorView.text = this.author
            setImageFromUrl(coverUrl)

        }
        return this
    }

    companion object {

        @JvmStatic
        fun newInstance(_book: Book) =
            BookListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("book", _book)
                }
            }
    }


}