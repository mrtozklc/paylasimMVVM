package com.example.paylasimmvvm.postDetail

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.comments.YorumlarRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentGonderiDetayBinding
import com.example.paylasimmvvm.createPost.KullaniciKampanya
import com.example.paylasimmvvm.model.Yorumlar
import com.example.paylasimmvvm.util.Bildirimler
import com.example.paylasimmvvm.home.HomeFragmentRecyclerAdapter
import com.example.paylasimmvvm.comments.YorumlarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.util.*


class GonderiDetayFragment : Fragment() {
    lateinit var binding:FragmentGonderiDetayBinding
    private lateinit var gonderiDetayViewModel: GonderiDetayViewModel
    private lateinit var yorumlarDetayViewModel: YorumlarViewModel
    private lateinit var recyclerviewadapter: HomeFragmentRecyclerAdapter
    private lateinit var recyclerviewadapter2: YorumlarRecyclerAdapter
    private var gonderiArray= ArrayList<KullaniciKampanya>()
    private var tumYorumlar=ArrayList<Yorumlar>()
    var gonderiID:String?=null
    var userID:String?=null
    var yorumKey:String?=null
    var yorumVarMi:Boolean?=null
    private var isLastItemVisible = false





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= FragmentGonderiDetayBinding.inflate(layoutInflater,container,false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gonderiDetayViewModel = ViewModelProvider(this)[GonderiDetayViewModel::class.java]
        yorumlarDetayViewModel = ViewModelProvider(this)[YorumlarViewModel::class.java]

        arguments?.let {
            yorumVarMi = it.getBoolean("yorum_var")
            gonderiID = it.getString("gonderi_id")
            userID = it.getString("user_id")
            yorumKey = it.getString("yorumKey")
            val  bildirimID = it.getString("bildirimID")
            if (bildirimID!=null){
                val databaseRef = FirebaseDatabase.getInstance().reference.child("bildirimler").child(FirebaseAuth.getInstance().currentUser!!.uid).child(bildirimID)
                databaseRef.child("goruldu").setValue(true)
            }

        }


            gonderiDetayViewModel.getGonderiDetayi(userID!!, gonderiID!!)

        if (yorumKey!=null){
            yorumlarDetayViewModel.gonderiYorumlariniAlWithKey(userID!!,gonderiID!!,yorumKey)
        }else{
            yorumlarDetayViewModel.gonderiYorumlariniAl(userID!!, gonderiID!!)
        }



        observeLiveData()

        val layoutManager = LinearLayoutManager(activity)
        binding.gonderiRecycler.layoutManager = layoutManager
        val layoutManager2 = LinearLayoutManager(activity)
        binding.yorumlarRecycler.layoutManager = layoutManager2

        recyclerviewadapter = HomeFragmentRecyclerAdapter(requireActivity(), gonderiArray, true)
        recyclerviewadapter2 = YorumlarRecyclerAdapter(tumYorumlar, true, gonderiID!!, userID!!)
        binding.gonderiRecycler.adapter = recyclerviewadapter
        binding.yorumlarRecycler.adapter = recyclerviewadapter2

        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()
        }
/*
        binding.nestedScroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val layoutManager = binding.yorumlarRecycler.layoutManager as LinearLayoutManager
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            val totalItemCount = layoutManager.itemCount

            if (lastVisibleItemPosition == totalItemCount - 1 && scrollY > 0) {
                // User reached the end of the nested RecyclerView, handle accordingly
                Log.e("kaydırmaayapıldı", "")
                yorumlarDetayViewModel.nextComments()
            }
        }
*/


        binding.yorumEkle.setOnClickListener {
            val yorum=binding.yorumEditText.text.toString().trim()
            if(!TextUtils.isEmpty(yorum)){
                val yeniYorumGonderiReference = FirebaseDatabase.getInstance().reference.child("kampanya").child(userID!!).child(gonderiID!!).child("yorumlar").push()
                val pushKey = yeniYorumGonderiReference.key
                val yeniYorum = hashMapOf<String, Any>(
                    "yorum_key" to pushKey.toString(),
                    "user_id" to FirebaseAuth.getInstance().currentUser!!.uid,
                    "yorum" to binding.yorumEditText.text.toString(),
                    "yorum_begeni" to "0",
                    "yorum_tarih" to ServerValue.TIMESTAMP)
                yeniYorumGonderiReference.setValue(yeniYorum)
                if (userID!=FirebaseAuth.getInstance().currentUser!!.uid) {}
                Bildirimler.bildirimKaydet(
                    userID!!,
                    Bildirimler.YORUM_YAPILDI,
                    gonderiID!!,
                    "",
                    binding.yorumEditText.text.toString(),
                    pushKey)
                yorumlarDetayViewModel.gonderiYorumlariniAl(userID!!,gonderiID!!)
                binding.yorumEditText.setText("")

                binding.yorumlarRecycler.smoothScrollToPosition(binding.yorumlarRecycler.adapter!!.itemCount)
            }

        }


    }
    private fun observeLiveData() {
        gonderiDetayViewModel.gonderiMutable.observe(viewLifecycleOwner) { gonderi ->
            gonderi.let {


                binding.gonderiRecycler.visibility = View.VISIBLE

                recyclerviewadapter.kampanyalariGuncelle(gonderi)
            }

        }

        gonderiDetayViewModel.kampanyaYok.observe(viewLifecycleOwner) { kampanyaYok ->
            kampanyaYok.let {
                if (it) {
                    binding.hataId.visibility = View.VISIBLE
                    binding.gonderiRecycler.visibility = View.GONE

                } else {
                    binding.hataId.visibility = View.GONE

                }

            }

        }

        gonderiDetayViewModel.yukleniyor.observe(viewLifecycleOwner) { yukleniyor ->
            yukleniyor.let {
                if (it) {

                    binding.progressBar8.visibility = View.VISIBLE
                    binding.hataId.visibility = View.GONE
                    binding.gonderiRecycler.visibility = View.GONE

                } else {
                    binding.progressBar8.visibility = View.GONE

                }

            }

        }

        yorumlarDetayViewModel.mutableYorumlar.observe(viewLifecycleOwner) { yorumlar ->
            yorumlar?.let {
                binding.yorumlarRecycler.visibility = View.VISIBLE
                recyclerviewadapter2.yorumlariGuncelle(yorumlar)
                tumYorumlar = yorumlar as ArrayList<Yorumlar>


            }
        }

        yorumlarDetayViewModel.yorumYok.observe(viewLifecycleOwner) { YorumYok->
            YorumYok.let {
                if (it){
                    binding.hataId.visibility=View.VISIBLE
                    binding.yorumlarRecycler.visibility=View.GONE

                }else{
                    binding.hataId.visibility=View.GONE

                }

            }

        }
        yorumlarDetayViewModel.yukleniyor.observe(viewLifecycleOwner) { yukleniyor->
            yukleniyor.let {
                if (it){
                    binding.progressBar8.visibility=View.VISIBLE
                    binding.hataId.visibility=View.GONE
                    binding.yorumlarRecycler.visibility=View.GONE

                }else{
                    binding.progressBar8.visibility=View.GONE

                }

            }

        }


    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }


}