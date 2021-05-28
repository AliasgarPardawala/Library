package com.pardawala.aliasgar.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.list_rv.view.*

class BookRecyclerViewAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<Book> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_rv, parent, false))
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is BookViewHolder -> {
                holder.bind(items.get(position))
                holder.itemView.setOnClickListener {
                    listener.onItemClick(items[position])
                }
            }
        }
    }

    fun submitList(bookList: ArrayList<Book>){
        items = bookList
    }


    class BookViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val blogImage: ImageView = itemView.book_image
        val blogTitle: TextView = itemView.book_title
        val blogAuthor: TextView = itemView.book_author
        val blogRating: RatingBar = itemView.rating

        fun bind(book: Book){

            blogTitle.text = book.title
            blogAuthor.text = "by  " + book.author
            blogRating.rating = book.rating

            val requestOptions = RequestOptions
                .placeholderOf(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(book.image)
                .into(blogImage)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(book: Book)
    }
}