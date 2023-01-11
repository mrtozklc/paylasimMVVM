package com.example.paylasimmvvm.view.login

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.google.firebase.auth.FirebaseAuth


class SignOutFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alert = AlertDialog.Builder(this.requireActivity(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert)
            .setTitle("Çıkış Yap")
            .setMessage("Emin misiniz?")
            .setPositiveButton("Çıkış Yap") { p0, p1 ->
                FirebaseAuth.getInstance().signOut()

                findNavController().popBackStack()

                findNavController().navigate(R.id.loginFragment)
            }
            .setNegativeButton("İptal"
            ) { p0, p1 -> dismiss() }
            .create()

        return alert
    }


}