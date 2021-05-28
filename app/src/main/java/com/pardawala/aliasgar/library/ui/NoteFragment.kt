package com.pardawala.aliasgar.library.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.pardawala.aliasgar.library.R
import kotlinx.android.synthetic.main.fragment_note.view.*
import kotlinx.coroutines.launch

class NoteFragment : Fragment(R.layout.fragment_note) {

    lateinit var note: EditText
    lateinit var title: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = NoteFragmentArgs.fromBundle(requireArguments()).title
        note = view.et_note
        lifecycleScope.launch {
            val text = (activity as MainActivity).read(title)
            Log.d("Database", text.toString())
            if(text != null)
                note.setText(text)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MenuInflater(context).inflate(R.menu.menu, menu)
        val menuItem = menu.findItem(R.id.save_review)
        menuItem.isVisible = true
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save_review -> {
                lifecycleScope.launch {
                    (activity as MainActivity).save(title,note.text.toString())
                    Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return true
    }

}