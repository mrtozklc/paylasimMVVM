package com.example.paylasimmvvm.login

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SignOutFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alert = AlertDialog.Builder(this.requireActivity(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert)
            .setTitle("Çıkış Yap")
            .setMessage("Emin misiniz?")
            .setPositiveButton("Çıkış Yap") { p0, p1 ->
                deleteToken()
                FirebaseAuth.getInstance().signOut()

                findNavController().popBackStack()

                findNavController().navigate(R.id.loginFragment)
            }
            .setNegativeButton("İptal"
            ) { p0, p1 -> dismiss() }
            .create()

        return alert
    }
    private fun deleteToken() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val database = FirebaseDatabase.getInstance().reference
            val usersRef = database.child("users")
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        for (userSnapshot in childSnapshot.children) {
                            if (userSnapshot.child("user_id").value == currentUser.uid) {
                                val userRef = userSnapshot.ref
                                userRef.child("FCM_TOKEN").removeValue()
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    databaseError.toException().printStackTrace()
                }
            })
        }
    }


}