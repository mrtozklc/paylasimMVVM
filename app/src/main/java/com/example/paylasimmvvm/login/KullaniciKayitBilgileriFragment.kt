package com.example.paylasimmvvm.login

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
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentKullaniciKayitBilgileriBinding
import com.example.paylasimmvvm.util.EventbusData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.EmailAuthProvider
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
    private var adSoyadWatcher: TextWatcher? = null
    private var sifreWatcher: TextWatcher? = null
    private var kullaniciAdiWatcher: TextWatcher? = null


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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adSoyadWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etAdSoyadlayout.error = null
            }

            override fun afterTextChanged(s: Editable?) {



            }
        }

        kullaniciAdiWatcher= object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    binding.etkullaniciAdilayout.error = null

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

        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.btnGiris.setOnClickListener {

            val adSoyad = binding.etAdSoyad.text.toString().trim()
            val kullaniciAdi = binding.etKullaniciAdi.text.toString().trim()
            val sifre = binding.etSifre.text.toString().trim()
            val sifreTekrar = binding.etSifreTekrar.text.toString().trim()

            if (adSoyad.isEmpty()) {
                binding.etAdSoyadlayout.error = "Ad ve Soyad boş bırakılamaz"
            } else if (kullaniciAdi.isEmpty()) {
                binding.etkullaniciAdilayout.error = "Kullanıcı adı boş bırakılamaz"
            } else if (sifre.isEmpty()) {
                binding.etPasswordLayout.error = "Şifre boş bırakılamaz"
            } else if (sifre.length < 6 || sifre != sifreTekrar) {
                binding.etPasswordLayout.error = "Şifre en az 6 karakter olmalıdır ve eşleşmelidir."
            } else {
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
                                    binding.etkullaniciAdilayout.error = "Kullanıcı adı kullanımda"

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
                                                    binding.etkullaniciAdilayout.error = "Kullanıcı adı kullanımda"
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
                                                val userID = auth.currentUser!!.uid
                                                val kaydedilecekKullaniciDetaylari =
                                                    KullaniciBilgiDetaylari("0", "", "", "", "", null, null,null)
                                                val kaydedilecekKullanici = KullaniciBilgileri(gelenEmail, sifre, userName, adSoyad, "", userID, "", kaydedilecekKullaniciDetaylari)

                                                mref.child("users")
                                                    .child("kullanicilar")
                                                    .child(userID)
                                                    .setValue(kaydedilecekKullanici)
                                                    .addOnCompleteListener { p0 ->
                                                        if (p0.isSuccessful) {    val user = auth.currentUser
                                                            user?.sendEmailVerification()
                                                                ?.addOnCompleteListener { verificationTask ->
                                                                    if (verificationTask.isSuccessful) {
                                                                        Toast.makeText(activity,"Lütfen Mailinizi Kontrol Edin",Toast.LENGTH_LONG).show()
                                                                        val action=KullaniciKayitBilgileriFragmentDirections.actionKullaniciKayitBilgileriFragmentToLoginFragment()
                                                                        Navigation.findNavController(it).navigate(action)
                                                                    } else {
                                                                        Log.e("doğrulama epostası hatası",""+verificationTask.exception)

                                                                    }
                                                                }

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
                                                        val user = auth.currentUser
                                                        user?.sendEmailVerification()
                                                            ?.addOnCompleteListener { verificationTask ->
                                                                if (verificationTask.isSuccessful) {
                                                                    Toast.makeText(activity,"Lütfen Mailinizi Kontrol Edin",Toast.LENGTH_LONG).show()
                                                                    val action=KullaniciKayitBilgileriFragmentDirections.actionKullaniciKayitBilgileriFragmentToLoginFragment()
                                                                    Navigation.findNavController(it).navigate(action)
                                                                } else {
                                                                    Log.e("doğrulama epostası hatası",""+verificationTask.exception)

                                                                }
                                                            }
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
            }

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
        binding.etAdSoyad.addTextChangedListener(adSoyadWatcher)
        binding.etKullaniciAdi.addTextChangedListener(kullaniciAdiWatcher)
        binding.etSifre.addTextChangedListener(sifreWatcher)
        binding.etSifreTekrar.addTextChangedListener(sifreWatcher)


    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }
    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    override fun onStop() {
        super.onStop()
        binding.etAdSoyad.removeTextChangedListener(adSoyadWatcher)
        binding.etKullaniciAdi.removeTextChangedListener(kullaniciAdiWatcher)
        binding.etSifre.removeTextChangedListener(sifreWatcher)
        binding.etSifreTekrar.removeTextChangedListener(sifreWatcher)
    }

}