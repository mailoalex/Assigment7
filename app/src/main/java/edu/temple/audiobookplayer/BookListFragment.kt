package edu.temple.audiobookplayer

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable


class BookListFragment : Fragment() {
    var _bookList: BookList? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_book_list, container, false)
        val recycler_view = layout.findViewById<RecyclerView>(R.id.recycler)

        if(arguments != null){
            recycler_view.adapter = BookAdapter( arguments?.getSerializable("bookList") as BookList){

            }
        }

        recycler_view.layoutManager = LinearLayoutManager(this.context)
        return layout
    }


    companion object {


        fun newInstance( bookList: BookList):BookListFragment{
            val args: Bundle = Bundle()
            args.putSerializable("bookList", bookList as Serializable)
            val fragment = BookListFragment()
            fragment.arguments = args
            return fragment
        }
    }

}