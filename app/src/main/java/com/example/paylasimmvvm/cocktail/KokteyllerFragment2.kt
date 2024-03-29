package com.example.paylasimmvvm.cocktail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R

import com.example.paylasimmvvm.databinding.FragmentKokteyller2Binding
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.util.setBadge
import com.example.paylasimmvvm.util.BadgeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class KokteyllerFragment2 : Fragment() {
    private lateinit var binding:FragmentKokteyller2Binding
    private lateinit var kokteylViewModeli: KokteylViewModel
    private lateinit var badgeViewModeli: BadgeViewModel
    private var tumKokteyller=ArrayList<Drink>()
    lateinit var searchView: SearchView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


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
        badgeViewModeli= ViewModelProvider(this)[BadgeViewModel::class.java]
        badgeViewModeli.refreshMessageBadge()

        binding.imageViewBack.setOnClickListener {
            binding.searchh.visibility=View.VISIBLE
            findNavController().navigateUp()
        }

        binding.searchh.setOnClickListener {
            binding.searchh.visibility=View.INVISIBLE
            binding.icecek.visibility=View.GONE

            binding.edittextSearch.visibility=view.visibility

        }


        arguments?.let {

            val  icecekOzellik = KokteyllerFragment2Args.fromBundle(it).icecekOzellik
            binding.icecek.text=icecekOzellik


            kokteylViewModeli.getCategoryList("list")
            kokteylViewModeli.getCocktailNameList("name")

            kokteylViewModeli.kokteylIsimleriLiveData.observe(viewLifecycleOwner) {
                if (icecekOzellik in it) kokteylViewModeli.getNameItem(icecekOzellik)

            }

            kokteylViewModeli.kokteylKategorileriLiveData.observe(viewLifecycleOwner) {
                if (icecekOzellik in it) kokteylViewModeli.getCategoriesItem(icecekOzellik)
            }



        }

        observeLiveData()



    }


    private fun observeLiveData(){
        val gridView = requireActivity().findViewById(R.id.idGRV) as GridView

        val customAdapter = KokteyllerGridAdapter(tumKokteyller)

        gridView.adapter = customAdapter




        kokteylViewModeli.kokteyller.observe(viewLifecycleOwner) { Kokteyller ->
            Kokteyller.let {


                binding.idGRV.visibility = View.VISIBLE
                if (Kokteyller != null) {
                    customAdapter.kokteylListesiniGuncelle(Kokteyller)
                    searchView = requireActivity().findViewById(R.id.edittextSearch)
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {

                            if(!query.isNullOrEmpty()){

                                val filteredList = Kokteyller.filter {
                                    it.kokteylIsim!!.contains(query, true)
                                }
                                customAdapter.kokteylListesiniGuncelle(filteredList)
                            }

                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if (newText.isNullOrEmpty()) {
                                customAdapter.kokteylListesiniGuncelle(Kokteyller)

                            } else {
                                val filteredList = Kokteyller.filter { it.kokteylIsim!!.contains(newText, true) }


                                customAdapter.kokteylListesiniGuncelle(filteredList)
                            }
                            customAdapter.notifyDataSetChanged()
                            return false
                        }
                    })
                }

            }

        }

        kokteylViewModeli.kokteylHataMesaji.observe(viewLifecycleOwner){
            it.let {
                if(it){
                    binding.hataMesaji.visibility=View.VISIBLE
                    binding.idGRV.visibility=View.GONE

                }else{
                    binding.hataMesaji.visibility=View.GONE


                }
            }

        }


        kokteylViewModeli.kokteylYukleniyor.observe(viewLifecycleOwner){
            it.let {
                if(it){
                    binding.hataMesaji.visibility=View.GONE
                    binding.idGRV.visibility=View.GONE
                    binding.progresBarKokteyl.visibility=View.VISIBLE

                }else{
                    binding.progresBarKokteyl.visibility=View.GONE

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

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()

    }



}