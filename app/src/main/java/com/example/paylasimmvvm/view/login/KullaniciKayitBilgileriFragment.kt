package com.example.paylasimmvvm.view.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentKullaniciKayitBilgileriBinding
import com.example.paylasimmvvm.model.KullaniciBilgiDetaylari
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.util.EventbusData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class KullaniciKayitBilgileriFragment : Fragment() {
    private lateinit var binding: FragmentKullaniciKayitBilgileriBinding

    var gelenEmail = ""
    private var telNo = ""
    private var verificationID = ""
    lateinit var auth: FirebaseAuth
    lateinit var mref: DatabaseReference
    var emailleKayit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentKullaniciKayitBilgileriBinding.inflate(layoutInflater,container,false)

        val view = binding.root

        auth = Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

        if (auth.currentUser != null) {
            auth.signOut()
        }

        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etAdSoyad.addTextChangedListener(watcher)
        binding.etKullaniciAdi.addTextChangedListener(watcher)
        binding.etSifre.addTextChangedListener(watcher)

        binding.btnGiris.setOnClickListener {
            if(binding.etAdSoyad.text.toString().trim().isNotEmpty()){

                if(binding.etKullaniciAdi.text.toString().trim().isNotEmpty() && binding.etSifre.text.toString().trim().length>5 && binding.etAdSoyad.text.toString().trim().isNotEmpty())
                {
                    var userNameKullanimdaMi = false

                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.value != null) {
                                for (user in snapshot.children) {
                                    val okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                    if (okunanKullanici!!.user_name!! == binding.etKullaniciAdi.text.toString()) {
                                        Toast.makeText(activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT).show()
                                        userNameKullanimdaMi = true
                                        break
                                    }
                                    mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value !=null){
                                                for (userr in snapshot.children){
                                                    val okunanKullanici = user.getValue(
                                                        KullaniciBilgileri::class.java)
                                                    if (okunanKullanici!!.user_name!! == binding.etKullaniciAdi.text.toString()) {
                                                        Toast.makeText(activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT).show()
                                                        userNameKullanimdaMi = true
                                                        break
                                                    }
                                                }
                                            }

                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                                }
                                if (!userNameKullanimdaMi) {
                                    //kullanıcı email ile kayıt
                                    if (emailleKayit) {
                                        val sifre = binding.etSifre.text.toString()
                                        val adSoyad = binding.etAdSoyad.text.toString()
                                        val userName = binding.etKullaniciAdi.text.toString()

                                        auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                            .addOnCompleteListener { p0 ->
                                                if (p0.isSuccessful) {
                                                    Log.e("success","çalıştı")
                                                    val userID = auth.currentUser!!.uid
                                                    val kaydedilecekKullaniciDetaylari =
                                                        KullaniciBilgiDetaylari("0", "", "", "", "", null, null,null)
                                                    val kaydedilecekKullanici = KullaniciBilgileri(gelenEmail, sifre, userName, adSoyad, "", userID, "", kaydedilecekKullaniciDetaylari)

                                                    mref.child("users")
                                                        .child("kullanicilar")
                                                        .child(userID)
                                                        .setValue(kaydedilecekKullanici)
                                                        .addOnCompleteListener { p0 ->
                                                            if (p0.isSuccessful) { Toast.makeText(activity, "Hoşgeldiniz \n${userName}", Toast.LENGTH_SHORT).show()

                                                                findNavController().navigate(R.id.homeFragment)
                                                                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                                                                bottomNav.visibility = View.VISIBLE

                                                            } else { auth.currentUser!!.delete()
                                                                .addOnCompleteListener { p0 ->
                                                                    if (p0.isSuccessful) {
                                                                        Toast.makeText(
                                                                            activity,
                                                                            "Kullanıcı kaydedilemedi, Tekrar deneyin",
                                                                            Toast.LENGTH_SHORT
                                                                        )
                                                                            .show()
                                                                    }
                                                                }
                                                            }
                                                        }

                                                } else {
                                                    Log.e("excepition","çalıştı"+p0.exception.toString())
                                                    Toast.makeText(activity, "Oturum açılamadı :" + p0.exception, Toast.LENGTH_SHORT).show()
                                                    Log.e("oturumacılamadı ","elsee")
                                                }
                                            }
                                    }
                                }
                            }
                            //veritabanında kullanıcı yok, aynen kaydet
                            else{
                                if (emailleKayit) {
                                    val sifre = binding.etSifre.text.toString()
                                    val adSoyad = binding.etAdSoyad.text.toString()
                                    val userName = binding.etKullaniciAdi.text.toString()

                                    auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                        .addOnCompleteListener { p0 ->
                                            if (p0.isSuccessful) {

                                                val userID = auth.currentUser!!.uid.toString()


                                                //oturum açan kullanıcın verilerini databaseye kaydedelim...
                                                val kaydedilecekKullaniciDetaylari =
                                                    KullaniciBilgiDetaylari("0", "", "", "", "", null, null,null)

                                                val kaydedilecekKullanici = KullaniciBilgileri(gelenEmail, sifre, userName, adSoyad, "", userID, "", kaydedilecekKullaniciDetaylari)

                                                mref.child("users").child("kullanicilar")
                                                    .child(userID).setValue(kaydedilecekKullanici)
                                                    .addOnCompleteListener { p0 ->
                                                        if (p0.isSuccessful) {
                                                            Toast.makeText(activity, "Hoşgeldiniz \n${userName}", Toast.LENGTH_SHORT).show()
                                                            findNavController().navigate(R.id.homeFragment)
                                                            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                                                            bottomNav.visibility = View.VISIBLE

                                                        } else {
                                                            auth.currentUser!!.delete()
                                                                .addOnCompleteListener { p0 ->
                                                                    if (p0.isSuccessful) {
                                                                        Toast.makeText(activity, "Kullanıcı kaydedilemedi, Tekrar deneyin", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }
                                                        }
                                                    }
                                            }
                                        }
                                }
                            }
                        }
                    })
                }else{
                    Toast.makeText(activity,"Şifre en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(activity,"Ad ve Soyad boş bırakılamaz.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    var watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            binding.btnGiris.isEnabled = binding.etAdSoyad.text.toString().isNotEmpty() && binding.etKullaniciAdi.text.toString()
                .isNotEmpty() && binding.etSifre.text.toString().isNotEmpty()
        }
        override fun afterTextChanged(p0: Editable?) {
        }
    }

    @Subscribe(sticky = true)

    internal fun onKayitEvent(kayitbilgileri: EventbusData.kayitBilgileriniGonder) {

        if (kayitbilgileri.emailkayit) {
            emailleKayit = true
            gelenEmail = kayitbilgileri.email!!



        } else {
            emailleKayit = false
            telNo = kayitbilgileri.telNo!!
            verificationID = kayitbilgileri.verificationID!!


        }
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }
    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

}