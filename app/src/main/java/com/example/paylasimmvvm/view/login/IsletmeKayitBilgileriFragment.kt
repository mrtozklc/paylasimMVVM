package com.example.paylasimmvvm.view.login

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentIsletmeKayitBilgileriBinding
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
import java.io.IOException
import java.util.*

class IsletmeKayitBilgileriFragment : Fragment() {
    private lateinit var binding:FragmentIsletmeKayitBilgileriBinding
    var gelenEmail = ""

    lateinit var auth: FirebaseAuth
    lateinit var mref: DatabaseReference
    var emailleKayit = true
    var secilenMuzik:String?=null
    var secilenIsletmeTuru:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding= FragmentIsletmeKayitBilgileriBinding.inflate(layoutInflater,container,false)
        val view= binding.root



        auth = Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

        val muzikTuru = java.util.ArrayList<String>()
        muzikTuru.add("Pop")
        muzikTuru.add("Rock & Blues")
        muzikTuru.add("Rap")
        muzikTuru.add("Jazz")
        muzikTuru.add("Electronic")
        muzikTuru.add("Country")
        muzikTuru.add("Funk,Disco,Soul")
        muzikTuru.add("Hafif Dinleti")



        val isletmeTuru = java.util.ArrayList<String>()
        isletmeTuru.add("Cafe-Bar")
        isletmeTuru.add("Kokteyl Bar")
        isletmeTuru.add("Pub")
        isletmeTuru.add("Club")
        isletmeTuru.add("Restoran Bar")
        isletmeTuru.add("??arap Evi")
        isletmeTuru.add("Meyhane")



