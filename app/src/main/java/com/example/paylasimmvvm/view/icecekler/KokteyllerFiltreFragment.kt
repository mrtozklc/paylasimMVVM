package com.example.paylasimmvvm.view.icecekler


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.adapter.KokteyllerRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentKokteyllerFiltreBinding
import com.example.paylasimmvvm.viewmodel.BadgeViewModel
import com.example.paylasimmvvm.viewmodel.KokteylViewModel


class KokteyllerFiltreFragment : Fragment() {
    private lateinit var binding:FragmentKokteyllerFiltreBinding

    private lateinit var recyclerAdapter: KokteyllerRecyclerAdapter
    private lateinit var kokteylViewModeli: KokteylViewModel
    private var tumKokteyller=ArrayList<String>()
    lateinit var searchView: SearchView
    private lateinit var badgeViewModeli: BadgeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentKokteyllerFiltreBinding.inflate(layoutInflater,container,false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kokteylViewModeli= ViewModelProvider(this)[KokteylViewModel::class.java]
        badgeViewModeli= ViewModelProvider(this)[BadgeViewModel::class.java]
        badgeViewModeli.refreshBadge()



        observeLiveData()

        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerKokteyl.layoutManager=layoutManager
        recyclerAdapter= KokteyllerRecyclerAdapter(tumKokteyller)
        binding.recyclerKokteyl.adapter=recyclerAdapter

        kokteylViewModeli.getCategoryList("list")

        binding.searchh.setOnClickListener {
            binding.edittextSearch.visibility=View.VISIBLE
            binding.imageViewBack.visibility=View.VISIBLE
            kokteylViewModeli.getCocktailNameList("name")
        }
            binding.imageViewBack.setOnClickListener {
            binding.edittextSearch.visibility=View.INVISIBLE
            binding.imageViewBack.visibility=View.INVISIBLE
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
                                recyclerAdapter.kokteylListesiniGuncelle(filteredList as List<String>)
                            }

                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if (newText.isNullOrEmpty()) {
                                binding.recyclerKokteyl.visibility=View.VISIBLE
                                recyclerAdapter.kokteylListesiniGuncelle(it as List<String>)

                            } else {
                                val filteredList = it.filter { it!!.contains(newText, true) }


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

            binding.recyclerKokteyl.visibility=View.VISIBLE
            recyclerAdapter.kokteylListesiniGuncelle(it as List<String>)

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

    }



}