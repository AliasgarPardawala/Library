package com.pardawala.aliasgar.library.ui

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pardawala.aliasgar.library.R
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddFragment : Fragment(R.layout.fragment_add) {

    private var genre = Firebase.firestore.collection("Fiction")
    lateinit var editArray : Array<EditText>
    lateinit var text :TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        text = view.check_login
        editArray = arrayOf(view.title, view.author, view.genre, view.description, view.image,view.buy)
        val submit = view.add_book

        submit.setOnClickListener {
            if(!(activity as MainActivity).loggedInState) {
                Toast.makeText(activity, "Login to Add Book", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            editArray.forEach {
                if(it.text.isEmpty()) {
                    it.error = "Field cannot be empty"
                    it.requestFocus()
                    return@setOnClickListener
                    }
                }
            if(!Patterns.WEB_URL.matcher(view.image.text).matches()) {
                view.image.error = "Invalid URL"
                view.image.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.WEB_URL.matcher(view.buy.text).matches()) {
                view.buy.error = "Invalid URL"
                view.buy.requestFocus()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val data = hashMapOf(
                    "title" to editArray[0].text.toString(),
                    "author" to editArray[1].text.toString(),
                    "genre" to editArray[2].text.toString(),
                    "description" to editArray[3].text.toString(),
                    "image" to editArray[4].text.toString(),
                    "buy_link" to editArray[5].text.toString()
                 )
                save_document(data)
            }
        }
    }

    suspend fun save_document(data: HashMap<String, String>) {
        CoroutineScope(Dispatchers.Main).launch {
            genre.document(editArray[0].text.toString())
                .set(data)
                .addOnSuccessListener {
                    Log.d("ADD Data", "Book Added")
                    Toast.makeText(activity, "Book Added", Toast.LENGTH_SHORT).show()
                }
            clearEditText()
        }
    }

    fun clearEditText() {
        editArray.forEach {
            it.text.clear()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MenuInflater(context).inflate(R.menu.menu_login, menu)
        val title = menu.findItem(R.id.login)
        if((activity as MainActivity).loggedInState)
            title.title = "Log Out"
        else
            text.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.login -> {
                (activity as MainActivity).login()
                if(item.title == "Log In") {
                    item.title = "Log Out"
                    text.visibility = View.VISIBLE
                }
                else {
                    item.title = "Login"
                    text.visibility = View.GONE
                }
            }
        }
        return true
    }
}