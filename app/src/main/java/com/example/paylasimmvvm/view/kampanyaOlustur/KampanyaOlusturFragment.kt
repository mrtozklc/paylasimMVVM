package com.example.paylasimmvvm.view.kampanyaOlustur

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentKampanyaOlusturBinding
import com.example.paylasimmvvm.model.KampanyaOlustur
import com.example.paylasimmvvm.adapter.HomeFragmentRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class KampanyaOlusturFragment : Fragment() {
    private lateinit var binding:FragmentKampanyaOlusturBinding
    private var secilenbitmap: Bitmap? = null
    var secilenSure:String?=null
    private var secilengorsel: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var db:DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var mauthLis: FirebaseAuth.AuthStateListener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKampanyaOlusturBinding.inflate(layoutInflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db= FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        auth=Firebase.auth




        setupAuthLis()

        val timer = ArrayList<String>()
        timer.add("Geri Sayım Süresi Seciniz")
        timer.add("1 saat")
        timer.add("2 saat")
        timer.add("3 saat")


        val spinnerAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, timer)
        binding.spinner.adapter = spinnerAdapter


        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                secilenSure = binding.spinner.selectedItem.toString()


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        binding.gorselSec.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)


            }else{
                val galeriintent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriintent,2)
            }
        }
        binding.btnPaylas.setOnClickListener {
            binding.progressBarKampanya.visibility=View.VISIBLE
            binding.gorselSec.visibility=View.GONE
            binding. aciklamaId.visibility=View.GONE
            binding. spinner.visibility=View.GONE
            binding.btnPaylas.visibility=View.GONE
            secilenSure = binding.spinner.selectedItem.toString()

            val intent=Intent(requireActivity(), HomeFragmentRecyclerAdapter::class.java)
            intent.putExtra("time", secilenSure)




            val uuid = UUID.randomUUID()
            val gorselismi = "${uuid}.jpg"
            val reference = storage.reference
            val gorselreference = reference.child("kampanyalar").child(gorselismi)


            if (secilengorsel!=null){
                gorselreference.putFile(secilengorsel!!).addOnSuccessListener {
                    val yuklenengorselreference=
                        FirebaseStorage.getInstance().reference.child("kampanyalar").child(gorselismi)
                    yuklenengorselreference.downloadUrl.addOnSuccessListener { uri->
                        val downloadurl=uri.toString()
                        veritabaninakaydet(downloadurl)


                    }.addOnFailureListener {  exception->
                        Toast.makeText(requireActivity(), exception.localizedMessage, Toast.LENGTH_LONG).show()

                    }



                }



            }else{
                Toast.makeText(requireActivity(),"Lütfen Fotoğraf Yükleyiniz", Toast.LENGTH_LONG).show()




            }

        }
    }

    private fun setupAuthLis() {


        mauthLis= FirebaseAuth.AuthStateListener {
            val user=FirebaseAuth.getInstance().currentUser
            Log.e("auth", "auth çalıstı$user")

            if (user==null){
                findNavController().popBackStack(R.id.kampanyaOlusturFragment,true)


                findNavController().navigate(R.id.loginFragment)


            }
        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","kampanyaolusturdasın")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
    }




    private fun veritabaninakaydet(downloadurl:String?){




        val postID = db.child("kampanya").child(auth.uid!!).push().key
        val yuklenenPost = KampanyaOlustur(auth.uid, postID, 0,binding.aciklamaId.text.toString(),secilenSure, downloadurl)



        db.child("kampanya").child(auth.uid!!).child(postID!!).setValue(yuklenenPost)
        db.child("kampanya").child(auth.uid!!).child(postID).child("yuklenme_tarih").setValue(
            ServerValue.TIMESTAMP)
        Toast.makeText(requireActivity(),"Kampanya Oluşturuldu", Toast.LENGTH_LONG).show()


        if(binding.aciklamaId.text.toString().isNotEmpty()){

            db.child("yorumlar").child(postID).child(postID).child("user_id").setValue(auth.uid)
            db.child("yorumlar").child(postID).child(postID).child("yorum_tarih").setValue(
                ServerValue.TIMESTAMP)
            db.child("yorumlar").child(postID).child(postID).child("yorum").setValue(binding.aciklamaId.text.toString())
            db.child("yorumlar").child(postID).child(postID).child("yorum_begeni").setValue("0")

        }
        kampanyaSayisiniGuncelle()
        binding.progressBarKampanya.visibility=View.GONE
        binding.gorselSec.visibility=View.VISIBLE
        binding.aciklamaId.visibility=View.VISIBLE
        binding.spinner.visibility=View.VISIBLE
        binding.btnPaylas.visibility=View.VISIBLE

        findNavController().navigate(R.id.homeFragment)



    }

    private fun kampanyaSayisiniGuncelle() {

        db.child("users").child("isletmeler").child(auth.uid!!).child("user_detail").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var oankiGonderiSayisi= snapshot.child("post").value.toString().toInt()
                oankiGonderiSayisi++
                db.child("users").child("isletmeler").child(auth.uid!!).child("user_detail").child("post").setValue(oankiGonderiSayisi.toString())
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }




    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                val galeriintent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriintent,2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==2&& resultCode== Activity.RESULT_OK&& data!=null){
            secilengorsel= data.data
            if(secilengorsel!=null){
                if (Build.VERSION.SDK_INT>=28){
                    val source= ImageDecoder.createSource(requireActivity().contentResolver,secilengorsel!!)
                    secilenbitmap= ImageDecoder.decodeBitmap(source)
                    binding.gorselSec.setImageBitmap(secilenbitmap)

                }else{
                    secilenbitmap=
                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilengorsel)
                    binding.gorselSec.setImageBitmap(secilenbitmap) }


            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}