package com.example.paylasimmvvm.view.icecekler

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.KokteyllerGridAdapter

import com.example.paylasimmvvm.adapter.KokteyllerRecyclerAdapter2
import com.example.paylasimmvvm.adapter.MenulerGridAdapter
import com.example.paylasimmvvm.databinding.FragmentKokteyller2Binding
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.viewmodel.KokteylViewModel


class KokteyllerFragment2 : Fragment() {
    private lateinit var binding:FragmentKokteyller2Binding
    private lateinit var kokteylViewModeli: KokteylViewModel
    private var tumKokteyller=ArrayList<Drink>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
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

        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchh.setOnClickListener {
            binding.icecek.visibility=View.GONE
            binding.edittextSearch.visibility=view.visibility

        }


        arguments?.let {

            val  icecekOzellik = KokteyllerFragment2Args.fromBundle(it).icecekOzellik
            binding.icecek.text=icecekOzellik


            kokteylViewModeli.getCategoryList("list")
            kokteylViewModeli.getGlassList("list")
            kokteylViewModeli.getIngredientList("list")

            kokteylViewModeli.kokteylKategorileriLiveData.observe(viewLifecycleOwner) {
                if (icecekOzellik in it) kokteylViewModeli.getCategoriesItem(icecekOzellik)
            }

            kokteylViewModeli.kokteylBardaklariLiveData.observe(viewLifecycleOwner) {
                if (icecekOzellik in it) kokteylViewModeli.getGlassItem(icecekOzellik)
            }

            kokteylViewModeli.kokteylIcerikleriLiveData.observe(viewLifecycleOwner) {
                if (icecekOzellik in it) kokteylViewModeli.getIngredientsItem(icecekOzellik)
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
                    Log.e("observe",""+Kokteyller)


                    customAdapter.kokteylListesiniGuncelle(Kokteyller)
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


    }



}