package com.pardawala.aliasgar.library.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.pardawala.aliasgar.library.R
import kotlinx.android.synthetic.main.write_review.view.*

class ReviewFragment : Fragment(R.layout.write_review) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rating = ReviewFragmentArgs.fromBundle(requireArguments()).rating
        view.rating_bar.rating = rating
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MenuInflater(context).inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save_review -> {

            }
        }
        return true
    }
}