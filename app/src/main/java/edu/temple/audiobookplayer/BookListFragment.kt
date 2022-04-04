package edu.temple.audiobookplayer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable


class BookListFragment : Fragment() {

    var bookList : BookList? =null
    var adapter : BookAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            bookList = it.getSerializable("bookList") as BookList
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler)


        if( bookList!= null) {
            adapter = BookAdapter(bookList!!) {
                ViewModelProvider(requireActivity()).get(BookViewModel::class.java).update_book(
                    it
                )
            }
            recycler.adapter = adapter
            recycler.layoutManager = LinearLayoutManager(requireActivity())
        }


    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(_bookList: BookList) =
            BookListFragment().apply {
                arguments = Bundle().apply {
                   putSerializable("bookList", _bookList)
                }
            }
    }
}