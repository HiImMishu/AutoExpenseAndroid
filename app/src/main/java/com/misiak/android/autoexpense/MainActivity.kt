package com.misiak.android.autoexpense

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.misiak.android.autoexpense.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener{ _, destination, _ ->
            if (destination.id == R.id.mainScreenFragment)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            else
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            return onNavigateUp()

        return super.onOptionsItemSelected(item)
    }
}