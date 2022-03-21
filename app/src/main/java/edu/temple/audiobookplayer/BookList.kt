package edu.temple.audiobookplayer

class BookList {
    var books = ArrayList<Book>()

    // add book to book list
    fun add(_book: Book ){
        books.add(_book)
    }

    // remove book from book list
    fun remove(_book: Book){
        books.remove(_book)
    }

    // get book with index
    fun get(_index: Int ) : Book{
        return books.get(_index)
    }

    // get amount of books in BookList
    fun size() : Int {
        return books.size
    }






}