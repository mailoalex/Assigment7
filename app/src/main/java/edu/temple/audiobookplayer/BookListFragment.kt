package edu.temple.audiobookplayer

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable


class BookListFragment : Fragment() {
    var _bookList: BookList? = null
    val viewmodel: BookViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_book_list, container, false)
        val recycler_view = layout.findViewById<RecyclerView>(R.id.recycler)

        if (arguments != null) {
            recycler_view.adapter = BookAdapter(arguments?.getSerializable("bookList") as BookList)
            {
                clicked_item(it)
            }
        }


        recycler_view.layoutManager = LinearLayoutManager(this.context)


        return layout
    }



    fun clicked_item(book: Book) {
        val is_portrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        var fragment_id_to_be_replaced = if(is_portrait) R.id.port_frag else R.id.land_detail


        parentFragmentManager.beginTransaction()
            .replace(fragment_id_to_be_replaced, BookDetailsFragment.newInstance(book))
            .addToBackStack(null)
            .commit()

        viewmodel.update_book( book)


    }


    companion object {

        fun newInstance(bookList: BookList): BookListFragment {
            val args: Bundle = Bundle()
            args.putSerializable("bookList", bookList as Serializable)
            val fragment = BookListFragment()
            fragment.arguments = args
            return fragment
        }
    }

}