package com.example.mymemorygame.models

import com.example.mymemorygame.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize){

    var cards: List<MemoryCard>
    var numPairsFound = 0

    private var numMoves: Int = 0
    private var indexOfSingleSelectedCard: Int? = null

    init{
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map{ MemoryCard(it) } //because isFaceUp and isMatched is defined defaultly as 'False', we are not defining here.

    }

    fun flipCard(position: Int): Boolean {
        ++numMoves
        val card = cards[position]
        var foundMatch: Boolean = false
//        Three cases:
//        0 card flipped - flip 1 card and keep the state
//        1 card flipped - flip another card and keep the state
//        2 card flipped - flip no cards and if match,keep them ; if no match - flip them back
        if (indexOfSingleSelectedCard == null){
            restoreCards()
            indexOfSingleSelectedCard = position
        }else{
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
//            if (!foundMatch)restoreCards()
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier != cards[position2].identifier){
//            restoreCards()
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound +=1
        return true
    }

    private fun restoreCards() {
        for (card in cards){
            if(!card.isMatched) card.isFaceUp = false
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numMoves/2
    }

}