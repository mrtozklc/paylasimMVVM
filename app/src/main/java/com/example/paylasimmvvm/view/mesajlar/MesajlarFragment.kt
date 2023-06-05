package com.example.paylasimmvvm.view.mesajlar

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.MesajlarRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentMesajlarBinding
import com.example.paylasimmvvm.model.Mesajlar
import com.example.paylasimmvvm.util.setBadge
import com.example.paylasimmvvm.viewmodel.BadgeViewModel
import com.example.paylasimmvvm.viewmodel.MesajlarViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MesajlarFragment : Fragment() {
    private lateinit var binding: FragmentMesajlarBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    private lateinit var recyclerAdapter:MesajlarRecyclerAdapter
    private lateinit var mesajlarViewModeli:MesajlarViewModel
    private var tumMesajlar=ArrayList<Mesajlar>()
    private lateinit var badgeViewModeli: BadgeViewModel
    private var listenerAtandiMi=false

    companion object {
        var fragmentAcikMi=false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMesajlarBinding.inflate(layoutInflater,container,false)
        val view=binding.root
        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference
        setupAuthLis()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerForContextMenu(binding.recyclerMesajlar)

        if(!listenerAtandiMi){
            listenerAtandiMi=true

            mesajlarViewModeli= ViewModelProvider(this)[MesajlarViewModel::class.java]

            mesajlarViewModeli.refreshMesajlar()

            badgeViewModeli= ViewModelProvider(this)[BadgeViewModel::class.java]
            badgeViewModeli.refreshMessageBadge()

            observeLiveData()

        }

        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerMesajlar.layoutManager=layoutManager
        recyclerAdapter= MesajlarRecyclerAdapter(tumMesajlar)
        binding.recyclerMesajlar.adapter=recyclerAdapter

        recyclerAdapter.setOnItemLongClickListener {
            showMenu()
        }
    }
    private fun showMenu() {
        binding.textView2.visibility=View.INVISIBLE
        binding.delete.visibility=View.VISIBLE
        binding.tumunuSec.visibility=View.VISIBLE
        binding.imageViewBack.visibility=View.VISIBLE

        binding.tumunuSec.setOnClickListener {
            recyclerAdapter.selectAll()
        }
        binding.imageViewBack.setOnClickListener {
            binding.textView2.visibility=View.VISIBLE
            binding.delete.visibility=View.INVISIBLE
            binding.tumunuSec.visibility=View.INVISIBLE
            binding.imageViewBack.visibility=View.INVISIBLE
            recyclerAdapter.deSelectAll()
        }
        binding.delete.setOnClickListener {
            val selectedItemsCount = recyclerAdapter.getSelectedItems().size

            if (selectedItemsCount > 0) {
                val alertDialog = AlertDialog.Builder(requireContext())
                    .setTitle("Mesajları Sil")
                    .setMessage("$selectedItemsCount mesajı silmek istiyor musunuz?")
                    .setPositiveButton("Evet") { _, _ ->

                        recyclerAdapter.setOnAllItemsDeletedListener {
                            mesajlarViewModeli.refreshMesajlar()
                            badgeViewModeli.refreshMessageBadge()
                        }
                        recyclerAdapter.deleteSelectedItems()
                        binding.textView2.visibility=View.VISIBLE
                        binding.delete.visibility=View.INVISIBLE
                        binding.tumunuSec.visibility=View.INVISIBLE
                        binding.imageViewBack.visibility=View.INVISIBLE
                    }
                    .setNegativeButton("Hayır", null)
                    .create()

                alertDialog.show()
            } else {
                Toast.makeText(requireContext(), "Lütfen öğeleri seçin", Toast.LENGTH_SHORT).show()
            }
        }

    }

  private  fun observeLiveData(){
        mesajlarViewModeli.mesajlarMutable.observe(viewLifecycleOwner) { Mesajlar ->
            Mesajlar.let {

                binding.recyclerMesajlar.visibility = View.VISIBLE
                if (Mesajlar != null) {


                    recyclerAdapter.mesajlariGuncelle(Mesajlar)
                }

            }

        }

      mesajlarViewModeli.mesajYok.observe(viewLifecycleOwner) {
          it.let {
              if (it) {
                  binding.mesajYok.visibility = View.VISIBLE
                  binding.recyclerMesajlar.visibility = View.GONE
              } else {
                  binding.mesajYok.visibility = View.GONE
              }
          }
      }

      mesajlarViewModeli.yukleniyor.observe(viewLifecycleOwner) {
            it.let {
                if (it) {
                    binding.progressBarMesajlar.visibility = View.VISIBLE
                    binding.recyclerMesajlar.visibility=View.GONE
                }else{
                    binding.progressBarMesajlar.visibility=View.GONE
                }
            }
        }
      badgeViewModeli.badgeLiveMessage.observe(viewLifecycleOwner) {gorulmeyenMesajSayisi ->
          gorulmeyenMesajSayisi.let {

              val navView: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
              if (gorulmeyenMesajSayisi != null) {
                  navView.setBadge(R.id.mesajlarFragment, gorulmeyenMesajSayisi.size)


              }

          }

      }
    }

    private fun setupAuthLis() {
        mauthLis= FirebaseAuth.AuthStateListener {
            val user=FirebaseAuth.getInstance().currentUser
            if (user==null){
                findNavController().popBackStack(R.id.mesajlarFragment,true)
                findNavController().navigate(R.id.loginFragment)

            }
        }

    }


    override fun onStart() {
        (activity as AppCompatActivity).supportActionBar?.hide()

        fragmentAcikMi=true
        super.onStart()


        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        fragmentAcikMi=false

        super.onStop()

        auth.removeAuthStateListener(mauthLis)

    }

    override fun onResume() {


        fragmentAcikMi=true

        if(listenerAtandiMi==false){
            tumMesajlar.clear()
            listenerAtandiMi=true
            recyclerAdapter.notifyDataSetChanged()
            mesajlarViewModeli.refreshMesajlar()
        }
        super.onResume()


    }

    override fun onPause() {
        fragmentAcikMi=false
        super.onPause()


        if(listenerAtandiMi){
            tumMesajlar.clear()

            listenerAtandiMi=false
            mesajlarViewModeli.removeListeners()
        }


    }



}