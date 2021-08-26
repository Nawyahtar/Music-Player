package com.example.musicplayer

import android.net.Uri

data class Song(
    val title:String,
    val artist:String,
    val uri:Uri
)