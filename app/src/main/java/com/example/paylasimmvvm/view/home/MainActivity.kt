package com.example.paylasimmvvm.view.home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.MenuItem

import androidx.navigation.NavController

import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.paylasimmvvm.R

import com.example.paylasimmvvm.databinding.ActivityMainBinding
import com.example.paylasimmvvm.view.mesajlar.ChatFragment

import com.google.android.material.bottomnavigation.BottomNavigationView



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav?.setupWithNavController(navController)

        if (intent.extras != null) {
            val gidilecekUserID = intent.extras?.getString("konusulacakKisi")

            if (gidilecekUserID != null ) {
                val chatNavHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                val chatNavController = chatNavHostFragment.navController
                val bundle = Bundle()
                bundle.putString("konusulacakKisi", gidilecekUserID)
                chatNavController.navigate(R.id.chatFragment, bundle)
            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        val currentDestination = navController.currentDestination

        when (item.itemId) {
            R.id.profilFragment -> {
                if (currentDestination != null && currentDestination.id != R.id.profilFragment) {
                    navController.popBackStack(R.id.profilFragment, true)


                    navController.navigate(R.id.profilFragment)
                }
                return true
            }
            R.id.isletmeListFragment -> {
                if (currentDestination != null && currentDestination.id != R.id.isletmeListFragment) {
                    navController.popBackStack(R.id.isletmeListFragment, true)


                    navController.navigate(R.id.isletmeListFragment)
                }
                return true
            }
            R.id.mesajlarFragment -> {
                if (currentDestination != null && currentDestination.id != R.id.mesajlarFragment) {
                    navController.popBackStack(R.id.mesajlarFragment, true)
                    navController.navigate(R.id.mesajlarFragment)
                }
                return true
            }
            R.id.kokteyllerFragment -> {
                if (currentDestination == null || currentDestination.id != R.id.kokteyllerFragment) {
                    navController.popBackStack(R.id.kokteyllerFragment, true)
                    navController.navigate(R.id.kokteyllerFragment)
                }
                return true
            }
            R.id.homeFragment -> {
                if (currentDestination != null && currentDestination.id != R.id.homeFragment) {
                    navController.popBackStack(R.id.homeFragment, true)
                    navController.navigate(R.id.homeFragment)}
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        val navController = findNavController(R.id.fragmentContainerView)
        val currentDestination = navController.currentDestination

        if (currentDestination != null && currentDestination.id == R.id.homeFragment) {
            finish()

        }else if(currentDestination != null && currentDestination.id == R.id.isletmeListFragment){
            navController.popBackStack(R.id.isletmeListFragment, true)


            navController.navigate(R.id.homeFragment)


        }
        else if(currentDestination != null && currentDestination.id == R.id.mesajlarFragment){
            navController.popBackStack(R.id.mesajlarFragment, true)

            navController.navigate(R.id.homeFragment)


        }
        else if(currentDestination != null && currentDestination.id == R.id.kokteyllerFragment){
            navController.popBackStack(R.id.kokteyllerFragment, true)
            navController.navigate(R.id.homeFragment)


        }
        else if(currentDestination != null && currentDestination.id == R.id.profilFragment){
            navController.popBackStack(R.id.profilFragment, true)
            navController.navigate(R.id.homeFragment)


        }
        else{
            super.onBackPressed()
        }

    }

    }




