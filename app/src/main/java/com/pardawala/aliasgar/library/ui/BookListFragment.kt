package com.pardawala.aliasgar.library.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.view.View.VISIBLE
import android.widget.Adapter
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pardawala.aliasgar.library.*
import com.pardawala.aliasgar.library.Book
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.book_list.*
import kotlinx.android.synthetic.main.book_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BookListFragment : Fragment(R.layout.book_list) ,
    BookRecyclerViewAdapter.OnItemClickListener {

    private lateinit var bookAdapter: BookRecyclerViewAdapter
    private var genre = Firebase.firestore.collection("Fiction")
    private var data: ArrayList<Book> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerOptions = resources.getStringArray(R.array.spinnerItems)
        data = arrayListOf()
        initRecyclerView()

        lifecycleScope.launch {
            val prev_genre = (activity as MainActivity).read("Genre")
            Log.d("Spinner", "! $prev_genre")
            if(prev_genre != null) {
                booksByGenre(spinnerOptions[prev_genre.toInt()])
                view.genre.setSelection(prev_genre.toInt())
            }
            else {
                booksByGenre(spinnerOptions[0])
                view.genre.setSelection(0)
            }
        }
        if((activity as MainActivity).bottomNavigationView != null)
            (activity as MainActivity).bottomNavigationView.visibility = VISIBLE
        setHasOptionsMenu(true)

        view.genre.setSelection(Adapter.NO_SELECTION, true)
        view.genre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("Spinner", p2.toString())
                data = arrayListOf()
                lifecycleScope.launch {
                    (activity as MainActivity).save("Genre", p2.toString())
                    booksByGenre(spinnerOptions[p2])
                }
            }
        }

        view.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                data = arrayListOf()
                lifecycleScope.launch {
                    if (p0 != null) {
                        booksByName(p0)
                    }
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
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
                BookRecyclerViewAdapter(this@BookListFragment)
            Log.d("BookAdapter", bookAdapter.toString())
            adapter = bookAdapter
            setHasFixedSize(true)
        }
    }

    private suspend fun booksByGenre(gen: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val querySnapshot = genre.get().await()
                querySnapshot.forEach {
                    if (it.get("genre").toString() == gen  || gen == "All") {
                        val title = it.get("title").toString()
                        val author = it.get("author").toString()
                        val image = it.get("image").toString()
                        val rating = it.get("rating").toString().toFloat()
                        val description = it.get("description").toString()
                        val buy_link = it.get("buy_link").toString()
                        data.add(
                            Book(
                                title,
                                image,
                                author,
                                rating,
                                description,
                                buy_link
                            )
                        )
                    }
                }
                bookAdapter.submitList(data)
                bookAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.d("FirebaseError", e.message.toString())
            }
        }
    }

    private suspend fun booksByName(search: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val querySnapshot = genre.get().await()
                querySnapshot.forEach {
                    if (it.get("title").toString().contains(search)) {
                        val title = it.get("title").toString()
                        val author = it.get("author").toString()
                        val image = it.get("image").toString()
                        val rating = it.get("rating").toString().toFloat()
                        val description = it.get("description").toString()
                        val buy_link = it.get("buy_link").toString()
                        data.add(
                            Book(
                                title,
                                image,
                                author,
                                rating,
                                description,
                                buy_link
                            )
                        )
                    }
                }
                bookAdapter.submitList(data)
                bookAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Log.d("FirebaseError", e.message.toString())
            }
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
        val action =
            BookListFragmentDirections.actionBookListFragmentToBookFragment(
                book,
                book.title
            )
        findNavController().navigate(action)
    }
}