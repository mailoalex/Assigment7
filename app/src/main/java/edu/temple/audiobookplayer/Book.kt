package edu.temple.audiobookplayer

import java.io.Serializable

data class Book (val title: String, val author: String, val id : Int, val coverUrl: String, val duration: Int) : Serializable