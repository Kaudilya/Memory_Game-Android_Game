package com.example.mymemorygame.models

//objective is to list out every attribute of memory card
data class MemoryCard(
    val identifier:Int,
    var isFaceUp:Boolean = false,
    var isMatched:Boolean = false,

)