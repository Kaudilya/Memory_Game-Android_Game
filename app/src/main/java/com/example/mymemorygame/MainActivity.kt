package com.example.mymemorygame

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemorygame.models.BoardSize
import com.example.mymemorygame.models.MemoryGame
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var adapter: MemoryBoardAdapter
    //getting references to any views that are in screen, so we change it programatically.any widget we've,we declare as private memory variable.
//var will be set by onCreate method by android system, not created at time of construction of activity.
    private lateinit var rvBoard: RecyclerView

    //    private lateinit var llGameInfo: LinearLayout
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private lateinit var memoryGame: MemoryGame  //we know memoryGame is initialized properly, but it'll happen only in onCreate.so lateinit is used.
    private var boardSize: BoardSize = BoardSize.EASY
//    private var boardSize: BoardSize = BoardSize.MEDIUM
//    private var boardSize: BoardSize = BoardSize.HARD

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)
// now we've setup scaffolding for the mainactivity and key references for the components in the layout.

        setupBoard()
    }

    //inflate the menu activity file created in MainActivity:
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.mi_refresh -> {
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()){
                    showAlertDialog("Quit your current game?", null, View.OnClickListener{
                        setupBoard()
                    } )
                }else{
                    setupBoard()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener:View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel",null)
            .setPositiveButton("OK"){_,_ ->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun setupBoard() {
        tvNumPairs.setTextColor(ContextCompat.getColor(this,R.color.noneProgress))
        memoryGame = MemoryGame(boardSize)
//reponsible for underlying dataset of recyclerView and adapting each piece of data into view.provide a binding for the dataset to the views of the recyclerView.
//we're doing so that we can access adapter from anywhere not just MainActivity.kt
        adapter = MemoryBoardAdapter(this, boardSize,memoryGame.cards,object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
//                Log.i(TAG,"Card clicked $position")
                updateGameWithFlip(position)
            }
        }) //total grids=8,hardcoded for now, can change it later.we define new class(MemoryBoardAdapter)
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
//optional.but used for performance optimisation. read documentation - 'ctrl+click'.rv has fixed size regardless of dataset.grids are defined once program runs,so its fixed size,right?so we use this.

//layout manager is responsible for structuring the views. context explanation - stackOverflow
        rvBoard.layoutManager = GridLayoutManager(this,boardSize.getWidth())  //so we have grid effect.;// MainActivity is a context, so we use 'this'.span count is columns(here 2 columns).now hardcoded, later it is dynamic based on user picking.
    }

    private fun updateGameWithFlip(position: Int){

        if (memoryGame.haveWonGame()){
            Snackbar.make(clRoot,"You already won!", Snackbar.LENGTH_LONG).show()
            return
        }

        if (memoryGame.isCardFaceUp(position)){
            Snackbar.make(clRoot, "Invalid move", Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.flipCard(position)){
            Log.i(TAG,"Number of pairs found : ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.noneProgress),
                ContextCompat.getColor(this, R.color.fullProgress),
            ) as Int
            tvNumPairs.setTextColor(color) // setTextColor requires integer. so we convert as int in previous step
            tvNumPairs.text = "Pairs : ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.haveWonGame()){
                Snackbar.make(clRoot,"You Won",Snackbar.LENGTH_SHORT).show()
            }
        }
        tvNumMoves.text = "Moves : ${memoryGame.getNumMoves()}"

        adapter.notifyDataSetChanged()
    }
}