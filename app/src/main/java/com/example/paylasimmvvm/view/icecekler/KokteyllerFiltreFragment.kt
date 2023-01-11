package com.example.paylasimmvvm.view.icecekler

import android.R
import android.os.Build
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.adapter.KokteyllerRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentKokteyllerFiltreBinding
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.viewmodel.KokteylViewModel


class KokteyllerFiltreFragment : Fragment() {
    private lateinit var binding:FragmentKokteyllerFiltreBinding

    private lateinit var recyclerAdapter: KokteyllerRecyclerAdapter
    private lateinit var kokteylViewModeli: KokteylViewModel
    private var tumKokteyller=ArrayList<Drink>()
    var secilenFiltre:String?=null

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



        observeLiveData()

        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerKokteyl.layoutManager=layoutManager
        recyclerAdapter= KokteyllerRecyclerAdapter(tumKokteyller)
        binding.recyclerKokteyl.adapter=recyclerAdapter

        val filtre = java.util.ArrayList<String>()
        filtre.add("Category")
        filtre.add("Glass")
        filtre.add("Ingredient")


        val spinnerAdapter = ArrayAdapter(requireActivity(), R.layout.simple_list_item_1, filtre)
        binding.spinner2.adapter = spinnerAdapter


        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                secilenFiltre = binding.spinner2.selectedItem.toString()


                if (secilenFiltre.equals("Category")){

                    kokteylViewModeli.getCategoryList("list")

                }else if (secilenFiltre.equals("Glass")){

                    kokteylViewModeli.getGlassList("list")

                }else{
                    kokteylViewModeli.getIngredientList("list")



                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

    }

    private fun observeLiveData(){

        kokteylViewModeli.kokteyller.observe(viewLifecycleOwner) { Kokteyller ->
            Kokteyller.let {

                binding.recyclerKokteyl.visibility = View.VISIBLE
                if (Kokteyller != null) {


                    recyclerAdapter.kokteylListesiniGuncelle(Kokteyller)
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