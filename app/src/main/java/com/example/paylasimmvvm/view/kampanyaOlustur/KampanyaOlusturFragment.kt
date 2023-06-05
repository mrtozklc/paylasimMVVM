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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentKampanyaOlusturBinding
import com.example.paylasimmvvm.model.KampanyaOlustur
import com.example.paylasimmvvm.view.profil.UserProfilFragmentArgs
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKampanyaOlusturBinding.inflate(layoutInflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      arguments?.let {
       val isUser= KampanyaOlusturFragmentArgs.fromBundle(it).isUser
          Log.e("isUser",""+isUser)


      }

        db= FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        auth=Firebase.auth




        setupAuthLis()


        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()
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
            binding.btnPaylas.isEnabled=false

            val uuid = UUID.randomUUID()
            val gorselismi = "${uuid}.jpg"
            val reference = storage.reference
            val gorselreference = reference.child("kampanyalar").child(gorselismi)


            if (secilengorsel!=null){
                binding.progressBar.visibility=View.VISIBLE
                gorselreference.putFile(secilengorsel!!).addOnSuccessListener {
                    val yuklenengorselreference=
                        FirebaseStorage.getInstance().reference.child("kampanyalar").child(gorselismi)
                    yuklenengorselreference.downloadUrl.addOnSuccessListener { uri->
                        val downloadurl=uri.toString()
                        veritabaninakaydet(downloadurl)


                    }.addOnFailureListener {  exception->
                        Toast.makeText(requireActivity(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility=View.INVISIBLE
                        binding.btnPaylas.isEnabled=true


                    }
                }
            }else{

                Toast.makeText(requireActivity(),"Lütfen Fotoğraf Yükleyiniz", Toast.LENGTH_LONG).show()
                binding.btnPaylas.isEnabled=true

            }

        }
    }

    private fun setupAuthLis() {



        mauthLis= FirebaseAuth.AuthStateListener {
            val user=FirebaseAuth.getInstance().currentUser

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
        (activity as AppCompatActivity).supportActionBar?.hide()
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


        kampanyaSayisiniGuncelle()
        binding.progressBar.visibility=View.GONE
        binding.gorselSec.visibility=View.VISIBLE
        binding.aciklamaId.visibility=View.VISIBLE
        binding.btnPaylas.visibility=View.VISIBLE

        val action=KampanyaOlusturFragmentDirections.actionKampanyaOlusturFragmentToProfilFragment()
        Navigation.findNavController(requireView()).navigate(action)




    }

    override fun onDetach() {
        super.onDetach()
        (activity as AppCompatActivity).supportActionBar?.show()
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