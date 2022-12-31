package com.example.paylasimmvvm.view.kokteyller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.adapter.KokteyllerRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentKokteyllerBinding
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.model.Kokteyl
import com.example.paylasimmvvm.viewmodel.KokteylViewModel


class KokteyllerFragment : Fragment() {
    private lateinit var binding:FragmentKokteyllerBinding

    private lateinit var recyclerAdapter: KokteyllerRecyclerAdapter
    private lateinit var kokteylViewModeli: KokteylViewModel
    private var tumKokteyller=ArrayList<Drink>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentKokteyllerBinding.inflate(layoutInflater,container,false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kokteylViewModeli= ViewModelProvider(this)[KokteylViewModel::class.java]
        kokteylViewModeli.refreshData()


        observeLiveData()

        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerKokteyl.layoutManager=layoutManager
        recyclerAdapter= KokteyllerRecyclerAdapter(tumKokteyller)
        binding.recyclerKokteyl.adapter=recyclerAdapter

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

    }


}