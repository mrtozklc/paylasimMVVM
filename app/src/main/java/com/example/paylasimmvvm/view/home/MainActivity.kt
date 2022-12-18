package com.example.paylasimmvvm.view.home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.MenuItem

import androidx.navigation.NavController

import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.paylasimmvvm.R

import com.example.paylasimmvvm.databinding.ActivityMainBinding

import com.google.android.material.bottomnavigation.BottomNavigationView



class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        val navController = navHostFragment.navController
        setupBottomNavMenu(navController)



    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav?.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.fragmentContainerView)) || super.onOptionsItemSelected(item)
    }



}