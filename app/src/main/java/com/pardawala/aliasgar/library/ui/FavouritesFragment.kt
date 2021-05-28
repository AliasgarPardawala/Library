package com.pardawala.aliasgar.library.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pardawala.aliasgar.library.Book
import com.pardawala.aliasgar.library.BookRecyclerViewAdapter
import com.pardawala.aliasgar.library.R
import com.pardawala.aliasgar.library.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.book_list.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class FavouritesFragment : Fragment(R.layout.fragment_favourites) ,
    BookRecyclerViewAdapter.OnItemClickListener {

    private lateinit var bookAdapter: BookRecyclerViewAdapter

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        lifecycleScope.launch {
            val data = (activity as MainActivity).db.getBooks() as ArrayList<Book>
            bookAdapter.submitList(data)
            bookAdapter.notifyDataSetChanged()
        }
        (activity as MainActivity).bottomNavigationView!!.visibility = View.VISIBLE

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MenuInflater(context).inflate(R.menu.menu_login, menu)
        val title = menu.findItem(R.id.login)
        if((activity as MainActivity).loggedInState)
            title.title = "Log Out"
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val topSpacingDecoration =
                TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            bookAdapter =
                BookRecyclerViewAdapter(this@FavouritesFragment)
            Log.d("BookAdapter", bookAdapter.toString())
            adapter = bookAdapter
            setHasFixedSize(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.login -> {
                (activity as MainActivity).login()
                if(item.title == "Log In")
                    item.title = "Log Out"
                else
                    item.title = "Login"
            }
        }
        return true
    }

    override fun onItemClick(book: Book) {
        val action = FavouritesFragmentDirections.actionFavouritesFragmentToBookFragment(
            book,
            book.title
        )
        findNavController().navigate(action)
    }
}