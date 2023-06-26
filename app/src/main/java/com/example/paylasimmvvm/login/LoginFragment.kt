package com.example.paylasimmvvm.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    lateinit var auth: FirebaseAuth
    lateinit var mref: DatabaseReference
    private lateinit var mauthLis: FirebaseAuth.AuthStateListener
    private var mailWatcher: TextWatcher? = null
    private var sifreWatcher: TextWatcher? = null
    private var sifreResetWatcher: TextWatcher? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= FragmentLoginBinding.inflate(layoutInflater,container,false)
        val view=binding.root
        binding.progressBarLogin.visibility=View.VISIBLE
        binding.loginContainer.visibility=View.GONE
        auth = FirebaseAuth.getInstance()
        mref = FirebaseDatabase.getInstance().reference
        setupAuthLis()
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mailWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etMailLayout.error = null


            }

            override fun afterTextChanged(s: Editable?) {

            }

        }

        sifreWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etPasswordLayout.error = null

            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        sifreResetWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etPasswordResetLayout.error = null

            }

            override fun afterTextChanged(s: Editable?) {

            }
        }


        binding. tvKaydoll.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNav.visibility=View.GONE
       }
        binding.sifremiUnuttum.setOnClickListener{
            binding.loginContainer.visibility=View.GONE
            binding.progressContainer.visibility=View.GONE
            binding.sifremiUnuttumContainer.visibility=View.VISIBLE
        }
        binding.imageViewBack.setOnClickListener {
            binding.loginContainer.visibility=View.VISIBLE
            binding.progressContainer.visibility=View.GONE
            binding.sifremiUnuttumContainer.visibility=View.GONE
        }
        binding.btnLogin.setOnClickListener {
            val email=binding.tvMail.text.toString().trim()
            val checkEmail=android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val sifre=binding.tvSifre.text.toString()
            if (email.isEmpty()||!checkEmail) {
                binding.etMailLayout.error = "Geçersiz kullanici adı"
            }else if(sifre.isEmpty()){
                binding.etPasswordLayout.error = "Şifre boş bırakılamaz"
            }else{
                girisYapacakKullanici(binding.tvMail.text.toString(),binding. tvSifre.text.toString())
            }
        }

        binding.btnEpostaGonder.setOnClickListener {
            val emailSifremiUnuttum=binding.tvSifreUnuttumMail.text.toString().trim()
            val isEmailSifremiUnuttumMailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailSifremiUnuttum).matches()
            if (isEmailSifremiUnuttumMailValid||emailSifremiUnuttum.isEmpty()){
                 val mail= binding.tvSifreUnuttumMail.text.toString()
            Firebase.auth.sendPasswordResetEmail(mail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.etPasswordResetLayout.helperText="E-posta gönderildi"
                    binding.sifremiUnuttumContainer.visibility=View.GONE
                    binding.btnEpostaGonder.isEnabled=false
                }else{
                    binding.etPasswordResetLayout.error="E-posta gönderilirken hata olustu"
                }
            }
        }else{
              binding.etPasswordResetLayout.error="Geçersiz e-posta"
            }
        }

    }

    private fun setupAuthLis() {

        mauthLis= FirebaseAuth.AuthStateListener {
            val user=FirebaseAuth.getInstance().currentUser

            if (user != null && user.isEmailVerified){
                findNavController().navigate(R.id.homeFragment)

                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNav.visibility=View.VISIBLE
                binding.progressBarLogin.visibility=View.GONE

            }else{

                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNav.visibility=View.GONE
                binding.progressBarLogin.visibility=View.GONE
                binding.loginContainer.visibility=View.VISIBLE


            }
            if (binding.sifremiUnuttumContainer.visibility==View.VISIBLE){

                binding.loginContainer.visibility=View.GONE
            }else{
                binding.loginContainer.visibility=View.VISIBLE
            }
        }
    }
    private fun oturumAc(okunanKullanici: KullaniciBilgileri, sifre: String) {

        val girisYapacakEmail: String = okunanKullanici.email.toString()

        auth.signInWithEmailAndPassword(girisYapacakEmail, sifre)
            .addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val navController = findNavController()
                    val user = auth.currentUser

                    if (user != null && user.isEmailVerified) {
                        fcmTokenAl()
                        Toast.makeText(
                            requireContext(),
                            "Hoşgeldiniz \n" + okunanKullanici.user_name,
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigate(R.id.homeFragment)

                        val bottomNav =
                            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                        bottomNav.visibility = View.VISIBLE


                    } else {
                        binding.etMailLayout.error = "E-posta doğrulaması gerekli"

                    }

                } else {
                    binding.etPasswordLayout.error="Hatalı şifre"


                }
            }

    }
    private fun girisYapacakKullanici(emailPhoneNumberUserName: String, sifre: String) {

        var kullaniciBulundu=false

        mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.value != null ){
                    for (ds in snapshot.children) {

                        val okunanKullanici = ds.getValue(KullaniciBilgileri::class.java)

                        if (okunanKullanici!!.email!!.isNotEmpty() && okunanKullanici.email!!.toString() == emailPhoneNumberUserName) {

                            oturumAc(okunanKullanici, sifre)
                            kullaniciBulundu=true
                            break

                        } else if (okunanKullanici.user_name!!.isNotEmpty() && okunanKullanici.user_name!!.toString() == emailPhoneNumberUserName) {
                            oturumAc(okunanKullanici, sifre)
                            kullaniciBulundu=true
                            break
                        }
                    }
                }

                mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        if(snapshot.value != null ){
                            for (ds in snapshot.children) {

                                val okunanKullanici = ds.getValue(KullaniciBilgileri::class.java)

                                if (okunanKullanici!!.email!!.isNotEmpty() && okunanKullanici.email!!.toString() == emailPhoneNumberUserName) {

                                    oturumAc(okunanKullanici, sifre)
                                    kullaniciBulundu=true
                                    break

                                } else if (okunanKullanici.user_name!!.isNotEmpty() && okunanKullanici.user_name!!.toString() == emailPhoneNumberUserName) {
                                    oturumAc(okunanKullanici, sifre)
                                    kullaniciBulundu=true
                                    break
                                }
                            }
                        }
                        if(!kullaniciBulundu){
                            binding.etMailLayout.error="Kullanıcı bulunamadı"

                        }
                    }
                })
            }
        })
    }
    private fun fcmTokenAl() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token=task.result
            newTokenAl(token)
        })
    }
    private fun newTokenAl(newToken: String) {
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
                                userRef.child("FCM_TOKEN").setValue(newToken)
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
    override fun onStart() {
        super.onStart()
        Log.e("hata","logindesin")
        auth.addAuthStateListener(mauthLis)
        binding.tvMail.addTextChangedListener(mailWatcher)
        binding.tvSifre.addTextChangedListener(sifreWatcher)
        binding.tvSifreUnuttumMail.addTextChangedListener(sifreResetWatcher)


        (activity as AppCompatActivity).supportActionBar?.hide()
    }
    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
        binding.tvMail.removeTextChangedListener(mailWatcher)
        binding.tvSifre.removeTextChangedListener(sifreWatcher)
        binding.tvSifreUnuttumMail.removeTextChangedListener(sifreResetWatcher)
    }

}