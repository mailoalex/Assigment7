package edu.temple.audiobookplayer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel : ViewModel() {


    var selectedBook  = MutableLiveData<Book>()
    val bookList: MutableLiveData<BookList> by lazy {
        MutableLiveData<BookList>()
    }

    fun updateBooks(_bookList: BookList){

        bookList.value = _bookList
        Log.d("alex", "view model changed books ${bookList.value}")
    }

    fun updateBook(newBook: Book?) {
        Log.d("alex", "view model changed book ${newBook}")
        if(newBook == null){
            return
        }
        selectedBook.value = newBook!!

    }



}