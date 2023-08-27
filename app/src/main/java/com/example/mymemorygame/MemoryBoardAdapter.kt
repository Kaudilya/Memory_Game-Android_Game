package com.example.mymemorygame

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemorygame.models.BoardSize
import com.example.mymemorygame.models.MemoryCard
import kotlin.math.min

class MemoryBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<MemoryCard>,
    private val cardClickListener:CardClickListener
) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {
//viewholder is an object that provides access to all views of 1 recyclerView element, here its 1 memoryCard.we need to define viewHolder that'l
//encapsulate that view. so we define the inner class below:

    companion object{
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter" //useful while searching in Logcat
    }

    interface CardClickListener{
        fun onCardClicked(position:Int)
    }


//RVAdapterClass is abstract class. for it to function, we've to define all functions of it.

//below func is responsible for figuring out how to create 1 view of our rv.

    //here parent is recyclerView that is passed.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth:Int = parent.width / boardSize.getWidth() - (2 * MARGIN_SIZE)
        val cardHeight:Int = parent.height / boardSize.getHeight() - (2 * MARGIN_SIZE)
        val cardSideLength:Int = min(cardWidth,cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card,parent,false)
        //we are casting as ViewGroup.MarginLayoutParams because then we can set margin parameter.
        val layoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardWidth
        layoutParams.height = cardHeight
        layoutParams.setMargins(MARGIN_SIZE,MARGIN_SIZE,MARGIN_SIZE,MARGIN_SIZE)
        return ViewHolder(view)
    }


//to see how many elements r in rv
    override fun getItemCount() = boardSize.numCards


//takes data at the 'position' and binds it to the viewHolder 'holder'
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

//so instead of being parametrized by base class 'RecyclerView.ViewHolder' it can be parameterized by our inner class 'ViewHolder'.
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)
        fun bind(position:Int){
            val memoryCard = cards[position]

            imageButton.setImageResource(if (memoryCard.isFaceUp) memoryCard.identifier else R.drawable.ic_launcher_background)
            imageButton.alpha = if (memoryCard.isMatched) .4f else 1.0f
            val colorStateList = if (memoryCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_gray) else null
            ViewCompat.setBackgroundTintList(imageButton, colorStateList)
            imageButton.setOnClickListener{
                Log.i(TAG,"Clicked on position $position")
                cardClickListener.onCardClicked(position)
            }
        }
    }

}
