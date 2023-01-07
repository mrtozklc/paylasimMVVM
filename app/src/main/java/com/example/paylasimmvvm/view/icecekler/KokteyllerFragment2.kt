package com.example.paylasimmvvm.view.icecekler

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.paylasimmvvm.adapter.KokteyllerRecyclerAdapter2
import com.example.paylasimmvvm.databinding.FragmentKokteyller2Binding
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.viewmodel.KokteylViewModel


class KokteyllerFragment2 : Fragment() {
    private lateinit var binding:FragmentKokteyller2Binding
    private lateinit var recyclerAdapter: KokteyllerRecyclerAdapter2
    private lateinit var kokteylViewModeli: KokteylViewModel
    private var tumKokteyller=ArrayList<Drink>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentKokteyller2Binding.inflate(layoutInflater,container,false)
        // Inflate the layout for this fragment
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kokteylViewModeli= ViewModelProvider(this)[KokteylViewModel::class.java]

        arguments?.let {

            val  icecekOzellik = KokteyllerFragment2Args.fromBundle(it).icecekOzellik


            kokteylViewModeli.getCategoriesItem(icecekOzellik)

            kokteylViewModeli.getGlassItem(icecekOzellik)

            kokteylViewModeli.getIngredientsItem(icecekOzellik)



        }




        observeLiveData()

        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerKokteyl2.layoutManager=layoutManager
        recyclerAdapter= KokteyllerRecyclerAdapter2(tumKokteyller)
        binding.recyclerKokteyl2.adapter=recyclerAdapter
    }


    private fun observeLiveData(){

        kokteylViewModeli.kokteyller.observe(viewLifecycleOwner) { Kokteyller ->
            Kokteyller.let {

                binding.recyclerKokteyl2.visibility = View.VISIBLE
                if (Kokteyller != null) {


                    recyclerAdapter.kokteylListesiniGuncelle(Kokteyller)
                }

            }

        }

        kokteylViewModeli.kokteylHataMesaji.observe(viewLifecycleOwner){
            it.let {
                if(it){
                    binding.hataMesaji.visibility=View.VISIBLE
                    binding.recyclerKokteyl2.visibility=View.GONE

                }else{
                    binding.hataMesaji.visibility=View.GONE


                }
            }

        }

        kokteylViewModeli.kokteylYukleniyor.observe(viewLifecycleOwner){
            it.let {
                if(it){
                    binding.hataMesaji.visibility=View.GONE
                    binding.recyclerKokteyl2.visibility=View.GONE
                    binding.progresBarKokteyl.visibility=View.VISIBLE

                }else{
                    binding.progresBarKokteyl.visibility=View.GONE

                }
            }
        }


    }


}