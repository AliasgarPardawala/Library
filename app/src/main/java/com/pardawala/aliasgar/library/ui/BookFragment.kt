package com.pardawala.aliasgar.library.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RatingBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pardawala.aliasgar.library.R
import com.pardawala.aliasgar.library.db.BookDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_book.*
import kotlinx.android.synthetic.main.fragment_book.view.*
import kotlinx.android.synthetic.main.fragment_book.view.fab_fav
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch


class BookFragment : Fragment(R.layout.fragment_book) {

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val book = BookFragmentArgs.fromBundle(requireArguments()).book
        (activity as MainActivity).bottomNavigationView.visibility = GONE
        var rating = book.rating

        view.title.text = book.title
        view.author.text = "by " + book.author
        view.star_rating.text = book.rating.toString()
        view.star.alpha = book.rating / 5.0F
        view.book_description.text = book.descripton

        view.rating.setOnRatingBarChangeListener() { ratingBar: RatingBar, fl: Float, b: Boolean ->
            view.rev.visibility = VISIBLE
            rating = fl
        }

        view.rev.setOnClickListener {
            val action = BookFragmentDirections.actionBookFragmentToReviewFragment(rating)
            findNavController().navigate(action)

        }

        view.a_logo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(book.buy_link)
            intent.setPackage("com.android.chrome")
            try {
                startActivity(intent)
            } catch (e:Exception) {
                intent.setPackage(null)
                startActivity(intent)
            }
        }

        val requestOptions = RequestOptions
            .placeholderOf(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        Glide.with(View(context).context)
            .applyDefaultRequestOptions(requestOptions)
            .load(book.image)
            .into(book_image)

        fab_fav.setOnClickListener {
            lifecycleScope.launch {
                try {
                    (activity as MainActivity).db.insert(book)
                    Toast.makeText(activity, "Book added to favourites", Toast.LENGTH_SHORT).show()
                }catch (e: Exception) {
                    Log.d("DatabaseError", e.message.toString())
                }
            }
        }
        fab_note.setOnClickListener {
            val action = BookFragmentDirections.actionBookFragmentToNoteFragment(book.title)
            findNavController().navigate(action)
        }
    }
}