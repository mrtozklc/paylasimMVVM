package com.example.paylasimmvvm.cocktail


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentKokteyllerFiltreBinding
import com.example.paylasimmvvm.util.setBadge
import com.example.paylasimmvvm.util.BadgeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class KokteyllerFiltreFragment : Fragment() {
    private lateinit var binding:FragmentKokteyllerFiltreBinding
    private lateinit var gridAdapter: KokteyllerFiltreGridAdapter
    private lateinit var recyclerAdapter: KokteyllerRecyclerAdapter
    private lateinit var kokteylViewModeli: KokteylViewModel
    private var tumKokteyller=ArrayList<String>()
    lateinit var searchView: SearchView
    private lateinit var badgeViewModeli: BadgeViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentKokteyllerFiltreBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kokteylViewModeli= ViewModelProvider(this)[KokteylViewModel::class.java]
        badgeViewModeli= ViewModelProvider(this)[BadgeViewModel::class.java]
        badgeViewModeli.refreshMessageBadge()



        observeLiveData()

       val layoutManager= LinearLayoutManager(activity)
        binding.recyclerKokteyl.layoutManager=layoutManager
        recyclerAdapter= KokteyllerRecyclerAdapter(tumKokteyller)
        binding.recyclerKokteyl.adapter=recyclerAdapter

        val gridView = requireActivity().findViewById(R.id.idGRVKokteyl) as GridView
        gridAdapter = KokteyllerFiltreGridAdapter(tumKokteyller)

        gridView.adapter = gridAdapter





        kokteylViewModeli.getCategoryList("list")

        binding.searchh.setOnClickListener {
            binding.kokteylBarId.visibility=View.GONE
            binding.edittextSearch.visibility=View.VISIBLE
            binding.imageViewBack.visibility=View.VISIBLE
            binding.searchh.visibility=View.GONE
            kokteylViewModeli.getCocktailNameList("name")
        }


            binding.imageViewBack.setOnClickListener {
                binding.kokteylBarId.visibility=View.VISIBLE
                binding.recyclerKokteyl.visibility=View.GONE
                binding.searchh.visibility=View.VISIBLE
                binding.edittextSearch.visibility=View.INVISIBLE
                binding.imageViewBack.visibility=View.INVISIBLE
                binding.idGRVKokteyl.visibility=View.VISIBLE
                kokteylViewModeli.getCategoryList("list")

        }
    }
    private fun observeLiveData(){

        kokteylViewModeli.kokteylIsimleriLiveData.observe(viewLifecycleOwner){
            it.let {
                if (it!=null){

                    searchView = requireActivity().findViewById(com.example.paylasimmvvm.R.id.edittextSearch)
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {

                            if(!query.isNullOrEmpty()){

                                val filteredList = it.filter {
                                    it!!.contains(query, true)
                                }
                                binding.idGRVKokteyl.visibility=View.GONE
                                recyclerAdapter.kokteylListesiniGuncelle(filteredList as List<String>)
                            }

                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if (newText.isNullOrEmpty()) {
                                binding.recyclerKokteyl.visibility=View.GONE
                                binding.idGRVKokteyl.visibility=View.VISIBLE
                                gridAdapter.kokteylListesiniGuncelle(it as List<String>)


                            } else {
                                val filteredList = it.filter { it!!.contains(newText, true) }
                                binding.idGRVKokteyl.visibility=View.GONE
                                binding.recyclerKokteyl.visibility=View.VISIBLE

                                recyclerAdapter.kokteylListesiniGuncelle(filteredList as List<String>)
                            }

                            return false
                        }
                    })
                }
            }
        }


        kokteylViewModeli.kokteylKategorileriLiveData.observe(viewLifecycleOwner){
         it.let {
        if (it!=null){

            binding.recyclerKokteyl.visibility=View.GONE
            gridAdapter.kokteylListesiniGuncelle(it as List<String>)

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