package com.example.paylasimmvvm.login

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentRegisterBinding
import com.example.paylasimmvvm.util.EventbusData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.greenrobot.eventbus.EventBus


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private var mailWatcher: TextWatcher? = null
    lateinit var mref: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentRegisterBinding.inflate(layoutInflater,container,false)

        val view=binding.root

        mref = FirebaseDatabase.getInstance().reference
        auth= FirebaseAuth.getInstance()

        register()
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mailWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etRegisterMailLayout.error = null

            }

            override fun afterTextChanged(s: Editable?) {

            }

        }

        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun register() {
        binding.etRegisterMailLayout.visibility=View.GONE
        binding.btnKayit.visibility=View.GONE

        binding.textViewKullanici.setOnClickListener {
            binding.textViewKullanici.isEnabled=false
            binding.textViewIsletme.isEnabled=true
            binding.btnKayit.visibility=View.VISIBLE
            binding.etRegisterMailLayout.visibility=View.VISIBLE
            binding. registerMail.setText("")
            binding.textViewKullanici.background=null
            binding.textViewIsletme.setBackgroundResource(R.drawable.textview_bg2)

            binding.registerMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        }

        binding.textViewIsletme.setOnClickListener {
            binding.textViewKullanici.isEnabled=true
            binding.textViewIsletme.isEnabled=false
            binding.btnKayit.visibility=View.VISIBLE
            binding.textViewKullanici.setBackgroundResource(R.drawable.textview_bg2)
            binding.textViewIsletme.background=null
            binding.etRegisterMailLayout.visibility=View.VISIBLE
            binding. registerMail.setText("")
            binding.registerMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        }

        binding.btnKayit.setOnClickListener {

            if(!binding.textViewKullanici.isEnabled){

                if (checkMail( binding.registerMail.text.toString())) {

                    var emailKullanimdaMi = false

                    mref.child("users").child("isletmeler")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }
                            override fun onDataChange(snapshot: DataSnapshot) {

                                if (snapshot.value != null) {

                                    for (user in snapshot.children) {

                                        val okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                        if (okunanKullanici!!.email!! == binding.registerMail.text.toString()) {
                                            binding.etRegisterMailLayout.error="E-posta Kullanımda"
                                            emailKullanimdaMi = true
                                            break
                                        }

                                    }
                                    //işletme değil kullanicilara bak
                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value !=null){
                                                for (user in snapshot.children){

                                                    val okunanKullanici = user.getValue(
                                                        KullaniciBilgileri::class.java)

                                                    if (okunanKullanici!!.email!! == binding.registerMail.text.toString()) {
                                                        binding.etRegisterMailLayout.error="E-posta Kullanımda"
                                                        emailKullanimdaMi = true
                                                        break
                                                    }

                                                } // işletme var kullanici var kullanici bulunamadı,kaydet
                                                if (!emailKullanimdaMi){
                                                    binding. loginroot.visibility = View.GONE

                                                    findNavController().navigate(R.id.kullaniciKayitBilgileriFragment)
                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            binding.registerMail.text.toString(),
                                                            null,
                                                            true,
                                                        )
                                                    )
                                                }
                                            }
                                            if(!emailKullanimdaMi){

                                                binding.loginroot.visibility = View.GONE

                                                findNavController().navigate(R.id.kullaniciKayitBilgileriFragment)

                                                EventBus.getDefault().postSticky(
                                                    EventbusData.kayitBilgileriniGonder(
                                                        null,
                                                        binding.registerMail.text.toString(),
                                                        null,
                                                        true,
                                                    )
                                                )
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    })
                                }

                                //veritabanında hiç işletme  yok, kullanicilara bak
                                else{

                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {

                                            if (snapshot.value !=null){
                                                for (user in snapshot.children){

                                                    val okunanKullanici = user.getValue(
                                                        KullaniciBilgileri::class.java)
                                                    if (okunanKullanici!!.email!! == binding.registerMail.text.toString()) {
                                                        binding.etRegisterMailLayout.error="E-posta Kullanımda"
                                                        emailKullanimdaMi = true
                                                        break
                                                    }

                                                }
                                                //veritabanında işletme yok,kullanici var ancak kayıtlı değil,kaydet
                                                if(!emailKullanimdaMi)
                                                {

                                                    binding.loginroot.visibility = View.GONE

                                                    findNavController().navigate(R.id.kullaniciKayitBilgileriFragment)

                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            binding. registerMail.text.toString(),
                                                            null,
                                                            true,

                                                        )
                                                    )
                                                }
                                            }
                                            if  (!emailKullanimdaMi)
                                            //veritabanında  işletme ve kullanici yok direkt kaydet
                                            {

                                                binding. loginroot.visibility = View.GONE

                                                findNavController().navigate(R.id.kullaniciKayitBilgileriFragment)

                                                EventBus.getDefault().postSticky(
                                                    EventbusData.kayitBilgileriniGonder(
                                                        null,
                                                        binding.registerMail.text.toString(),
                                                        null,
                                                        true,

                                                    )
                                                )

                                            }

                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                                }
                            }
                        })
                }else {
                    binding.etRegisterMailLayout.error="Geçersiz E-posta"

                }
            }
            else{
                if (checkMail( binding.registerMail.text.toString())) {

                    var emailKullanimdaMi = false

                    mref.child("users").child("isletmeler")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }
                            override fun onDataChange(snapshot: DataSnapshot) {

                                if (snapshot.value != null) {

                                    for (user in snapshot.children) {


                                        val okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                        if (okunanKullanici!!.email!! == binding.registerMail.text.toString()) {

                                            binding.etRegisterMailLayout.error="E-posta Kullanımda"
                                            emailKullanimdaMi = true
                                            break
                                        }

                                    }
                                    //işletme değil kullanicilara bak
                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value !=null){
                                                for (user in snapshot.children){

                                                    val okunanKullanici = user.getValue(
                                                        KullaniciBilgileri::class.java)

                                                    if (okunanKullanici!!.email!! == binding.registerMail.text.toString()) {

                                                        binding.etRegisterMailLayout.error="E-posta Kullanımda"
                                                        emailKullanimdaMi = true
                                                        break

                                                    }

                                                } // işletme var kullanici var kullanici bulunamadı,kaydet
                                                if (!emailKullanimdaMi){
                                                    binding. loginroot.visibility = View.GONE

                                                    findNavController().navigate(R.id.isletmeKayitBilgileriFragment)


                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            binding.registerMail.text.toString(),
                                                            null,
                                                            true,

                                                            )
                                                    )
                                                }
                                            }
                                            if(!emailKullanimdaMi){

                                                binding.loginroot.visibility = View.GONE

                                                findNavController().navigate(R.id.isletmeKayitBilgileriFragment)

                                                EventBus.getDefault().postSticky(
                                                    EventbusData.kayitBilgileriniGonder(
                                                        null,
                                                        binding.registerMail.text.toString(),
                                                        null,
                                                        true,

                                                        )
                                                )

                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })

                                }

                                //veritabanında hiç işletme  yok, kullanicilara bak
                                else{

                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {

                                            if (snapshot.value !=null){
                                                for (user in snapshot.children){


                                                    val okunanKullanici = user.getValue(
                                                        KullaniciBilgileri::class.java)
                                                    if (okunanKullanici!!.email!! == binding.registerMail.text.toString()) {


                                                        binding.etRegisterMailLayout.error="E-posta Kullanımda"

                                                        emailKullanimdaMi = true
                                                        break
                                                    }



                                                }
                                                //veritabanında işletme yok,kullanici var ancak kayıtlı değil,kaydet
                                                if(!emailKullanimdaMi)
                                                {

                                                    binding.loginroot.visibility = View.GONE

                                                    findNavController().navigate(R.id.isletmeKayitBilgileriFragment)

                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            binding. registerMail.text.toString(),
                                                            null,
                                                            true,

                                                            )
                                                    )
                                                }
                                            }
                                            if  (!emailKullanimdaMi)
                                            //veritabanında  işletme ve kullanici yok direkt kaydet
                                            {

                                                binding. loginroot.visibility = View.GONE

                                                findNavController().navigate(R.id.isletmeKayitBilgileriFragment)

                                                EventBus.getDefault().postSticky(
                                                    EventbusData.kayitBilgileriniGonder(
                                                        null,
                                                        binding.registerMail.text.toString(),
                                                        null,
                                                        true,

                                                        )
                                                )

                                            }

                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                                }
                            }
                        })
                }else {
                    binding.etRegisterMailLayout.error="Geçersiz E-posta"

                }

            }

        }

    }
    private fun checkMail(kontrolEdilenMail: String): Boolean {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(kontrolEdilenMail).matches()

    }


    override fun onStart() {
        super.onStart()
        binding.registerMail.addTextChangedListener(mailWatcher)
        Log.e("hata","registedasın")
        (activity as AppCompatActivity).supportActionBar?.hide()

    }

    override fun onStop() {
        super.onStop()
        binding.registerMail.removeTextChangedListener(mailWatcher)
    }

}