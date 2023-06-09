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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


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

        val extras = intent.extras
        if (extras != null) {
            val gidilecekUserIDChatFragment = extras.getString("konusulacakKisi")
            val begenilenGonderiID = extras.getString("begenilenGonderi")
            val yorumYapilanGonderiID = extras.getString("yorumYapilanGonderi")
            val begenilenYorumGonderiID = extras.getString("begenilenYorumGonderiID")
            val begenilenUserID = extras.getString("begenilenUserID")
            val begenilenYorumKey = extras.getString("begenilenYorumKey")
            val gonderiYorumKey = extras.getString("gonderiYorumKey")
            val isletmeYorumUserID = extras.getString("isletmeYorumYapilanUserID")
            val isletmeYorumKey = extras.getString("isletmeYorumKey")
            val bildirimID = extras.getString("bildirimID")

            if (!isletmeYorumUserID.isNullOrEmpty()) {
                val yorumlarNavHostFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                val yorumlarNavController = yorumlarNavHostFragment.navController
                val bundle = Bundle().apply {
                    putBoolean("isPost", false)
                    putString("user_id", isletmeYorumUserID)
                    putString("post_id", "")
                    putString("post_url", "")
                    putString("yorumKey", isletmeYorumKey)
                    putString("bildirimID", bildirimID)
                }
                yorumlarNavController.navigate(R.id.comment_fragment, bundle)
            }

            if (!gidilecekUserIDChatFragment.isNullOrEmpty()) {
                val chatNavHostFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                val chatNavController = chatNavHostFragment.navController
                val bundle = Bundle().apply {
                    putString("konusulacakKisi", gidilecekUserIDChatFragment)
                }
                chatNavController.navigate(R.id.chatFragment, bundle)
            }

            if (!begenilenGonderiID.isNullOrEmpty()) {

                val homeNavHostFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                val homeNavController = homeNavHostFragment.navController
                val bundle = Bundle().apply {
                    putString("gonderi_id", begenilenGonderiID)
                    putString("user_id", begenilenUserID)
                    putString("yorumKey", "")
                    putBoolean("yorum_var", true)
                    putString("bildirimID", bildirimID)
                }
                homeNavController.navigate(R.id.gonderiDetayFragment, bundle)
            }

            if (!yorumYapilanGonderiID.isNullOrEmpty()) {
                val homeNavHostFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                val homeNavController = homeNavHostFragment.navController
                val bundle = Bundle().apply {
                    putString("gonderi_id", yorumYapilanGonderiID)
                    putString("user_id", begenilenUserID)
                    putString("yorumKey", gonderiYorumKey)
                    putBoolean("yorum_var", true)
                    putString("bildirimID", bildirimID)
                }
                homeNavController.navigate(R.id.gonderiDetayFragment, bundle)
            }

            if (!begenilenYorumKey.isNullOrEmpty()) {
                if (!begenilenYorumGonderiID.isNullOrEmpty() && begenilenYorumGonderiID.isNotEmpty()) {
                    val yorumlarNavHostFragment =
                        supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                    val yorumlarNavController = yorumlarNavHostFragment.navController
                    val bundle = Bundle().apply {
                        putString("gonderi_id", begenilenYorumGonderiID)
                        putString("user_id", begenilenUserID)
                        putString("yorumKey", begenilenYorumKey)
                        putBoolean("yorum_var", true)
                        putString("bildirimID", bildirimID)
                    }
                    yorumlarNavController.navigate(R.id.gonderiDetayFragment, bundle)
                } else {
                    val yorumlarNavHostFragment =
                        supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                    val yorumlarNavController = yorumlarNavHostFragment.navController
                    val bundle = Bundle().apply {
                        putBoolean("isPost", false)
                        putString("user_id", begenilenUserID)
                        putString("post_id", begenilenUserID)
                        putString("post_url", "")
                        putString("yorumKey", begenilenYorumKey)
                        putString("bildirimID", bildirimID)
                    }
                    yorumlarNavController.navigate(R.id.comment_fragment, bundle)
                }
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




