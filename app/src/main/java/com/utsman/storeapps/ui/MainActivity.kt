/*
 * Created by Muhammad Utsman on 22/12/20 9:51 PM
 * Copyright (c) 2020 . All rights reserved.
 */

package com.utsman.storeapps.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.utsman.abstraction.extensions.intentTo
import com.utsman.storeapps.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //Melakukan pemanggilan kelas super onCreate untuk menyelesaikan pembuatan aktivitas seperti
    // hierarki tampilan
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  //Mengatur tata letak antarmuka pengguna
        // untuk aktivitas ini file tata letak didefinisikan dalam file projek res/layout/main_activity.xml

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        NavigationUI.setupWithNavController(bottomNav, navHostFragment.navController)

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu_static, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_action -> {
                intentTo("com.utsman.listing.ui.activity.SearchAppActivity")
                true
            }
            R.id.options_action -> {
                intentTo(OptionsActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}