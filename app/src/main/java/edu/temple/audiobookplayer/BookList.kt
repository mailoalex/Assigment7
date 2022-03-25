package edu.temple.audiobookplayer

import java.io.Serializable

class BookList : Serializable {
    var books = ArrayList<Book>()

    // add book to book list
    fun add(_book: Book ){
        books.add(_book)
    }


    fun indexOf( _book: Book) : Int {
        return books.indexOf(_book)
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
    
    fun generate_books() : BookList{
        books.add(Book("Smoke", "Tanny Petteford"))
        books.add(Book(" The Old Gun", "Yvor Wasbey"))
        books.add(Book("Meatballs Part II", "Gisela Urquhart"))
        books.add(Book("Phantasm II", "Trudi Rochell"))
        books.add(Book("Fudoh", "Lynnette McBay"))
        books.add(Book("Sanctum", "Lindy Orcas"))
        books.add(Book("Just a Sigh", "Jesselyn Vallintine"))
        books.add(Book("Sublime", "Yvor Wasbey"))
        books.add(Book("Miehen tie", "Griffin McCree"))
        books.add(Book("Smoke", "Tanny Petteford"))
        books.add(Book(" The Old Gun", "Yvor Wasbey"))
        books.add(Book("Meatballs Part II", "Gisela Urquhart"))
        books.add(Book("Phantasm II", "Trudi Rochell"))
        books.add(Book("Fudoh", "Lynnette McBay"))
        books.add(Book("Sanctum", "Lindy Orcas"))
        books.add(Book("Just a Sigh", "Jesselyn Vallintine"))
        books.add(Book("Sublime", "Yvor Wasbey"))
        books.add(Book("Miehen tie", "Griffin McCree"))
        return this

    }



}