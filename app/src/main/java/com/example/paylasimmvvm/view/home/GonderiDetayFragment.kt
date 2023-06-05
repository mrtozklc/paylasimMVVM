package com.example.paylasimmvvm.view.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.HomeFragmentRecyclerAdapter
import com.example.paylasimmvvm.adapter.YorumlarRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentGonderiDetayBinding
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.model.Yorumlar
import com.example.paylasimmvvm.view.profil.UserProfilFragmentArgs
import com.example.paylasimmvvm.viewmodel.BadgeViewModel
import com.example.paylasimmvvm.viewmodel.GonderiDetayViewModel
import com.example.paylasimmvvm.viewmodel.YorumlarViewModel
import com.example.paylasimmvvm.viewmodel.kampanyalarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
            yorumKey = it.getString("yorum_key")
            val  bildirimID = it.getString("bildirimID")
            if (bildirimID!=null){
                val databaseRef = FirebaseDatabase.getInstance().reference.child("bildirimler").child(FirebaseAuth.getInstance().currentUser!!.uid).child(bildirimID)
                databaseRef.child("goruldu").setValue(true)
            }

        }


            gonderiDetayViewModel.getGonderiDetayi(userID!!, gonderiID!!)
            yorumlarDetayViewModel.gonderiYorumlariniAl(userID!!, gonderiID!!)


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

                // ilgili yoruma kaydırma
                binding.yorumlarRecycler.post {
                    val yorumPosition = tumYorumlar.indexOfFirst { it.yorum_key == yorumKey }
                    val layoutManager = binding.yorumlarRecycler.layoutManager as LinearLayoutManager
                    val targetView = layoutManager.findViewByPosition(yorumPosition)
                    val targetY = targetView?.let {
                        it.top + binding.nestedScroll.scrollY
                    } ?: 0
                    binding.nestedScroll.smoothScrollTo(0, targetY)
                }
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