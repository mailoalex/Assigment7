package edu.temple.audiobookplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel : ViewModel() {
    var selected_book  = MutableLiveData<Book>()

    fun update_book(newBook: Book) {
        selected_book.value = newBook
    }




    fun is_empty() : Boolean {
        return selected_book.value == null
    }

}