        val spinnerAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, muzikTuru)
        binding.spinner3.adapter = spinnerAdapter


        val spinnerAdapter2 = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, isletmeTuru)
        binding.spinner4.adapter = spinnerAdapter2

        binding.spinner4.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
              secilenIsletmeTuru=binding.spinner4.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }


        binding.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                secilenMuzik = binding.spinner3.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


        if (auth.currentUser != null) {
            auth.signOut()
        }

        binding.tvKaydoll.setOnClickListener {
            val action=IsletmeKayitBilgileriFragmentDirections.actionIsletmeKayitBilgileriFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.etAdSoyadISletme.addTextChangedListener(watcher)
        binding.etKullaniciAdiISletme.addTextChangedListener(watcher)
        binding.etSifreIsletme.addTextChangedListener(watcher)
        binding.etAdresIsletme.addTextChangedListener(watcher)
        binding.etTelefonIsletme.addTextChangedListener(watcher)


        binding.btnGirisISletme.setOnClickListener{
            if( binding.etKullaniciAdiISletme.text.toString().trim().length>5 &&  binding.etSifreIsletme.text.toString().trim().length>5 && binding.etAdSoyadISletme.text.toString().trim()
                    .isNotEmpty()){
                if ( binding.etTelefonIsletme.text.toString().trim().length==10){
                    var userNameKullanimdaMi = false
                    mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.value != null) {

                                for (user in snapshot.children) {
                                    val okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                    if (okunanKullanici!!.user_name!! == binding.etKullaniciAdiISletme.text.toString()) {
                                        Toast.makeText(activity, "Kullan??c?? ad?? Kullan??mda", Toast.LENGTH_SHORT).show()
                                        userNameKullanimdaMi = true
                                        break
                                    }
                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value !=null){
                                                for (userr in snapshot.children ){
                                                    val okunanKullanici= user.getValue(
                                                        KullaniciBilgileri::class.java)
                                                    if (okunanKullanici != null) {
                                                        if (okunanKullanici.user_name!!.equals( binding.etKullaniciAdiISletme.text.toString())) {
                                                            Toast.makeText(activity, "Kullan??c?? ad?? Kullan??mda", Toast.LENGTH_SHORT).show()
                                                            userNameKullanimdaMi = true
                                                            break
                                                        }
                                                    }

                                                }
                                            }
                                            if (!userNameKullanimdaMi) {

                                                if (emailleKayit) {

                                                    val sifre =  binding.etSifreIsletme.text.toString()
                                                    val adSoyad =  binding.etAdSoyadISletme.text.toString()
                                                    val userName =  binding.etKullaniciAdiISletme.text.toString()
                                                    val adres= binding.etAdresIsletme.text.toString()
                                                    val telefon= binding.etTelefonIsletme.text.toString()

                                                    auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                                        .addOnCompleteListener { p0 ->
                                                            if (p0.isSuccessful) {
                                                                val userID = auth.currentUser!!.uid
                                                                getAddressFromLocation(adres, context, userID)

                                                                val kaydedilecekKullaniciDetaylari = KullaniciBilgiDetaylari("0", "", "", secilenMuzik, secilenIsletmeTuru, adres, null,null)

                                                                val kaydedilecekKullanici = KullaniciBilgileri(gelenEmail, sifre, userName, adSoyad, telefon, userID, "", kaydedilecekKullaniciDetaylari)

                                                                mref.child("users")
                                                                    .child("isletmeler")
                                                                    .child(userID)
                                                                    .setValue(kaydedilecekKullanici)
                                                                    .addOnCompleteListener { p0 ->
                                                                        if (p0.isSuccessful) { Toast.makeText(activity, "Ho??geldiniz $userName", Toast.LENGTH_SHORT).show()
                                                                            val action = IsletmeKayitBilgileriFragmentDirections.actionIsletmeKayitBilgileriFragmentToHomeFragment()
                                                                            Navigation.findNavController(it).navigate(action)
                                                                            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                                                                            bottomNav.visibility = View.VISIBLE

                                                                        } else {
                                                                            auth.currentUser!!.delete().addOnCompleteListener { p0 ->
                                                                                    if (p0.isSuccessful) { Toast.makeText(activity, "Kullan??c?? kaydedilemedi, Tekrar deneyin", Toast.LENGTH_SHORT).show()
                                                                                    }
                                                                                }
                                                                        }
                                                                    }
                                                            }
                                                        }
                                                }
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                                }
                            }
                            //veritaban??nda kullan??c?? yok, aynen kaydet
                            else{
                                if (emailleKayit) {
                                    val sifre =  binding.etSifreIsletme.text.toString()
                                    val adSoyad =  binding.etAdSoyadISletme.text.toString()
                                    val userName =  binding.etKullaniciAdiISletme.text.toString()
                                    val adres= binding.etAdresIsletme.text.toString()
                                    val telefon= binding.etTelefonIsletme.text.toString()

                                    auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                        .addOnCompleteListener { p0 ->
                                            if (p0.isSuccessful) {
                                                val userID = auth.currentUser!!.uid

                                                getAddressFromLocation(adres, context, userID)

                                                //oturum a??an kullan??c??n verilerini databaseye kaydet

                                                val kaydedilecekKullaniciDetaylari =
                                                    KullaniciBilgiDetaylari("0", "", "", secilenMuzik, secilenIsletmeTuru, adres, null,null)

                                                val kaydedilecekKullanici = KullaniciBilgileri(gelenEmail, sifre, userName, adSoyad, telefon, userID, "", kaydedilecekKullaniciDetaylari)

                                                mref.child("users").child("isletmeler")
                                                    .child(userID).setValue(kaydedilecekKullanici)
                                                    .addOnCompleteListener { p0->
                                                        if (p0.isSuccessful) {
                                                            Toast.makeText(activity, "Ho??geldiniz $userName", Toast.LENGTH_SHORT).show()
                                                            val action = IsletmeKayitBilgileriFragmentDirections.actionIsletmeKayitBilgileriFragmentToHomeFragment()
                                                            Navigation.findNavController(it).navigate(action)
                                                            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                                                            bottomNav.visibility = View.VISIBLE
                                                        } else {
                                                            auth.currentUser!!.delete().addOnCompleteListener { p0 ->
                                                                    if (p0.isSuccessful) {
                                                                        Toast.makeText(activity, "Kullan??c?? kaydedilemedi, Tekrar deneyin", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(activity,"L??tfen telefon numaras??n?? 10 hane ??eklinde giriniz.", Toast.LENGTH_SHORT).show()

                }

            }else{
                Toast.makeText(activity,"Kullan??c?? ad?? ve ??ifre en az 6 karakter olmal??d??r.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    var watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            if ( binding.etAdSoyadISletme.text.toString().isNotEmpty() && binding.etKullaniciAdiISletme.text.toString()
                    .isNotEmpty() && binding.etSifreIsletme.text.toString().isNotEmpty() && binding.etAdresIsletme.text.toString()
                    .isNotEmpty() && binding.etTelefonIsletme.text.toString().isNotEmpty()
            ) {
                binding. btnGirisISletme.isEnabled = true
                binding.btnGirisISletme.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnGirisISletme.setBackgroundColor(
                    ContextCompat.getColor(activity!!, R.color.teal_700))
            } else {
                binding.btnGirisISletme.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))
                binding. btnGirisISletme.setTextColor(ContextCompat.getColor(activity!!,R.color.black))
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }

    @Subscribe(sticky = true)

    internal fun onKayitEvent(kayitbilgileri: EventbusData.kayitBilgileriniGonder) {

        if (kayitbilgileri.emailkayit) {
            emailleKayit = true
            gelenEmail = kayitbilgileri.email!!



        }
    }

    fun getAddressFromLocation(
        locationAddress: String,
        context: Context?,
        userID:String,
    ) {
        val thread: Thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    val addressList: List<*>? = geocoder.getFromLocationName(locationAddress, 1)
                    if (addressList != null && addressList.isNotEmpty()) {
                        val address = addressList[0] as Address



                        val latitude=address.latitude
                        val longitude=address.longitude

                        mref.child("users").child("isletmeler").child(userID).child("user_detail").child("latitude").setValue(latitude)
                        mref.child("users").child("isletmeler").child(userID).child("user_detail").child("longitude").setValue(longitude)
                    }
                } catch (_: IOException) {
                } finally {

                }
            }
        }
        thread.start()
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