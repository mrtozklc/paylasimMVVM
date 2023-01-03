package com.example.paylasimmvvm.view.icecekler

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.adapter.KokteylDetayRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentKokteylDetayiBinding
import com.example.paylasimmvvm.model.DrinkDetay
import com.example.paylasimmvvm.viewmodel.KokteylDetayiViewModel

class KokteylDetayiFragment : Fragment() {
    private lateinit var binding:FragmentKokteylDetayiBinding
    private lateinit var recyclerAdapter: KokteylDetayRecyclerAdapter
    private lateinit var kokteylViewModeli: KokteylDetayiViewModel
    private var tumKokteyller=ArrayList<DrinkDetay>()


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
        arguments?.let {

          val  kokteylID = KokteylDetayiFragmentArgs.fromBundle(it).kokteylId

            kokteylViewModeli= ViewModelProvider(this)[KokteylDetayiViewModel::class.java]

            kokteylViewModeli.verileriInternettenAl(kokteylID)

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

    }


}
