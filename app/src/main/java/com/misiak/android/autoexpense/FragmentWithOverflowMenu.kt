package com.misiak.android.autoexpense

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.misiak.android.autoexpense.authentication.SignInFragment


open class FragmentWithOverflowMenu : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.signInFragment)
            SignInFragment.signOut(requireContext())

        val mIntent = Intent(requireActivity(), MainActivity::class.java)
        requireActivity().finish()
        requireActivity().startActivity(mIntent)
        return super.onOptionsItemSelected(item)
    }
}