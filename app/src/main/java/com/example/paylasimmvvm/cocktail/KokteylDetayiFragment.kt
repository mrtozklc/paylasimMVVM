package com.example.paylasimmvvm.cocktail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentKokteylDetayiBinding
import com.example.paylasimmvvm.model.DrinkDetay
import com.example.paylasimmvvm.util.setBadge
import com.example.paylasimmvvm.util.BadgeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class KokteylDetayiFragment : Fragment() {
    private lateinit var binding:FragmentKokteylDetayiBinding
    private lateinit var recyclerAdapter: KokteylDetayRecyclerAdapter
    private lateinit var kokteylViewModeli: KokteylDetayiViewModel
    private lateinit var badgeViewModeli: BadgeViewModel
    private var tumKokteyller=ArrayList<DrinkDetay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentKokteylDetayiBinding.inflate(layoutInflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        badgeViewModeli= ViewModelProvider(this)[BadgeViewModel::class.java]
        badgeViewModeli.refreshMessageBadge()

        arguments?.let {


          val  kokteylID = KokteylDetayiFragmentArgs.fromBundle(it).kokteylId


            kokteylViewModeli= ViewModelProvider(this)[KokteylDetayiViewModel::class.java]

            kokteylViewModeli.verileriInternettenAl(kokteylID)

        }
        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()
        }



        observeLiveData()

        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerKokteyl.layoutManager=layoutManager
        recyclerAdapter= KokteylDetayRecyclerAdapter(tumKokteyller)
        binding.recyclerKokteyl.adapter=recyclerAdapter

    }

    private fun observeLiveData(){

        kokteylViewModeli.kokteyller.observe(viewLifecycleOwner) { Kokteyller ->
            Kokteyller.let {

                binding.recyclerKokteyl.visibility = View.VISIBLE
                if (Kokteyller != null) {

                    val kokteylIsmi = Kokteyller[0].kokteylIsim
                    binding.kokteylismi.text=kokteylIsmi



                    recyclerAdapter.kokteylDetayListesiniGuncelle(Kokteyller)
                }

            }

        }


        kokteylViewModeli.kokteylHataMesaji.observe(viewLifecycleOwner){
            it.let {
                if(it){
                    binding.hataMesaji.visibility=View.VISIBLE
                    binding.recyclerKokteyl.visibility=View.GONE

                }else{
                    binding.hataMesaji.visibility=View.GONE


                }
            }

        }

        kokteylViewModeli.kokteylYukleniyor.observe(viewLifecycleOwner){
            it.let {
                if(it){
                    binding.hataMesaji.visibility=View.GONE
                    binding.recyclerKokteyl.visibility=View.GONE
                    binding.progresBarKokteyl.visibility=View.VISIBLE

                }else{
                    binding.progresBarKokteyl.visibility=View.GONE

                }
            }
        }

        badgeViewModeli.badgeLiveMessage.observe(viewLifecycleOwner) {gorulmeyenMesajSayisi ->
            Log.e("hoomeegelenbadge",""+gorulmeyenMesajSayisi.size)
            gorulmeyenMesajSayisi.let {



                val navView: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
                if (gorulmeyenMesajSayisi != null) {
                     navView.setBadge(R.id.mesajlarFragment, gorulmeyenMesajSayisi.size)


                }

            }

        }

    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()

    }



}
